package com.quui.tm2.agents.classifier.bayestree;

import java.util.ArrayList;
import java.util.List;

import com.quui.tm2.agents.classifier.bayestree.permutations.PermutationGenerator;
import com.quui.tm2.agents.classifier.bayestree.permutations.RecursivePermutationGenerator;
import com.quui.tm2.agents.classifier.weka.WsdClassifier;

/**
 * A bayes tree: a hierarchical bayes classifier, based on concepts of
 * hierarchical temporal memory (HTM), using belief propagation
 * 
 * @author fsteeg
 * 
 */
public class BayesTree implements WsdClassifier{

	/**
	 * The root node
	 */
	BayesNode root;

	/**
	 * Tree topology, number of children of each node on the levels of the tree
	 */
	int[] treeStructure;

	/**
	 * The factor to use for multiplication with the number of leafs in the tree
	 */
	float patternFactor;

	List<BayesNode> leafs;

	/**
	 * The number of classes this tree shoudl be able to classify
	 */
	private List<String> classes;

	private int nodeCounter;

	private List<List<String>> allLanguages;

	/**
	 * @param layers
	 *            The layers the tree should have (root is 0)
	 * @param patternFactor
	 *            The factor to use for multiplication with the number of leafs
	 *            in the tree
	 * @param classes
	 *            The number of possible classes
	 */
	public BayesTree(int[] structure, float patternFactor, List<String> classes) {
		this.patternFactor = patternFactor;
		this.classes = classes;
		this.treeStructure = structure;
		constructTree();
	}

	/**
	 * @param input
	 *            The feature vector to train the tree with
	 * @param correct
	 *            The correct class (supervised learning)
	 */
	public void train(float[] input, String correct) {
		// this.trained = false;
		// train, counting pattern occurences
		for (int i = 0; i < input.length; i++) {
			leafs.get(i).train(input[i], correct);
		}
		// update the cpds of all nodes after each go
		for (BayesNode node : leafs) {
			node.updateCPDs(correct);
		}
	}

	/**
	 * @param input
	 *            The feature vector to classify
	 * @return Returns the class for the input
	 */
	public String classify(float[] input) {
		if (input.length != this.leafs.size()) {
			throw new IllegalStateException("Input vector has wrong size: "
					+ input.length);
		}
		int g = 0;
		clearMessages(root);
		for (BayesNode node : this.leafs) {
			node.classify(input[g]);
			g++;
		}
		return root.activation;
	}

	/**
	 * @return The tree root node
	 */
	public BayesNode getRoot() {
		return root;
	}

	public List<BayesNode> getLeafs() {
		return leafs;
	}

	private void clearMessages(BayesNode node) {
		node.messageFromParent = null;
		node.messagesFromChildren = new ArrayList<Float[]>();
		for (BayesNode child : node.children) {
			clearMessages(child);
		}

	}

	private void constructTree() {
		this.root = new BayesNode(null, 0, 0, this);
		nodeCounter++;
		leafs = addChildren(root, 0, new ArrayList<BayesNode>(), 0);
		allLanguages = new ArrayList<List<String>>();
		createLanguages(leafs.get(0));
		for (BayesNode n : leafs) {
			fillLanguages(n, 0);
		}
	}

	private void fillLanguages(BayesNode n, int pos) {
		n.language = allLanguages.get(pos);
		if (!n.isRoot() && pos + 1 < allLanguages.size()) {
			fillLanguages(n.parent, pos + 1);
		}
		initArrays(n);
	}

	/**
	 * Recursivly, top-down, creates the languages for the node and his children
	 */
	private void createLanguages(BayesNode node) {
		node.language = addPatterns(node);
		allLanguages.add(node.language);
		if (allLanguages.size() <= treeStructure.length) {
			if (!node.isRoot()) {
				createLanguages(node.parent);
			}
		}
	}

	/**
	 * Initializes the matrices based on the language
	 */
	private void initArrays(BayesNode node) {
		node.cpd = new Float[node.isRoot() ? 1 : node.parent.language.size()][node.language
				.size()];
		node.absCPD = new int[node.cpd.length][node.cpd[0].length];
		ArrayTools.fill(node.absCPD, 0);
		ArrayTools.fill(node.cpd, 0.0f);
	}

	/**
	 * @return Returns a list containing the possible patterns for this node,
	 *         depending on the position of the node in the tree and the chosen
	 *         tree topology (see inline comments)
	 */
	private List<String> addPatterns(BayesNode node) {
		List<String> lang = new ArrayList<String>();
		// for the root node, the possible patterns are the meanings or senses:
		if (node.isRoot()) {
			for (String string : classes) {
				lang.add(string);
			}
		}
		// leafs have patterns with only one activated element:
		else if (node.isLeaf()) {
			double length = leafs.size() * patternFactor;
			for (int j = 0; j < length; j++) {
				StringBuilder builder = new StringBuilder();
				for (int c = 0; c < length; c++) {
					builder.append(c == j ? "1" : "0");
				}
				if (!lang.contains(builder.toString()))
					lang.add(builder.toString());
			}
		}
		/*
		 * Inner nodes have {1, 2, 3 ..., number of children if children are
		 * leafs or until max length for others} elements activated
		 */
		else {
			PermutationGenerator gen = new RecursivePermutationGenerator();
			int length = node.children.get(0).language.get(0).length() / 2;
			lang = new ArrayList<String>(gen.permutations(length, node.children
					.get(0).isLeaf() ? node.children.size() : length));
		}
		return lang;
	}

	/**
	 * @param node
	 *            Node to create children for
	 * @param layer
	 *            The current layer
	 * @param leafs
	 *            The leafs
	 * @return Retruns the leafs of the completed tree
	 */
	private List<BayesNode> addChildren(BayesNode node, int layer,
			List<BayesNode> leafs, int index) {
		layer++;
		List<BayesNode> children = new ArrayList<BayesNode>();
		for (int i = 0; i < treeStructure[index]; i++) {
			children.add(new BayesNode(node, nodeCounter, layer, this));
			nodeCounter++;
		}
		index++;
		node.children = children;
		if (index <= treeStructure.length - 1) {
			for (BayesNode child : children) {
				addChildren(child, layer, leafs, index);
			}
		} else {
			for (BayesNode child : children) {
				leafs.add(child);
			}
		}
		return leafs;
	}

	public void resetClassify(BayesNode node) {
		node.inputLambda = null;
		for (BayesNode child : node.children) {
			resetClassify(child);
		}

	}

	public String getActivation() {
		return root.activation;
	}

	public int featureSize() {
		return getLeafs().size();
	}

	public void resetClassify() {
		resetClassify(getRoot());
	}
}
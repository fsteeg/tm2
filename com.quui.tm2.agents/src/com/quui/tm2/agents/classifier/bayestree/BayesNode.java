package com.quui.tm2.agents.classifier.bayestree;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A node in the bayes tree. This is a package protected class and should only
 * be used from the public methods in BayesTree
 * 
 * @author fsteeg
 * 
 */
class BayesNode {

	/**
	 * Layer (root is 0) and index (depth-first numbering) of this node
	 */
	int layer, index;

	/**
	 * A counter for each pattern this node has activated
	 */
	Map<String, Map<String, Integer>> counters;

	/**
	 * Counters for the co-occurence of patterns on this level with patterns of
	 * the node's children
	 */
	Map<String, Map<String, Map<String, Integer>>> frequencies;

	/**
	 * A node in a tree has a parent
	 */
	BayesNode parent;

	/**
	 * And multiple children
	 */
	List<BayesNode> children;

	/**
	 * The pattern activated in this node
	 */
	String activation;

	/**
	 * The possible words or patterns this node uses
	 */
	List<String> language;

	/**
	 * Absolute frequencies of occurences of patterns in this level with
	 * patterns on the children nodes
	 */
	int[][] absCPD;

	/**
	 * The actual knowledge of a node: the conditional probability distribution
	 * of patterns on this node with children's nodes patterns
	 */
	Float[][] cpd;

	/**
	 * The tree this node is a part of
	 */
	private final BayesTree tree;

	private int mods = 0;

	Float[] messageFromParent = null;

	List<Float[]> messagesFromChildren;

	Float[] inputLambda = null;

	/**
	 * @param parent
	 *            The parent node for this node
	 * @param index
	 *            The index of this node in the tree
	 * @param layer
	 *            The layer the node is at in the tree (root is 0)
	 * @param tree
	 *            The tree this node is a part of
	 */
	BayesNode(BayesNode parent, int index, int layer, BayesTree tree) {
		this.tree = tree;
		this.parent = parent;
		this.index = index;
		this.layer = layer;
		counters = new HashMap<String, Map<String, Integer>>();
		frequencies = new HashMap<String, Map<String, Map<String, Integer>>>();
		children = new ArrayList<BayesNode>();
		messagesFromChildren = new ArrayList<Float[]>();
	}

	/**
	 * @param d
	 *            Input value to bottom-up train the tree
	 * @param correct
	 *            The correct class for this input (supervised learning)
	 */
	void train(float d, String correct) {
		String s = createPattern(d);
		this.activation = s.toString();
		this.parent.train(this.activation, correct);
	}

	/**
	 * @param d
	 *            The input value to bottom up classify in the tree
	 */
	void classify(float d) {
		// input for leafs is the activation depending on the input number:
		int index = language.indexOf(createPattern(d));
		if (inputLambda == null) {
			inputLambda = ArrayTools.initialLambda(language.size(), index);
		} else {
			inputLambda[index] = (inputLambda[index] + 1.0f) / 2.0f;
		}
		classify();
	}

	/**
	 * Recursivly, bottom up update of the cpd at this node from the patterns
	 * the node has seen
	 * @param correct 
	 */
	void updateCPDs(String correct) {
		if (activation == null) {
			throw new IllegalArgumentException("Null activation");
		}
		// the level-2 name-frequencies for a level-1 name:
		Map<String, Map<String, Integer>> freqMap = frequencies.get(correct);
		if(freqMap==null){freqMap = new HashMap<String, Map<String, Integer>>(); frequencies.put(correct, freqMap);}
    Map<String, Integer> patterns = freqMap.get(activation);
		if (patterns == null) {
			patterns = new HashMap<String, Integer>();
			freqMap.put(activation, patterns);
		}
		Map<String, Integer> countMap = counters.get(correct);
    if(countMap==null){countMap = new HashMap<String, Integer>(); counters.put(correct, countMap);}
		for (String k : countMap.keySet()) {
			// the frequencies of level-2 names for level-1 names:
			Integer count = countMap.get(k);
			patterns.put(k, count);
			int indexOf1 = parent.language.indexOf(k);
			int indexOf2 = language.indexOf(activation);
			if (indexOf1 == -1 || indexOf2 == -1) {
				throw new IllegalStateException(
						"We have a language element that isn't found!");
			}
			absCPD[indexOf1][indexOf2]++;
		}
		cpd = ArrayTools.normalizeForSum(absCPD);
		// recurse upwards in the tree:
		if (parent != null)
			parent.updateCPDs(correct);
	}

	/**
	 * @return True, if this node has no parent
	 */
	boolean isRoot() {
		return parent == null;
	}

	/**
	 * @return True, if this node has no children
	 */
	boolean isLeaf() {
		return children.size() == 0;
	}

	/**
	 * Recursivly classifies bottom up.
	 */
	private void classify() {
		Float[] lambda = null;
		/* pi comes from the parent or is formed */
		Float[] fakePi = new Float[cpd.length];
		Arrays.fill(fakePi, 1f);
		// TODO pi has no effect, we need continous input!!
		Float[] pi = fakePi;
		// messageFromParent != null ? messageFromParent
		// : /* parent != null ? */ArrayTools.initialPi(cpd.length)
		/* : new Double[] {} */;
		if (pi.length != cpd.length) {
			throw new IllegalStateException("Created invalid pi: " + pi.length
					+ ", should be: " + cpd.length);
		}
		/* special case: leafs get their lambda from the input: */
		if (isLeaf() && inputLambda != null) {
			lambda = inputLambda;
		}
		/* if both children have sent their message, form a lambda form that: */
		else if (messagesFromChildren.size() == children.size()) {
			lambda = ArrayTools.product(messagesFromChildren);
		}
		/*
		 * if they haven't, we can stop recursion, and send more from lower
		 * levels:
		 */
		else {
			return;
		}

		/* calculate new messages: */
		Float[][] m = new Float[cpd.length][cpd[0].length];
		m = ArrayTools.multiplyRows(pi, cpd);
		m = ArrayTools.multiplyColums(lambda, m);// TODO oder hier m?
		Float[] lambdaNext = ArrayTools.maxForRows(m);
		Float[] piNext = ArrayTools.maxForCols(m);

		/* if we are above leafs, send messages downwards: */
		if (!isLeaf()) {
			for (BayesNode child : children) {
				child.messageFromParent = piNext;
				if (child.messageFromParent.length != child.cpd.length) {
					throw new IllegalStateException(
							"Sending invalid message from parent: "
									+ child.messageFromParent.length
									+ ", should be: " + child.cpd.length);
				}
			}
		}

		/* if we are below root, send a message upwards: */
		if (!isRoot()) {
			parent.messagesFromChildren.add(lambdaNext);
			// then, we recurse upwards:
			parent.classify();
		}

		// root nodes activate the result from the incoming lambda:
		else {
			// System.err.println(ArrayTools.format(lambda,Preferences.getInstance().digits));
			// if (Preferences.getInstance().debug) {
			// System.out.print("Lambda in Root: "
			// + ArrayTools.format(lambda, 3) + " ");
			// }
			Float max = Float.MIN_VALUE;
			int maxIndex = -1;
			for (int i = 0; i < lambda.length; i++) {
				if (lambda[i].compareTo(max) > 0) {
					max = lambda[i];
					maxIndex = i;
				}
			}
			if (maxIndex > -1) {
				this.activation = language.get(maxIndex);
			}
			return;
		}
	}

	/**
	 * @param d
	 *            The Double to create a pattern for (leafs only)
	 * @return Return a string with the appropriate element activated, its size
	 *         depends on the size of the tree and the chosen pattern factor
	 */
	private String createPattern(float d) {
		float leafs = tree.leafs.size();
		float length = leafs * tree.patternFactor;
		StringBuilder builder = new StringBuilder();
		for (int i = 1; i <= length; i++) {
			builder.append("0");
		}
		return pattern(builder, d);
	}

	private String pattern(StringBuilder m, float n) {
		for (int i = 0; i < m.length(); i++) {
			double v = 1.0 / m.length() * i;
			if (n <= v || i == m.length() - 1) {
				m.setCharAt(i, '1');
				return m.toString();
			}
		}
		throw new IllegalStateException("Could not create pattern in "
				+ m.toString() + " with: " + n);
		// return null;
	}

	private void train(String activation, String correct) {
		int size = children.size();

		if (isRoot() && correct != null) {
			this.activation = correct;

		} else {
			if (mods == size) {
				this.activation = null;
				mods = 0;
			}
			StringBuilder builder = new StringBuilder(
					this.activation != null ? this.activation : "");
			double pow = language.get(0).length();
			// init activation
			if (this.activation == null) {
				for (int i = 0; i < pow; i++) {
					builder.append("0");
				}
			}
			// a node activates an element in his pattern if one of the two
			// corresponding elements in the incoming pattern are activated:
			for (int i = 0; i < pow; i++) {
				try {
					if ((activation.charAt(i * 2) == '1' || activation
							.charAt(i * 2 + 1) == '1')
							&& mods < size) {
						builder.setCharAt(i, '1');
					}
				} catch (StringIndexOutOfBoundsException e) {
					e.printStackTrace();
				}
			}
			this.activation = builder.toString();
			mods++;
		}
		// the children track the activation of pattern on this level:
		for (BayesNode n : children) {
			n.count(this.activation, correct);
			// if(isRoot()){
			// System.err.println("Counting Acrtivation: " + this.activation);
			// }
		}
		// recurse upwards in the tree:
		if (parent != null)
			parent.train(this.activation, correct);
	}

	private void count(String pattern, String correct) {
		Map<String, Integer> map = counters.get(correct);
		if(map == null){ map = new HashMap<String, Integer>(); counters.put(correct, map); }
    Integer c = map.get(pattern);
		if (c == null) {
			map.put(pattern, 1);
		} else
			map.put(pattern, map.get(pattern) + 1);
	}
}

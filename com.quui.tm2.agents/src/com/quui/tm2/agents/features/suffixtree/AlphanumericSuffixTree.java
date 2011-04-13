/**
 * 
 */
package com.quui.tm2.agents.features.suffixtree;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.quui.tm2.agents.features.suffixtree.node.Node;
import com.quui.tm2.agents.features.suffixtree.node.NodeAccessor;



/**
 * @author fsteeg
 * 
 */
public abstract class AlphanumericSuffixTree extends NumericSuffixTree {
    String text;

    boolean reverse;

    boolean generalized;

    public Mapper mapper;

    SimpleSequenceAccessor sentenceAccessor;

    /**
     * @param text
     *            The text to add to the tree, termination is handeled
     *            internally
     * @param reverse
     *            If true a tree is built for the reveresed word order
     * @param generalized
     *            If true the text is splittet into sentences, which are
     *            inserted separately (<b>Attention</b>: In the current
     *            Implementation, for generalized trees the construction runtime
     *            seems to grow quadratic)
     */
    public AlphanumericSuffixTree(String text, boolean reverse,
            boolean generalized, NodeAccessor accessor) {
        super(accessor, new SimpleSequenceAccessor());
        this.reverse = reverse;
        this.generalized = generalized;
        setText(text);
    }

    /**
     * Sets the text and constructs the tree. TODO implement adding of multiple
     * texts from outside (public API)
     * 
     * @param text
     *            The text to add, termination is handled internally
     * 
     */
    private void setText(String text) {
        this.text = text;
        construct();
    }

    abstract void construct();

    /**
     * <b>Experimental</b>
     * 
     * @param textNo
     *            The text number to get the suffix from
     * @param suffixIndex
     *            The index at which the suffix starts
     * @return The leaf representing the suffix starting at suffixIndex in
     *         textNo
     */
    public Node getNodeForSuffix(int textNo, int suffixIndex) {
        ArrayList<Node> leafs = this.getAllNodes(getRoot(), null, true);
        for (int i = 0; i < leafs.size(); i++) {
            Node node = (Node) leafs.get(i);
            if (node.getTextNumber() == textNo
                    && node.getSuffixIndex() == suffixIndex)
                return node;
        }
        return null;
    }

    /**
     * <b>Experimental</b>
     * 
     * @param id
     *            The id of the node to get
     * @return The Node with id e
     */
    public Node getNodeForId(int id) {
        ArrayList<Node> leafs = this.getAllNodes(getRoot(), null, false);
        for (int i = 0; i < leafs.size(); i++) {
            Node node = (Node) leafs.get(i);
            if (node.getDfs() == id)
                return node;
        }
        return null;
    }

    /**
     * Prints all nodes
     */
    public void printNodes() {
        Node root = super.getRoot();
        ArrayList<Node> all = new ArrayList<Node>();
        all.addAll(super.getAllNodes(root, null, false));
        for (Node node : all) {
            System.out.println(node.toString());
        }

    }

    /**
     * @param string
     *            The location to store the dot file
     * @param numerical
     *            If true the numerical values are displayed in the tree
     */
    public void exportDot(String string, boolean numerical) {
        if (numerical)
            super.exportDot(string);
        else
            mapper.exportDot(string);
    }

    /**
     * (non-Javadoc)
     * 
     * @see de.uni_koeln.spinfo.strings.algo.suffixtrees.NumericSuffixTree#exportDot(java.lang.String)
     */
    public void exportDot(String string) {
        mapper.exportDot(string);
    }

    /**
     * @param node
     *            The node to get the incoming label for
     * @return Returns the incoming label for the node
     */
    public String getIncomingEdgeLabel(Node node) {
        return mapper.getTranslatedEdgeLabel(node).toString().replaceAll("\\$",
                "").trim();
    }

    /**
     * @return Returns all nodes in the tree
     */
    public List<Node> getAllNodes() {
        return getAllNodes(super.getRoot(), new ArrayList(), false);
    }

    /**
     * @param node
     *            The node to start from
     * @return Returns all concatenations of all edge labels on all paths from
     *         the node to a leaf
     */
    public Collection<? extends String> getLabelsUntilLeaf(Node node) {
        Set<String> result = new HashSet<String>();
        addAllLabels(node, result, "");
        return result;
    }

    private void addAllLabels(Node node, Set<String> result, String builder) {
        if (!node.isInternal())
            return;
        List<Node> children = node.getChildren();
        for (Node child : children) {
            String concat = builder + getIncomingEdgeLabel(child) + " ";
            if (!child.isInternal()) {
                result.add(concat.trim());
            } else {
                addAllLabels(child, result, concat);
            }
        }

    }

    public ArrayList<Node> getAllNodes(Node node, boolean leafsOnly) {
        return getAllNodes(node, new ArrayList(), leafsOnly);
    }

//    public int[] find(String string) {
//        for (Node node : getAllNodes()) {
//            // if (node.isInternal()) {
//            String prefix = getIncomingEdgeLabel(node);
//            if (string.startsWith(prefix)) {
//                String suffix = string.substring(prefix.length());
//                Collection<? extends String> labelsUntilLeaf = getLabelsUntilLeaf(node);
//                if (prefix.equals(string) || labelsUntilLeaf.contains(suffix.trim())) {
//                    return new int[] { node.getSuffixIndex(),
//                            node.getTextNumber() };
//                }
//            }
//            // }
//        }
//        return null;
//    }
}

package com.quui.tm2.agents.features.suffixtree.node;

import java.util.List;
import java.util.Map;

public class SimpleNodeAccessor implements NodeAccessor<SimpleNode> {

    private int idCounter = 1;

    private SimpleNode root;

    /*
     * (non-Javadoc)
     * 
     * @see de.uni_koeln.spinfo.strings.algo.suffixtrees.node.NodeAccessor#getParent(de.uni_koeln.spinfo.strings.algo.suffixtrees.node.Node)
     */
    public List<SimpleNode> getParents(SimpleNode node) {
        return ((SimpleNode) node).getParents();
    }

    /*
     * (non-Javadoc)
     * 
     * @see de.uni_koeln.spinfo.strings.algo.suffixtrees.node.NodeAccessor#getSuffixLink(de.uni_koeln.spinfo.strings.algo.suffixtrees.node.Node)
     */
    public SimpleNode getSuffixLink(SimpleNode currentNode) {
        return currentNode.getSuffixLink();
    }

    /*
     * (non-Javadoc)
     * 
     * @see de.uni_koeln.spinfo.strings.algo.suffixtrees.node.NodeAccessor#isTerminal(de.uni_koeln.spinfo.strings.algo.suffixtrees.node.Node)
     */
    public boolean isTerminal(SimpleNode currentNode) {
        return currentNode.isTerminal();
    }

    /*
     * (non-Javadoc)
     * 
     * @see de.uni_koeln.spinfo.strings.algo.suffixtrees.node.NodeAccessor#setSuffixLink(de.uni_koeln.spinfo.strings.algo.suffixtrees.node.Node)
     */
    public void setSuffixLink(SimpleNode from, SimpleNode to) {
        from.setSuffixLink(to);

    }

    /*
     * (non-Javadoc)
     * 
     * @see de.uni_koeln.spinfo.strings.algo.suffixtrees.node.NodeAccessor#setAdditionalLabels(de.uni_koeln.spinfo.strings.algo.suffixtrees.node.Node,
     *      int[])
     */
    public void setAdditionalLabels(SimpleNode leaf, int[] additionalLabels) {
        leaf.setAdditionalLabels(additionalLabels);

    }

    /*
     * (non-Javadoc)
     * 
     * @see de.uni_koeln.spinfo.strings.algo.suffixtrees.node.NodeAccessor#setParent(de.uni_koeln.spinfo.strings.algo.suffixtrees.node.Node,
     *      de.uni_koeln.spinfo.strings.algo.suffixtrees.node.Node)
     */
    public void setParents(SimpleNode child, List<SimpleNode> parent) {
        child.setParent(parent);

    }

    /*
     * (non-Javadoc)
     * 
     * @see de.uni_koeln.spinfo.strings.algo.suffixtrees.node.NodeAccessor#setLabelEnd(de.uni_koeln.spinfo.strings.algo.suffixtrees.node.Node,
     *      int)
     */
    public void setLabelEnd(SimpleNode leaf, int e) {
        leaf.setLabelEnd(e);
    }

    /*
     * (non-Javadoc)
     * 
     * @see de.uni_koeln.spinfo.strings.algo.suffixtrees.node.NodeAccessor#createNode()
     */
    public SimpleNode createRootNode() {
        if (root != null)
            return root;
        root = new SimpleNode();
        root.setId(idCounter++);
        return root;
    }

    /*
     * (non-Javadoc)
     * 
     * @see de.uni_koeln.spinfo.strings.algo.suffixtrees.node.NodeAccessor#createNode(de.uni_koeln.spinfo.strings.algo.suffixtrees.node.Node,
     *      int, int, int, int)
     */
    public SimpleNode createInternalNode(List<SimpleNode> parent,
            int suffixStart, int splittingPos) {
        SimpleNode toReturn = new SimpleNode(parent, suffixStart, splittingPos);
        toReturn.setId(idCounter++);
        // System.out.println("Creating Internal Node:" + toReturn.getId());
        return toReturn;
    }

    /*
     * (non-Javadoc)
     * 
     * @see de.uni_koeln.spinfo.strings.algo.suffixtrees.node.NodeAccessor#createNode(de.uni_koeln.spinfo.strings.algo.suffixtrees.node.Node,
     *      int, int, int)
     */
    public SimpleNode createLeafNode(List<SimpleNode> parent, int suffixStart,
            int number, int suffixIndex) {
        SimpleNode toReturn = new SimpleNode(parent, suffixStart, number,
                suffixIndex);
        toReturn.setId(idCounter++);
        // System.out.println("Creating Leaf Node:" + toReturn.getId());
        return toReturn;
    }

    /*
     * (non-Javadoc)
     * 
     * @see de.uni_koeln.spinfo.strings.algo.suffixtrees.node.NodeAccessor#addChild(de.uni_koeln.spinfo.strings.algo.suffixtrees.node.Node,
     *      java.lang.Long,
     *      de.uni_koeln.spinfo.strings.algo.suffixtrees.node.Node)
     */
    public void addChild(SimpleNode parent, Long x, SimpleNode middle) {
        // System.out.println("Add child " + middle.getId() + " to parent " +
        // parent.getId());

        SimpleNode old = parent.getChildrenMapping().put(x, middle);
        if (old != null) {
            // System.out.println("Old child: " + old.getId());
        }
    }

    public Map<Long, SimpleNode> getChildren(SimpleNode node) {
        return node.getChildrenMapping();
    }

    /*
     * (non-Javadoc)
     * 
     * @see de.uni_koeln.spinfo.strings.algo.suffixtrees.node.NodeAccessor#setId(de.uni_koeln.spinfo.strings.algo.suffixtrees.node.Node,
     *      int)
     */
    public void setId(SimpleNode node, int count) {
        node.setId(count);
    }

    public SimpleNode getRoot() {
        return root;
    }

}

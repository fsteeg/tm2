/** 
 Project Suffix Trees for Natural Language (STNL) (C) 2006 Fabian Steeg

 This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation; either version 2.1 of the License, or (at your option) any later version.

 This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details.

 You should have received a copy of the GNU Lesser General Public License along with this library; if not, write to the Free Software Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 */
package com.quui.tm2.agents.features.suffixtree.node;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Node Class for the suffix tree, implementing an Interface for {@link LCA}-queries
 * 
 * @author Francois Pepin (original version from BioJava API)
 * @author Fabian Steeg (fsteeg)
 * @author Stephan Schwiebert (sschwieb)
 */
public class SimpleNode implements Node {

    private int dfs = 0;

    private int suffixIndex;

    private List<SimpleNode> parent;

    private SimpleNode suffixLink;

    private int labelStart, labelEnd;

    private HashMap<Long, SimpleNode> children;

    private int[] additionalLabels;

    private long id;

    private int textNumber;

    public String toString() {

        String parentString = parent == null || parent.size() == 0 ? " none "
                : " " + parent.size();
        return "Node [" + id + " lStart " + labelStart + " lEnd " + labelEnd
                + " adlLab " + additionalLabels + " link: " + suffixLink
                + " parents: " + parentString + " sIndex " + suffixIndex + "]";
    }

    /**
     * Creates a root
     */
    public SimpleNode() {
        parent = null;
        suffixLink = null;
        labelStart = 0;
        labelEnd = 0;
        children = new HashMap<Long, SimpleNode>();
        additionalLabels = null;
        textNumber = 0;
        suffixIndex = 0;
    }

    /**
     * creates a leaf
     * 
     * @param parents
     *            the parent node
     * @param position
     *            the starting value of the suffix
     */
    public SimpleNode(List<SimpleNode> parents, int position, int textNumber,
            int suffixIndex) {
        this();
        this.parent = parents;
        labelStart = position;
        labelEnd = A_LEAF;
        children = null;
        this.textNumber = textNumber;
        this.suffixIndex = suffixIndex;
    }

    /**
     * creates an internal node
     * 
     * @param parents
     *            the parent of this node
     * @param labelStart
     *            the starting point of the path label
     * @param labelStop
     *            the ending point of the path label
     */
    public SimpleNode(List<SimpleNode> parents, int labelStart, int labelStop) {
        this();
        this.parent = parents;
        this.labelStart = labelStart;
        this.labelEnd = labelStop;
        this.textNumber = 0;
        this.suffixIndex = 0;
    }

    /**
     * @return Returns the children
     */
    // public HashMap<Long, SimpleNode> getChildren() {
    // return children;
    // }
    public List<Node> getChildren() {
        // TODO vorsicht das jetzt ein neues...
        return children != null ? new ArrayList<Node>(children
                .values()) : null;
    }

    public int getLabelStart() {
        return labelStart;
    }

    public int getLabelEnd() {
        return labelEnd;
    }

    public int[] getAdditionalLabels() {
        return additionalLabels;
    }

    public void setId(int id) {
        this.id = id;
    }

    public long getId() {
        return id;
    }

    public int getTextNumber() {
        return textNumber;
    }

    /**
     * Determine is this node is terminal (has no children).
     * <p>
     * Note that this only happens at the terminated node (if the sequences have
     * been terminated.
     * 
     * @return <code>true</code> if and only if this node has no children.
     */
    public boolean isTerminal() {
        return children == null;
    }

    /**
     * Determine if this node has a child corresponding to a given character
     * 
     * @param i
     *            the first <code>Long</code> of the edge coming down this
     *            node.
     * @return <code>true</code> if the node has a child going down from that
     *         Long, false otherwise
     */
    public boolean hasChild(Long x) {
        return getChild(x) != null;
    }

    /**
     * Gets the child of of a node that is coming down from that particular
     * node. It returns null if no child exists or if no child exists starting
     * on that particular character.
     * 
     * @param i
     *            the first <code>Long</code> of the edge coming down this
     *            node
     * @return the appropriate child <code>SuffixNode</code>, or null if it
     *         doesn't exists.
     */
    public Node getChild(Long x) {
        return (children == null) ? null : children.get(x);
    }

    /**
     * Returns the parent of this node, null if it's the root.
     * 
     * @return the parent of this node, null if it's the root.
     */
    public List<SimpleNode> getParents() {
        return parent;
    }

    /**
     * @see de.uni_koeln.spinfo.strings.algo.lca.TreeNode#numChildren()
     */
    public int numChildren() {
        return children == null ? 0 : children.keySet().size();
    }

    /**
     * @see de.uni_koeln.spinfo.strings.algo.lca.TreeNode#getChild(int)
     */
//    public TreeNode getChild(int num) {
//        Iterator iter = children.keySet().iterator();
//        int i = 0;
//        while (iter.hasNext()) {
//            TreeNode node = (TreeNode) children.get(iter.next());
//            if (i == num)
//                return node;
//            i++;
//        }
//        return null;
//    }

    /**
     * @see de.uni_koeln.spinfo.strings.algo.lca.TreeNode#acceptDFSNum(int)
     */
    public void acceptDFSNum(int x) {
        this.dfs = x;
        setId(dfs);

    }

    /**
     * @see de.uni_koeln.spinfo.strings.algo.lca.TreeNode#getDFSNum()
     */
    public int getDFSNum() {
        return this.dfs;
    }

    public int getDfs() {
        return dfs;
    }

    public int getSuffixIndex() {
        return suffixIndex;
    }

    public SimpleNode getSuffixLink() {
        return this.suffixLink;
    }

    public void setSuffixLink(SimpleNode to) {
        this.suffixLink = to;
    }

    public void setAdditionalLabels(int[] additionalLabels) {
        this.additionalLabels = additionalLabels;

    }

    public void setParent(List<SimpleNode> parents) {
        this.parent = parents;

    }

    public void setLabelEnd(int e) {
        this.labelEnd = e;
    }

    public void setLabelStart(int s) {
        this.labelStart = s;

    }

    public Map<Long, SimpleNode> getChildrenMapping() {

        return children;
    }

    public boolean isInternal() {
        return this.getParents() != null && this.getParents().size() > 0
                && this.children != null && this.children.values().size() > 0;
    }

}
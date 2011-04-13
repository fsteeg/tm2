/** 
 Project Suffix Trees for Natural Language (STNL) (C) 2006 Fabian Steeg

 This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation; either version 2.1 of the License, or (at your option) any later version.

 This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details.

 You should have received a copy of the GNU Lesser General Public License along with this library; if not, write to the Free Software Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 */
package com.quui.tm2.agents.features.suffixtree.node;

import java.util.HashMap;

/**
 * Abstract superclass for tree nodes
 * 
 * @author Francois Pepin (original version from BioJava API)
 * @author Fabian Steeg (fsteeg)
 */
public abstract class SuffixNode implements Node{

    protected SuffixNode parent;

    protected SuffixNode suffixLink;

    protected int labelStart, labelEnd;

    protected HashMap<Long, Node> children;

    protected int[] additionalLabels;

    private long id;

    protected int textNumber;

    /**
     * Determine is this node is terminal (has no children).
     * <p>
     * Note that this only happens at the terminated node (if the sequences have
     * been terminated.
     * 
     * @return <code>true</code> if and only if this node has no children.
     */

    abstract public boolean isTerminal();

    /**
     * Determine if this node has a child corresponding to a given character
     * 
     * @param i
     *            the first <code>Long</code> of the edge coming down
     *            this node.
     * @return <code>true</code> if the node has a child going down from that
     *         Long, false otherwise
     */
    public abstract boolean hasChild(Long i);

    /**
     * Gets the child of of a node that is coming down from that particular
     * node. It returns null if no child exists or if no child exists starting
     * on that particular character.
     * 
     * @param i
     *            the first <code>Long</code> of the edge coming down
     *            this node
     * @return the appropriate child <code>SuffixNode</code>, or null if it
     *         doesn't exists.
     */
    public abstract Node getChild(Long i);

    // abstract void addChild(SuffixTree tree, int i, SuffixNode n);

    /**
     * Returns the parent of this node, null if it's the root.
     * 
     * @return the parent of this node, null if it's the root.
     */
    public abstract SuffixNode getParent();

    /**
     * @return Returns the children
     */
    public HashMap<Long, Node> getChildrenMapping() {
        return children;
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

	public void setId(long id) {
		this.id = id;
	}
	
	public long getId() {
		return id;
	}


	public int getTextNumber() {
		return textNumber;
	}
	

}
package com.quui.tm2.agents.features.suffixtree.node;

import java.util.List;
import java.util.Map;

/**
 * 
 * @author sschwieb
 *
 */
public interface NodeAccessor<T extends Node> {

	public List<T> getParents(T node);

	public T getSuffixLink(T node);

	public boolean isTerminal(T node);

	public void setSuffixLink(T from, T to);

	public void setAdditionalLabels(T node, int[] additionalLabels);

	public void setParents(T child, List<T> parent);

	public void setLabelEnd(T leaf, int end);

	public T createInternalNode(List<T> parent, int suffixStart, int splittingPos);

	/**
	 * Adds Node child to the children of Node parent. It is referenced by Long ref.
	 * The modified parent is returned.
	 * @param parent
	 * @param ref
	 * @param child
	 * @return Modified parent
	 */
	public void addChild(T parent, Long ref, T child);

	public T createLeafNode(List<T> parent, int suffixStart, int number,
			int suffixIndex);

	/**
	 * 
	 * @param root
	 * @param count
	 * @deprecated setId can't be used in EJB context and should either be
	 * removed or renamed (and change something else, but not the id).
	 */
	public void setId(T root, int count);

	public T createRootNode();
	
	public T getRoot();

	public Map<Long, T> getChildren(T node);


}

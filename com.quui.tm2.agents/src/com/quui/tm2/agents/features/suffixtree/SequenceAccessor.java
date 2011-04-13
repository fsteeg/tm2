package com.quui.tm2.agents.features.suffixtree;

import java.util.List;

/**
 * 
 * @author sschwieb
 *
 */
public interface SequenceAccessor {

	/**
	 * Returns the size of the sequence.
	 * @return the size of the sequence.
	 */
	int size();

	/**
	 * All values are added to the sequence.
	 * @param seq A list of values to be added.
	 */
	void addAll(List<Long> seq);

	/**
	 * Returns the value at index from.
	 * <b>NOTE</b>: In DB-Implementation, this method might
	 * perform a query (slow!).
	 * @param from 
	 * @return
	 */
	Long get(int from);

	/**
	 * Returns all values between from and to.
	 * <b>NOTE</b>: In DB-Implementation, this method
	 * performs a query (slow!).
	 * @param from 
	 * @return
	 */
	List<Long> subList(int from, int to);

}



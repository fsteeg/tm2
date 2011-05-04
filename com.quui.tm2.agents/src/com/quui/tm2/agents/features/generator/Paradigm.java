package com.quui.tm2.agents.features.generator;

import java.util.Iterator;
import java.util.SortedSet;
import java.util.TreeSet;

/**
 * @author fsteeg
 *
 */
public class Paradigm implements Comparable<Paradigm> {
	// members need to be sorted for the compareTo-method to work as implemented
	SortedSet<String> members = null;
	float value = 0.0f;

	public SortedSet<String> getMembers() {
		return members;
	}

	public Paradigm() {
		this.members = new TreeSet<String>();
	}

	public int compareTo(Paradigm o) {
		Iterator<String> otherIterator = o.members.iterator();
		for (String m1 : members) {
			String m2 = null;
			if (otherIterator.hasNext())
				m2 = otherIterator.next();
			if (!m1.equals(m2) && m2 != null) {
				// compare the first differing member:
				return m1.compareTo(m2);
			}
		}
		// if no member was different, the paradigms are equal:
		return 0;
	}

	/**
	 * @param s
	 *            The string to add to this paradigm
	 */
	public void add(String s) {
		members.add(s);
	}
	
	@Override
	public String toString(){
		return this.value + " | " + this.members;
	}

}

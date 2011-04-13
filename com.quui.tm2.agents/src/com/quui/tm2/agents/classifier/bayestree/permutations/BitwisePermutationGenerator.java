package com.quui.tm2.agents.classifier.bayestree.permutations;

import java.math.BigInteger;
import java.util.HashSet;
import java.util.Set;


public class BitwisePermutationGenerator implements PermutationGenerator {
	private Set<String> set = new HashSet<String>();

	private boolean done = false;

	private int length;

	public Set<String> permutations(int length, int num) {
		this.length = length;
		int all = setAll(length);
		subsets(all, num);
		return set;
	}

	private int setAll(int i) {
		int a = (1 << i);
		return a | ~a;
	}

	void subsets(int s, int num) {
		for (int a = s;; a = (a - 1) & s) {
			collect(a, num);
			if (a == 0 || done)
				break;
		}
		System.out.println();
	}

	private void collect(int a, int num) {
		StringBuilder builder = new StringBuilder();
		for (int i = length; i >= 0; i--) {
			builder.append((test(a, i) ? "1" : "0"));
		}
		if (set.contains(builder.toString()))
			done = true;
		if (new BigInteger(builder.toString(), 2).bitCount() == num)
			set.add(builder.toString());

	}

	private boolean test(int a, int i) {
		return ((1 << i) & a) != 0;
	}
}

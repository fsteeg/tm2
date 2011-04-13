package com.quui.tm2.agents.classifier.bayestree.permutations;

import java.util.HashSet;
import java.util.Set;

public class RecursivePermutationGenerator implements PermutationGenerator {
	public Set<String> permutations(int length, int activations) {
		Set<String> lang = new HashSet<String>();
		addElements(length, 0, activations, new StringBuilder(), lang);
		return lang;
	}

	private void addElements(double length, int counter, int max,
			StringBuilder builder, Set<String> words) {
		if (builder.length() == length) {
			if (builder.toString().contains("1"))
				words.add(builder.toString());
		} else {
			if (counter == max) {
				StringBuilder b = new StringBuilder(builder);
				for (int j = b.toString().length(); j <= length - 1; j++) {
					b.append("0");
				}
				if (b.toString().contains("1"))
					words.add(b.toString());
			} else {
				addElements(length, counter + 1, max,
						new StringBuilder(builder).append("1"), words);
				addElements(length, counter, max, new StringBuilder(builder)
						.append("0"), words);
			}
		}
	}
}

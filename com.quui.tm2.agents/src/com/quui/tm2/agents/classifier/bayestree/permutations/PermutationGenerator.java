package com.quui.tm2.agents.classifier.bayestree.permutations;

import java.util.Set;

public interface PermutationGenerator {
	public Set<String> permutations(int length, int activations);
}

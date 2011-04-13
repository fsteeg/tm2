package com.quui.tm2.agents.classifier;

import static org.junit.Assert.assertTrue;

import java.util.Set;

import org.junit.Test;

import com.quui.tm2.agents.classifier.bayestree.permutations.BigBitwisePermutationGenerator;
import com.quui.tm2.agents.classifier.bayestree.permutations.BitwisePermutationGenerator;
import com.quui.tm2.agents.classifier.bayestree.permutations.RecursivePermutationGenerator;

public class PermutationGeneratorTest {
	@Test
	public void permutations() {
		Set<String> intPermutations = intPermutations();
		System.out.println();
		Set<String> bigPermutations = bigPermutations();
		System.out.println();
		Set<String> recPermutations = recPermutations();
//		assertTrue(intPermutations.equals(recPermutations));
		assertTrue(recPermutations.equals(bigPermutations));
	}

	public Set<String> intPermutations() {
		BitwisePermutationGenerator gen = new BitwisePermutationGenerator();
		Set<String> permutations = gen.permutations(5, 4);
		for (String string : permutations) {
			System.out.println(string);
		}
		return permutations;
	}

	public Set<String> bigPermutations() {
		BigBitwisePermutationGenerator gen = new BigBitwisePermutationGenerator();
		Set<String> permutations = gen.permutations(5, 4);
		for (String string : permutations) {
			System.out.println(string);
		}
		return permutations;
	}

	public Set<String> recPermutations() {
		RecursivePermutationGenerator gen = new RecursivePermutationGenerator();
		Set<String> permutations = gen.permutations(5, 4);
		for (String string : permutations) {
			System.out.println(string);
		}
		return permutations;
	}
}

package com.quui.tm2.agents.classifier.bayestree.permutations;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * Computation of permutations of strings with 0 to n activated (1) and the rest
 * unactivated (0) positions of a specified length. Computation is low-level
 * optimized by usings binary representations of integers representing sets. The
 * implementation is based on the BigInteger class to allow arbitrary set sizes
 * (opposed to 32-bit limit for ints and 64-bit limit for longs) and high-level
 * coding style (e.g. by using the testBit method instead of direct bit shifting
 * operators).
 * 
 * @author fsteeg
 * 
 */
public class BigBitwisePermutationGenerator implements PermutationGenerator {
	private Set<String> set = new HashSet<String>();

	private int length;

	public Set<String> permutations(int length, int num) {
		this.length = length;
		/** We create a binary number of the required length: */
		BigInteger all = setAll(length);
		/** We compute all subsets with a maximum number of activations: */
		subsets(all, num);
		return set;
	}

	private BigInteger setAll(int length) {
		char[] a = new char[length];
		Arrays.fill(a, '1');
		return new BigInteger("1" + new String(a), 2);
	}

	void subsets(BigInteger all, int num) {
		/**
		 * For a set with N entries, the binary representation of every N-bit
		 * value corresponds to a subset, so while we are greater than 0 we
		 * always substract 1 and use the resulting value as the binary
		 * representation of a subset:
		 */
		for (BigInteger a = all; !a.equals(BigInteger.ZERO); a = (a
				.subtract(BigInteger.ONE))) {
			/**
			 * If the permutation has the correct number of activations, add it
			 * to the results:
			 */
			collect(a, num);
		}
	}

	private void collect(BigInteger a, int num) {
		/**
		 * We create a string representation, filled up with 0s to the desired
		 * length:
		 */
		StringBuilder builder = new StringBuilder();
		for (int i = length; i >= 1; i--) {
			builder.append(a.testBit(i) ? "1" : "0");
		}
		/** And store it in the result list */
		/** If the permutation has the required number of activated positions: */
		BigInteger b = new BigInteger(builder.toString(), 2);
		if (b.bitCount() <= num && b.bitCount() > 0) {
			set.add(builder.toString());
		}
	}
}

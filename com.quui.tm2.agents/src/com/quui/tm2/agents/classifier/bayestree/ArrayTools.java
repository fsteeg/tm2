package com.quui.tm2.agents.classifier.bayestree;

import java.math.BigInteger;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Arrays;
import java.util.List;

/**
 * Helper class with static methods for array and matrix operations
 * 
 * @author fsteeg
 * 
 */
public class ArrayTools {

	/**
	 * @param vectors
	 *            The vectors to multiply
	 * @return Returns a vector of the size of each vector in vectors containing
	 *         the products of the corresponding elements, or, if one of them is
	 *         zero, the other element
	 */
	static Float[] product(List<Float[]> vectors) {
		Float[] result = new Float[vectors.get(0).length];
		for (int i = 0; i < result.length; i++) {
			Float prod = 0.0f;
			for (int j = 0; j < vectors.size(); j++) {
				Float current = vectors.get(j)[i];
				prod = prod == 0.0f ? current : current == 0.0f ? prod : prod
						* current;
			}
			result[i] = prod;
		}
		result = normalizeForSum(result);
		return result;
	}

	/**
	 * @param m
	 *            The matrix to normalize
	 * @return Returns a douoble[][] of the same dmension containing the
	 *         normalized values (division of each element by the row sum)
	 */
	static Float[][] normalizeForSum(Float[][] m) {
		Float[][] res = new Float[m.length][m[0].length];
		for (int i = 0; i < m.length; i++) {
			Float[] bigIntegers = m[i];
			Float[] row = normalizeForSum(bigIntegers);
			res[i] = row;
		}
		return res;

	}

	static Float[][] normalizeForSum(int[][] m) {
		Float[][] res = new Float[m.length][m[0].length];
		for (int i = 0; i < m.length; i++) {
			int[] ints = m[i];
			Float[] row = normalizeForSum(ints);
			res[i] = row;
		}
		return res;

	}

	public static Float[] normalizeForSum(Float[] bigIntegers) {
		Float sum = 0f;
		Float[] row = new Float[bigIntegers.length];
		for (int j = 0; j < bigIntegers.length; j++) {
			sum += bigIntegers[j].floatValue();
		}
		for (int j = 0; j < bigIntegers.length; j++) {
			if (sum == 0)
				row[j] = 0.0f;
			else {
				row[j] = bigIntegers[j].floatValue() / sum;
			}
		}
		return row;
	}

	public static Float[] normalizeForSum(int[] bigIntegers) {
		Float sum = 0f;
		Float[] row = new Float[bigIntegers.length];
		for (int j = 0; j < bigIntegers.length; j++) {
			sum += bigIntegers[j];
		}
		for (int j = 0; j < bigIntegers.length; j++) {
			if (sum == 0)
				row[j] = 0.0f;
			else {
				row[j] = (float) bigIntegers[j] / sum;
			}
		}
		return row;
	}

	/**
	 * @param m1
	 *            Matrix 1
	 * @param m2
	 *            Matrix 2
	 * @return A matrix of the same dimension as m1 and m2 containing their
	 *         products
	 */
	static int[][] multiply(int[][] m1, int[][] m2) {
		int[][] res = new int[m1.length][m1[0].length];
		for (int i = 0; i < m1.length; i++) {
			for (int j = 0; j < m1[i].length; j++) {
				res[i][j] = m1[i][j] * m2[i][j];
			}
		}
		return res;
	}

	/**
	 * @param m
	 *            The matrix
	 * @return Returns an array containing the max values for each column in m
	 */
	static Float[] maxForCols(Float[][] m) {
		Float[] r = new Float[m[0].length];
		for (int i = 0; i < m[0].length; i++) {
			Float max = Float.MIN_VALUE;
			for (int j = 0; j < m.length; j++) {
				if (m[j][i] > max) {
					max = m[j][i];
				}
			}
			r[i] = max;
		}
		return r;
	}

	/**
	 * @param m
	 *            The matrix
	 * @return Returns an array containing the max values for each row in m
	 */
	static Float[] maxForRows(Float[][] m) {
		Float[] max = new Float[m.length];
		for (int i = 0; i < max.length; i++) {
			max[i] = maximum(m[i]);
		}
		return max;
	}

	/**
	 * @param a
	 *            The array
	 * @return Returns the sum of all the values in the array
	 */
	double sum(double[] a) {
		double sum = 0.0;
		for (double d : a) {
			sum += d;
		}
		return sum;
	}

	/**
	 * @param a
	 *            The array
	 * @return Returns the max value in a
	 */
	static Float maximum(Float[] a) {
		Float max = Float.MIN_VALUE;
		for (Float d : a) {
			if (d > max)
				max = d;
		}
		return max;
	}

	/**
	 * @param a
	 *            The array
	 * @param m
	 *            The matrix
	 * @return Retruns a matrix of the same dimension as m containing modified
	 *         values from m: each element in each row is multiplied with the
	 *         corresponding element in a
	 */
	static Float[][] multiplyRows(Float[] a, Float[][] m) {
		if (a.length != m.length)
			throw new IllegalStateException(
					"Different array and matrix sizes: " + a.length + ", "
							+ m.length);
		Float[][] c = new Float[a.length][m[0].length];
		try {
			for (int i = 0; i < c.length; i++) {
				for (int j = 0; j < c[i].length; j++) {
					c[i][j] = a[i] * m[i][j];
				}
			}
		} catch (ArrayIndexOutOfBoundsException e) {
			e.printStackTrace();
		}
		c = normalizeForSum(c);
		return c;
	}

	static Float[][] multiplyColums(Float[] a, Float[][] m) {
		if (a.length != m[0].length)
			throw new IllegalStateException(
					"Different array and matrix sizes: " + a.length + ", "
							+ m[0].length);
		Float[][] c = new Float[m.length][m[0].length];
		for (int i = 0; i < a.length; i++) {
			for (int j = 0; j < c.length; j++) {
				c[j][i] = a[i] * m[j][i];
			}
		}
		
		return c;
	}

	/**
	 * @param m
	 *            The matrix to fill with v
	 * @param v
	 *            The value to fill m with
	 */
	static void fill(Number[][] m, Number v) {
		for (Number[] ns : m) {
			Arrays.fill(ns, v);
		}
	}

	static void fill(int[][] m, int v) {
		for (int[] ns : m) {
			Arrays.fill(ns, v);
		}
	}

	static void fill(float[][] m, float v) {
		for (float[] ns : m) {
			Arrays.fill(ns, v);
		}
	}

	/**
	 * @param s
	 *            The size
	 * @return Returns a array filled with values 1 of size s
	 */
	static Float[] initialPi(int s) {
		Float[] piVector = new Float[s];
		Arrays.fill(piVector, 1.0f);
		return piVector;
	}

	/**
	 * @param in
	 *            The double to classify
	 * @param node
	 *            The node to create the input vector for
	 * @return An array, currently with only one element activated: the one
	 *         corresponding to in
	 */
	static Float[] initialLambda(int size, int index) {
		// currently, only one element is observed to classify...
		Float[] lambdaVector = new Float[size];
		Arrays.fill(lambdaVector, 0.004f);
		lambdaVector[index] = 1.0f;
		lambdaVector = normalizeForSum(lambdaVector);
		return lambdaVector;
	}

	static String format(boolean[] activation2) {
		StringBuilder buf = new StringBuilder();
		for (boolean b : activation2) {
			buf.append(b ? "1" : "0");
		}
		return buf.toString();
	}

	public static String numberFormat(Number t, int digits) {
		DecimalFormat instance = (DecimalFormat) NumberFormat.getInstance();
		instance.setMinimumFractionDigits(digits);
		instance.setMaximumFractionDigits(digits);
		String format = instance.format(t);
		return format;
	}

	public static void printArray(Double[] vector) {
		for (double d : vector) {
			System.out.print(numberFormat(d, 6) + " ");
		}
	}

	public static void printArray(int[] vector) {
		for (int d : vector) {
			System.out.print(d + " ");
		}
	}

	public static void printArray(BigInteger[] vector) {
		for (BigInteger d : vector) {
			System.out.print(d + " ");
		}
	}

	public static String format(Float[] features2, int digits) {
		String s = "";
		for (Float d : features2) {
			s += numberFormat(d, digits) + " ";
		}
		return s.trim();
	}

	public static String format(float[] features2, int digits) {
		String s = "";
		for (float d : features2) {
			s += numberFormat(d, digits) + " ";
		}
		return s.trim();
	}

	public static String format(Double[] features2, int digits) {
		String s = "";
		for (double d : features2) {
			s += numberFormat(d, digits) + " ";
		}
		return s.trim();
	}
}

package com.quui.tm2.agents.features.generator;

import java.io.FileNotFoundException;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.Vector;

/**
 * Simple feature computation for a token in its context, using a bag-of-word
 * approach (see e.g. Agirre-and-Stevenson2006:227) or optional the word length.
 * Results in a numerical representation of a learning sample, which acts as an
 * input for a machine learning algorithm.
 * 
 * @author fsteeg
 * 
 */
public class FeatureGenerator {
	// int contextSize;

	List<String> text;

	private float[] features;

	private Map<String, Integer> words;

	private int max;

	/**
	 * @param vocabulary
	 * @param text
	 *            The text to create feature vectors for
	 * @param contextSize
	 *            The size of the context to use in each direction, resulting in
	 *            a contextSize*2-dimensional feature representation
	 * @throws FileNotFoundException
	 */

	boolean nGram = false;

	boolean paradigms = false;

	private int context;

	private int nGramNum = -1;

	private ParadigmsWalker paradigmsWalker;

    private String configString;
    
    @Override
    public String toString() {
        return String.format("%s(@%s@)", getClass().getSimpleName(), configString);
    }

	public FeatureGenerator(List<String> vocabulary, String features,
			int context) {
	    configString = features;
		Collections.sort(vocabulary);
		this.context = context;
		// List<String> arrayList = new ArrayList<String>(vocabulary);
		this.words = new HashMap<String, Integer>();
		nGram = features.contains("gram");// equals(Keys.FEATURE_TRIGRAM.key);
		paradigms = features.equals("paradigms");
		if (features.equals("combo")) {
			paradigms = true;
			// TODO implement combo with feature-specification in properties
			// file like this: word,3-gram,paradigms
			// nGramNum = 3;
		} else if (nGram) {
			nGramNum = Integer.parseInt(features.split("-")[0]);
			// see below
		}
		if (paradigms) {
			StringBuilder buf = new StringBuilder();
			for (String string : vocabulary) {
				buf.append(string + " ");
			}
			paradigmsWalker = new ParadigmsWalker(clean(buf.toString()));
			assignValues(paradigmsWalker.paradigms);
			// SortedSet<Paradigm> pardigmsInText = p.pardigmsInText;
			// for (Paradigm paradigm : pardigmsInText) {
			// System.out.println(paradigm.members + "\n\t(" + paradigm.value
			// + ")");
			// }

		}
		if (features.equals("word") || features.equals("length")) { // word
			// based
			// features
			int i = 0;
//			System.out.println();
			for (String s : vocabulary) {
				boolean length = features.equals("length");
				int x = (length ? s.length() : i);
				// System.out.println("ADDING WORD: " + s);
				if (!words.keySet().contains(s)) {
					words.put(s, x);
					max = (length ? Math.max(max, x) : i);
					i++;
				}
			}
		} else if (!paradigms && !nGram) {
			throw new IllegalArgumentException("Invalid features: " + features);
		}
		List<String> l = new Vector<String>(words.keySet());
		Collections.sort(l);
//		for (String k : l) {
//			System.out.println(k + ", " + (words.get(k)/(double)max));
//		}
	}

	private void assignValues(SortedSet<Paradigm> sortedSet) {
		int c = 1;
		// Collections.sort(sortedSet);
		int max = sortedSet.size();
		for (Paradigm paradigm : sortedSet) {
			paradigm.value = 1.0f / max * c;
			c++;
		}
	}

	/**
	 * @param wordPos
	 *            The position of the target word in the text
	 * @param context
	 * @param text2
	 * @return Returns a feature vector for the word in its context, size is
	 *         contextSize*2, non-present context is represented by -1, present
	 *         context by (normalized) values between 0 and 1.
	 */
	public float[] getFeatures(int wordPos, List<String> text, int context) {
		this.text = text;
		int contextSize = context / 2;
		this.features = new float[contextSize * 2];
		Arrays.fill(features, 0);

		String end = "";
		String start = "";
		int max = Integer.MIN_VALUE;
		if (nGram) {

			for (int i = 0; i < nGramNum; i++) {
				end += "z";
				start += "a";
			}
			max = end.hashCode() - start.hashCode();
		}
		int count = 1;
		// fill before target word
		for (int i = contextSize - 1; i >= 0 && (wordPos - count) >= 0
				&& count <= contextSize; i--, count++) {
			int j = wordPos - count;
			checkBounds(j, text);
			String string = text.get(j);

			if (features.equals("combo")) {
				features[i] = wordFeature(string);
				i++;
				count++;
				features[i] = paradigmFeature(string);

			} else {
				features[i] = nGram ? nGramFeature(string, max, start)
						: paradigms ? paradigmFeature(string)
								: wordFeature(string);
			}
		}

		count = 1;
		// fill after target word
		for (int i = contextSize; i < features.length
				&& (wordPos + count) < text.size() && count <= contextSize; i++, count++) {
			int k = wordPos + count;
			checkBounds(k, text);
			String string = text.get(k);
			if (features.equals("combo")) {
				features[i] = wordFeature(string);
				i++;
				count++;
				features[i] = paradigmFeature(string);
			} else {
				features[i] = nGram ? nGramFeature(string, max, start)
						: paradigms ? paradigmFeature(string)
								: wordFeature(string);
			}
		}

		// }
		// else {
		//
		// int count = 1;
		// // fill before target word
		// for (int i = contextSize - 1; i >= 0 && (wordPos - count) >= 0
		// && count <= contextSize; i--, count++) {
		// features[i] = wordFeature(wordPos - count, text);
		// }
		// count = 1;
		// // fill after target word
		// for (int i = contextSize; i < features.length
		// && (wordPos + count) < text.size() && count <= contextSize; i++,
		// count++) {
		// features[i] = wordFeature(wordPos + count, text);
		// }
		// }
		return features;
	}

	private void checkBounds(int j, List<String> text) {
		if (j < 0 || j >= text.size())
			throw new IllegalStateException("Wrong index " + j + " for " + text);
	}

	public float getFeature(String w) {

		float feature = 0;

		String end = "";
		String start = "";
		int max = Integer.MIN_VALUE;
		if (nGram) {

			for (int i = 0; i < nGramNum; i++) {
				end += "z";
				start += "a";
			}
			max = end.hashCode() - start.hashCode();
		}

		feature = nGram ? nGramFeature(w, max, start)
				: paradigms ? paradigmFeature(w) : wordFeature(w);

		// }
		// else {
		//
		// int count = 1;
		// // fill before target word
		// for (int i = contextSize - 1; i >= 0 && (wordPos - count) >= 0
		// && count <= contextSize; i--, count++) {
		// features[i] = wordFeature(wordPos - count, text);
		// }
		// count = 1;
		// // fill after target word
		// for (int i = contextSize; i < features.length
		// && (wordPos + count) < text.size() && count <= contextSize; i++,
		// count++) {
		// features[i] = wordFeature(wordPos + count, text);
		// }
		// }
		return feature;
	}

	private float paradigmFeature(String w) {
		List<Paradigm> hits = retrieveHits(w);
		if (hits == null) {
			return 0f;
			// throw new IllegalStateException("No Paradigm found for: " +
			// text2.get(i));
		}
		float sum = 0.0f;
		for (Paradigm paradigm : hits) {
			sum += paradigm.value;
		}
		float d = sum / hits.size();
		// System.err.println("Para feature using: " + d);
		return d;
	}

	private List<Paradigm> retrieveHits(String w) {
		List<Paradigm> l = new Vector<Paradigm>();
		for (Paradigm p : paradigmsWalker.paradigms) {
			if (p.members.contains(w)) {
				l.add(p);
			}
		}
		return l.size() > 0 ? l : null;
	}

	public float wordFeature(String string) {

		if (!words.containsKey(string)) {
			throw new IllegalStateException(
					"Word not found in the word-feature mapping: " + string);
		}
		Integer integer = words.get(string);
		float d = (float) integer / max;
		return d;
	}

	private float nGramFeature(String w, int max, String start) {
		String word = clean(w);
		String ngram = word.substring(0, Math.min(nGramNum, word.length()));
		if (ngram.length() == 0) {
			throw new IllegalStateException("Zero-length word!");
		}
		if (ngram.length() != nGramNum) {
			while (ngram.length() < nGramNum) {
				ngram = ngram + ngram;
			}
			// }
			// for (int i = 0; i < nGramNum; i++) {
			// ngram = ngram + ngram;
			// }
			ngram = ngram.substring(0, nGramNum);
		}
		// System.err.println("n-gram:" + ngram);
		Integer integer = code(ngram);// ngram.hashCode();
		return (float) (integer - code(start)/* .hashCode() */) / max;
	}

	// based on the algorithm of java's string hashcode method in java 5
	private int code(String ngram) {
		int n = 0;
		char val[] = ngram.toCharArray();
		for (int i = 0; i < val.length; i++) {
			n = 31 * n + val[i];
		}
		return n;
	}

	private String clean(String substring) {
		return substring.toLowerCase().replaceAll("ö", "oe").replaceAll("ä",
				"ae").replaceAll("ü", "ue").replaceAll("ß", "ss");
	}

}

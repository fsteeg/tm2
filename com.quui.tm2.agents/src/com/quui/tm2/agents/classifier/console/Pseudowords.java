package com.quui.tm2.agents.classifier.console;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Pseudowords {

	public static String split = "[^a-zA-Z0-9:öäüßÖÄÜ-]";

	private String text;

	private int context;

	public Pseudowords(String text, int context, boolean filter,
			String stopwordsFile, String stopwordsEncoding) {
		this.text = text.replaceAll(":", "").replaceAll("-", "");
		this.context = context/2;
	}

	public String tag(String[] strings) {
		String resu = text;
		int i = 0;
		for (String s1 : strings) {
			String rep = "";
			// create the pseudoword
			for (String s2 : strings) {
				rep += s2;
			}
			resu = resu.replaceAll("[^a-zA-ZöäüÖÄÜß:]" + s1 + "[^a-zA-ZöäüÖÄÜß]", " " + rep
					+ ":" + "S" + i + " ");
			i++;
		}
		// TODO cleanup
		List<String> words = Arrays.asList(resu.split(split));
		
		if(ClassifierPreferences.getInstance().filter){
			words = StopwordsFilter.getInstance().filter(words);
		}
		
		words = shrink(words);
		StringBuilder buf = new StringBuilder();
		for (String string : words) {
			buf.append(string).append(" ");
		}
		this.text = buf.toString();
		return this.text;
	}

	public List<String> shrink(List<String> vocabulary) {
		List<String> shrinked = new ArrayList<String>();
		List<String> shrinked2 = new ArrayList<String>();
		for (String string : vocabulary) {
			if (string.length() > 0)
				shrinked2.add(string);
		}
		int i = 0;
		for (String string : shrinked2) {
			if (string.contains(":")) {
				List<String> context2 = context(shrinked2, i);
				shrinked.addAll(context2);
			}
			i++;
		}
		return shrinked;
	}

	public List<String> context(List<String> vocabulary, int pos) {
		List<String> shrinked = new ArrayList<String>();

		int count = 1;
		// fill before target word
		for (int i = context - 1; i >= 0 && (pos - count) >= 0
				&& count <= context; i--, count++) {
			shrinked.add(word(vocabulary, pos - count));
		}
		shrinked.add(vocabulary.toArray(new String[] {})[pos]);
		count = 1;
		// fill after target word
		for (int i = context; i < pos + context
				&& (pos + count) < vocabulary.size() && count <= context; i++, count++) {
			shrinked.add(word(vocabulary, pos + count));
		}
		return shrinked;
	}

	private String word(List<String> vocabulary, int pos) {
		String string = vocabulary.toArray(new String[] {})[pos];
		String string2 = string.contains(":") ? string.split("-")[0] : string;
		return string2;
	}

	
}

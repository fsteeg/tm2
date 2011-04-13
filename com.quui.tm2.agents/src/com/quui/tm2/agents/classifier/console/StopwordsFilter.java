package com.quui.tm2.agents.classifier.console;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Scanner;
import java.util.Set;

public class StopwordsFilter {
	private static StopwordsFilter theInstance;

	private String encoding;

	private String location;

	private Set<String> stopwords;

	private StopwordsFilter(String location, String encoding) {
		this.location = location;
		this.encoding = encoding;
	}

	public static StopwordsFilter getInstance() {
		if (theInstance == null) {
			theInstance = new StopwordsFilter(
					ClassifierPreferences.getInstance().stopwordsLocation, ClassifierPreferences
							.getInstance().encoding);
//			System.out.println("Init with: " + theInstance.encoding + ", " + theInstance.location);
			theInstance.init();
		}
		return theInstance;
	}

	public List<String> filter(List<String> text2) {
		assert stopwords != null;
		List<String> filtered = new ArrayList<String>();
		for (String s : text2) {
			if (!stopwords.contains(s)) {
				filtered.add(s);
			}
		}
		return filtered;
	}

	private void init() {
		this.stopwords = new HashSet<String>();
		Scanner s;
		try {
			s = new Scanner(new FileInputStream(location), encoding);
			while (s.hasNextLine()) {
				String word = s.nextLine().trim();
				this.stopwords.add(word);
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
}

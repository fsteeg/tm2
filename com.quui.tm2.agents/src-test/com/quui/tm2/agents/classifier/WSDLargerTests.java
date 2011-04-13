package com.quui.tm2.agents.classifier;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import org.junit.Test;

import com.quui.tm2.agents.classifier.console.ClassifierPreferences;
import com.quui.tm2.agents.classifier.console.Pseudowords;
import com.quui.tm2.agents.classifier.console.WSD;

public class WSDLargerTests {

	int context = ClassifierPreferences.getInstance().context;

	private String enc = ClassifierPreferences.getInstance().encoding;

	private String loc = ClassifierPreferences.getInstance().stopwordsLocation;

	private boolean filter = ClassifierPreferences.getInstance().filter;

	@Test
	public void testSpiegel() throws FileNotFoundException {
		String text = load("files/corpus-1");
		Pseudowords pseudo = new Pseudowords(text, context, filter, loc, enc);
		String[] words = { "bush", "merkel" };
		String training = pseudo.tag(words);
		System.out.println("Training text: " + training);
		text = load("files/corpus-2");
		pseudo = new Pseudowords(text, context, filter, loc, enc);
		String testing = pseudo.tag(words);
		System.out.println("Disambiguation text: " + testing);
		test(training, testing, new String[] { "S0", "S1" }, "bushmerkel");
	}
	
	@Test
	public void testSpiegel4er() throws FileNotFoundException {
		String text = load("files/corpus-1");
		Pseudowords pseudo = new Pseudowords(text, context, filter, loc, enc);
		String[] words = { "bush", "merkel", "mann", "frau" };
		String training = pseudo.tag(words);
		System.out.println("Training text: " + training);
		text = load("files/corpus-2");
		pseudo = new Pseudowords(text, context, filter, loc, enc);
		String testing = pseudo.tag(words);
		System.out.println("Disambiguation text: " + testing);
		test(training, testing, new String[] { "S0", "S1", "S2", "S3" }, "bushmerkelmannfrau");
	}
	
	@Test
	public void testSpiegelSelf() throws FileNotFoundException {
		String text = load("files/corpus-1");
		Pseudowords pseudo = new Pseudowords(text, context, filter, loc, enc);
		String[] words = { "bush", "merkel" };
		String training = pseudo.tag(words);
		System.out.println("Training text: " + training);
		text = load("files/corpus-1");
		pseudo = new Pseudowords(text, context, filter, loc, enc);
		String testing = pseudo.tag(words);
		System.out.println("Disambiguation text: " + testing);
		test(training, testing, new String[] { "S0", "S1" }, "bushmerkel");
	}
	
	@Test
	public void testSpiegel1() throws FileNotFoundException {
		String text = load("files/corpus-1");
		Pseudowords pseudo = new Pseudowords(text, context, filter, loc, enc);
		String[] words = { "mann", "frau" };
		String training = pseudo.tag(words);
		System.out.println("Training text: " + training);
		text = load("files/corpus-2");
		pseudo = new Pseudowords(text, context, filter, loc, enc);
		String testing = pseudo.tag(words);
		System.out.println("Disambiguation text: " + testing);
		test(training, testing, new String[] { "S0", "S1" }, "mannfrau");
	}
	
	@Test
	public void testSpiegel1Self() throws FileNotFoundException {
		String text = load("files/corpus-1");
		Pseudowords pseudo = new Pseudowords(text, context, filter, loc, enc);
		String[] words = { "mann", "frau" };
		String training = pseudo.tag(words);
		System.out.println("Training text: " + training);
		text = load("files/corpus-1");
		pseudo = new Pseudowords(text, context, filter, loc, enc);
		String testing = pseudo.tag(words);
		System.out.println("Disambiguation text: " + testing);
		test(training, testing, new String[] { "S0", "S1" }, "mannfrau");
	}
	
	@Test
	public void testFaust1() throws FileNotFoundException {
		String text = load("files/faust-1");
		Pseudowords pseudo = new Pseudowords(text, context, filter, loc, enc);
		String[] strings = new String[] { "kopf", "glas" };
		String training = pseudo.tag(strings);
		System.out.println("Training text: " + training);
		text = load("files/faust-2");
		pseudo = new Pseudowords(text, context, filter, loc, enc);
		String testing = pseudo.tag(strings);
		System.out.println("Disambiguation text: " + testing);
		test(training, testing, new String[] { "S0", "S1" }, "kopfglas");
	}
	@Test
	public void testFaust4er() throws FileNotFoundException {
		String text = load("files/faust-1");
		Pseudowords pseudo = new Pseudowords(text, context, filter, loc, enc);
		String[] strings = new String[] { "kopf", "glas", "geist", "mann" };
		String training = pseudo.tag(strings);
		System.out.println("Training text: " + training);
		text = load("files/faust-2");
		pseudo = new Pseudowords(text, context, filter, loc, enc);
		String testing = pseudo.tag(strings);
		System.out.println("Disambiguation text: " + testing);
		test(training, testing, new String[] { "S0", "S1", "S2", "S3" }, "kopfglasgeistmann");
	}
	@Test
	public void testSp4er() throws FileNotFoundException {
		String text = load("files/corpus-1");
		Pseudowords pseudo = new Pseudowords(text, context, filter, loc, enc);
		String[] strings = new String[] { "kopf", "glas", "geist", "mann" };
		String training = pseudo.tag(strings);
		System.out.println("Training text: " + training);
		text = load("files/corpus-2");
		pseudo = new Pseudowords(text, context, filter, loc, enc);
		String testing = pseudo.tag(strings);
		System.out.println("Disambiguation text: " + testing);
		test(training, testing, new String[] { "S0", "S1", "S2", "S3" }, "kopfglasgeistmann");
	}
	
	@Test
	public void testFaust1Self() throws FileNotFoundException {
		String text = load("files/faust-1");
		Pseudowords pseudo = new Pseudowords(text, context, filter, loc, enc);
		String[] strings = new String[] { "kopf", "glas" };
		String training = pseudo.tag(strings);
		System.out.println("Training text: " + training);
		text = load("files/faust-1");
		pseudo = new Pseudowords(text, context, filter, loc, enc);
		String testing = pseudo.tag(strings);
		System.out.println("Disambiguation text: " + training);
		test(training, testing, new String[] { "S0", "S1" }, "kopfglas");

	}

	@Test
	public void testFaust3() throws FileNotFoundException {
		String text = load("files/faust-1");
		Pseudowords pseudo = new Pseudowords(text, context, filter, loc, enc);
		String[] strings = new String[] { "geist", "mann" };
		String training = pseudo.tag(strings);
		System.out.println("Training text: " + training);
		text = load("files/faust-2");
		pseudo = new Pseudowords(text, context, filter, loc, enc);
		String testing = pseudo.tag(strings);
		System.out.println("Disambiguation text: " + testing);
		test(training, testing, new String[] { "S0", "S1" }, "geistmann");
	}


	@Test
	public void testFaust3Self() throws FileNotFoundException {
		String text = load("files/faust-1");
		Pseudowords pseudo = new Pseudowords(text, context, filter, loc, enc);
		String[] strings = new String[] { "geist", "mann" };
		String training = pseudo.tag(strings);
		System.out.println("Training text: " + training);
		text = load("files/faust-1");
		pseudo = new Pseudowords(text, context, filter, loc, enc);
		String testing = pseudo.tag(strings);
		System.out.println("Disambiguation text: " + testing);
		test(training, testing, new String[] { "S0", "S1" }, "geistmann");
	}

//	 @Test
//	 public void testTolstoi() throws FileNotFoundException {
//	 String text = load("files/wrnpc11");
//	 Pseudowords pseudo = new Pseudowords(text, context, filter, loc, enc);
//	 String[] strings = new String[]{"head", "hair"};
//	 String training = pseudo.tag(strings);
//	 System.out.println("Training text: " + training);
//	 text = load("files/wrnpc11");
//	 pseudo = new Pseudowords(text, context, filter, loc, enc);
//	 String testing = pseudo.tag(strings);
//	 System.out.println("Disambiguation text: " + testing);
//	 test(training, testing);
//	
//	 }

	private void test(String training, String testing, String[] senses,
			String lemma) {
		List<String> vocabulary = new ArrayList<String>(Arrays.asList((training
				+ " " + testing).split(Pseudowords.split)));
		List<String> s = Arrays.asList(senses);
		Map<String, List<String>> sensesMap = new HashMap<String, List<String>>();
		sensesMap.put(lemma, s);
		WSD wsd = new WSD(vocabulary, sensesMap);
		wsd.trainText(Arrays.asList(training.split(Pseudowords.split)));
		wsd.disambiguateText(Arrays.asList(testing.split(Pseudowords.split)));
//		GraphvizExport.export(wsd.treeForLemma(lemma).getRoot(), "output/htm.dot", Preferences
//				.getInstance().digits);
	}

	private String load(String string) throws FileNotFoundException {
		Scanner s = new Scanner(new FileInputStream(string), enc);
		StringBuilder buf = new StringBuilder();
		while (s.hasNextLine()) {
			buf.append(s.nextLine()).append(" ");
		}
		return buf.toString().trim().toLowerCase();
	}
}

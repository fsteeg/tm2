package com.quui.tm2.agents.classifier;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;

import com.quui.tm2.agents.classifier.console.WSD;

public class WSDMiniTests {
	@Test
	public void testWSDMiniHash() {
		System.out.println("AAA".hashCode());
		System.out.println("zzz".hashCode());
		System.out.println("zzz".hashCode() - "AAA".hashCode());
		System.out.println("ded".hashCode());
	}

	@Test
	public void testWSDMini0() {
		String split = "[^a-zA-Z0-9:;]";
		String trainingCorpus = "Ich hole Geld bei der Bank:S0 ab das tu ich gern."
				+ "Leider war die eine Bank:S0 schon zu gewesen gestern."
				+ "Sie sitzen auf einer Bank:S1 im Garten am Mittelmeer."
				+ "Es ist eine gruene Bank:S1 aus Spanien im Mittelmeer.";

		String classificationCorpus = "Ich hole Geld bei der Bank:S0 ab das tu ich gern."
				+ "Leider war die eine Bank:S0 schon zu gewesen gestern."
				+ "Sie sitzen auf einer Bank:S1 im Garten am Mittelmeer."
				+ "Es ist eine gruene Bank:S1 aus Spanien im Mittelmeer.";
		List<String> vocabulary = new ArrayList<String>(Arrays
				.asList((trainingCorpus + " " + classificationCorpus)
						.split(split)));
		List<String> s = Arrays.asList(new String[] { "S0", "S1" });
		Map<String, List<String>> senses = new HashMap<String, List<String>>();
		senses.put("Bank", s);
		WSD wsd = new WSD(vocabulary, new int[] { 8,4 }, 0.25f, senses, "word", 4);
		wsd.trainText(Arrays.asList(trainingCorpus.split(split)));
		wsd.disambiguateText(Arrays.asList(classificationCorpus.split(split)));
	}

	@Test
	public void testWSDMini1() {
		String split = "[^a-zA-Z0-9:;]";
		String trainingCorpus = "Der nagelneue rot gestreifte Apache:S0 flog ueber Kabul hinueber."
				+ "Bereits der zehnte der Apache:S0 wurde am Montag in Kabul abgeschossen."
				+ "Auf der anderen Seite ist es aber auch so, dass auch der Webserver der Apache:S1 Software Foundation abstuerzen kann."
				+ "Zudem gibt es von der Apache:S1 Software Foundation auch andere Programme."
				+ "In anderem Zusammenhang ist ein Apache:S2 ein Mitglied eines nordamerikanisches Indianervolkes."
				+ "Dieser bekannte und beruehmte Apache:S2 ist ein gefuerchteter Krieger. ";

		String classificationCorpus = "Es sass eimal ein Apache:S2 im Gras am Meer."
				+ "Der neue schnelle rote teure Apache:S0 flog an uns vorbei.";

		List<String> vocabulary = new ArrayList<String>(Arrays
				.asList((trainingCorpus + " " + classificationCorpus)
						.split(split)));
		List<String> s = Arrays.asList(new String[] { "S0", "S1", "S2" });
		Map<String, List<String>> senses = new HashMap<String, List<String>>();
		senses.put("Apache", s);
		WSD wsd = new WSD(vocabulary, new int[] { 4, 2 }, 1, senses, "word", 4);
		wsd.trainText(Arrays.asList(trainingCorpus.split(split)));
		wsd.disambiguateText(Arrays.asList(classificationCorpus.split(split)));
	}

	@Test
	public void testWSDMini3() {
		String split = "[^a-zA-Z0-9:;]";
		String trainingCorpus = "Ich hole Geld bei der Bank:S0 ab das tu ich gern."
				+ "Leider war die eine Bank:S0 schon zu gewesen gestern."
				+ "Sie sitzen auf einer Bank:S1 im Garten am Mittelmeer."
				+ "Es ist eine gruene Bank:S1 aus Spanien im Mittelmeer.";

		String classificationCorpus = "Leider war die andere Bank:S0 schon geschlossen gewesen heute."
				+ "Ich war bei tolle Bank:S0 ab zu geschlossen gewesen."
				+ "Es war die gruene Bank:S1 aus Spanien im Mittelmeer."
				+ "Hier war eine eine Bank:S1 aus Holland am rumstehen.";

		List<String> vocabulary = new ArrayList<String>(Arrays
				.asList((trainingCorpus + " " + classificationCorpus)
						.split(split)));
		List<String> s = Arrays.asList(new String[] { "S0", "S1" });
		Map<String, List<String>> senses = new HashMap<String, List<String>>();
		senses.put("Bank", s);
		WSD wsd = new WSD(vocabulary, new int[] { 2, 4 }, 1, senses, "word", 4);
		wsd.trainText(Arrays.asList(trainingCorpus.split(split)));
		wsd.disambiguateText(Arrays.asList(classificationCorpus.split(split)));
	}

	@Test
	public void testWSDMini4() {
		String split = "[^a-zA-Z0-9:;]";
		String trainingCorpus = "Meet me at the bank:S0 of the beautiful river. "
				+ "That night by the river bank:S0 we saw the stranger. "
				+ "He had always dreamed of working in the bank:S1 where his father had worked his entire life. "
				+ "He was saving in a piggy bank:S1 for that red bicycle.";

		String classificationCorpus = "We played some volleyball on the bank:S0 of the river that day. " +
				"The water had flooded the bank:S0 completely on the whole western part of the area. " +
				"In all the area, every bank:S1 was closed at four already. " +
				"When he entered the bank:S1 he immedeately saw her standing across the hall.";

		List<String> vocabulary = new ArrayList<String>(Arrays
				.asList((trainingCorpus + " " + classificationCorpus)
						.split(split)));
		List<String> s = Arrays.asList(new String[] { "S0", "S1" });
		Map<String, List<String>> senses = new HashMap<String, List<String>>();
		senses.put("bank", s);
		WSD wsd = new WSD(vocabulary, new int[] { 4,2 }, 1, senses, "3-gram", 4);
		wsd.trainText(Arrays.asList(trainingCorpus.split(split)));
		wsd.disambiguateText(Arrays.asList(classificationCorpus.split(split)));
	}

}

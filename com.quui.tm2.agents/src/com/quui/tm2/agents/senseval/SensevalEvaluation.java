package com.quui.tm2.agents.senseval;
//package de.uni_koeln.spinfo.tesla.component.wsd.senseval;
//
//import java.io.IOException;
//import java.util.ArrayList;
//import java.util.List;
//import java.util.Map;
//
//import de.uni_koeln.spinfo.wsd.Preferences;
//import de.uni_koeln.spinfo.wsd.hca.binary.GraphvizExport;
//import de.uni_koeln.spinfo.wsd.hca.binary.WSD;
//
//public class SensevalEvaluation {
//
//	private String train;
//
//	private String test;
//
//	public SensevalEvaluation(String train, String test) {
//		this.train = train;
//		this.test = test;
//	}
//
//	public void evaluate() {
//		SensevalDataReader trainDataReader = new SensevalDataReader(train);
//		trainDataReader.load();
//		List<Ambiguity> samples = trainDataReader.getAmbiguities();
//		List<String> train = trainDataReader.getWords();
//
//		// TODO hm die brauch ich doch nicht... korrekt?
//		Map<String, String> stems = trainDataReader.getStems();
//
//		Map<String, List<String>> senses = trainDataReader.getSenses();
//		System.out.println("Training text loaded");
//		SensevalDataReader testDataReader = new SensevalDataReader(test);
//		testDataReader.load();
//
//		List<String> test = testDataReader.getWords();
//
//		ArrayList<String> arrayList = new ArrayList<String>();
//		arrayList.addAll(train);
//		arrayList.addAll(test);
//		System.out.println("Testing text loaded");
//		WSD wsd = new WSD(arrayList, senses);
//		wsd.train(samples);
//
//		GraphvizExport.export(wsd.lexicon.values().iterator().next().getRoot(),
//				"output/htm.dot", Preferences.getInstance().digits);
//
//		try {
//			wsd.disambiguate(testDataReader.getAmbiguities());
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
//	}
//}

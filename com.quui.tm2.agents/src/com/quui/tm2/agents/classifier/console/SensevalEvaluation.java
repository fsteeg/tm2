package com.quui.tm2.agents.classifier.console;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.quui.tm2.agents.senseval.Ambiguity;
import com.quui.tm2.agents.senseval.STAXSensevalDataReader;

public class SensevalEvaluation {

	private String train;

	private String test;

	public SensevalEvaluation(String train, String test) {
		this.train = train;
		this.test = test;
	}

	public void evaluate() {
		System.out
				.println("_____________________________________________________________________\n"
						+ "Running evaluation on the SENSEVAL-3 ENGLISH LEXICAL SAMPLE TASK data"
						+ "\n(see http://www.cse.unt.edu/~rada/senseval/senseval3 for details)\n"
						+ "_____________________________________________________________________");
		System.out.println("Configuration: (specified in "
				+ ClassifierPreferences.getInstance().propertiesFileLocation + ") ");
		ClassifierPreferences.getInstance().properties.list(System.out);
		STAXSensevalDataReader trainDataReader = new STAXSensevalDataReader(
				new File(train));
		trainDataReader.load();
		List<Ambiguity> samples = trainDataReader.getAmbiguities();
		List<String> train = trainDataReader.getWords();

		// TODO hm die brauch ich doch nicht... korrekt?
		Map<String, String> stems = trainDataReader.getStems();

		Map<String, List<String>> senses = trainDataReader.getSenses();
		System.out.println("Training text loaded, loading testing text...");
		STAXSensevalDataReader testDataReader = new STAXSensevalDataReader(
				new File(test));
		testDataReader.load();

		List<String> test = testDataReader.getWords();

		ArrayList<String> arrayList = new ArrayList<String>();
		arrayList.addAll(train);
		arrayList.addAll(test);
		System.out.println("Testing text loaded, training networks...");
		WSD wsd = new WSD(arrayList, senses);
		wsd.train(samples);
		System.out.println("Training done, disambiguating testing samples...");
//		GraphvizExport.export(wsd.lexicon.values().iterator().next().getRoot(),
//				"files/htm.dot", Preferences.getInstance().digits, false);

		try {
			wsd.disambiguate(testDataReader.getAmbiguities());
		} catch (IOException e) {
			e.printStackTrace();
		}
		// System.out.println("Disambiguation done.");
	}
}

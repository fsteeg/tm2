package com.quui.tm2.agents.features;


import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import com.quui.tm2.agents.features.generator.FeatureGenerator;

public class FeaturesTest {

	private FeatureGenerator features;

	static List<String> text = Arrays.asList(new String[] { "Ich", "sitze",
			"auf", "der", "Bank:Moebel", "und", "schaue", "mich", "um", "Nach",
			"einer", "Weile", "gehe", "ich", "weiter", "Ich", "betrete",
			"eine", "Bank:Gebaeude", "um", "etwas", "Geld", "abzuheben", "Ich",
			"wollte", "schon", "laenger", "zu", "einer", "anderen",
			"Bank:Institution", "wechseln", "war", "jedoch", "bisher", "zu",
			"bequem" });

	@Before
	public void setUp() throws Exception {
		features = new FeatureGenerator(new ArrayList<String>(text), "paradigms", 2);
	}

	@Test
	public void testGetFeatures() {
		for (int i = 0; i < text.size(); i++) {
			if (text.get(i).contains(":")) {
				float[] f = features.getFeatures(i, text, 2);
				System.out.print("Features '" + text.get(i) + "': \t");
				for (double j : f) {
					if (j >= 0 && j < 10)
						System.out.print('0');
					System.out.print(numberFormat(j, 2) + " ");
				}
				System.out.print("\n");
			}
		}
	}
	
	public static String numberFormat(Number t, int digits) {
		DecimalFormat instance = (DecimalFormat) NumberFormat.getInstance();
		instance.setMinimumFractionDigits(digits);
		instance.setMaximumFractionDigits(digits);
		String format = instance.format(t);
		return format;
	}
}

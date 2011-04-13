package com.quui.tm2.agents.classifier;

import org.junit.Test;

import com.quui.tm2.agents.classifier.console.SensevalEvaluation;

public class SensevalTest {

	public static void main(String[] args) {
		new SensevalEvaluation("files/EnglishLS.train.xml",
				"files/EnglishLS.test.xml").evaluate();
	}

	@Test
	public void evaluateMini() {
		new SensevalEvaluation("files/EnglishLS.train-mini.xml",
				"files/EnglishLS.test-small.xml").evaluate();
	}

	@Test
	public void evaluateSmall() {
		new SensevalEvaluation("files/EnglishLS.train-small.xml",
				"files/EnglishLS.test-small.xml").evaluate();
	}

	@Test
	public void evaluateFull() {
		new SensevalEvaluation("files/EnglishLS.train.xml",
				"files/EnglishLS.test.xml").evaluate();
	}

}

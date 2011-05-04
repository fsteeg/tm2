package com.quui.tm2.agents.senseval;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

import org.junit.Before;
import org.junit.Test;

public class SensevalTest {

	private STAXSensevalDataReader reader;

	@Before
	public void setUp() throws Exception {

	}

	@Test
	public void readDataFromFile() {
		reader = new STAXSensevalDataReader(new File(
				"senseval/EnglishLS.train-mini.xml"));
		reader.load();
		for (Ambiguity sample : reader.getAmbiguities()) {
			System.out.println(sample);
		}
	}

	@Test
	public void readDataFromString() {
		String s = read(new File("senseval/EnglishLS.train-mini.xml"));
		reader = new STAXSensevalDataReader(s);
		reader.load();
		for (Ambiguity sample : reader.getAmbiguities()) {
			try {
				System.out.println(sample
						+ ", "
						+ s.substring(sample.context.targetStart,
								sample.context.targetEnd));
			} catch (StringIndexOutOfBoundsException e) {
				e.printStackTrace();
			}
		}
	}

	@Test
	public void readLexiconFromString() {
		String s = read(new File("senseval/EnglishLS.dictionary.xml"));
		STAXSensevalLexiconReader lexReader = new STAXSensevalLexiconReader(s);
		lexReader.load();
		for (String lemma : lexReader.getLexicon().keySet()) {
			System.out.println("Current lemma: " + lemma);
			for (LexicalEntry entry : lexReader.getLexicon().get(lemma)) {
				System.out.println(entry);
			}
		}

	}

	private String read(File file) {
		Scanner s;
		try {
			s = new Scanner(file);
			StringBuilder builder = new StringBuilder();
			while (s.hasNextLine()) {
				builder.append(s.nextLine() + "\n ");
			}
			return builder.toString();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		return null;
	}

}

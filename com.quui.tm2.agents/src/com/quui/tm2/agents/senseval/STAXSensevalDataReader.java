package com.quui.tm2.agents.senseval;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.stream.Location;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

/**
 * A reader for the XML used for the lexical sample tasks in Senseval-3
 * (http://www.cse.unt.edu/~rada/senseval/senseval3/), using StAX
 * (http://jcp.org/en/jsr/detail?id=173). As Bea's reference3 implementation of
 * StAX seems to to have a bug in the cursor into the current position in the
 * XML (reader.getLocation().getCharacterOffset()), this should be used with a
 * different implementation, e.g. Woodstox (http://woodstox.codehaus.org/).
 * 
 * @author fsteeg
 * 
 */
public class STAXSensevalDataReader {

	/**
	 * XML element names used in the XML to read
	 * 
	 */
	enum Elements {
		LEMMA("lexelt"), INSTANCE("instance"), CORRECT("answer"), CONTEXT(
				"context"), TARGET("head");
		public String key;

		Elements(String key) {
			this.key = key;
		}
	}

	private List<Ambiguity> ambiguities;

	private List<String> words;

	private Map<String, String> stems;

	private Map<String, List<String>> senses;

	private Reader bufferedReader;

	/**
	 * The location of the lexical sample file
	 * 
	 * @param location
	 */
	public STAXSensevalDataReader(File file) {
		this();
		try {
			bufferedReader = new BufferedReader(new FileReader(file));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

	public STAXSensevalDataReader() {
		this.words = new ArrayList<String>();
		this.stems = new HashMap<String, String>();
		this.senses = new HashMap<String, List<String>>();
	}

	public STAXSensevalDataReader(String signalData) {
		this();
		bufferedReader = new StringReader(signalData);
	}

	/**
	 * @return A list of samples from the file
	 */
	public void load() {
		List<Ambiguity> result = new ArrayList<Ambiguity>();
		XMLInputFactory factory = XMLInputFactory.newInstance();
		XMLStreamReader reader;
		System.setProperty("javax.xml.stream.isCoalescing", "true");
		try {
			reader = factory.createXMLStreamReader(bufferedReader);
			String currentLemma = null;
			String id = null;
			List<String> correct = new ArrayList<String>();
			String context = null;
			List<String> all = new ArrayList<String>();
			int target = -1;
			int contextStart = -1;
			int contextEnd = -1;
			int targetStart = -1;
			int targetEnd = -1;
			while (reader.hasNext()) {
				Location location = reader.getLocation();
				int characterOffset = location.getCharacterOffset();
				if (reader.isStartElement()) {
					if (reader.getLocalName().equals(Elements.LEMMA.key)) {
						currentLemma = reader.getAttributeValue(0);
					} else if (reader.getLocalName().equals(
							Elements.INSTANCE.key)) {
						id = reader.getAttributeValue(0);
					} else if (reader.getLocalName().equals(
							Elements.CORRECT.key)) {
						String c = reader.getAttributeValue(1);
						correct.add(c);
					} else if (reader.getLocalName().equals(
							Elements.CONTEXT.key)) {
						contextStart = characterOffset
								+ (Elements.CONTEXT.key.length() + 2);
						all = new ArrayList<String>();
					} else if (reader.getLocalName()
							.equals(Elements.TARGET.key)) {
						target = all.size();
						targetStart = characterOffset
								+ (Elements.TARGET.key.length() + 2);
					}
				} else if (reader.isCharacters() && !reader.isWhiteSpace()) {
					context = reader.getText();
					List<String> asList = Arrays.asList(context.replaceAll(
							"[^a-zA-Z0-9 ]", "").replaceAll("[ ]+", " ")
							.toLowerCase().split(" "));
					for (String string : asList) {
						if (!string.trim().equals("")) {
							all.add(string);
						}
					}
				} else if (reader.isEndElement()) {
					if (reader.getLocalName().equals(Elements.TARGET.key)) {
						targetEnd = characterOffset;
					} else if (reader.getLocalName().equals(
							Elements.CONTEXT.key)) {
						contextEnd = characterOffset;
					} else if (reader.getLocalName().equals(
							Elements.INSTANCE.key)) {
						if (context != null) {
							// the null correct value mark an instance which
							// should be disambiguated, so we add it here
							if (correct.size() == 0) {
								correct.add(null);
							}
							for (String c : correct) {
								Ambiguity sample = new Ambiguity(id,
										currentLemma, c, new Context(all,
												target, contextStart,
												contextEnd, targetStart,
												targetEnd, id, currentLemma));

								stems.put(sample.context.getTarget(),
										currentLemma);
								result.add(sample);
								words.addAll(sample.context.all);
								List<String> s = senses.get(sample.getLemma());
								if (s == null) {
									s = new ArrayList<String>();
								}
								if (!s.contains(sample.getCorrect()))
									s.add(sample.getCorrect());
								senses.put(sample.getLemma(), s);
							}

						}
						/*
						 * we set this to allow training and testing data in one
						 * file: after all training samples are done, we read
						 * the testing data. These don't have
						 * correct-annotation, thus the correct-Attribute would
						 * never be changed from the value of the last training
						 * sample.
						 */
						correct = new ArrayList<String>();
					}
				}
				reader.next();
			}
			reader.close();
		} catch (XMLStreamException e) {
			e.printStackTrace();
		}
		ambiguities = result;
		for (Ambiguity ambiguity : ambiguities) {
			ambiguity.getContext().senses = senses.get(ambiguity.lemma);
			 // TODO add senses to context
		}
	}

	public List<Ambiguity> getAmbiguities() {
		if (ambiguities == null) {
			throw new IllegalStateException(
					"No data present! (Have you leaded?)");
		}
		return ambiguities;
	}

	public List<String> getWords() {
		return words;
	}

	public Map<String, String> getStems() {
		return stems;
	}

	List<String> context(List<String> vocabulary, int pos, int context) {
		List<String> shrinked = new ArrayList<String>();

		int count = 1;
		// fill before target word
		for (int i = pos - count; i >= 0 && count <= context / 2; i--, count++) {
			shrinked.add(vocabulary.get(i));
		}
		shrinked.add(vocabulary.toArray(new String[] {})[pos]);
		count = 1;
		// fill after target word
		for (int i = pos + count; i < vocabulary.size() && count <= context / 2; i++, count++) {
			shrinked.add(vocabulary.get(i));
		}
		return shrinked;
	}

	public Map<String, List<String>> getSenses() {
		return senses;
	}
}

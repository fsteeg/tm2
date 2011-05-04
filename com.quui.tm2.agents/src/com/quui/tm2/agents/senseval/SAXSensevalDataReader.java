package com.quui.tm2.agents.senseval;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;

/**
 * A reader for the XML used for the lexical sample tasks in Senseval-3
 * (http://www.cse.unt.edu/~rada/senseval/senseval3/), using StAX
 * (http://jcp.org/en/jsr/detail?id=173)
 * 
 * @author fsteeg
 * 
 */
public class SAXSensevalDataReader {

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
	public SAXSensevalDataReader(File file) {
		this();
		try {
			bufferedReader = new BufferedReader(new FileReader(file));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

	public SAXSensevalDataReader() {
		this.words = new ArrayList<String>();
		this.stems = new HashMap<String, String>();
		this.senses = new HashMap<String, List<String>>();
	}

	public SAXSensevalDataReader(String signalData) {
		this();
		bufferedReader = new StringReader(signalData);
	}

	/**
	 * @return A list of samples from the file
	 */
	public void load() {
		List<Ambiguity> result = new ArrayList<Ambiguity>();
		
		SAXBuilder saxB = new SAXBuilder();
		Document xmlText;
		try {
			xmlText = saxB.build(bufferedReader);
			String currentLemma = null;
			String id = null;
			String correct = null;
			String context = null;
			List<String> all = new ArrayList<String>();
			int target = -1;
			int contextStart = -1;
			int contextEnd = -1;
			
			Element root = xmlText.getRootElement();
			List<Element> lemmata = root.getChildren(Elements.LEMMA.key);
			for (Element element : lemmata) {
				currentLemma = element.getAttributeValue("item");
				List<Element> instances = element.getChildren(Elements.INSTANCE.key);
				for (Element instance : instances) {
					id = instance.getAttributeValue("id");
					Element answer = instance.getChild(Elements.CORRECT.key);
					correct = answer.getAttributeValue("senseid");
					Element c = instance.getChild(Elements.CONTEXT.key);
					Element t = c.getChild(Elements.TARGET.key);
					System.err.println(c.getText());
					System.err.println("T: " + t.getText());
					System.out.println();
					
					
				}
			}
			
			
			
		} catch (JDOMException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		
		
//		XMLInputFactory factory = XMLInputFactory.newInstance();
//		XMLStreamReader reader;
//		
//		try {
//			reader = factory.createXMLStreamReader(bufferedReader);
//			String currentLemma = null;
//			String id = null;
//			String correct = null;
//			String context = null;
//			List<String> all = new ArrayList<String>();
//			int target = -1;
//			int contextStart = -1;
//			int contextEnd = -1;
//			while (reader.hasNext()) {
//				if (reader.isStartElement()) {
//					if (reader.getLocalName().equals(Elements.LEMMA.key)) {
//						currentLemma = reader.getAttributeValue(0);
//						// System.out.println("Lemma: " + currentLemma);
//					} else if (reader.getLocalName().equals(
//							Elements.INSTANCE.key)) {
//						id = reader.getAttributeValue(0);
//						// System.out.println("Instance: " + id);
//					} else if (reader.getLocalName().equals(
//							Elements.CORRECT.key)) {
//						correct = reader.getAttributeValue(1);
//						// if(correct.equals("U")){
//						// System.out.println(correct);
//						// }
//						// System.out.println("Correct: " + correct);
//					} else if (reader.getLocalName().equals(
//							Elements.CONTEXT.key)) {
//						contextStart = reader.getLocation()
//								.getCharacterOffset();
//						// if (Preferences.getInstance().debug) {
//						// System.out.println("Context: " + all);
//						// }
//						all = new ArrayList<String>();
//					} else if (reader.getLocalName()
//							.equals(Elements.TARGET.key)) {
//						target = all.size();
//						// System.err.println("Target: " + target);
//					}
//				} else if (reader.isCharacters() && !reader.isWhiteSpace()) {
//					context = reader.getText();
//					List<String> asList = Arrays.asList(context.replaceAll(
//							"[^a-zA-Z0-9 ]", "").replaceAll("[ ]+", " ")
//							.toLowerCase().split(" "));
//					// if (Preferences.getInstance().filter) {
//					// asList = StopwordsFilter.getInstance().filter(asList);
//					// }
//					for (String string : asList) {
//						if (!string.trim().equals("")) {
//							all.add(string);
//						}
//					}
//					// for (String string : asList) {
//					//						
//					// if (!words.contains(string))
//					// words.add(string);
//					// }
//					// System.out.println("Text: " + context);
//					// all.addAll(asList);
//				} else if (reader.isEndElement()) {
//					if (reader.getLocalName().equals(Elements.CONTEXT.key)) {
//						contextEnd = reader.getLocation().getCharacterOffset();
//					} else if (reader.getLocalName().equals(
//							Elements.INSTANCE.key)) {
//						if (context != null) {
//							Ambiguity sample = new Ambiguity(id, currentLemma,
//									correct, new Context(all, target,
//											contextStart, contextEnd));
//
//							stems.put(sample.context.getTarget(), currentLemma);
//							result.add(sample);
//							// List<String> context2 =
//							// context(sample.context.all,
//							// sample.context.target, Preferences
//							// .getInstance().context);
//							// if (context2.size() !=
//							// Preferences.getInstance().context) {
//							// throw new IllegalStateException(
//							// "Created context of wrong size: "
//							// + context2.size() + ", should be: "
//							// + Preferences.getInstance().context);
//							// }
//							words.addAll(sample.context.all);
//
//							List<String> s = senses.get(sample.getLemma());
//							if (s == null) {
//								s = new ArrayList<String>();
//							}
//							if (!s.contains(sample.getCorrect()))
//								s.add(sample.getCorrect());
//							senses.put(sample.getLemma(), s);
//						}
//					}
//				}
//				reader.next();
//			}
//			reader.close();
//		} catch (XMLStreamException e) {
//			e.printStackTrace();
//		}
//		ambiguities = result;
		// return result;
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

	// private String word(List<String> vocabulary, int pos) {
	// String string = vocabulary.toArray(new String[] {})[pos];
	// String string2 = string.contains(":") ? string.split("-")[0] : string;
	// return string2;
	// }

	public Map<String, List<String>> getSenses() {
		return senses;
	}
}

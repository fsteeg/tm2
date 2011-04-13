package com.quui.tm2.agents.senseval;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.StringReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

public class STAXSensevalLexiconReader {

	Map<String, List<LexicalEntry>> lexicon;

	/**
	 * XML element names used in the XML to read
	 * 
	 */
	enum Elements {
		LEMMA("lexelt"), INSTANCE("instance"), CORRECT("answer"), CONTEXT(
				"context"), TARGET("head"), SENSE("sense");
		public String key;

		Elements(String key) {
			this.key = key;
		}
	}

	private StringReader bufferedReader;

	public STAXSensevalLexiconReader(String signalData) {
		String regex = "gloss=\"(.+?)\"([^/].+?)\"([^/]*?\"/)";
		String replace = "gloss=\"$1'$2'$3";
		signalData = signalData.replaceAll(regex, replace).replaceAll(regex,
				replace).replaceAll(regex, replace).replaceAll(regex, replace)
				.replaceAll(regex, replace).replaceAll(regex, replace);
		bufferedReader = new StringReader(signalData);
		lexicon = new HashMap<String, List<LexicalEntry>>();
	}

//	public STAXSensevalLexiconReader(URL resource) {
//		
//		this(read(resource));
//	}

//	private static String read(URL resource) {
////		File f = new File(resource.getPath().substring(
////				resource.getPath().indexOf(':') + 1));
//		File f = new File("jar:"+resource.getPath());
//		try {
////			File f = new File(resource.toURI());
//			StringBuilder builder = new StringBuilder();
//			Scanner s = new Scanner(f);
//			while (s.hasNextLine()) {
//				builder.append(s.nextLine()).append("\n");
//			}
//			System.out.println("USING LEXICON: " + builder.toString());
//			return builder.toString();
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		return null;
//	}

	public void load() {
		XMLInputFactory factory = XMLInputFactory.newInstance();
		XMLStreamReader reader;
		try {
			reader = factory.createXMLStreamReader(bufferedReader);
			String lemma = null;
			List<LexicalEntry> list = new ArrayList<LexicalEntry>();
			while (reader.hasNext()) {

				if (reader.isStartElement()) {
					if (reader.getLocalName().equals(Elements.LEMMA.key)) {
						lemma = reader.getAttributeValue(0);
						List<LexicalEntry> l = new ArrayList<LexicalEntry>();
						lexicon.put(lemma, l);
						list = l;
					} else if (reader.getLocalName().equals(Elements.SENSE.key)) {
						LexicalEntry entry = new LexicalEntry(lemma, reader
								.getAttributeValue(0), reader
								.getAttributeValue(1), reader
								.getAttributeValue(2), reader
								.getAttributeValue(3));
						list.add(entry);
					}
				}
				if (reader.isEndElement()) {
					lexicon.put(lemma, list);
				}
				reader.next();
			}
			reader.close();

		} catch (XMLStreamException x) {
			x.printStackTrace();
		}
	}

	public Map<String, List<LexicalEntry>> getLexicon() {
		return lexicon;
	}
}

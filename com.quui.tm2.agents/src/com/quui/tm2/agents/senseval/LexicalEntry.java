package com.quui.tm2.agents.senseval;

/**
 * @author fsteeg
 *
 */
public class LexicalEntry {

	public String gloss;

	public String synset;

	public String source;

	public String id;

	public String lemma;

	public LexicalEntry(String lemma, String id, String source, String synset,
			String gloss) {
		this.lemma = lemma;
		this.id = id;
		this.source = source;
		this.synset = synset;
		this.gloss = gloss;
	}

	public String toString() {
		return "[" + lemma + " | " + id + " | " + source + " | " + synset + " | "
				+ gloss + "]";
	}
}

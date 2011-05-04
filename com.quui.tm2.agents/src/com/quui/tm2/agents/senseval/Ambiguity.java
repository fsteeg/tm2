package com.quui.tm2.agents.senseval;

import java.io.Serializable;

/**
 * @author fsteeg
 *
 */
public class Ambiguity implements Comparable<Ambiguity>, Serializable{
	/**
   * 
   */
  private static final long serialVersionUID = -3197794219019833712L;

  String lemma;

	String correct;

	Context context;

	String id;

	public Ambiguity(String id, String lemma, String correct,
			Context context) {
		this.id = id;
		this.lemma = lemma;
		this.context = context;
		this.correct = correct;
	}

	@Override
	public String toString() {
		return new StringBuilder().append("[").append(id).append(" ").append(
				lemma).append(" ").append(correct).append(" ").append(
				context.getTarget()).append(", ").append(context.all.size())
				.append(" w. in c.]").toString();
	}

	public Context getContext() {
		return context;
	}

	public String getCorrect() {
		return correct;
	}

	public String getLemma() {
		return lemma;
	}

	public String getID() {
		return id;
	}

	@Override
	public int compareTo(Ambiguity that) {
		return this.lemma.compareTo(that.lemma);
	}

}
package com.quui.tm2.agents.senseval;

import java.io.Serializable;

public class Ambiguity implements Comparable<Ambiguity>, Serializable{
	String lemma;

	String correct;

	Context context;

//	int target;

	String id;

	public Ambiguity(String id, String lemma, String correct,
			Context context) {
		this.id = id;
		this.lemma = lemma;
		this.context = context;
		this.correct = correct;
//		this.target = target;
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

//	public String getContext() {
//		return context.getTarget();
//	}

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
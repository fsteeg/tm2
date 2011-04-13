package com.quui.tm2.types;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Sense implements Comparable<Sense>, Serializable {

    public String lemma;
    public String id;
    public String correct;

	public Sense(String id, String lemma, String correct) {
    	this.lemma = lemma;
    	this.id = id;
        this.correct = correct;
    }

	@Override
	public int compareTo(Sense that) {
		return this.id.compareTo(that.id);
	}

}

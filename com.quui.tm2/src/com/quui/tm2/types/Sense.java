package com.quui.tm2.types;

import java.io.Serializable;

public class Sense implements Comparable<Sense>, Serializable {

  private static final long serialVersionUID = -1948524686753677141L;
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

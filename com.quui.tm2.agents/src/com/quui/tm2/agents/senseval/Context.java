package com.quui.tm2.agents.senseval;

import java.io.Serializable;
import java.util.List;

/**
 * @author fsteeg
 *
 */
public class Context implements Comparable<Context>, Serializable {

	/**
   * 
   */
  private static final long serialVersionUID = -2753606175812943929L;

  public int contextEnd;

	public int contextStart;

	public int target;

	public List<String> all;

	public int targetEnd;

	public int targetStart;

	public List<String> senses;

	public String id;

	public String lemma;

	public Context(List<String> all, int target, int contextStart,
			int contextEnd, int targetStart, int targetEnd, String id, String lemma) {
		if (contextStart > contextEnd) {
			String string = "Context start is larger than context end!";
			throw new IllegalStateException(string);
		}
		if (targetStart > targetEnd) {
			String string = "Target start is larger than target end!";
			throw new IllegalStateException(string);
		}
		this.all = all;
		this.target = target;
		this.contextStart = contextStart;
		this.contextEnd = contextEnd;
		this.targetStart = targetStart;
		this.targetEnd = targetEnd;
		this.id=id;
		this.lemma=lemma;
	}

	public String getTarget() {
		return all.get(target);
	}

	@Override
	public int compareTo(Context o) {
		return String.format("%s%s%s%s%s%s", contextStart, contextEnd, target,
				all, targetStart, targetEnd).compareTo(
				String.format("%s%s%s%s%s%s", o.contextStart, o.contextEnd,
						o.target, o.all, o.targetStart, o.targetEnd));
	}

}

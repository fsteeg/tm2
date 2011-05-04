package com.quui.tm2.agents;

import java.io.File;
import java.io.Serializable;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import com.quui.tm2.Agent;
import com.quui.tm2.Annotation;
import com.quui.tm2.AnnotationReader;
import com.quui.tm2.Evaluation;
import com.quui.tm2.ImmutableAnnotation;
import com.quui.tm2.Model;
import com.quui.tm2.util.Preferences;
import com.quui.tm2.util.Preferences.Default;
import com.quui.tm2.util.TM2Logger;

/**
 * @author Fabian Steeg (fsteeg)
 */
public class SimpleEvaluation<E extends Comparable<E> & Serializable> implements Agent<E, String>, Model<E, E>, Evaluation {

  /***/
  static class Result {
    /***/
    private float f;
    /***/
    private float r;
    /***/
    private float p;

    /**
     * @param p Precision
     * @param r Recall
     * @param f F-Measure
     */
    Result(final float p, final float r, final float f) {
      this.p = p;
      this.r = r;
      this.f = f;
    }

    /**
     * @return Returns a human-readable representation of the evaluation result, containing
     *         precision, recall and f-measure. The exact format is subject to change. If you need
     *         particular values, use the accessors.
     * @see java.lang.Object#toString()
     */
    public String toString() {
      return String.format("f: %.2f (p: %.2f, r: %.2f)", f, p, r);
    }
  }

  /** The result of this evaluation. */
  private Result result = null;
  /***/
  private String goldLocation;

  /***/
  public String resultFile;
  String agent;

  /**
   * Sets the GOLD file.
   * @param gold The gold annotations file to evaluate against
   */
  public SimpleEvaluation(final String gold) {
    this.goldLocation = gold;
  }

  /**
   * Uses default GOLD file.
   */
  public SimpleEvaluation() {
    this.goldLocation = Preferences.get(Default.GOLD);
  }

  public final <T extends Comparable<T> & Serializable> void evaluateWithDefaultGold(
      final Class<? extends Agent<?, T>> agentClass) {
    evaluate(Preferences.get(Preferences.Default.GOLD), agentClass);
  }

  /**
   * Evaluates the results of the experiment associated with this runner against the given gold
   * standard, using the given agent and the given annotation type.
   * @param <T> The annotation value type
   * @param goldFileLocation The location of the gold standard annotations
   * @param agentClass The class for the agent to use
   */
  public final <T extends Comparable<T> & Serializable> void evaluate(
      final String goldFileLocation, final Class<? extends Agent<?, T>> agentClass) {
    try {
      if (!new File(new URI(resultFile)).exists()) {
        throw new IllegalStateException("Result file not found: " + resultFile);
      }
    } catch (URISyntaxException e) {
      e.printStackTrace();
    }
    this.agent = agentClass.getSimpleName();
    List<Annotation<T>> gold = new AnnotationReader(goldFileLocation).readAnnotations(agentClass);
    List<Annotation<T>> resultToEvaluate = new AnnotationReader(resultFile)
        .readAnnotations(agentClass);
    evaluateAgainst(resultToEvaluate, gold);
    this.goldLocation = goldFileLocation;
    /* Print the evaluation result: */
    TM2Logger.singleton().info(
        String.format("%s for evaluation of '%s' annotations against '%s'", getResultString(),
            agentClass.getSimpleName(), gold));
  }

  /**
   * @param <T> The value types of the annotations to compare
   * @param resultAnnotations The annotations to evaluate against the gold annotations
   * @param goldAnnotations The gold standard annotations to evaluate the result against
   */
  public final <T extends Comparable<T>> void evaluateAgainst(
      final List<Annotation<T>> resultAnnotations, final List<Annotation<T>> goldAnnotations) {
    float tp = 0f;
    /* True positives: result and gold: */
    for (Annotation<?> a : resultAnnotations) {
      if (goldAnnotations.contains(a)) {
        tp++;
      }
    }
    /* Precision: tp / (tp + fp) (i.e. all result annotations): */
    float p = tp / resultAnnotations.size();
    /* Recall, tp / (tp + fn) (i.e. all gold annotations): */
    float r = tp / goldAnnotations.size();
    float f = 0f;
    if (p == 0 && r == 0) {
      f = 0;
    } else {
      f = 2 * p * r / (p + r);
    }
    if (f < 0 || f > 1.0f) {
      throw new IllegalStateException("f should be between 0 and 1 but is: " + f);
    }
    this.result = new Result(p, r, f);
  }

  /**
   * @return Returns true if this evaluation is finished and a result is available
   */
  public final boolean finished() {
    return this.result != null;
  }

  /**
   * @return The location of the gold standard file
   */
  public final String getGoldLocation() {
    return goldLocation;
  }

  /**
   * @return Returns a human-readable representation of the evaluation result, containing precision,
   *         recall and f-measure. The exact format is subject to change. If you need particular
   *         values, use the accessors.
   */
  public final String getResultString() {
    if (!finished()) {
      return String.format("Unfinished %s", this.getClass().getSimpleName());
    }
    return result.toString();
  }

  /**
   * @return Returns the f-measure
   */
  public final float getF() {
    checkFinished();
    return result.f;
  }

  /**
   * @return Returns the precision
   */
  public final float getPrecision() {
    checkFinished();
    return result.p;
  }

  /**
   * @return Returns the recall
   */
  public final float getRecall() {
    checkFinished();
    return result.r;
  }

  /**
   * Throws an IllegalArgumentException if the evaluation is not finished yet.
   */
  private void checkFinished() {
    if (!finished()) {
      throw new IllegalStateException("Evaluation is not finished!");
    }
  }

  @Override
  public String toString() {
    return String.format("%s using gold: %s, result: %s", getClass().getSimpleName(), goldLocation
        .substring(goldLocation.lastIndexOf('/') + 1), getResultString());
  }

  public List<Annotation<String>> process(final List<Annotation<E>> input) {
    // Generics with ? here work with the Eclipse compiler but not Sun's
    final Class author = input.get(0).author();
    List<Annotation<E>> gold = new AnnotationReader(goldLocation)
        .readAnnotations((Class<? extends Agent<?, E>>) author);
    return evaluate(input, author, gold);
  }

  private List<Annotation<String>> evaluate(final List<Annotation<E>> input,
      final Class<? extends Agent<?, ?>> author, List<Annotation<E>> gold) {
    evaluateAgainst(input, gold);
    try {
		final Class c = Class.forName(getClass().getName());
		return new ArrayList<Annotation<String>>() {
		  {
		    add(ImmutableAnnotation.getInstance(c, String.format("%s = %s;[%s]",
		        author.getSimpleName(), getResultString(), goldLocation), input.get(0).getStart(),
		        input.get(input.size() - 1).getEnd()));
		  }
		};
	} catch (ClassNotFoundException e) {
		e.printStackTrace();
		return null;
	}
  }

  public void setResultFile(String outputAnnotationLocation) {
    this.resultFile = outputAnnotationLocation;

  }

  public Model<E, E> train(List<Annotation<E>> values,
      List<Annotation<E>> correct) {
    // Generics with ? here work with the Eclipse compiler but not Sun's
    final Class author = values.get(0).author();
    // TODO use result here?
    List<Annotation<String>> result = evaluate(values, author, correct);
    evaluateAgainst(values, correct);
    return this;
  }
}

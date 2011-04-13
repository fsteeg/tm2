package com.quui.tm2.agents;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.junit.BeforeClass;
import org.junit.Test;

import com.quui.tm2.Annotation;
import com.quui.tm2.ImmutableAnnotation;

/**
 * Tests for the generic evaluation: creates gold standards for annotations of
 * string and annotations of integer. Evaluates lists of annotations of strings
 * and integers, each with a completely correct list and a list with one correct
 * annotation of three in all.
 * 
 * @author fsteeg
 * 
 */
public class EvaluationObjectsTest {

  private static List<Annotation<String>> goldStrings;
  private static List<Annotation<Integer>> goldIntegers;

  @BeforeClass
  public static void gold() {
    goldStrings = new ArrayList<Annotation<String>>();
    goldStrings.add(ImmutableAnnotation.getInstance(null, "a", 0, 3));
    goldStrings.add(ImmutableAnnotation.getInstance(null, "b", 0, 3));
    goldStrings.add(ImmutableAnnotation.getInstance(null, "c", 0, 3));

    goldIntegers = new ArrayList<Annotation<Integer>>();
    goldIntegers.add(ImmutableAnnotation.getInstance(null, 1, 0, 3));
    goldIntegers.add(ImmutableAnnotation.getInstance(null, 2, 0, 3));
    goldIntegers.add(ImmutableAnnotation.getInstance(null, 3, 0, 3));
  }

  @Test
  public void equal() {
    ImmutableAnnotation<String> a1 = ImmutableAnnotation.getInstance(null, "a", 0, 3);
    ImmutableAnnotation<String> a2 = ImmutableAnnotation.getInstance(null, "a", 0, 3);
    assertEquals("Identical Annotation are not equal", a1, a2);
  }

  @Test
  public void allStrings() {
    List<Annotation<String>> someStrings = new ArrayList<Annotation<String>>();
    someStrings.add(ImmutableAnnotation.getInstance(null, "a", 0, 3));
    someStrings.add(ImmutableAnnotation.getInstance(null, "b", 0, 3));
    someStrings.add(ImmutableAnnotation.getInstance(null, "c", 0, 3));
    assertResult(someStrings, goldStrings, 1f);
  }

  private <T extends Comparable<T>> void assertResult(
      List<Annotation<T>> result, List<Annotation<T>> gold, float f) {
    SimpleEvaluation evaluation = new SimpleEvaluation();
    evaluation.evaluateAgainst(result, gold);
    assertEquals("Wrong evaluation result;", f, evaluation.getF(), 1e-9);
  }

  @Test
  public void someStrings() {
    List<Annotation<String>> someStrings = new ArrayList<Annotation<String>>();
    someStrings.add(ImmutableAnnotation.getInstance(null, "a", 0, 3));
    someStrings.add(ImmutableAnnotation.getInstance(null, "b", 1, 3));
    someStrings.add(ImmutableAnnotation.getInstance(null, "c", 1, 3));
    assertResult(someStrings, goldStrings, 1 / 3f);
  }

  @Test
  public void allIntegers() {
    List<Annotation<Integer>> someIntegers = new ArrayList<Annotation<Integer>>();
    someIntegers.add(ImmutableAnnotation.getInstance(null, 1, 0, 3));
    someIntegers.add(ImmutableAnnotation.getInstance(null, 2, 0, 3));
    someIntegers.add(ImmutableAnnotation.getInstance(null, 3, 0, 3));
    assertResult(someIntegers, goldIntegers, 1f);
  }

  @Test
  public void someIntegers() {
    List<Annotation<Integer>> someIntegers = new ArrayList<Annotation<Integer>>();
    someIntegers.add(ImmutableAnnotation.getInstance(null, 1, 0, 3));
    someIntegers.add(ImmutableAnnotation.getInstance(null, 2, 1, 3));
    someIntegers.add(ImmutableAnnotation.getInstance(null, 3, 1, 3));
    assertResult(someIntegers, goldIntegers, 1 / 3f);
  }
}

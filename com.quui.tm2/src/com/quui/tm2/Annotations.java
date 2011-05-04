package com.quui.tm2;

import java.util.ArrayList;
import java.util.List;

/**
 * Static helper methods for working with annotations.
 * @author Fabian Steeg (fsteeg)
 */
public class Annotations {

    public static <T> List<T> toValues(List<Annotation<T>> input) {
        List<T> result = new ArrayList<T>();
        for (Annotation<T> annotation : input) {
            result.add(annotation.getValue());
        }
        return result;
    }

    public static <T1 extends Comparable<T1>, T2 extends Comparable<T2>> List<Annotation<?>>[] firstOverlapsSecond(List<Annotation<T1>> first,
            List<Annotation<T2>> second) {
        List<Annotation<T1>> result1 = new ArrayList<Annotation<T1>>();
        List<Annotation<T2>> result2 = new ArrayList<Annotation<T2>>();
        for (Annotation<T1> a1 : first) {
            for (Annotation<T2> a2 : second) {
                if (fullOverlap(a1, a2)) {
                    result1.add(new ImmutableAnnotation<T1>(a1));
                    result2.add(new ImmutableAnnotation<T2>(a2));
                }
            }
        }
        return new List[]{new ArrayList(result1), new ArrayList(result2)};
    }
    
    public static boolean fullOverlap(Annotation<?> a1, Annotation<?> a2) {
        return a1.getStart().equals(a2.getStart()) && a1.getEnd().equals(a2.getEnd());
    }

}

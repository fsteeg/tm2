package com.quui.tm2.agents;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.quui.tm2.Agent;
import com.quui.tm2.Annotation;
import com.quui.tm2.ImmutableAnnotation;

/**
 * Simple word counter.
 */
public final class Counter implements Agent<String, Integer> {

    /**
     * {@inheritDoc}
     * @see com.quui.tm2.Agent#process(java.util.List)
     */
    public List<Annotation<Integer>> process(final List<Annotation<String>> input) {
        Map<String, Integer> frequencies = new HashMap<String, Integer>();
        Map<String, Annotation<String>> annotations = new HashMap<String, Annotation<String>>();
        for (Annotation<String> annotation : input) {
            Integer c = frequencies.get(annotation.getValue());
            if (c == null) {
                c = 0;
            }
            frequencies.put(annotation.getValue(), c + 1);
            annotations.put(annotation.toString(), annotation);
        }
        List<Annotation<Integer>> result = new ArrayList<Annotation<Integer>>();
        for (Entry<String, Annotation<String>> s : annotations.entrySet()) {
            Integer f = frequencies.get(s.getValue().getValue());
            Annotation<Integer> annotation = ImmutableAnnotation.getInstance(getClass(), f, s.getValue().getStart(), s
                    .getValue().getEnd());
            result.add(annotation);
        }
        return result;
    }

}

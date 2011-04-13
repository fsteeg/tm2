package com.quui.tm2.agents;

import java.util.ArrayList;
import java.util.List;

import com.quui.tm2.Agent;
import com.quui.tm2.Annotation;
import com.quui.tm2.ImmutableAnnotation;

/**
 * A pointless Agent that should demonstrate framework usage.
 * @author fsteeg
 */
public final class Doubler implements Agent<Integer, Integer> {

    /**
     * {@inheritDoc}
     * @see com.quui.tm2.Agent#process(java.util.List)
     */
    public List<Annotation<Integer>> process(
            final List<Annotation<Integer>> input) {
        List<Annotation<Integer>> result = new ArrayList<Annotation<Integer>>();
        for (Annotation<Integer> annotation : input) {
            result.add(ImmutableAnnotation
                    .getInstance(getClass(), annotation.getValue() * 2,
                            annotation.getStart(), annotation.getEnd()));
        }
        return result;
    }

}

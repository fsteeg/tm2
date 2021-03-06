package com.quui.tm2.agents;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.quui.tm2.Agent;
import com.quui.tm2.Annotation;
import com.quui.tm2.ImmutableAnnotation;

/**
 * TODO work in progress, implement pseudo word ambiguity experiment.
 * @author Fabian Steeg (fsteeg)
 */
public class PseudoAmbig implements Agent<String, String> {

    private List<String> words;
    
    @Override
    public String toString() {
        return String.format("%s using %s", getClass().getSimpleName(), words);
    }

    /**
     * @param words The real words to concatenate to a pseudo-ambiguous word
     */
    public PseudoAmbig(final String... words) {
        this.words = Arrays.asList(words);
    }

    public List<Annotation<String>> process(List<Annotation<String>> input) {
        List<Annotation<String>> result = new ArrayList<Annotation<String>>();
        for (Annotation<String> annotation : input) {
            if (words.contains(annotation.getValue())) {
                String value = "S"
                        + words.indexOf(annotation.getValue());
                Annotation<String> newAnnotation = ImmutableAnnotation.getInstance(getClass(),
                        value, annotation.getStart(), annotation.getEnd());
                if(newAnnotation == null){
                    throw new IllegalStateException("Null annotation");
                }
                result.add(newAnnotation);
            }
        }
        return result;
    }
}

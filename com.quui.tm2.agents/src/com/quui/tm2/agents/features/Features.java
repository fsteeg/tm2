package com.quui.tm2.agents.features;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.quui.tm2.Agent;
import com.quui.tm2.Annotation;
import com.quui.tm2.Annotations;
import com.quui.tm2.ImmutableAnnotation;
import com.quui.tm2.Model;
import com.quui.tm2.agents.features.generator.FeatureGenerator;
import com.quui.tm2.types.FeatureVector;

/**
 * @author fsteeg
 *
 */
public class Features implements Model<String, String>, Agent<String, FeatureVector> {

    private FeatureGenerator generator;
    private HashMap<Annotation<String>, Annotation<String>> relevantData;
    private int context;

    @Override
    public String toString() {
        return String.format("%s using generator %s, context %s", getClass().getSimpleName(),
                generator, context);
    }

    public Features(List<String> vocabulary, String type, int context) {
        this.context = context;
        generator = new FeatureGenerator(vocabulary, type, context);
    }

    public Features() {}

    public Features(String s) {}

    public List<Annotation<FeatureVector>> process(List<Annotation<String>> input) {
        List<String> strings = Annotations.toValues(new ArrayList<Annotation<String>>(relevantData
                .values()));
        List<Annotation<FeatureVector>> result = new ArrayList<Annotation<FeatureVector>>();
        for (int i = 0; i < input.size(); i++) {
            FeatureVector vector = new FeatureVector(generator.getFeatures(i, strings, context * 2), 
            		input.get(i).getValue(), null);
            Annotation<FeatureVector> annotation = ImmutableAnnotation.getInstance(getClass(),
                    vector, input.get(i).getStart(), input.get(i).getEnd());
            result.add(annotation);
        }
        return result;
    }

    /* @Override */public Model<String, String> train(List<Annotation<String>> data,
            List<Annotation<String>> info) {
        relevantData = new HashMap<Annotation<String>, Annotation<String>>();
        for (Annotation<String> d : data) {
            for (Annotation<String> i : info) {
                if (Annotations.fullOverlap(d, i)) {
                    relevantData.put(i, d);
                }
            }
        }
        return this;
    }
}

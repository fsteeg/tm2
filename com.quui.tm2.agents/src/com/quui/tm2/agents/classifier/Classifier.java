package com.quui.tm2.agents.classifier;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.common.primitives.Ints;
import com.quui.tm2.Agent;
import com.quui.tm2.Annotation;
import com.quui.tm2.Annotations;
import com.quui.tm2.ImmutableAnnotation;
import com.quui.tm2.Model;
import com.quui.tm2.agents.classifier.bayestree.BayesTree;
import com.quui.tm2.agents.classifier.console.ClassifierPreferences;
import com.quui.tm2.agents.classifier.weka.WekaClassifier;
import com.quui.tm2.agents.classifier.weka.WsdClassifier;
import com.quui.tm2.types.FeatureVector;

public class Classifier implements Agent<FeatureVector, String>, Model<FeatureVector, String> {

    public Map<String, WsdClassifier> lexicon = new HashMap<String, WsdClassifier>();

    private HashMap<String, List<String>> sensesMap;

    private List<Integer> structure;

    private float patternFactor;

    private weka.classifiers.Classifier wekaClassifier;

    public Classifier(List<Integer> structure, float patternFactor,
            weka.classifiers.Classifier wekaClassifier, String... senses) {
        // List<String> s = Arrays.asList(new String[] { "S0", "S1" });
        this.patternFactor = patternFactor;
        this.structure = structure;
        sensesMap = new HashMap<String, List<String>>();
        // TODO one lemma only currently
        sensesMap.put("dummy", Arrays.asList(senses));
        this.wekaClassifier = wekaClassifier;
        System.out.println("Configuration: (specified in "
				+ ClassifierPreferences.getInstance().propertiesFileLocation + ") ");
		ClassifierPreferences.getInstance().properties.list(System.out);
    }

    public Classifier(String algo) {}

    public Classifier() {}

    public Model<FeatureVector, String> train(List<Annotation<FeatureVector>> value,
            List<Annotation<String>> correct) {
    	System.err.println("Hello training");
        List<Annotation<FeatureVector>> correspondingFeatures = (List)Annotations.firstOverlapsSecond(
                value, correct)[0];
        // System.out.println("Corresponding (train): " + correspondingFeatures);
        for (int i = 0; i < correspondingFeatures.size(); i++) {
            FeatureVector wordFeatures = value.get(i).getValue();
            String correctValue = correct.get(i).getValue();
            String key = wordFeatures.lemma;
            WsdClassifier classifier = lexicon.get(key);
            if (classifier == null) {
                // System.out.println("Creating classifier for: " + key);
                // TODO customize tree or use weka, etc
                List<String> classes = sensesMap.values().iterator().next();
                classifier = wekaClassifier == null ? new BayesTree(Ints.toArray(structure),
                        patternFactor, classes) : new WekaClassifier(classes, structure.get(0)
                        .intValue(), wekaClassifier);
                System.err.println("Storing classifier in lexicon for: " + key);
                lexicon.put(key, classifier);
            }
            // System.err.println(String.format("Training %s as %s", wordFeatures, correctValue));
            classifier.train(wordFeatures.getValues(), correctValue);
        }
        return this;
    }

    public List<Annotation<String>> process(List<Annotation<FeatureVector>> input) {
        List<Annotation<String>> result = new ArrayList<Annotation<String>>();
        // System.out.println("Processing (process): " + input);
        for (Annotation<FeatureVector> annotation : input) {
            FeatureVector featureVector = annotation.getValue();
            WsdClassifier classifier = lexicon.get(featureVector.lemma);
            if(classifier==null){
            	throw new IllegalStateException("No classifier in lexicon for: " + featureVector.lemma);
            }
            String r = classifier.classify(featureVector.getValues());
            // System.err.println(String.format("Classified %s as %s", featureVector, r));
            result.add(ImmutableAnnotation.getInstance(getClass(), r, annotation.getStart(),
                    annotation.getEnd()));
        }
        return result;
    }

    @Override
    public String toString() {
        return String.format("%s using %s with senses @%s@, structure @%s@, factor @%s@", getClass()
                .getSimpleName(), wekaClassifier == null ? BayesTree.class.getSimpleName()
                : wekaClassifier.getClass().getSimpleName(), sensesMap, structure, patternFactor);
    }

}

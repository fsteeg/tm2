package com.quui.tm2.agents.senseval;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.common.primitives.Ints;
import com.quui.tm2.Agent;
import com.quui.tm2.Annotation;
import com.quui.tm2.ImmutableAnnotation;
import com.quui.tm2.Model;
import com.quui.tm2.agents.classifier.bayestree.BayesTree;
import com.quui.tm2.agents.classifier.weka.WekaClassifier;
import com.quui.tm2.agents.classifier.weka.WsdClassifier;
import com.quui.tm2.types.FeatureVector;
import com.quui.tm2.types.Sense;

public class SensevalClassifier implements Agent<FeatureVector, Sense>, Model<FeatureVector, Ambiguity> {

    // TODO one lemma only currently
    public Map<String, WsdClassifier> lexicon = new HashMap<String, WsdClassifier>();

//    private HashMap<String, List<String>> sensesMap;

    private List<Integer> structure;

    private float patternFactor;

    private weka.classifiers.Classifier wekaClassifier;

    public SensevalClassifier(List<Integer> structure, float patternFactor,
            weka.classifiers.Classifier wekaClassifier, String... senses) {
        // List<String> s = Arrays.asList(new String[] { "S0", "S1" });
        this.patternFactor = patternFactor;
        this.structure = structure;
//        sensesMap = new HashMap<String, List<String>>();
        // TODO one lemma only currently
//        sensesMap.put("dummy", Arrays.asList(senses));
        this.wekaClassifier = wekaClassifier;
//        System.out.println("Configuration: (specified in "
//				+ Preferences.getInstance().propertiesFileLocation + ") ");
//		Preferences.getInstance().properties.list(System.out);
        System.out.println();
    }

    public SensevalClassifier(String algo) {}

    public SensevalClassifier() {}

    public Model<FeatureVector, Ambiguity> train(List<Annotation<FeatureVector>> value,
            List<Annotation<Ambiguity>> correct) {
    	System.err.println("Training " + this);
        List<Annotation<FeatureVector>> correspondingFeatures = value;//(List)Annotations.firstOverlapsSecond(
//                value, correct)[0];
        List<Annotation<Ambiguity>> correspondingAmbiguities = correct;//(List)Annotations.firstOverlapsSecond(
//                value, correct)[1];
        if(correspondingAmbiguities.size() != correspondingFeatures.size()){
        	throw new IllegalStateException("Different sizes!");
        }
        
        // System.out.println("Corresponding (train): " + correspondingFeatures);
        for (int i = 0; i < correspondingFeatures.size(); i++) {
            FeatureVector wordFeatures = correspondingFeatures.get(i).getValue();
            Ambiguity ambiguity = correspondingAmbiguities.get(i).getValue();
			String correctValue = ambiguity.getCorrect();
            String key = wordFeatures.lemma;
            WsdClassifier classifier = lexicon.get(key);
            if (classifier == null) {
                // System.out.println("Creating classifier for: " + key);
                List<String> classes = ambiguity.getContext().senses;
                classifier = wekaClassifier == null ? new BayesTree(Ints.toArray(structure),
                        patternFactor, classes) : new WekaClassifier(classes, structure.get(0)
                        .intValue(), wekaClassifier);
//                System.out.println("Storing classifier in lexicon for: " + key);
                lexicon.put(key, classifier);
            }
            // System.err.println(String.format("Training %s as %s", wordFeatures, correctValue));
            classifier.train(wordFeatures.getValues(), correctValue);
        }
        return this;
    }

    public List<Annotation<Sense>> process(List<Annotation<FeatureVector>> input) {
        List<Annotation<Sense>> result = new ArrayList<Annotation<Sense>>();
        // System.out.println("Processing (process): " + input);
        for (Annotation<FeatureVector> annotation : input) {
            FeatureVector featureVector = annotation.getValue();
            WsdClassifier classifier = lexicon.get(featureVector.lemma);
            if(classifier==null){
            	throw new IllegalStateException("No classifier in lexicon for: " + featureVector.lemma);
            }
            String correct = classifier.classify(featureVector.getValues());
            Sense r = new Sense(featureVector.id, featureVector.lemma, correct);
            // System.err.println(String.format("Classified %s as %s", featureVector, r));
            result.add(ImmutableAnnotation.getInstance(SensevalClassifier.class, r, annotation.getStart(),
                    annotation.getEnd()));
            if(classifier instanceof BayesTree) ((BayesTree) classifier).resetClassify(); // TODO see WSD
        }
        return result;
    }

    @Override
    public String toString() {
        return String.format("%s using %s", getClass()
                .getSimpleName(), wekaClassifier == null ? BayesTree.class.getSimpleName()
                : wekaClassifier.getClass().getSimpleName()/*, structure, patternFactor*/);
    }

}

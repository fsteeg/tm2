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

/**
 * @author fsteeg
 *
 */
public class SensevalClassifier implements Agent<FeatureVector, Sense>, Model<FeatureVector, Ambiguity> {

    public Map<String, WsdClassifier> lexicon = new HashMap<String, WsdClassifier>();

    private List<Integer> structure;

    private float patternFactor;

    private weka.classifiers.Classifier wekaClassifier;

    public SensevalClassifier(List<Integer> structure, float patternFactor,
            weka.classifiers.Classifier wekaClassifier, String... senses) {
        this.patternFactor = patternFactor;
        this.structure = structure;
        this.wekaClassifier = wekaClassifier;
        System.out.println();
    }

    public SensevalClassifier(String algo) {}

    public SensevalClassifier() {}

    public Model<FeatureVector, Ambiguity> train(List<Annotation<FeatureVector>> value,
            List<Annotation<Ambiguity>> correct) {
    	System.err.println("Training " + this);
        List<Annotation<FeatureVector>> correspondingFeatures = value;
        List<Annotation<Ambiguity>> correspondingAmbiguities = correct;
        if(correspondingAmbiguities.size() != correspondingFeatures.size()){
        	throw new IllegalStateException("Different sizes!");
        }
        for (int i = 0; i < correspondingFeatures.size(); i++) {
            FeatureVector wordFeatures = correspondingFeatures.get(i).getValue();
            Ambiguity ambiguity = correspondingAmbiguities.get(i).getValue();
			String correctValue = ambiguity.getCorrect();
            String key = wordFeatures.lemma;
            WsdClassifier classifier = lexicon.get(key);
            if (classifier == null) {
                List<String> classes = ambiguity.getContext().senses;
                classifier = wekaClassifier == null ? new BayesTree(Ints.toArray(structure),
                        patternFactor, classes) : new WekaClassifier(classes, structure.get(0)
                        .intValue(), wekaClassifier);
                lexicon.put(key, classifier);
            }
            classifier.train(wordFeatures.getValues(), correctValue);
        }
        return this;
    }

    public List<Annotation<Sense>> process(List<Annotation<FeatureVector>> input) {
        List<Annotation<Sense>> result = new ArrayList<Annotation<Sense>>();
        for (Annotation<FeatureVector> annotation : input) {
            FeatureVector featureVector = annotation.getValue();
            WsdClassifier classifier = lexicon.get(featureVector.lemma);
            if(classifier==null){
            	throw new IllegalStateException("No classifier in lexicon for: " + featureVector.lemma);
            }
            String correct = classifier.classify(featureVector.getValues());
            Sense r = new Sense(featureVector.id, featureVector.lemma, correct);
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

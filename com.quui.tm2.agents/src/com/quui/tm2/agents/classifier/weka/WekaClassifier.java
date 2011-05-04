package com.quui.tm2.agents.classifier.weka;

import java.text.NumberFormat;
import java.util.List;

import weka.classifiers.Classifier;
import weka.classifiers.bayes.NaiveBayes;
import weka.core.Attribute;
import weka.core.FastVector;
import weka.core.Instance;
import weka.core.Instances;

/**
 * Wraps a Weka classifier.
 * @author fsteeg
 */
public class WekaClassifier implements WsdClassifier {

    private List<String> classes;
    private Instances trainingSet;
    private Classifier classifier;
    private int featureSize;
    private boolean trained;

    public WekaClassifier(List<String> classes, int featureSize) {
        this(classes, featureSize, new NaiveBayes());
    }

    public WekaClassifier(List<String> classes, int featureSize, Classifier classifier) {
        this.classifier = classifier;
        this.classes = classes;
        this.featureSize = featureSize;
        trainingSet = createSet(this.classes);
    }

    private Instances createSet(List<String> classes) {
        FastVector attributes = new FastVector(1 + featureSize);
        FastVector vals = new FastVector(classes.size());
        for (String c : classes) {
            vals.addElement(c);
        }
        Attribute classAttribute = new Attribute("Sense", vals);
        attributes.addElement(classAttribute);
        for (int i = 0; i < featureSize; i++) {
            attributes.addElement(new Attribute(i + 1 + ""));
        }
        Instances set = new Instances("Rel", attributes, 1);
        set.setClassIndex(0);
        return set;
    }

    public String classify(float[] features) {
        if (!trained) {
            /*
             * Tested with: NaiveBayes BayesNet AdaBoostM1 SMO
             */
            classifier.setDebug(true);
            try {
                // classifier.setOptions(new String[]{"-D"});
                // weka.classifiers.bayes.net.estimate.BMAEstimator
                classifier.buildClassifier(trainingSet);
                trained = true;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        Instance instance = createInstance(features, null, trainingSet);
        if(!instance.classIsMissing()) {
            throw new IllegalStateException(
                    "Makes no sense to classify classified instance: " + instance);
        }
        try {
             // System.out.println(NumberFormat.getInstance().format(fDistribution[i]));
            String res = classes.get((int) classifier.classifyInstance(instance));
            return res;
        } catch (Exception e) {
            System.err.println("Using classifier failed:");
            e.printStackTrace();
        }
        return null;
    }

    public void train(float[] features, String correct) {
        // System.out.println("Training " + correct + " with features: " +
        // toString(features));
        Instance instance = createInstance(features, correct, trainingSet);
        trainingSet.add(instance);
        trained = false;
    }

    private String toString(float[] features) {
        StringBuilder b = new StringBuilder();
        for (int i = 0; i < features.length; i++) {
            NumberFormat nf = NumberFormat.getInstance();
            b.append(nf.format(features[i])).append(" ");
        }
        return b.toString().trim();
    }

    private Instance createInstance(float[] ds, String string, Instances instances) {

        if (ds.length != featureSize) {
            throw new IllegalArgumentException(
                    String.format("Cannot create instance with %s features for classifier set up for %s features", ds.length, featureSize));
        }

        double[] attrs = new double[ds.length + 1];
        for (int i = 0; i < ds.length; i++) {
            attrs[i + 1] = ds[i];
        }

        Instance instance = new Instance(1.0, attrs);

        instance.setDataset(instances);
        if (string == null) {
            instance.setClassMissing();
        } else {
            instance.setClassValue(string);
        }

        return instance;
    }

    public int featureSize() {
        return featureSize;
    }

    public void resetClassify() {
      // not required for weka classifiers
    }

}

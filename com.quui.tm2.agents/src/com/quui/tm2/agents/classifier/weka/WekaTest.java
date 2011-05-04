package com.quui.tm2.agents.classifier.weka;

import weka.classifiers.Classifier;
import weka.classifiers.Evaluation;
import weka.classifiers.bayes.BayesNet;
import weka.core.Attribute;
import weka.core.FastVector;
import weka.core.Instance;
import weka.core.Instances;

public class WekaTest {

	/**
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {
		Instances isTrainingSet = createSet(4);
		Instance instance1 = createInstance(
				new double[] { 1, 0.7, 0.1, 0.7 }, "S1", isTrainingSet);
		Instance instance2 = createInstance(
				new double[] { 0.1, 0.2, 1, 0.3 }, "S2", isTrainingSet);
		Instance instance22 = createInstance(
				new double[] { 0, 0, 0, 0 }, "S3", isTrainingSet);
		isTrainingSet.add(instance1);
		isTrainingSet.add(instance2);
		isTrainingSet.add(instance22);
		Instances isTestingSet = createSet(4);
		Instance instance3 = createInstance(
				new double[] { 1, 0.7, 0.1, 0.7 }, "S1", isTrainingSet);
		Instance instance4 = createInstance(
				new double[] { 0.1, 0.2, 1, 0.3 }, "S2", isTrainingSet);
		isTestingSet.add(instance3);
		isTestingSet.add(instance4);

		// Create a naïve bayes classifier
		Classifier cModel = (Classifier) new BayesNet();// M5P
		cModel.buildClassifier(isTrainingSet);

		// Test the model
		Evaluation eTest = new Evaluation(isTrainingSet);
		eTest.evaluateModel(cModel, isTestingSet);

		// Print the result à la Weka explorer:
		String strSummary = eTest.toSummaryString();
		System.out.println(strSummary);

		// Get the likelihood of each classes
		// fDistribution[0] is the probability of being “positive”
		// fDistribution[1] is the probability of being “negative”
		double[] fDistribution = cModel.distributionForInstance(instance4);
		for (int i = 0; i < fDistribution.length; i++) {
			System.out.println(fDistribution[i]);
		}

	}

	private static Instance createInstance(double[] ds, String string,
			Instances instances) {
		int l = ds.length;
		// Create the instance
		Instance instance = new Instance(1+l);
		instance.setDataset(instances);
		for (int i = 1; i <= l; i++) {
			instance.setValue(i, ds[i-1]);
		}
		if (string == null) {
			 instance.setClassMissing();
		} else {
			instance.setClassValue(string);
		}
		return instance;
	}

	private static Instances createSet(int l) {
		FastVector attributes = new FastVector(1+l);
		FastVector vals = new FastVector(3);
		vals.addElement("S1");
		vals.addElement("S2");
		vals.addElement("S3");
		Attribute classAttribute = new Attribute("Sense", vals);
		attributes.addElement(classAttribute);
		for (int i = 0; i < l; i++) {
			attributes.addElement(new Attribute(i+1+""));
		}
		Instances set = new Instances("Rel", attributes, 1+l);
		set.setClassIndex(0);
		return set;
	}

}

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

		// Get the confusion matrix
		// double[][] cmMatrix = eTest.confusionMatrix();

		// Create the instance
//		Instance iUse = createInstance(
//				new double[] { 0.3, 0.2, 0.9, 0.2 }, null, isTrainingSet);

		// Specify that the instance belong to the training set
		// in order to inherit from the set description
//		iUse.setDataset(isTrainingSet);
//
//		iUse.setValue(0, 1.0);
//		iUse.setValue(1, 0.5);
//		iUse.setValue(2, 0.2);
//		iUse.setValue(3, 0.3);
		// iUse.setValue(4, "S1");

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
//		instance.setValue(0, string);
		for (int i = 1; i <= l; i++) {
			instance.setValue(i, ds[i-1]);
		}
		if (string == null) {
			 instance.setClassMissing();
		} else {
//			 instance.setClassValue(string);
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

//	private static Instances createSet(double[] vals) {
//		// Declare two numeric attributes
//		// Attribute attribute1 = new Attribute("one");
//		// Attribute attribute2 = new Attribute("two");
//		// Attribute attribute3 = new Attribute("three");
//		// Attribute attribute4 = new Attribute("four");
//
//		// Declare the feature vector
//		FastVector fvWekaAttributes = new FastVector(1);
//		// fvWekaAttributes.addElement(new Attribute("S1"));
//		// fvWekaAttributes.addElement(new Attribute("S2"));
//		// fvWekaAttributes.addElement(new Attribute("S3"));
//		// fvWekaAttributes.addElement(attribute1);
//		// fvWekaAttributes.addElement(attribute2);
//		// fvWekaAttributes.addElement(attribute3);
//		// fvWekaAttributes.addElement(attribute4);
//
//		// Declare a nominal attribute along with its values
//		// FastVector fvNominalVal = new FastVector(3);
//		// fvNominalVal.addElement("S1");
//		// fvNominalVal.addElement("S2");
//		// fvNominalVal.addElement("S3");
//		// Attribute attribute3 = new Attribute("S", fvNominalVal);
//
//		// Declare the class attribute along with its values
//		FastVector fvClassVal = new FastVector(3);
//		fvClassVal.addElement("S1");
//		fvClassVal.addElement("S2");
//		fvClassVal.addElement("S3");
//		Attribute classAttribute = new Attribute("Sense", fvClassVal);
//
//		// fvWekaAttributes.addElement(attribute3);
//		fvWekaAttributes.addElement(classAttribute);
//
//		// Create an empty training set
//		Instances isTrainingSet = new Instances("Rel", fvWekaAttributes, 1);
//
//		// Set class index
//		isTrainingSet.setClassIndex(0);
//
//		int l = vals.length;
//
//		// Create the instance
//		Instance iExample = new Instance(4);
//		for (int i = 0; i < l; i++) {
//			iExample.setValue((Attribute) fvWekaAttributes.elementAt(0),
//					vals[i]);
//		}
//		// iExample.setValue(classAttribute, "S1");
//
//		// iExample.setValue((Attribute) fvWekaAttributes.elementAt(0), "gray");
//		// iExample
//		// .setValue((Attribute) fvWekaAttributes.elementAt(1), "positive");
//
//		// add the instance
//		isTrainingSet.add(iExample);
//		return isTrainingSet;
//	}
}

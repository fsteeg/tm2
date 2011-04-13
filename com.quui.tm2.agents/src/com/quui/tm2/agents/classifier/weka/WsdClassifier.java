package com.quui.tm2.agents.classifier.weka;


public interface WsdClassifier {
	public void train(float[] features, String correct);
	public String classify(float[] features);
	public int featureSize();
//	public void reset(); // now we have sep. stuff for each sense
	public void resetClassify();
}

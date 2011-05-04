package com.quui.tm2.agents.features;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.quui.tm2.Agent;
import com.quui.tm2.Annotation;
import com.quui.tm2.ImmutableAnnotation;
import com.quui.tm2.agents.features.generator.FeatureGenerator;
import com.quui.tm2.agents.senseval.Context;
import com.quui.tm2.types.FeatureVector;

/**
 * @author fsteeg
 *
 */
public abstract class ContextFeatures implements Agent<Context, FeatureVector> {

	private FeatureGenerator generator;
	private HashMap<Annotation<String>, Context> relevantData;
	private int context;

	@Override
	public String toString() {
		return String.format("%s using %s, context %s", getClass()
				.getSimpleName(), generator, context);
	}

	public ContextFeatures(List<String> vocabulary, String type, int context) {
		this.context = context;
		generator = new FeatureGenerator(vocabulary, type, context);
	}

	public ContextFeatures() {
	}

	public ContextFeatures(String s) {
	}

	public List<Annotation<FeatureVector>> process(
			List<Annotation<Context>> input) {
		List<Annotation<FeatureVector>> result = new ArrayList<Annotation<FeatureVector>>();
		for (int i = 0; i < input.size(); i++) {
			Context c = input.get(i).getValue();
			FeatureVector vector = new FeatureVector(generator.getFeatures(
					c.target, c.all, context * 2), c.lemma, c.id);
			Annotation<FeatureVector> annotation = ImmutableAnnotation
					.getInstance(getClass(), vector, input.get(i).getStart(),
							input.get(i).getEnd());
			result.add(annotation);
		}
		return result;
	}
}

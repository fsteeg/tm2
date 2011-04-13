package com.quui.tm2.agents;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import com.quui.tm2.AbstractAgent;
import com.quui.tm2.Analysis;
import com.quui.tm2.Annotation;
import com.quui.tm2.Model;
import com.quui.tm2.Synthesis;

public class TypeRuntimeTests {
	@Test
	public void analysis() {
		Analysis<String> a1 = new Analysis.Builder().source(new Tokenizer())
				.target(new Gazetteer()).build();
		Assert.assertEquals(String.class, a1.getTypeClass());
		Analysis<Integer> a2 = new Analysis.Builder().source(new Counter())
				.target(new Doubler()).build();
		Assert.assertEquals(Integer.class, a2.getTypeClass());
	}

	@Test
	public void synthesis() {
		Synthesis<String, Integer> s = new Synthesis.Builder(
				new Model<String, Integer>() {
					public Model<String, Integer> train(
							List<Annotation<String>> info,
							List<Annotation<Integer>> data) {
						// TODO Auto-generated method stub
						return null;
					}
				}).info(new Tokenizer()).data(new Counter()).build();
		Assert.assertEquals(Integer.class, s.getDataTypeClass());
		Assert.assertEquals(String.class, s.getInfoTypeClass());
	}
}

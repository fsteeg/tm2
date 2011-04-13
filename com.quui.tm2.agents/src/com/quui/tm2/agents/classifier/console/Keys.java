package com.quui.tm2.agents.classifier.console;

/**
 * Enum for keys used in the properties file
 */
public enum Keys {
	DEBUG("debug"), FILTER("filter"), STOP("stopwords"), LENGTH("use_length"), TRUE(
			"true"), CONTEXT("context"), ENCODING("encoding"), DIGITS("digits"), SENSES(
			"senses"), LAYERS("layers"), PATTERN_FACTOR("pattern_factor"), TREE_STRUCTURE(
			"tree_structure"), FEATURES("features"), FEATURES_LENGTH("length"), FEATURE_N_GRAM(
			"gram"), PARALLEL("parallel"), FEATURE_PARADIGMS("paradigms"), WEKA("weka");
	public String key;

	Keys(String key) {
		this.key = key;
	}
}
package com.quui.tm2.agents.classifier.console;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

import com.quui.tm2.agents.classifier.bayestree.BayesTree;

/**
 * Preferences from a properties file, accessed using a Singleton.
 * 
 * @author fsteeg
 * 
 */
public class ClassifierPreferences {

	private static ClassifierPreferences theInstance;

	public Properties properties;

	public final String propertiesFileLocation = "config.properties";

//	public int senses;

	public int digits;

	public int context;

	public boolean filter;

	public float patternFactor;

	public String encoding;

	public String stopwordsLocation;

	public int[] treeStructure;

	public String features;

	public boolean debug;

	public boolean parallel;

	public boolean weka;

	public static ClassifierPreferences getInstance() {
		if (theInstance == null) {
			theInstance = new ClassifierPreferences();
			try {
				theInstance.readPropertiesFile();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return theInstance;

	}

	private void readPropertiesFile() throws IOException, FileNotFoundException {
		this.properties = new Properties();
		properties.load(BayesTree.class.getResourceAsStream(propertiesFileLocation));
//		this.senses = Integer.parseInt((String) properties
//				.getProperty(Keys.SENSES.key));
		this.digits = Integer.parseInt((String) properties
				.getProperty(Keys.DIGITS.key));
		this.filter = properties.getProperty(Keys.FILTER.key).equals(
				Keys.TRUE.key);
		this.patternFactor = Float.parseFloat(properties
				.getProperty(Keys.PATTERN_FACTOR.key));
		this.encoding = properties.getProperty(Keys.ENCODING.key);
		this.stopwordsLocation = (String) properties.get(Keys.STOP.key);
		String[] s = ((String) properties.getProperty(Keys.TREE_STRUCTURE.key))
				.split(",");
		this.treeStructure = new int[s.length];
		int inputSize = 1;
		for (int i = 0; i < s.length; i++) {
			this.treeStructure[i] = Integer.parseInt(s[i]);
			inputSize *= treeStructure[i];
		}
		this.context = inputSize;
		this.features = (String) properties.getProperty(Keys.FEATURES.key);
		this.debug = properties.getProperty(Keys.DEBUG.key).equals(Keys.TRUE.key);
		this.parallel = properties.getProperty(Keys.PARALLEL.key).equals(Keys.TRUE.key);
		this.weka = properties.getProperty(Keys.WEKA.key).equals(Keys.TRUE.key);

	}
}

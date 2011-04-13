package com.quui.tm2;

import java.io.File;
import java.net.URI;

import org.apache.commons.io.FileUtils;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import com.quui.Tm2Suite;
import com.quui.tm2.agents.AnnotationReadWriteTest;
import com.quui.tm2.agents.AnnotationReaderTest;
import com.quui.tm2.agents.BatchTest;
import com.quui.tm2.agents.ConsoleRetrievalTest;
import com.quui.tm2.agents.DoublerTest;
import com.quui.tm2.agents.EvaluationAsAgentTests;
import com.quui.tm2.agents.EvaluationFilesTests;
import com.quui.tm2.agents.EvaluationObjectsTest;
import com.quui.tm2.agents.ExperimentApiTests;
import com.quui.tm2.agents.ExperimentSimpleApiTests;
import com.quui.tm2.agents.RetrievalTest;
import com.quui.tm2.agents.SampleApiUsage;
import com.quui.tm2.agents.SkeletalStrategyUsage;
import com.quui.tm2.agents.SynthesisTest;
import com.quui.tm2.agents.TestWikitextExport;
import com.quui.tm2.agents.TokenizerTest;
import com.quui.tm2.agents.WsdExperimentTest;
import com.quui.tm2.agents.classifier.PermutationGeneratorTest;
import com.quui.tm2.agents.classifier.SensevalTest;
import com.quui.tm2.agents.classifier.WSDLargerTests;
import com.quui.tm2.agents.classifier.WSDMiniTests;
import com.quui.tm2.agents.features.FeaturesTest;
import com.quui.tm2.util.Preferences;
import com.quui.tm2.util.Preferences.Default;

/**
 * A suite to run all short running tests.
 * 
 * @author fsteeg
 */
@RunWith(Suite.class)
@Suite.SuiteClasses({ Tm2Suite.class, PermutationGeneratorTest.class,
		SensevalTest.class, WSDLargerTests.class, WSDMiniTests.class,
		FeaturesTest.class, SensevalTest.class, AnnotationReaderTest.class,
		AnnotationReadWriteTest.class, BatchTest.class,
		ConsoleRetrievalTest.class, DoublerTest.class,
		EvaluationAsAgentTests.class, EvaluationFilesTests.class,
		EvaluationObjectsTest.class, ExperimentApiTests.class,
		ExperimentSimpleApiTests.class, RetrievalTest.class,
		SampleApiUsage.class, SkeletalStrategyUsage.class, SynthesisTest.class,
		TestWikitextExport.class, TokenizerTest.class, WsdExperimentTest.class })
/**/
public class Tm2AgentsSuite {
	@BeforeClass
	public static void before() {
		cleanup();
	}

	private static void cleanup() {
		URI rootUri = URI.create(Preferences.get(Default.ROOT));
		System.out.println("Using root URI: " + rootUri);
		File root = new File(rootUri);
		if (!root.exists())
			root.mkdir();
		if (root.exists() && root.listFiles() != null
				&& root.listFiles().length > 0) {
			for (File f : root.listFiles()) {
				if (!f.isHidden()) {
					boolean delete = FileUtils.deleteQuietly(f);
					if (!delete && f.exists()) {
						throw new IllegalStateException("Could not delete: "
								+ f);
					}
				}
			}
		}

	}
}

package com.quui;

import java.io.File;
import java.net.URI;

import org.apache.commons.io.FileUtils;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import com.quui.tm2.AnnotationTest;
import com.quui.tm2.ValidationTest;
import com.quui.tm2.doc.AgentInfo;
import com.quui.tm2.doc.AgentInfoTest;
import com.quui.tm2.util.Preferences;
import com.quui.tm2.util.Preferences.Default;
import com.quui.tm2.util.PreferencesTests;

/**
 * A suite to run all short running tests.
 * 
 * @author fsteeg
 */
@RunWith(Suite.class)
@Suite.SuiteClasses({ AnnotationTest.class, ValidationTest.class,
		PreferencesTests.class, AgentInfoTest.class })
/**/
public class Tm2Suite {
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

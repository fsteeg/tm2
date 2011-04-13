package com.quui.tm2;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import com.quui.Tm2Suite;
import com.quui.tm2.spec.AnalysisSpec;
import com.quui.tm2.spec.ExperimentSpec;
import com.quui.tm2.spec.GazetteerUsage;
import com.quui.tm2.spec.SynthesisSpec;
import com.quui.tm2.spec.WsdUsage;
import com.quui.tm2.spec.WsdUsageSenseval;
import com.quui.tm2.spec.WsdUsageSensevalAnnotated;

/**
 * A suite to run all tests (Java and Scala).
 * 
 * @author fsteeg
 */
@RunWith(Suite.class)
@Suite.SuiteClasses({ Tm2Suite.class, Tm2AgentsSuite.class, AnalysisSpec.class,
		SynthesisSpec.class, ExperimentSpec.class, GazetteerUsage.class,
		WsdUsage.class, WsdUsageSenseval.class, WsdUsageSensevalAnnotated.class })
/**/
public class AllTm2Suite {
}

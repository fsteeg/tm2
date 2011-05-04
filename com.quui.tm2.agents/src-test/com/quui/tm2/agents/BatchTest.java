package com.quui.tm2.agents;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;

import org.junit.Test;

import com.quui.tm2.Analysis;
import com.quui.tm2.Batch;
import com.quui.tm2.Experiment;
import com.quui.tm2.util.Preferences;
import com.quui.tm2.util.Preferences.Default;

/**
 * @author Fabian Steeg (fsteeg)
 */
public final class BatchTest {
    private static final String ROOT = Preferences.get(Default.ROOT);
    /***/
    private static final String XML = ".xml";
    /***/
    private static final String E1 = "e1";
    /***/
    private static final String E2 = "e2";

    /**
     * @throws URISyntaxException
     * @throws MalformedURLException
     */
    @Test
    public void batch() throws MalformedURLException, URISyntaxException {
        // Create two experiments:
        /*
         * Here, we use the factory: the good: we don't need to redundantly
         * specify the parameter; the bad: type inference won't work if we
         * cascade immediately (cf. below). We also can't an explicit type
         * parameter, as this is not allowed for static methods or members (for
         * which it makes no sense)
         */
        Analysis<String> i1 = new Analysis.Builder<String>().source(new Corpus())
                .target(new Tokenizer()).build();

        Analysis<String> i2 = new Analysis.Builder<String>().source(new Tokenizer())
                .target(new Gazetteer()).build();
        /*
         * OK, so giving a List<Interaction<String>> does not work as generics
         * are invariant. If we had a bound, we could use a wildcard like ?
         * extends T but we use an unbounhded wildcard in the factory...
         */
        Experiment e1 = new Experiment.Builder().name(E1).root(ROOT).analysis(i1)
                .analysis(i2).build();
        Experiment e2 = new Experiment.Builder().name(E2).root(ROOT).analysis(i1)
                .analysis(i2).build();
        // Run them in a batch:
        Batch.run(e1, e2);
        assertTrue("No result for 1;", new File(new URL(e1
                .getOutputAnnotationLocation()).toURI()).exists());
        assertTrue("No result for 2;", new File(new URL(e2
                .getOutputAnnotationLocation()).toURI()).exists());
    }
}

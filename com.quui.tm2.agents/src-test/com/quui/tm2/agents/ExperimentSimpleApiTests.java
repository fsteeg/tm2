package com.quui.tm2.agents;

import org.junit.Test;

import com.quui.tm2.Analysis;
import com.quui.tm2.Experiment;
import com.quui.tm2.doc.WikitextExport;

/**
 * Minimal sample usage via the Java API.
 * @author Fabian Steeg (fsteeg)
 */
public class ExperimentSimpleApiTests {
    @Test
    public void simple() {
        /* Set a name and a root output folder: */
        Experiment x = new Experiment.Builder().name("Simple")
        /* Add interactions: */
        .analysis(new Analysis.Builder<String>().source(new Corpus()).target(new Tokenizer()).build())
                .analysis(
                        new Analysis.Builder<String>().source(new Tokenizer()).target(
                                new Gazetteer()).build()).analysis(
                        new Analysis.Builder<String>().source(new Gazetteer()).target(
                                new SimpleEvaluation()).build()).build();
        x.run();
        /* Export documentation: */
        String resultLocation = WikitextExport.of(x);
        System.out.println("Wrote to: " + resultLocation);
    }
}

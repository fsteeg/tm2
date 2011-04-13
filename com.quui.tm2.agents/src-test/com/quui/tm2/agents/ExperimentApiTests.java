/**
 * 
 */
package com.quui.tm2.agents;

import org.junit.Test;

import com.quui.tm2.Analysis;
import com.quui.tm2.Experiment;
import com.quui.tm2.util.Preferences;
import com.quui.tm2.util.Preferences.Default;

public class ExperimentApiTests {
    private static final String ROOT = Preferences.get(Default.ROOT);

    /** A minimal sample text mining application. */
    @Test
    public void apiMini() {

        /* We can create and run an experiment in one line: */

        new Experiment.Builder().name("Sample0").root(ROOT)
                /***/
                .analysis(
                        new Analysis.Builder<String>().source(new Corpus()).target(new Tokenizer())
                                .build())
                /***/
                .analysis(
                        new Analysis.Builder<String>().source(new Tokenizer()).target(
                                new Gazetteer()).build()).build().run();

        /* Or split it up a bit (right below) or more (further below) */

        Experiment experiment = new Experiment.Builder().name("Sample linear experiment")
                .root(ROOT)
                /***/
                .analysis(
                        new Analysis.Builder<String>().source(new Corpus()).target(new Tokenizer())
                                .build())
                /***/
                .analysis(
                        new Analysis.Builder<String>().source(new Tokenizer()).target(
                                new Gazetteer()).build()).build();
        runAndExport(experiment, true);
        runAndExport(experiment, false);
    }

    @Test
    public void apiSimple1() {
        String title = "Sample 1-flow branching experiment";

        Analysis<String> interaction1 = new Analysis.Builder<String>().source(new Corpus()).target(
                new Tokenizer()).build();

        Analysis<String> interaction2 = new Analysis.Builder<String>().source(new Tokenizer())
                .target(new Gazetteer()).target(new Counter()).build();

        Experiment experiment = new Experiment.Builder().name(title).root(ROOT).analysis(
                interaction1).analysis(interaction2).build();
        runAndExport(experiment, true);
        runAndExport(experiment, false);
    }

    @Test
    public void apiSimple2() {
        String title = "Sample 2-flow branching experiment";

        Analysis<String> interaction0 = new Analysis.Builder<String>().source(new Corpus()).target(
                new Tokenizer()).build();

        /* The first interaction is of type String: */
        Analysis<String> interaction1 = new Analysis.Builder<String>().source(new Tokenizer())
                .target(new Gazetteer()).target(new Counter()).build();

        /* The second interaction is of type Integer: */
        Analysis<Integer> interaction2 = new Analysis.Builder<Integer>().source(new Counter())
                .target(new Doubler()).build();

        Experiment experiment = new Experiment.Builder().name(title).root(ROOT).analysis(
                interaction0).analysis(interaction1).analysis(interaction2).build();

        runAndExport(experiment, true);
        runAndExport(experiment, false);
    }

    private void runAndExport(Experiment experiment, boolean eval) {
        experiment.run();
        if (eval) {
          SimpleEvaluation evaluation = new SimpleEvaluation();
            evaluation.resultFile = experiment.getOutputAnnotationLocation();
            evaluation.evaluate(Preferences.get(Default.ROOT) + "../files/Gold_Gazetteer.xml",
                    Gazetteer.class);
            /* Print the evaluation result: */
            System.out.println("Result: " + evaluation.getResultString());
        }
    }
}

package com.quui.tm2.agents;

import java.util.List;

import org.junit.Test;

import com.quui.tm2.Analysis;
import com.quui.tm2.Experiment;
import com.quui.tm2.Retrieval;
import com.quui.tm2.util.Preferences;
import com.quui.tm2.util.Preferences.Default;

public class SampleApiUsage {
    private static final String ROOT = Preferences.get(Default.ROOT);

    /*
     * API usage sample involving tokenization, gezetteering, word counting, evaluation and
     * retrieval.
     */
    @Test
    public void usage() {
        /* Instantiate an experiment: */
        // String output = ROOT + "output/Result";
        /* Define the interaction of agents in the experiment: */
        Analysis<String> interaction1 = new Analysis.Builder<String>().source(new Corpus()).target(
                new Tokenizer()).build();
        /* Second interaction: */
        Analysis<String> interaction2 = new Analysis.Builder<String>().source(new Tokenizer())
                .target(new Gazetteer()).target(new Counter()).build();
        /* Add the interaction to an experiment: */
        Experiment experiment = new Experiment.Builder().name(
                "Sample named-entity extraction experiment").root(ROOT).analysis(interaction1)
                .analysis(interaction2).build();
        /* Run it: */
        experiment.run();
        /* Evaluate against a gold standard: */
        SimpleEvaluation evaluation = new SimpleEvaluation();
        evaluation.resultFile = experiment.getOutputAnnotationLocation();
        evaluation.evaluate(Preferences.get(Default.ROOT) + "../files/Gold_Gazetteer.xml",
                Gazetteer.class);
        /* Print the evaluation result: */
        System.out.println("Result: " + evaluation.getResultString());
        /* Export both annotations and documentation */
        // Export export = new Export(experiment, false);
        /*
         * Generated documentation for the experiment has been generated, stored in a file with the
         * given result name, plus the ending ".pdf". Contains graphical experiment setup, the used
         * date, the used components source, produced annotations, the gold standard and the
         * evaluation result.
         */
        // export.writeDocumentation(evaluation);
        /* We use the generated annotations for retrieval: */
        Retrieval r = new Retrieval(experiment.getOutputAnnotationLocation());
        /*
         * Here, we search for 'determiner' in the annotations produced by Gazetteer, and return
         * corresponding annotations produced by Tokenizer, i.e. we find those words (produced by
         * the tokenizer) that were tagged as "determiner" (by the gazetteer):
         */
        List<String> values = Retrieval.valuesOf(r.search(Gazetteer.class, "determiner",
                Tokenizer.class));
        print(values);
        /*
         * Generics allow for typesafe retrieval; here, we search for '2' in the annotations
         * produced by Counter and return corresponding annotations produced by Gazetteer, i.e. we
         * find the gazetteer labels for word that appeared twice:
         */
        values = Retrieval.valuesOf(r.search(Counter.class, 2, Gazetteer.class));
        print(values);
    }

    private void print(List<String> values) {
        System.out.println("Retrieval:");
        for (String v : values) {
            System.out.println(v);
        }
    }
}

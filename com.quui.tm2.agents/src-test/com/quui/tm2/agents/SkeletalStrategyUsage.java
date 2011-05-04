package com.quui.tm2.agents;

import org.junit.Test;

import com.quui.tm2.AbstractAgent;
import com.quui.tm2.Agent;
import com.quui.tm2.Analysis;
import com.quui.tm2.Experiment;
import com.quui.tm2.Retrieval;
import com.quui.tm2.util.Preferences;
import com.quui.tm2.util.Preferences.Default;

/**
 * Usage sample for using an ad-hoc/on-the-fly implementation of the skeletal
 * agent implementation.
 * @author Fabian Steeg (fsteeg)
 */

/**
 * Suppose we have some Tagger already, and we merely want to enable usage of it
 * in TM2, e.g. using it in an Experiment...
 */
class Tagger {
    /**
     * @param input The word to tag
     * @return Would return a tag for the input
     */
    static String tag(final String input) {
        return "The Tag";
    }
}

class TaggerAgent extends AbstractAgent<String, String> {

    @Override
    public String process(String input) {
        return Tagger.tag(input);
    }

}

public class SkeletalStrategyUsage {
    private static final String ROOT = Preferences.get(Default.ROOT);

    @Test
    public void anonymous() {
        Analysis<String> interaction1 = new Analysis.Builder<String>().source(
                new Corpus()).target(new Tokenizer()).build();
        Analysis<String> interaction2 = new Analysis.Builder<String>().source(
                new Tokenizer()).target(new AbstractAgent<String, String>() {
            @Override
            public String process(String input) {
                return input.toLowerCase();
            }
        }).build();
        Experiment experiment = new Experiment.Builder().name("Sample").root(
                ROOT).analysis(interaction1).analysis(interaction2).build();
        experiment.run();

    }

    @Test
    public void normal() {
        /*
         * The class above is something we have anyway, so what we do to get
         * agent to use is using the skeletal agent implementation as a strategy
         * (like comparators are often used, etc). An alternative approach would
         * be to make you existing class implement the interface.
         */
        Agent<String, String> taggerAgent = new TaggerAgent();
        /* Now we can use it with the other AMAS elements: */
        Experiment experiment = new Experiment.Builder().name("Sample").root(
                ROOT)
        /* tokenization */
        .analysis(
                new Analysis.Builder<String>().source(new Corpus()).target(
                        new Tokenizer()).build())
        /* tagging */
        .analysis(
                new Analysis.Builder<String>().source(new Tokenizer()).target(
                        taggerAgent).build()).build();
        experiment.run();
        // TODO it does not work with an anonymous class...
        Retrieval r = new Retrieval(experiment.getOutputAnnotationLocation());
    }
}

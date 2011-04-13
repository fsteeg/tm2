package com.quui.tm2.agents;

import org.junit.Test;

import com.quui.tm2.Agent;
import com.quui.tm2.Analysis;
import com.quui.tm2.Experiment;
import com.quui.tm2.doc.WikitextExport;

public class EvaluationAsAgentTests {

    @Test
    public void test1() {
        Experiment x = new Experiment.Builder()
        /**/
        .analysis(
                new Analysis.Builder<String>().source(new Corpus()).target(
                        new Tokenizer()).build())
        /**/
        .analysis(
                new Analysis.Builder<String>().source(new Tokenizer())
                        .target(new Gazetteer()).build())
        /**/
        .analysis(
                new Analysis.Builder<String>().source(new Gazetteer())
                        .target(new SimpleEvaluation()).build())
        /**/
        .build();
        x.run();
        String of = WikitextExport.of(x);
        System.out.println("Wrote to: " + of);
    }

    @Test
    public void test2() {
        Agent<String, String> evaluation = new SimpleEvaluation();
        Experiment x = new Experiment.Builder()
        /**/
        .analysis(
                new Analysis.Builder<String>().source(new Corpus()).target(
                        new Tokenizer()).build())
        /**/
        .analysis(
                new Analysis.Builder<String>().source(new Tokenizer())
                        .target(new Gazetteer()).build())
        /**/
        .analysis(
                new Analysis.Builder<String>().source(new Gazetteer())
                        .target(evaluation).build())
        /**/
        .build();
        x.run();
        String of = WikitextExport.of(x);
        System.out.println("Wrote to: " + of);
    }
}

package com.quui.tm2.agents;

import java.math.BigInteger;
import java.util.List;

import org.junit.Test;

import com.quui.tm2.AbstractAgent;
import com.quui.tm2.Analysis.Builder;
import com.quui.tm2.Annotation;
import com.quui.tm2.Experiment;
import com.quui.tm2.Model;
import com.quui.tm2.Synthesis;
import com.quui.tm2.doc.WikitextExport;

public class SynthesisTest {
    @Test
    public void training() {
        AbstractAgent<String, BigInteger> numberGen = new AbstractAgent<String, BigInteger>() {

            @Override
            public BigInteger process(String input) {
                return BigInteger.ONE;
            }
        };

        AbstractAgent<String, String> stringGen = new AbstractAgent<String, String>() {

            @Override
            public String process(String input) {
                return input;
            }
        };

        Model<String, BigInteger> trainable = new Model<String, BigInteger>() {

            public Model<String, BigInteger> train(List<Annotation<String>> values,
                    List<Annotation<BigInteger>> correct) {
                return this;
            }
        };

        Experiment x = new Experiment.Builder().name("Simple")
        /* Add interactions: */
        .analysis(new Builder<String>().source(new Corpus()).target(new Tokenizer()).build()).analysis(
                new Builder<String>().source(new Tokenizer()).target(new Gazetteer()).build()).analysis(
                new Builder<String>().source(new Gazetteer()).target(new SimpleEvaluation()).build())
                .synthesis(
                        new Synthesis.Builder<String, BigInteger>(trainable).info(numberGen).data(
                                stringGen).build()).build();
        x.run();
        /* Export documentation: */
        String resultLocation = WikitextExport.of(x);
        System.out.println("Wrote to: " + resultLocation);
    }
}

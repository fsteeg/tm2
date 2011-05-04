package com.quui.tm2.agents;

import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import weka.classifiers.bayes.NaiveBayes;

import com.quui.tm2.Analysis;
import com.quui.tm2.Annotation;
import com.quui.tm2.Experiment;
import com.quui.tm2.Experiment.Builder;
import com.quui.tm2.Retrieval;
import com.quui.tm2.Synthesis;
import com.quui.tm2.agents.classifier.Classifier;
import com.quui.tm2.agents.classifier.console.Pseudowords;
import com.quui.tm2.agents.features.Features;
import com.quui.tm2.doc.WikitextExport;
import com.quui.tm2.types.FeatureVector;

public class WsdExperimentTest {
    @Test public void testExperiment() throws MalformedURLException {
        Builder x = new Experiment.Builder();
        // agents:
        Corpus corpus = new Corpus();
        Tokenizer tokenizer = new Tokenizer();
        List<String> vocabulary = new ArrayList<String>(new HashSet<String>(
                Arrays.asList(corpus.text.split(Pseudowords.split))));
        Features featureGenerator = new Features(vocabulary, "3-gram", 4);
        PseudoAmbig pseudoAmbig = new PseudoAmbig("and", "the");
        PseudoGold pseudoGold = new PseudoGold("and", "the");

        Classifier classifier = new Classifier(Arrays.asList(8), 1f, new NaiveBayes(), "S0", "S1");
        SimpleEvaluation evaluation = new SimpleEvaluation();
        // setup:
        x.analysis(new Analysis.Builder<String>().source(corpus).target(tokenizer).build());
        x.analysis(new Analysis.Builder<String>().source(tokenizer).target(pseudoAmbig).build());
        x.synthesis(new Synthesis.Builder<String, String>(featureGenerator).data(tokenizer)
                .info(pseudoAmbig).build());
        x.analysis(new Analysis.Builder<String>().source(pseudoAmbig).target(featureGenerator)
                .build());
        // train:
        x.synthesis(new Synthesis.Builder<FeatureVector, String>(classifier).data(featureGenerator)
                .info(pseudoAmbig).build());
        // classify:
        x.analysis(new Analysis.Builder<FeatureVector>().source(featureGenerator)
                .target(classifier).build());
        // evaluate: // TODO: why twice here? cf. Scala experiments
        x.analysis(new Analysis.Builder<String>().source(tokenizer).target(pseudoGold).build());
        x.analysis(new Analysis.Builder<String>().source(tokenizer).target(pseudoGold).build());
        x.synthesis(new Synthesis.Builder<String, String>(evaluation).data(classifier)
                .info(pseudoGold).build());

        Experiment experiment = x.build();
        experiment.run();
        System.out.println(evaluation.getResultString());
        System.out.println(WikitextExport.of(experiment));
        Retrieval retrieval = new Retrieval(experiment);
        /* We check some assumptions necessary for a proper experiment setup: */
        List<Annotation<String>> pseudoAmbigAnnos = retrieval.annotationsOf(pseudoAmbig.getClass());
        List<Annotation<String>> pseudoGoldAnnos = retrieval.annotationsOf(pseudoGold.getClass());
        List<Annotation<String>> classifierAnnos = retrieval.annotationsOf(classifier.getClass());
        Assert.assertEquals(
                "All ambiguous annotations (no more or less) should be covered by the gold standard annotations",
                pseudoAmbigAnnos.size(), pseudoGoldAnnos.size());
        /*
         * Let's dig a little deeper: make sure the annotation refer to the same tokens:
         */
        for (int i = 0; i < pseudoAmbigAnnos.size(); i++) {
            Annotation<String> pseudoAmbigAnno = pseudoAmbigAnnos.get(i);
            Annotation<String> pseudoGoldAnno = pseudoGoldAnnos.get(i);
            Annotation<String> tokenizerAnnotationForAmbig = retrieval.annotationCorresponding(
                    pseudoAmbigAnno, tokenizer.getClass());
            Annotation<String> tokenizerAnnotationForGold = retrieval.annotationCorresponding(
                    pseudoGoldAnno, tokenizer.getClass());
            Assert.assertEquals(
                    "Tokenizer annotations for ambiguous words and gold standard words should be equal",
                    tokenizerAnnotationForAmbig, tokenizerAnnotationForGold);
        }
        Assert.assertEquals("All ambiguous annotations (no more or less) should be classified",
                pseudoAmbigAnnos.size(), classifierAnnos.size());
        /*
         * Let's dig a little deeper: make sure the annotation refer to the same tokens:
         */
        for (int i = 0; i < pseudoAmbigAnnos.size(); i++) {
            Annotation<String> pseudoAmbigAnno = pseudoAmbigAnnos.get(i);
            Annotation<String> classifierAnno = classifierAnnos.get(i);
            Annotation<String> tokenizerAnnotationForAmbig = retrieval.annotationCorresponding(
                    pseudoAmbigAnno, tokenizer.getClass());
            Annotation<String> tokenizerAnnotationForGold = retrieval.annotationCorresponding(
                    classifierAnno, tokenizer.getClass());
            Assert.assertEquals(
                    "Tokenizer annotations for ambiguous words and classified words should be equal",
                    tokenizerAnnotationForAmbig, tokenizerAnnotationForGold);
        }
        Assert.assertTrue("Result should be reasonable (>50% for two classes)",
                evaluation.getF() > 0.5);
    }
}

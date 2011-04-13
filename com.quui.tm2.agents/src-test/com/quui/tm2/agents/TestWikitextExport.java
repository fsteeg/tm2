package com.quui.tm2.agents;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;

import org.junit.Assert;
import org.junit.Test;

import com.quui.tm2.Analysis;
import com.quui.tm2.Experiment;
import com.quui.tm2.doc.WikitextExport;

public class TestWikitextExport {
    @Test
    public void test() {
        Experiment x = new Experiment.Builder()
                .name("Wikitext Export Experiment")
                .analysis(
                        new Analysis.Builder<String>().source(new Corpus()).target(
                                new Tokenizer()).build()).analysis(
                        new Analysis.Builder<String>().source(new Tokenizer()).target(
                                new Gazetteer()).build()).analysis(
                        new Analysis.Builder<String>().source(new Gazetteer()).target(
                                new SimpleEvaluation()).build()).analysis(
                        new Analysis.Builder<String>().source(new Tokenizer()).target(
                                new Counter()).build()).build();
        x.run();
        WikitextExport.of(x);
        try {
            File file;
            file = new File(new URL(x.getOutputDocumentationLocation()).toURI());
            Assert.assertTrue(file.exists());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        System.out.println("Wrote documentation to: "
                + x.getOutputDocumentationLocation());
    }
}

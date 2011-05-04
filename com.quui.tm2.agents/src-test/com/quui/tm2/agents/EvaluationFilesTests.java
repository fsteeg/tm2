package com.quui.tm2.agents;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.net.MalformedURLException;
import java.util.List;

import org.junit.BeforeClass;
import org.junit.Test;

import com.quui.tm2.Annotation;
import com.quui.tm2.AnnotationReader;
import com.quui.tm2.util.Preferences;
import com.quui.tm2.util.Preferences.Default;
import com.quui.tm2.util.TM2Logger;

public class EvaluationFilesTests {

    private static List<Annotation<String>> gold;
    private static List<Annotation<String>> result;
    private static String ROOT;

    static {
        try {
            ROOT = new File(".").toURI().toURL().toString();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }
    @BeforeClass
    public static void gold() {
        gold = new AnnotationReader(Preferences.get(Default.GOLD))
                .readAnnotations(Gazetteer.class);
        result = new AnnotationReader(Preferences.get(Default.RESULT))
                .readAnnotations(Gazetteer.class);
    }

    @Test
    public void evalGoldGold() {
        SimpleEvaluation evaluation = new SimpleEvaluation();
        evaluation.evaluateAgainst(gold, gold);
        assertEquals("Wrong evaluation result;", 1f, evaluation.getF(), 1e-9);
        TM2Logger.singleton(getClass()).info(evaluation);
    }

    @Test
    public void evalGoldResult() {
        SimpleEvaluation evaluation = new SimpleEvaluation();
        evaluation.evaluateAgainst(result, gold);
        assertTrue("Wrong evaluation result;", 1f != evaluation.getF());
        TM2Logger.singleton(getClass()).info(evaluation);
    }

}

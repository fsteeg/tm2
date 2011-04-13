package com.quui.tm2.agents;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.Serializable;
import java.util.List;

import org.junit.Test;

import com.quui.tm2.Agent;
import com.quui.tm2.Annotation;
import com.quui.tm2.AnnotationReader;
import com.quui.tm2.util.AmasLogger;
import com.quui.tm2.util.Preferences;
import com.quui.tm2.util.Preferences.Default;

public class AnnotationReaderTest {
    
    public final static String location = Preferences.get(Default.ROOT)
            + "../files/Gazetteer_And_Others_Result.xml";

    @Test
    public void gazetteerAnnotations() {
        test(Gazetteer.class, String.class);
    }

    @Test
    public void counterAnnotations() {
        test(Counter.class, Integer.class);
    }

    private <T extends Comparable<T> & Serializable> void test(
            Class<? extends Agent<?, T>> agentClass, Class<T> annotationClass) {
        List<Annotation<T>> annotations = new AnnotationReader(location)
                .readAnnotations(agentClass);
        assertTrue("Reader did not return any annotations;", annotations.size() > 0);
        AmasLogger.singleton(getClass()).info(
                "We have " + annotations.size() + " annotation for " + agentClass);
        for (Annotation<T> annotation : annotations) {
            assertTrue("Annotation start and end positions are not correct;", annotation.getStart()
                    .intValue() < annotation.getEnd().intValue());
            assertNotNull(annotation.getValue());
            assertEquals("Annotation value has wrong type;", annotationClass, annotation.getValue()
                    .getClass());
        }
    }

}

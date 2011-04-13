package com.quui.tm2.agents;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;

import com.quui.tm2.Agent;
import com.quui.tm2.Annotation;
import com.quui.tm2.AnnotationReader;
import com.quui.tm2.AnnotationWriter;
import com.quui.tm2.ImmutableAnnotation;
import com.quui.tm2.agents.Counter;
import com.quui.tm2.agents.Gazetteer;
import com.quui.tm2.util.AmasLogger;
import com.quui.tm2.util.Preferences;
import com.quui.tm2.util.Preferences.Default;

public class AnnotationReadWriteTest {

    public final static String location = Preferences.get(Default.ROOT) + "../files/test";

    @Test
    public void stringAnnotations() {
        test(Gazetteer.class, "String1", "String2");
    }

    @Test
    public void integerAnnotations() {
        test(Counter.class, 1, 2);
    }

    private <T extends Comparable<T> & Serializable> void test(
            Class<? extends Agent<?, T>> agentClass, T t1, T t2) {

        Annotation<T> a1 = ImmutableAnnotation.getInstance(null, t1, 0, 1);
        Annotation<T> a2 = ImmutableAnnotation.getInstance(null, t2, 1, 2);

        Map<Class<? extends Agent<?, ?>>, List<Annotation<?>>> map = new HashMap<Class<? extends Agent<?, ?>>, List<Annotation<?>>>();
        List<Annotation<?>> list = new ArrayList<Annotation<?>>();
        list.add(a1);
        list.add(a2);
        map.put(agentClass, list);
        AnnotationWriter writer = new AnnotationWriter(map, "yummy");
        writer.writeAnnotations(location);

        List<Annotation<T>> annotations = new AnnotationReader(
                location + ".xml").readAnnotations(agentClass);
        assertTrue("Reader did not return any annotations;",
                annotations.size() > 0);
        AmasLogger.singleton(getClass()).info(
                "We have " + annotations.size() + " annotation for "
                        + agentClass);
        for (Annotation<T> annotation : annotations) {
            assertTrue("Annotation start and end positions are not correct;",
                    annotation.getStart().intValue() < annotation.getEnd()
                            .intValue());
            assertNotNull(annotation.getValue());
        }

        T rt1 = annotations.get(0).getValue();
        T rt2 = annotations.get(1).getValue();

        assertEquals("Wrong annotation value after deserialization; ", t1, rt1);
        assertEquals("Wrong annotation value after deserialization; ", t2, rt2);

    }

}

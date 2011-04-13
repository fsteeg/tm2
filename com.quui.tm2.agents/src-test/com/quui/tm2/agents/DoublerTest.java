package com.quui.tm2.agents;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import com.quui.tm2.Annotation;
import com.quui.tm2.ImmutableAnnotation;

public class DoublerTest {

    @Test
    public void testProcess() {
        Annotation<Integer> input = ImmutableAnnotation.getInstance(null, 2, 0, 1);
        Doubler d = new Doubler();
        List<Annotation<Integer>> inList = new ArrayList<Annotation<Integer>>();
        inList.add(input);
        List<Annotation<Integer>> out = d.process(inList);
        for (Annotation<Integer> a : out) {
            assertEquals("Doubler did not double;", input.getValue()
                    .doubleValue() * 2, a.getValue().doubleValue(), 1e-9);
        }
    }
}

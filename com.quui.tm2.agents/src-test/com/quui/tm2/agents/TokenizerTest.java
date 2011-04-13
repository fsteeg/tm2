package com.quui.tm2.agents;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import com.quui.tm2.Annotation;
import com.quui.tm2.ImmutableAnnotation;

public class TokenizerTest {

    @Test
    public void testProcess() {
        String text = "Hi there, this is a short test!";
        Annotation<String> input = ImmutableAnnotation.getInstance(null, text, 0, text
                .length());
        Tokenizer t = new Tokenizer();
        List<Annotation<String>> inList = new ArrayList<Annotation<String>>();
        inList.add(input);
        List<Annotation<String>> out = t.process(inList);
        for (Annotation<String> a : out) {
            // Assert that the annotation value corresponds with the signal:
            assertEquals(
                    "Annotation does not point to the correct part in the signal!",
                    a.getValue(), text.substring(a.getStart().intValue(), a
                            .getEnd().intValue()));
        }

    }

}

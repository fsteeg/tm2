package com.quui.tm2.agents;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.BeforeClass;
import org.junit.Test;

import com.quui.tm2.Annotation;
import com.quui.tm2.Retrieval;
import com.quui.tm2.ui.ConsoleRetrieval;
import com.quui.tm2.util.Preferences;
import com.quui.tm2.util.Preferences.Default;

public class RetrievalTest {
    private static Retrieval r;

    @BeforeClass
    public static void setup() {
        r = new Retrieval(Preferences.get(Default.ROOT)
                + "../files/Gazetteer_And_Others_Result.xml");
    }

    @Test
    public void listAgents() {
        List<String> agents = r.getAgents();
        assertNotNull("No agents retrieved;", agents);
        assertTrue("No agents retrieved;", agents.size() > 0);
        for (String ag : agents) {
            System.out.println(ag);
            List<Annotation<String>> annotations = ConsoleRetrieval.getAnnotations(ag, r);
            assertNotNull("No annotations retrieved;", annotations);
            assertTrue("No annotations retrieved;", annotations.size() > 0);
            for (Annotation<String> an : annotations) {
                System.out.println("\t" + an);
            }
        }
    }

    @Test
    public void search() {
        /*
         * Search in the results of Gazetteer for organization keys and return corresponding results
         * of the tokenizer (i.e. find words that are organization keys):
         */
        // List<String> result1 = r.searchValues(Gazetteer.class, "org_key",
        // Tokenizer.class);
        List<Annotation<String>> result0 = r.search(Gazetteer.class, "organization_nouns",
                Tokenizer.class);
        /* For a CLI or GUI usgae, we would need to give strings as parameters: */
        List<Annotation<String>> result2 = ConsoleRetrieval.search(Gazetteer.class.getName(),
                "organization_nouns", Tokenizer.class.getName(), r);
        /* Now, the first thing we'd expect is those give equal results: */
        assertEquals("Class-based search and String-based search returned different results;",
                result0, result2);
        assertTrue("Empty retrieval result;", result2.size() > 0);
        for (Annotation<String> string : result2) {
            System.out.println(string);
        }
    }

}

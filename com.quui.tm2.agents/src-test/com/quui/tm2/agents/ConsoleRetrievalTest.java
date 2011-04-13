package com.quui.tm2.agents;

import java.io.File;
import java.net.MalformedURLException;

import org.junit.Test;

import com.quui.tm2.ui.ConsoleRetrieval;
import com.quui.tm2.util.Preferences;
import com.quui.tm2.util.Preferences.Default;

public class ConsoleRetrievalTest {
    private static String ROOT;

    static {
        try {
            ROOT = new File(".").toURI().toURL().toString();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }

    @Test public void retrieval1() {
        ConsoleRetrieval.main(new String[] {
                Preferences.get(Default.RESULT), "list" });
    }

    @Test public void retrieval2() {
        ConsoleRetrieval.main(new String[] {
                Preferences.get(Default.RESULT), "list",
                Gazetteer.class.getName() });
    }

    @Test public void retrieval3() {
        ConsoleRetrieval.main(new String[] {
                Preferences.get(Default.RESULT), "find", "org_key",
                Gazetteer.class.getName(),
                Tokenizer.class.getName() });
    }

    @Test public void retrieval4() {
        ConsoleRetrieval.main(new String[] {
                Preferences.get(Default.RESULT), "find", "mining",
                Tokenizer.class.getName(),
                Gazetteer.class.getName() });
    }
}

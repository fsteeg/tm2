package com.quui.tm2.agents;

import java.math.BigInteger;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import com.quui.tm2.Agent;
import com.quui.tm2.Annotation;
import com.quui.tm2.ImmutableAnnotation;
import com.quui.tm2.util.FileIO;
import com.quui.tm2.util.Preferences;

/**
 * @author Fabian Steeg (fsteeg)
 */
public final class Corpus implements Agent<String, String> {

    private URL location;
     public String text;

    /**
     * {@inheritDoc}
     * @see com.quui.tm2.Agent#process(java.util.List)
     */
    public List<Annotation<String>> process(final List<Annotation<String>> input) {
        List<Annotation<String>> texts = new ArrayList<Annotation<String>>();
        texts.add(ImmutableAnnotation.getInstance(getClass(), text,
                BigInteger.ZERO, BigInteger.valueOf(text.length())));
        return new ArrayList<Annotation<String>>(texts);
    }

    /**
     * @param location The corpus data location
     */
    public Corpus(final String location) {
        try {
            this.location = new URL(location);
            text = FileIO.read(location.toString()).trim();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Use the default corpus location.
     */
    public Corpus() {
        this(Preferences.get(Preferences.Default.CORPUS));
    }

    /**
     * @return The location of the corpus data.
     */
    public URL getLocation() {
        return location;
    }

    /**
     * {@inheritDoc}
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return location.toString();
    }
}

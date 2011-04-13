package com.quui.tm2;

import java.io.Serializable;
import java.util.List;

/**
 * Interface for an agent. This is basically a Strategy Interface (cf. Bloch 2008, Item 21).
 * @author fsteeg
 * @param <I> The type of the input annotations, must be comparable and serializable
 * @param <O> the type of the output annotations, must be comparable and serializable
 */
public interface Agent<I extends Comparable<I> & Serializable, O extends Comparable<O> & Serializable> {
    /**
     * @param input The input annotations of type I
     * @return Returns a list of annotations of type O
     */
    List<Annotation<O>> process(List<Annotation<I>> input);
}

package com.quui.tm2;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Skeletal implementation of the Agent interface (cf. Bloch 2008, Item 18)
 * <p/>
 * TODO: document inheritance (Bloch 2008, Item 17)
 * @author Fabian Steeg (fsteeg)
 * @param <I> The input type
 * @param <O> The output type
 */
public abstract class AbstractAgent<I extends Comparable<I> & Serializable, O extends Comparable<O> & Serializable>
        implements Agent<I, O> {

    /**
     * @param input The input to process, of type I
     * @return Returns the result of processing I, of type O
     */
    public abstract O process(I input);

    /**
     * @param input The input annotations
     * @return Returns the output annotationscreated for the input
     * @see com.quui.tm2.Agent#process(java.util.List)
     */
    public final List<Annotation<O>> process(final List<Annotation<I>> input) {
        List<Annotation<O>> result = new ArrayList<Annotation<O>>();
        for (Annotation<I> annotation : input) {
            @SuppressWarnings( "unchecked" )// We are AbstractAgent<I,O>, should be safe
            Class<? extends AbstractAgent<I, O>> author = (Class<? extends AbstractAgent<I, O>>) getClass();
            Annotation<O> resultAnnotation = ImmutableAnnotation.getInstance(author, // TODO not convenient for API!
                    process(annotation.getValue()), annotation.getStart(), annotation.getEnd());
            result.add(resultAnnotation);
        }
        return result;
    }
}

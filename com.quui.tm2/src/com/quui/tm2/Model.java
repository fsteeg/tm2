package com.quui.tm2;

import java.util.List;

/**
 * A model is created for data of type D and info of type I.
 * @param <D> The data type
 * @param <I> The info type
 * @author Fabian Steeg (fsteeg)
 */
public interface Model<D extends Comparable<D>, I extends Comparable<I>> {
    /**
     * @param data The data input for this model
     * @param info The info associated with the data
     * @return The model created for the given data and the associated info
     */
    Model<D, I> train(List<Annotation<D>> data, List<Annotation<I>> info);
}

package com.quui.tm2;

import java.io.Serializable;
import java.util.List;

/**
 * A model is created for data of type D and info of type I.
 * @param <D> The data type
 * @param <I> The info type
 * @author Fabian Steeg (fsteeg)
 */
public interface ModelBackup<D extends Comparable<D> & Serializable, I extends Comparable<I> & Serializable> {
    /**
     * @param data The data input for this model
     * @param info The info associated with the data
     * @return The model created for the given data and the associated info
     */
    ModelBackup<D, I> synthesize(List<Annotation<D>> data, List<Annotation<I>> info);
}

package com.quui.tm2;

import java.io.Serializable;
import java.math.BigInteger;
import java.net.URL;

/**
 * The interchange format of a text mining application: an annotation of data
 * with meta-data.
 * @author Fabian Steeg (fsteeg)
 * @param <T>
 */
public interface Annotation<T> extends Comparable<Annotation<T>>, Serializable {
    /**
     * @return The authoring agent of this annotation.
     */
    Class<? extends Agent<?, ?>> author();
    /**
     * @return The data referenced by this annotation.
     */
    URL getData();

    /**
     * @return Returns the annotation value
     */
    T getValue();

    /**
     * @return Returns the annotation's end index
     */
    BigInteger getEnd();

    /**
     * @return Returns the annotation's start index
     */
    BigInteger getStart();

}

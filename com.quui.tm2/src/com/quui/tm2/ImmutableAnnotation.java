package com.quui.tm2;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.math.BigInteger;
import java.net.URL;

import org.apache.commons.lang.NotImplementedException;

/**
 * The interchange format of a text mining application: an annotation of data with meta-data.
 * <p/>
 * This class is immutable:
 * <ol>
 * <li>It can't be extended: it is final and has just a private constructor</li>
 * <li>All fields are final</li>
 * <li>All fields are private</li>
 * <li>On construction, the BigInteger objects are secured such that a new 'real' BigInteger is
 * created from they bytes of the given values</li>
 * <li>Parameters and return values of accessors are defensively copied</li>
 * </ol>
 * <p/>
 * Annotations therefore are inherently thread-safe and can be shared and reused freely (cf. Bloch
 * 2008, i.e. Effective Java, 2nd Ed., Item 15)
 * @author Fabian Steeg (fsteeg)
 * @param <T> The type of the meta-data annotating some data
 */
public final class ImmutableAnnotation<T extends Comparable<T>> implements Annotation<T> {
    /***/
    private static final long serialVersionUID = -4685629518898888821L;
    /*
     * TODO As the constructor is private, the class is final, but should it be? TODO Should an
     * annotation hold some kind of reference to the actual signal?
     */
    /***/
    private final T value;
    /***/
    private final BigInteger start;
    /***/
    private final BigInteger end;
    private Class<? extends Agent<?, ?>> author;
    /***/
    private static final int HASH_FACTOR = 31;
    /***/
    private static final int HASH_START = 17;

    /**
     * We make the constructor private and use a factory method to avoid redundant generics
     * specification.
     * @param <T> The annotation value type
     * @param author The class of the agent authoring this annotation
     * @param value The annotation value
     * @param start The start index
     * @param end The end index
     * @return Returns an annotation typed to the type of the value, with the specified range
     */
    public static <T extends Comparable<T>> Annotation<T> getInstance(
    		//TODO should be Agent<?,T>
            final Class<? extends Agent<?, ?>> author, final T value, final BigInteger start,
            final BigInteger end) {
        return new ImmutableAnnotation<T>(author, value, start, end);
    }

    /**
     * Convenience factory method for small texts, using int values for indices.
     * @param <T> The annotation value type
     * @param author The class of the agent authoring this annotation
     * @param value The annotation value
     * @param start The start index
     * @param end The end index
     * @return Returns an annotation typed to the type of the value, with the specified range
     */
    public static <T extends Comparable<T>> ImmutableAnnotation<T> getInstance(
            final Class<? extends Agent<?, ?>> author, final T value, final int start, final int end) {
        return new ImmutableAnnotation<T>(author, value, BigInteger.valueOf(start),
                BigInteger.valueOf(end));
    }

    /**
     * @param value The value this annotation should represent
     * @param start The starting index in the signal (inclusive)
     * @param end The ending index in the signal (exclusive)
     */
    private ImmutableAnnotation(final Class<? extends Agent<?, ?>> author, final T value,
            final BigInteger start, final BigInteger end) {
        this.author = author;
        this.value = value;// newInstance(value);
        this.start = newInstance(start);
        this.end = newInstance(end);
    }

    public ImmutableAnnotation(Annotation<T> annotation) {
        this(annotation.author(), annotation.getValue(), annotation.getStart(), annotation.getEnd());
    }

    /**
     * @param big The BigInteger to copy
     * @return Returns a defensive copy safely non-mutable (real) BigInteger, not any subclass
     */
    private BigInteger newInstance(final BigInteger big) {
        return new BigInteger(big.toByteArray());
    }

    /**
     * @return Returns the annotation value
     */
    public T getValue() {
        return value;// newInstance(value);
    }

    /**
     * @param val The value to create a new instance for
     * @return Returns a new reflective instance for val
     */
    private T newInstance(final T val) {
        /*
         * It's a T, so casting the class to a class of T can't go wrong... or am I missing
         * something?
         */
        @SuppressWarnings( "unchecked" ) Class<T> clazz = (Class<T>) val.getClass();
        try {
            /* As elsewhere, we assume a String constructor */
            Constructor<T> constructor = clazz.getConstructor(String.class);
            /* That will create a valid instance from a toString representation */
            T instance = constructor.newInstance(val.toString());
            /*
             * TODO We need an early place to check if an agent (or an annotation, interaction...
             * maybe inside an experiment) produces usable values. Makes me wonder about using an
             * abstract class instead of a constructor again...
             */
            return instance;
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (SecurityException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * @return Returns the annotation's end index
     */
    public BigInteger getEnd() {
        return newInstance(end);
    }

    /**
     * @return Returns the annotation's start index
     */
    public BigInteger getStart() {
        return newInstance(start);
    }

    /**
     * Clients should not use equals to compare annotations, as it is not type-safe. Rather use
     * compareTo directly, which will ensure only comparable types are compared
     * @return Returns true if this annotation equals obj
     * @param obj The object to compare this annotation to
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(final Object obj) {
        /* If it ain't no annotation at all it ain't equal */
        if (!(obj instanceof ImmutableAnnotation<?>)) {
            return false;
        }
        /*
         * The actual class cast exception won't be thrown from here, but from the compare to
         * method; we don't catch it as it seems to be just what we want to tell a client who calls
         * equals on annotations of different types. (TODO as opposed to lists of different types...
         * an experienced Java programmer might expect them to be equal, as lists are, but equals
         * and compareTo should be consistent... And just because generics are reified in current
         * Java does not make annotations of different types equal)
         */
        Annotation<?> a = (Annotation<?>) obj;
        if (!(this.getValue().getClass().equals(a.getValue().getClass()))) {
            return false;
        }
        @SuppressWarnings( "unchecked" ) ImmutableAnnotation<T> castedAnnotation = (ImmutableAnnotation<T>) obj;
        /* Consistent with compareTo: */
        return this.compareTo(castedAnnotation) == 0;
    }

    /**
     * Implemented as suggested in Bloch 2008, Item 9.
     * @return Returns a hash code for this annotation
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        int result = HASH_START;
        result = HASH_FACTOR * result + value.hashCode();
        result = HASH_FACTOR * result + start.hashCode();
        result = HASH_FACTOR * result + end.hashCode();
        return result;
    }

    /**
     * @return Returns a user-readable representation of this annotation, containing the value and
     *         the start and end indices. The exact format is subject to change. If you need the
     *         individual properties of this annotation, use the appropriate accessors.
     * @see java.lang.Object#toString()
     */
    public String toString() {
        return "'" + value + "' from '" + start + "' to '" + end + "'";
    }

    /**
     * Compares this annotation to the other annotation according to three ordered criteria:
     * <ol>
     * <li>Actual value</li>
     * <li>Start index</li>
     * <li>End index</li>
     * </ol>
     * This means annotations with different values will be sorted according to these values, if
     * they have the same value they will be sorted according to their start index. If they have
     * both identical values and identical start indices, they will be sorted according to their end
     * indices
     * @param other The annotation to compare this to
     * @return Returns a negative int if <code>this</code> is smaller than <code>other</code>, 0 if
     *         they are equal and a positive int if <code>this</code> is larger than
     *         <code>other</code>
     * @see java.lang.Comparable#compareTo(java.lang.Object)
     */
    public int compareTo(final Annotation<T> other) {
        /*
         * TODO it might make more sense to give precedence to the indices (start, value, end?). Do
         * we actually need the ordering? Can we make it configurable, e.g. supplying different
         * comparators in an enum...
         */
        /* First ordering criterion: the actual value of this annotation */
        if (this.value.equals(other.getValue())) {
            /* Second ordering criterion: start index */
            if (this.start.equals(other.getStart())) {
                /* Third ordering criterion: end index */
                if (this.end.equals(other.getEnd())) {
                    return 0;
                }
                return this.end.compareTo(other.getEnd());
            }
            return this.start.compareTo(other.getStart());
        }
        /*
         * This can cause a ClassCastException if the other T is not the same as this T, which
         * happens if this was called with a casted annotation, as when calling this method from the
         * equals method, which must do the unsafe cast
         */
        return this.value.compareTo(other.getValue());
    }

    /**
     * @param other The annotation to compare with <code>this</code>
     * @return Returns true if this annotation and the other annotation annotate the same area, else
     *         false
     */
    public boolean covers(final ImmutableAnnotation<?> other) {
        return this.start.equals(other.start) && this.end.equals(other.end);
    }

    /**
     * {@inheritDoc}
     * @see com.quui.tm2.Annotation#getData()
     */
    public URL getData() {
        throw new NotImplementedException();
    }

    /**
     * {@inheritDoc}
     * @see com.quui.tm2.Annotation#author()
     */
    public Class<? extends Agent<?, ?>> author() {
        return author;
    }
}

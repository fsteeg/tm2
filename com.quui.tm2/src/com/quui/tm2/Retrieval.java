package com.quui.tm2;

import java.io.Serializable;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;

import com.quui.tm2.util.FileIO;

/**
 * @author Fabian Steeg (fsteeg)
 */
public final class Retrieval {
    /***/
    private AnnotationReader reader;

    /**
     * @param location The location of the XML file containing the annotations written by an
     *        experiment
     */
    public Retrieval(final String location) {
        reader = new AnnotationReader(location);
    }

    public Retrieval(Experiment experiment) throws MalformedURLException {
        // this(new URL(experiment.getOutputAnnotationLocation()).toString());
        this(experiment.getOutputAnnotationLocation());
    }

    /**
     * @return Returns a list of agent names that wrote annotations into the file associated with
     *         this retrieval
     */
    public List<String> getAgents() {
        return reader.readAgents();
    }

    /**
     * @param <S> The type of annotations to search in
     * @param <R> The type of annotations to return results for
     * @param search The agent that produced the annotations to search in
     * @param value The value to search for
     * @param from The agent that produced the annotations to return the result from
     * @return Returns a List of annotations produced by the <code>from</code> agent, which
     *         correspond to the annotations produced by <code>search</code> with the specified
     *         value
     */
    public <S extends Comparable<S> & Serializable, R extends Comparable<R> & Serializable> List<Annotation<R>> search(
            final Class<? extends Agent<?, S>> search, final S value,
            final Class<? extends Agent<?, R>> from) {
        List<Annotation<S>> annotationToSearchIn = reader.readAnnotations(search);
        List<Annotation<S>> hits = new ArrayList<Annotation<S>>();
        // TODO use binary search?
        for (Annotation<S> annotation : annotationToSearchIn) {
            if (annotation.getValue().equals(value)) {
                hits.add(annotation);
            }
        }
        List<Annotation<R>> result = new ArrayList<Annotation<R>>();
        // TODO do this more efficiently:
        List<Annotation<R>> annotationsToReturnFrom = reader.readAnnotations(from);
        for (Annotation<R> ra : annotationsToReturnFrom) {
            for (Annotation<S> ha : hits) {
                // TODO add to interface?
                if (((ImmutableAnnotation<R>) ra).covers((ImmutableAnnotation<S>) ha)) {
                    result.add(ra);
                }
            }
        }
        return result;
    }

    /**
     * @param <T> The type of the annotations
     * @param agent The agent
     * @return Returns the annotations produced by the agent
     */
    public <T extends Comparable<T> & Serializable> List<Annotation<T>> annotationsOf(
            final Class<? extends Agent<?, T>> agent) {
        return reader.readAnnotations(agent);
    }

    /**
     * @return Returns the actual data, the XML string containing the annotations
     */
    public String getData() {
        // TODO should this method actually exist?
        String data = FileIO.read(reader.getData());
        return data;
    }

    /**
     * @param <T> The annotation type
     * @param annotations The annotations to get the values of
     * @return Returns a list containing the values of the annotations
     */
    public static <T extends Comparable<T>> List<T> valuesOf(final List<Annotation<T>> annotations) {
        List<T> result = new ArrayList<T>();
        for (Annotation<T> a : annotations) {
            result.add(a.getValue());
        }
        return result;
    }

    /**
     * @return The data location TODO: remove?
     */
    public String getDataLocation() {
        return reader.getData();

    }

    public <T extends Comparable<T> & Serializable> Annotation<T> annotationCorresponding(
            Annotation<?> a, Class<? extends Agent<?, T>> by) {
        List<Annotation<T>> candidates = annotationsOf(by);
        for (Annotation<T> annotation : candidates) {
            if (Annotations.fullOverlap(annotation, a)) {
                return annotation;
            }
        }
        return null;
    }
}

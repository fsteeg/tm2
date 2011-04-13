package com.quui.tm2;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import com.quui.tm2.util.AmasLogger;

/**
 * Synthesis of data and info into a model.
 * @param <D> The data type
 * @param <I> The info type
 * @author Fabian Steeg (fsteeg)
 */
public interface Synthesis<D extends Comparable<D> & Serializable, I extends Comparable<I> & Serializable> {

    /**
     * Run this synthesis using the given blackboard.
     * @param blackboard The blackboard to perform this synthesis on
     * @return The unchanged blackboard (TODO: should this be the model?)
     */
    Map<Class<? extends Agent<?, ?>>, List<Annotation<?>>> run(
            Map<Class<? extends Agent<?, ?>>, List<Annotation<?>>> blackboard);

    /** @return The agents supplying the data to synthesize with the info into a model */
    List<Agent<?, D>> data();

    /** @return The agents supplying the info to synthesize with the data into a model */
    List<Agent<?, I>> info();

    /**
     * @return The model that will be created based on the data and info
     *         TODO: should this be returned via run()?
     */
    Model<D, I> model();

    /**
     * Builder for an immutable synthesis.
     * @author Fabian Steeg (fsteeg)
     */
    public static final class Builder<D extends Comparable<D> & Serializable, I extends Comparable<I> & Serializable> {
        private List<Agent<?, D>> data = new ArrayList<Agent<?, D>>();
        private List<Agent<?, I>> info = new ArrayList<Agent<?, I>>();
        private final Model<D, I> model;

        /**
         * A builder for a synthesis of data and info into a model.
         * @param model The model to synthesize data and info into.
         */
        public Builder(final Model<D, I> model) {
            this.model = model;
        }

        /**
         * @return The immutable synthesis instance
         */
        public Synthesis<D, I> build() {
            return new ImmutableSynthesis<D, I>(this);
        }

        /**
         * @param agent An agent contributing to the synthesized data
         * @return The builder, for cascaded calls
         */
        public Builder<D, I> data(final Agent<?, D> agent) {
            data.add(agent);
            return this;
        }

        /**
         * @param agent An agent contributing to the synthesized info
         * @return The builder, for cascaded calls
         */
        public Builder<D, I> info(final Agent<?, I> agent) {
            info.add(agent);
            return this;
        }

    }

    /**
     * Package-private synthesis implementation, instantiated by the builder.
     * @author Fabian Steeg (fsteeg)
     * @param <D> The data type
     * @param <I> The info type
     */
    class ImmutableSynthesis<D extends Comparable<D> & Serializable, I extends Comparable<I> & Serializable>
            implements Synthesis<D, I> {
        private final List<Agent<?, D>> data;
        private final List<Agent<?, I>> info;
        private final Model<D, I> model;

        ImmutableSynthesis(final Synthesis.Builder<D, I> builder) {
            this.data = builder.data;
            this.info = builder.info;
            this.model = builder.model;
        }

        /**
         * {@inheritDoc}
         * @see com.quui.tm2.Synthesis#run(java.util.Map)
         */
        public Map<Class<? extends Agent<?, ?>>, List<Annotation<?>>> run(
                final Map<Class<? extends Agent<?, ?>>, List<Annotation<?>>> blackboard) {
            if (!canTrain(blackboard)) {
                return null;
            }
            AmasLogger.singleton().info("Running synthesis with blackboard: " + blackboard);

            for (int i = 0; i < data.size(); i++) {
                model.train(
                        ImmutableAnalysis.typedAnnotations(data.get(i), blackboard),
                        ImmutableAnalysis.typedAnnotations(info.get(i), blackboard));
            }

            return blackboard;

        }

        /**
         * {@inheritDoc}
         * @see com.quui.tm2.Synthesis#info()
         */
        public List<Agent<?, I>> info() {
            return Collections.unmodifiableList(info);
        }

        /**
         * {@inheritDoc}
         * @see com.quui.tm2.Synthesis#data()
         */
        public List<Agent<?, D>> data() {
            return Collections.unmodifiableList(data);
        }

        /**
         * {@inheritDoc}
         * @see com.quui.tm2.Synthesis#model()
         */
        public Model<D, I> model() {
            return model;
        }

        private boolean canTrain(
                final Map<Class<? extends Agent<?, ?>>, List<Annotation<?>>> blackboard) {
            for (int i = 0; i < data.size(); i++) {
                Agent<?, D> dat = data.get(i);
                Agent<?, I> inf = info.get(i);
                if (!(blackboard.containsKey(dat.getClass()) && blackboard.containsKey(inf.getClass()))) {
                    AmasLogger.singleton().debug("Not training with blackboard: " + blackboard);
                    return false;
                }
            }
            AmasLogger.singleton().debug("Training with blackboard: " + blackboard);
            return true;
        }

		@Override
		public Class<I> getInfoTypeClass() {
			return AnnotationReader.getAgentClass((Class<? extends Agent<?, I>>) info().get(0).getClass());
		}

		@Override
		public Class<D> getDataTypeClass() {
			return AnnotationReader.getAgentClass((Class<? extends Agent<?, D>>) data().get(0).getClass());
		}

    }

	Class<I> getInfoTypeClass();

	Class<D> getDataTypeClass();

}

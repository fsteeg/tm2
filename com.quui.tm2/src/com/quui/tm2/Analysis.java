package com.quui.tm2;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;

import com.quui.tm2.util.TM2Logger;

/**
 * @author Fabian Steeg (fsteeg)
 * @param <T>
 *            The annotation value type exchanged in this interaction
 */
public interface Analysis<T extends Comparable<T> & Serializable> {

	/**
	 * @return Returns an unmodifiable view on the source agents of this
	 *         interaction
	 */
	List<Agent<?, T>> sources();

	/**
	 * @return Returns an unmodifiable view on the target agents of this
	 *         interaction
	 */
	List<Agent<T, ?>> targets();

	/**
	 * Execute this interaction, using the given blackboard.
	 * 
	 * @param blackboard
	 *            The blackboard to use to retrieve the input annotations for
	 *            this interaction and to store the result annotations of this
	 *            interaction
	 * @return The result of applying this interaction using the given
	 *         blackboard: the altered blackboard (or a new blackboard with the
	 *         results added)
	 */
	Map<Class<? extends Agent<?, ?>>, List<Annotation<?>>> run(
			final Map<Class<? extends Agent<?, ?>>, List<Annotation<?>>> blackboard);

	/**
	 * Builder for immutable interactions.
	 * 
	 * @author Fabian Steeg (fsteeg)
	 */
	public static final class Builder<T extends Comparable<T> & Serializable> {
		/** The source agents. */
		final List<Agent<?, T>> sources = new ArrayList<Agent<?, T>>();
		/** The target agents. */
		final List<Agent<T, ?>> targets = new ArrayList<Agent<T, ?>>();

		/**
		 * @return The ready interaction
		 */
		public Analysis<T> build() {
			return new ImmutableAnalysis<T>(this);
		}

		/**
		 * @param agent
		 *            The agent to add as a source of this interaction
		 * @return This builder, for cascaded calls
		 */
		public Builder<T> source(final Agent<?, T> agent) {
			sources.add(agent);
			return this;
		}

		/**
		 * @param agent
		 *            The agent to add as a target of this interaction
		 * @return This builder, for cascaded calls
		 */
		public Builder<T> target(final Agent<T, ?> agent) {
			targets.add(agent);
			return this;
		}
	}

	Class<T> getTypeClass();

}

/**
 * <p/>
 * This class is immutable, and can therefore be shared and reused freely and
 * concurrently.
 * <p/>
 * 
 * @author Fabian Steeg (fsteeg)
 * @param <T>
 *            The annotation value type exchanged in this interaction
 */
final class ImmutableAnalysis<T extends Comparable<T> & Serializable>
		implements Analysis<T> {
	/** The source agents. */
	private final List<Agent<?, T>> sources;
	/** The target agents. */
	private final List<Agent<T, ?>> targets;

	/**
	 * @param builder
	 *            The builder to construct an interaction from
	 */
	ImmutableAnalysis(final Builder<T> builder) {
		this.sources = builder.sources;
		this.targets = builder.targets;
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see com.quui.tm2.Analysis#sources()
	 */
	public synchronized List<Agent<?, T>> sources() {
		return Collections.unmodifiableList(sources);
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see com.quui.tm2.Analysis#targets()
	 */
	public synchronized List<Agent<T, ?>> targets() {
		return Collections.unmodifiableList(targets);
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see com.quui.tm2.Analysis#run(java.util.Map)
	 */
	public synchronized Map<Class<? extends Agent<?, ?>>, List<Annotation<?>>> run(
			final Map<Class<? extends Agent<?, ?>>, List<Annotation<?>>> blackboard) {
		List<Annotation<T>> input = new ArrayList<Annotation<T>>();

		/* If we have sources, collect their outputs from the blackboard: */
		for (Agent<?, T> source : sources()) {
			input.addAll(ImmutableAnalysis.typedAnnotations(source, blackboard));
		}
		/*
		 * If we found neither a corpus nor source annotations, something is
		 * wrong:
		 */
		if (input.size() == 0 && sources.size() > 0) {
			throw new IllegalStateException("We need results of " + sources
					+ " but have nothing on the blackboard;");
		}
		/*
		 * If we have collected the input data for the targets, they can process
		 * it concurrently:
		 */
		ExecutorService exec = Executors
				.newFixedThreadPool(1/* targets().size() */);
		for (Agent<T, ?> target : targets()) {
			exec.execute(new InteractionRunnable<T>(input, target, blackboard));
		}
		shutdownAndWait(exec);
		return blackboard;
	}

	/**
	 * @return Returns a concise human-readable representation of this
	 *         interaction. The exact format is subject to change. If you need
	 *         the individual elements, use the accessors instead {@inheritDoc}
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		List<Agent<?, T>> s = this.sources;
		List<Agent<T, ?>> t = this.targets;
		/* Sources */
		for (Agent<?, T> agent : s) {
			builder.append(agent.getClass().getName()).append(" ");
		}
		/*
		 * The "operator" between sources and target: one-to-one (---),
		 * many-to-one (>--), one-to-many (--<), e.g. Tokenizer --- Gazetteer
		 * Tokenizer1 Toeknizer2 >-- Gazetteer Tokenizer --< Gazetteer Counter
		 */
		builder.append(s.size() > 1 ? ">" : "-").append("-")
				.append(t.size() > 2 ? "<" : "- ");
		/* Targets */
		for (Agent<T, ?> agent : t) {
			builder.append(agent.getClass().getName()).append(" ");
		}
		return builder.toString().trim();
	}

	// private API:

	/**
	 * @param agent
	 *            The agent class
	 * @param blackboard
	 *            The blackboard to use
	 * @return Returns the annotations produced by the agent found on the
	 *         blackboard
	 */
	static <T extends Comparable<T> & Serializable> List<Annotation<T>> typedAnnotations(
			final Agent<?, T> agent,
			final Map<Class<? extends Agent<?, ?>>, List<Annotation<?>>> blackboard) {
		/*
		 * The blackboard is a heterogeneous container for non-reifiable
		 * elements. As of Java 1.6, there is no way to implement this
		 * completely type-safe (cf. Bloch 2008 and Neal Gafter's blog on
		 * limitations on super type tokens). Instead of implementing a
		 * "Typesafe Heterogenous Container", which will be unsafe for reifiable
		 * elements anyway, we use the unbounded wirldcard and cast below.
		 */
		List<Annotation<?>> produced = blackboard.get(agent.getClass());
		if (produced == null) {
			throw new IllegalStateException("We need results of "
					+ agent.getClass() + " but have nothing on the blackboard;");
		}
		List<Annotation<T>> annotations = new ArrayList<Annotation<T>>();
		for (Annotation<?> o : produced) {
			try {
				/*
				 * This cast should actually be safe, because the annotations
				 * were produced by an Agent<?,T>, although this could still
				 * fail if they were incorrectly stored. But currently, this
				 * seems to be as far as we get in Java regarding heterogeneous
				 * container for non-reifiable elements. If generics were
				 * reified (read: when generics will be reified) we could use a
				 * typesafe heterogeneous container (which is almaost what we do
				 * anyway), cf. Bloch 2008, Effective Java, 2nd Ed., Item 29.
				 */
				@SuppressWarnings("unchecked")
				ImmutableAnnotation<T> typed = (ImmutableAnnotation<T>) o;
				annotations.add(typed);
			} catch (ClassCastException e) {
				IllegalStateException exec = new IllegalStateException(
						"Results of "
								+ agent.getClass().getSimpleName()
								+ " on the blackboard are not of the agent's output type;");
				exec.initCause(e);
				throw exec;
			}
		}
		return annotations;
	}

	/**
	 * @param exec
	 *            The service to shut down
	 */
	private void shutdownAndWait(final ExecutorService exec) {
		exec.shutdown();
		try {
			exec.awaitTermination(Long.MAX_VALUE, TimeUnit.SECONDS);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	@Override
	public Class<T> getTypeClass() {
		return AnnotationReader.getAgentClass((Class<? extends Agent<?, T>>) sources.get(0).getClass());
	}
}

/**
 * A runnable that executes an agent with a specified input, storing results on
 * the specified blackboard. The blackboard access is synchronized.
 * 
 * @author fsteeg
 * @param <T>
 *            The type of the input
 */
final class InteractionRunnable<T extends Comparable<T> & Serializable>
		implements Runnable {
	/***/
	private final List<Annotation<T>> input;
	/***/
	private final Logger logger = TM2Logger.singleton();
	/***/
	private final Map<Class<? extends Agent<?, ?>>, List<Annotation<?>>> blackboard;
	/***/
	private final Agent<T, ?> agent;

	/**
	 * @param input
	 *            The input to be processed by the agent
	 * @param target
	 *            The agent to use for processing the input
	 * @param blackboard
	 *            The blackboard to store the results of the agent on
	 */
	public InteractionRunnable(
			final List<Annotation<T>> input,
			final Agent<T, ?> target,
			final Map<Class<? extends Agent<?, ?>>, List<Annotation<?>>> blackboard) {
		this.input = Collections.unmodifiableList(input);
		this.agent = target;
		this.blackboard = blackboard;
	}

	/**
	 * @see java.lang.Runnable#run()
	 */
	public synchronized void run() {
		/*
		 * We cast the class of a typed agent <T,?> to the typed class <T,?>,
		 * this should never go wrong
		 */
		@SuppressWarnings("unchecked")
		Class<Agent<T, ?>> agentClass = (Class<Agent<T, ?>>) agent.getClass();
		logger.info(System.currentTimeMillis() + ": running agent: "
				+ agentClass.getSimpleName());
		/* TODO Is this really the best we get? */
		List<?> produced = agent.process(input);
		if (produced == null) {
			throw new NullPointerException(String.format(
					"Agent %s did not produce any annotations!", agent));
		}
		/* Remember the results: */
		List<Annotation<?>> annotations = new ArrayList<Annotation<?>>();
		for (Object o : produced) {
			annotations.add((Annotation<?>) o);
		}
		/*
		 * We store this agent's results in the map (we write it on the
		 * blackboard):
		 */
		blackboard.put(agentClass, annotations);
	}
}
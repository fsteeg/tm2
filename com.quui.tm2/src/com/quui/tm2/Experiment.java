package com.quui.tm2;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.quui.tm2.util.Preferences;
import com.quui.tm2.util.Preferences.Default;

/**
 * Interface to a TM2 experiment.
 * @author Fabian Steeg (fsteeg)
 */
public interface Experiment extends Runnable {
  /**
   * Builder for Experiment instances.
   * @author Fabian Steeg (fsteeg)
   */
  public static final class Builder {
    String name, root, agent, gold;
    final List<Analysis<?>> interactions = new ArrayList<Analysis<?>>();
    final List<Synthesis<?, ?>> syntheses = new ArrayList<Synthesis<?, ?>>();
    long time;

    /**
     * @return The finished experiment instance
     */
    public Experiment build() {
      return new SynchronizedExperiment(this);
    }

    /**
     * A builder representing an experiment that is completely configured using the default values
     * from the amas.properties file.
     */
    public Builder() {
      time = System.currentTimeMillis();
    }

    /**
     * @param name The experiment name
     * @return This builder, for cascaded calls.
     */
    public Builder name(final String name) {
      this.name = name;
      return this;
    }

    /**
     * @param root The experiment output root directory (URL)
     * @return This builder, for cascaded calls.
     */
    public Builder root(final String root) {
      this.root = root;
      return this;
    }

    /**
     * @param agent The fully qualified name of the agent whose results should be evaluated
     * @return This builder, for cascaded calls.
     */
    public Builder agent(final String agent) {
      this.agent = agent;
      return this;
    }

    /**
     * @param gold The location of the gold standard annotations file (URL)
     * @return This builder, for cascaded calls.
     */
    public Builder gold(final String gold) {
      this.gold = gold;
      return this;
    }

    /**
     * @param interaction The interaction to add to the experiment
     * @return This builder, for cascaded calls.
     */
    public Builder analysis(final Analysis<?> interaction) {
      // TODO not sure what this is -- corpus stuff?
      if (interactions.size() == 0) {
        @SuppressWarnings( "unchecked" ) Analysis<?> i = new Analysis.Builder().target(
            interaction.sources().get(0)).build();
        interactions.add(i);
      }
      this.interactions.add(interaction);
      return this;
    }

    /**
     * @param synthesis The synthesis to add to this experiment
     * @return This builder, for cascaded calls.
     */
    public Builder synthesis(final Synthesis<?, ?> synthesis) {
      this.syntheses.add(synthesis);
      return this;
    }
  }

  /**
   * @return Returns the location of the corpus data
   */
  String getCorpusLocation();

  /**
   * @return Returns the root location name to output results (this is neither a file nor a folder,
   *         it's the base name)
   */
  String getOutputRootName();

  /**
   * @return Returns the location of the result annotation file
   */
  String getOutputAnnotationLocation();

  /**
   * @return Returns the location of the documentation file
   */
  String getOutputDocumentationLocation();

  /**
   * @return Returns the interactions of this experiment
   */
  List<Analysis<?>> interactions();

  /**
   * @return Returns the title
   */
  String getTitle();

  /**
   * @return True, if this experiment is set up for evaluation
   */
  Boolean includesEvaluation();

  /**
   * @return The class of the agent whose results are evaluated
   */
  Class<?> getEvaluationAgent();

  /**
   * @return The location of the gold standard annotations file
   */
  String getGoldStandard();

  /**
   * @return The syntheses used in this experiment
   */
  List<Synthesis<?, ?>> syntheses();

  Evaluation getEvaluation();

  List<Annotation<?>> getAnnotations();

long getTime();
}

/**
 * This class is limited in mutability as far as possible: only the run method (which is
 * synchronized) will change the blackboard, which is not obtainable. The interactions are locked
 * when running each.
 * @author Fabian Steeg (fsteeg)
 */
final class SynchronizedExperiment implements Experiment {
  /***/
  private String outputLocation;
  /***/
  private String corpusLocation;
  /***/
  private String title;
  /**
   * The blackboard, the common data structure for the results of the different agents.
   */
  private final Map<Class<? extends Agent<?, ?>>, List<Annotation<?>>> blackboard; // FIXME
  // currently,
  // class
  // names
  // are
  // used,
  // cannot
  // use
  // multiple
  // instances
  /*
   * TODO I'd rather not use arrays in the public API... but I can't get it type-safe for lists
   * without... Could arrays be a solution for more type safety on the blackboard?
   */
  /**
   * This is a hetrongenous container of non-reifiable types... that is a problem
   */
  private final List<Analysis<?>> analyses;
  private boolean includesEvaluation;
  private Class<?> evaluationAgent;
  private String goldStandard;
  private String id;
  private List<Synthesis<?, ?>> syntheses;
private long took;

  public String toString() {
//    List<Agent<?, ?>> analysingAgents = new ArrayList<Agent<?, ?>>();
//    for (Analysis<?> a : analyses) {
//      analysingAgents.addAll(a.sources());
//      analysingAgents.addAll(a.targets());
//    }
    List<Agent<?, ?>> synthesizingAgents = new ArrayList<Agent<?, ?>>();
    for (Synthesis<?, ?> s : syntheses) {
      synthesizingAgents.addAll(s.info());
      synthesizingAgents.addAll(s.data());
    }

    List<Model<?, ?>> synthesizedModels = new ArrayList<Model<?, ?>>();
    for (Synthesis<?, ?> s : syntheses) {
      synthesizedModels.add(s.model());
    }
    Evaluation eval = getEvaluation();
    return String.format("| %s | \"details\":%s | %s | %s | %s | %s |", //  %s |
        eval == null ? "no evaluation included" : eval.getResultString(),
        getOutputDocumentationLocation()/*, analysingAgents*/, synthesizingAgents, synthesizedModels, (took / 1000000) + " ms.", getEvaluation()

    ).replaceAll("[\\[\\]]", "");
  }

  public Evaluation getEvaluation() {
    Evaluation eval = null;
    for (Synthesis<?, ?> t : syntheses) {
      if(eval != null) break;
      if (t.model() instanceof Evaluation) {// FIXME quick hack
        eval = (Evaluation) t.model(); // FIXME won't compile on OpenJDK
        System.out.println("Found evaluation (in synthesis): " + eval.getResultString());
      }
    }
    for (Analysis<?> t : analyses) {
      if(eval != null) break;
      for (Agent<?, ?> a : t.targets()) {
    	if(eval != null) break;
        if (a instanceof Evaluation) {// FIXME quick hack
          eval = (Evaluation) a; // FIXME won't compile on OpenJDK
          System.err.println("Found evaluation (in analysis): " + eval.getResultString());
        }
      }
    }
    return eval;
  };

  /**
   * @param builder The builder to create an experiment from
   */
  SynchronizedExperiment(final Builder builder) {
    this(builder.name, builder.root, builder.agent, builder.gold, builder.interactions,
        builder.syntheses, builder.time);
  }

  /**
   * @param name The name for this experiment
   * @param corpus The location of the corpus data
   * @param output The location to store results
   * @param goldLocation The location of the gold standard annotation file
   * @param evaluateAgent The full qualified name of the agent whose output should be evaluated
   * @param interactions The interactions
   * @param trainings
   * @param time The creation time
   */
  private SynchronizedExperiment(final String name, final String output,
      final String evaluateAgent, final String goldLocation, final List<Analysis<?>> interactions,
      final List<Synthesis<?, ?>> trainings, final long time) {

    if (evaluateAgent != null && goldLocation != null) {
      this.includesEvaluation = true;
      try {
        this.evaluationAgent = Class.forName(evaluateAgent);
      } catch (ClassNotFoundException e) {
        e.printStackTrace();
      }
      this.goldStandard = goldLocation;
    } else {
      this.goldStandard = Preferences.get(Default.GOLD);
    }
    this.title = name != null ? name : Preferences.get(Preferences.Default.NAME);
    /* Now that we have a proper name, we can create the ID string: */
    this.title = title.replaceAll("\\s", "_");
    id = title + "@" + time;
    this.outputLocation = (output != null ? output : Preferences.get(Default.ROOT)) + id;
    this.blackboard = new HashMap<Class<? extends Agent<?, ?>>, List<Annotation<?>>>();
    this.analyses = interactions;
    this.syntheses = trainings;
    /*
     * Phew, nasty way to get the corpus data location from the first agent...
     */
    Agent<?, ?> agent = interactions.get(0).targets().get(0);
    URL location;
    try {
      location = new URL(agent.toString());
    } catch (MalformedURLException e) {
      e.printStackTrace();
      throw new IllegalArgumentException(
          "First interaction needs a target agent that returns a data location as "
              + "URL as the toString representation");
    }
    this.corpusLocation = location.toString();
  }

  /**
   * Runs the specified agents. Creates each agent by creating the instance reflectively, but then
   * accesses it normally via the Agent interface (cf. Item 53 in Effective Java 2dn ed.) TODO find
   * a way to get annotation info at runtime
   */
  public synchronized void run() {
	long start = System.nanoTime();
    /* Agents: */
    synchronized (analyses) {
      for (Analysis<?> interaction : interactions()) {
        trainIfReady();
        interaction.run(blackboard);
//        System.err.println("Agent on BB: " + blackboard.keySet());
      }
      trainIfReady(); // FIXME
    }
    AnnotationWriter writer = new AnnotationWriter(blackboard, corpusLocation);
    writer.writeAnnotations(outputLocation);
    String data = getOutputAnnotationLocation();
    try {
      if (!new File(new URL(data).toURI()).exists()) {
        throw new IllegalArgumentException("Can'access output file: " + data);
      }
    } catch (MalformedURLException e) {
      e.printStackTrace();
    } catch (URISyntaxException e) {
      e.printStackTrace();
    }
    took = System.nanoTime() - start;
  }

  Set<Synthesis<?, ?>> ran = new HashSet<Synthesis<?, ?>>();

  private void trainIfReady() {
    for (Synthesis<?, ?> t : syntheses) {
      if (!ran.contains(t)) {
        Object ret = t.run(blackboard);
        if (ret != null) {
          ran.add(t);
        }
      }
    }
  }

  /**
   * @return Returns the location of the corpus data
   */
  public String getCorpusLocation() {
    return corpusLocation;
  }

  /**
   * @return Returns the root location name to output results (this is neither a file nor a folder,
   *         it's the base name)
   */
  public String getOutputRootName() {
    return outputLocation;
  }

  /**
   * @return Returns the location of the result annotation file
   */
  public String getOutputAnnotationLocation() {
    return outputLocation + ".xml";
  }

  /**
   * @return Returns the location of the documentation file
   */
  public String getOutputDocumentationLocation() {
    return outputLocation + ".html";
  }

  /**
   * @return Returns the interactions of this experiment
   */
  public List<Analysis<?>> interactions() {
    return Collections.unmodifiableList(analyses);
  }

  /**
   * @return Returns the title
   */
  public String getTitle() {
    return title;
  }

  /**
   * @return True, if this experiment is set up for evaluation
   */
  public Boolean includesEvaluation() {
    return includesEvaluation;
  }

  /**
   * @return The class of the agent whose results are evaluated
   */
  public Class<?> getEvaluationAgent() {
    return evaluationAgent;
  }

  /**
   * @return The location of the gold standard annotations file
   */
  public String getGoldStandard() {
    return goldStandard;
  }

  public List<Synthesis<?, ?>> syntheses() {
    return Collections.unmodifiableList(syntheses);
  }

  @Override
  public List<Annotation<?>> getAnnotations() {
    List<Annotation<?>> res = new ArrayList<Annotation<?>>();
    for (List<Annotation<?>> list : blackboard.values()) {
      for (Annotation<?> a : list) {
        res.add(a);
      }

    }
    return res;
  }

@Override
public long getTime() {
	return took;
}
}

package com.quui.tm2.doc;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.quui.tm2.Agent;
import com.quui.tm2.Analysis;
import com.quui.tm2.Annotation;
import com.quui.tm2.AnnotationReader;
import com.quui.tm2.Experiment;
import com.quui.tm2.util.DotDrawer;
import com.quui.tm2.util.FileIO;
import com.quui.tm2.util.Preferences;
import com.quui.tm2.util.Preferences.Environment;

/**
 * @author Fabian Steeg (fsteeg)
 */
public final class ExportHelper {
    private static final String JAVA = ".java";

    private ExportHelper() {}

    private static final String DOT_DIR = Preferences.get(Environment.DOT_HOME);
    private static final String RENDER = Preferences.get(Environment.DOT_FORMAT);
    private static final String DOT = ".dot";
    private static final String VISUAL = "-visual";

    /**
     * @param metadata The attributes for rendering
     * @param experiment The experiment to render
     */
    public static void renderDot(final ExperimentInfo metadata, final Experiment experiment) {
        String outDot = experiment.getOutputRootName() + VISUAL + DOT;
        ExperimentDotTemplate dt = new ExperimentDotTemplate();
        String generatedDot = dt.generate(metadata);
        try {
            save(generatedDot, outDot);
            DotDrawer dd = new DotDrawer("", "", outDot, experiment.getOutputRootName() + VISUAL + "." + RENDER,
                    DOT_DIR);
            dd.renderImage(RENDER);
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * @param x The experiment
     * @return The attributes to use when passing into {@link #renderDot(ExperimentInfo, Experiment)}
     */
    static ExperimentInfo createMetadata(final Experiment x) {
        ExperimentInfo experimentInfo = new ExperimentInfo();
        experimentInfo.corpusLocation = x.getCorpusLocation();
        experimentInfo.title = x.getTitle();
        experimentInfo.trains = x.syntheses();
        experimentInfo.resultAnnotationsLocation = x.getOutputAnnotationLocation();
        experimentInfo.evalAgent =(x.getEvaluation() != null ? x.getEvaluation().getClass().getSimpleName(): "No eval available");
        Class evalAgentClass = (x.getEvaluation() != null ? x.getEvaluation().getClass(): null);
        experimentInfo.evalResult = 
				(x.getEvaluation() != null ? x.getEvaluation().getResultString() : "No eval available")
				+ ";"
				+ (x.getGoldStandard() != null ? x.getGoldStandard() : "No gold available");
        List<String> sources = new ArrayList<String>();
        String src = Preferences.get(Environment.SOURCES);
        String outputLocation = x.getOutputRootName();
        List<String> files = new ArrayList<String>();
        Set<Agent<?, ?>> agents = agentsInvolved(x.interactions());
        Set<AgentInfo> agentInfos = new HashSet<AgentInfo>();
        for (Agent<?, ?> agent : agents) {
            String a = agent.getClass().getName();
            String sourceFile = (src + a.replaceAll("\\.", "/")) + JAVA;
            String source = "No source available";
            try {
                if (new File(new URL(sourceFile).toURI()).exists()) {
                    source = FileIO.read(sourceFile);
                }
                sources.add(sourceFile);
                String name = agent.getClass().getName();
                System.out.println(name);
                try {
					name = agent.getClass().getSimpleName();
				} catch (Exception e) {
					e.printStackTrace();
				}
                AgentInfo agentInfo = new AgentInfo(name, agent.toString(), source);
                agentInfos.add(agentInfo);
                files.add(draw(outputLocation + "/", agentInfo));
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (URISyntaxException e) {
                e.printStackTrace();
            }
        }
        files.add(0, outputLocation + VISUAL + "." + RENDER);
        experimentInfo.agentSources = sources;
        List<Analysis<?>> interactions = new ArrayList<Analysis<?>>();
        interactions.addAll(x.interactions());
        experimentInfo.flows = interactions;
        experimentInfo.agentNames = new ArrayList<AgentInfo>(agentInfos);
        String outputAnnotationLocation = x.getOutputAnnotationLocation();
        AnnotationReader reader = new AnnotationReader(outputAnnotationLocation);
        if (evalAgentClass != null) {
          List<Annotation<String>> annotations = reader.readAnnotations(evalAgentClass);
          if (annotations != null && annotations.size() > 0) {
            experimentInfo.evalResult = annotations.get(0).getValue();
          }
        }
        experimentInfo.referencedFiles = files;
        experimentInfo.took = x.getTime();
        return experimentInfo;
    }

    private static String configString(Agent<?, ?> agent) {
        return String.format("configuration: %s", agent.toString());
    }

    /**
     * @param agentInfo The agent info to draw with dot
     * @return The location of the viewable file generated with dot
     * @throws InterruptedException
     * @throws InvocationTargetException
     */
    private static String draw(String location, AgentInfo agent) {
        String dotText = new AgentDotTemplate().generate(agent);
        String dotLocation = location + agent.name + ".dot";
        File parent;
        String resultLocation = null;
        try {
            parent = new File(new URL(location).toURI());
            if (!parent.exists()) {
                boolean mkdir = parent.mkdir();
                if (!mkdir) {
                    throw new IllegalStateException("Could not create: " + parent);
                }
            }
            FileWriter w = new FileWriter(new File(new URL(dotLocation).toURI()));
            w.write(dotText);
            w.close();

            resultLocation = location + agent.name + "." + RENDER;
            DotDrawer dd = new DotDrawer("", "", dotLocation, resultLocation, DOT_DIR);
            dd.renderImage(RENDER);
        } catch (MalformedURLException e1) {
            e1.printStackTrace();
        } catch (URISyntaxException e1) {
            e1.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return resultLocation;
    }

    private static Set<Agent<?, ?>> agentsInvolved(List<Analysis<?>> interactions) {
        Set<Class<? extends Agent<?, ?>>> set = new HashSet<Class<? extends Agent<?, ?>>>();
        Set<Agent<?, ?>> set2 = new HashSet<Agent<?, ?>>();
        for (Analysis<?> interaction : interactions) {
            for (Agent<?, ?> agent : interaction.sources()) {
                Class<? extends Agent<?, ?>> c = (Class<? extends Agent<?, ?>>) agent.getClass();
                if (!set.contains(c)) {
                    set2.add(agent);
                }
                set.add(c);
            }
            for (Agent<?, ?> agent : interaction.targets()) {
                Class<? extends Agent<?, ?>> c = (Class<? extends Agent<?, ?>>) agent.getClass();
                if (!set.contains(c)) {
                    set2.add(agent);
                }
                set.add(c);
            }
        }
        return set2;
    }

    private static void save(String content, String location) throws IOException {
        FileWriter fw;
        try {
            fw = new FileWriter(new File(new URL(location).toURI()));
            fw.write(content);
            fw.close();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }
}

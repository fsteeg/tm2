package com.quui.tm2.agents.classifier.console;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import com.quui.tm2.agents.classifier.bayestree.ArrayTools;
import com.quui.tm2.agents.classifier.bayestree.BayesTree;
import com.quui.tm2.agents.classifier.weka.WekaClassifier;
import com.quui.tm2.agents.classifier.weka.WsdClassifier;
import com.quui.tm2.agents.features.generator.FeatureGenerator;
import com.quui.tm2.agents.senseval.Ambiguity;

public class WSD {

    private FeatureGenerator feat;

    Map<String, Integer> lower = new HashMap<String, Integer>();

    private String mostFreq;

    public Map<String, WsdClassifier> lexicon;

    private Map<String, List<String>> classes;

    private float patternFactor;

    private int[] structure;

    private boolean weka;

    public WSD(List<String> vocabulary, int[] structure, float patternFactor,
            Map<String, List<String>> classes, String feat, int context) {
        this.lexicon = Collections.synchronizedMap(new HashMap<String, WsdClassifier>());
        this.structure = ClassifierPreferences.getInstance().features.equals("combo") ? comboTree(structure)
                : structure;
        this.patternFactor = patternFactor;
        this.classes = Collections.synchronizedMap(classes);
        List<String> cleaned = new ArrayList<String>();
        for (String string : vocabulary) {
            if (string.trim().length() > 0) {
                cleaned.add(string);
            }
        }
        this.feat = new FeatureGenerator(cleaned, feat, context);
        this.weka = ClassifierPreferences.getInstance().weka;
    }

    private int[] comboTree(int[] structure2) {
        int[] res = new int[] { structure2.length };
        for (int i = 0; i < res.length; i++) {
            res[i] = structure2[i] * 2;
        }
        return res;
    }

    /**
     * @param vocabulary the vocabulary to use
     */
    public WSD(List<String> vocabulary, Map<String, List<String>> senses) {
        this(vocabulary, ClassifierPreferences.getInstance().treeStructure,
                ClassifierPreferences.getInstance().patternFactor, senses,
                ClassifierPreferences.getInstance().features, ClassifierPreferences.getInstance().context / 2);
    }

    /**
     * @param text The text to use for training (already disambiguated)
     */
    public void trainText(List<String> text0) {
        List<String> text = new ArrayList<String>();
        for (String string2 : text0) {
            if (string2.trim().length() > 0)
                text.add(string2);
        }
        Map<String, List<Integer>> indicesForAmbiguousWords = collectAmbiguous(text);
        // for each sense...
        int i = 0;
        for (String word : indicesForAmbiguousWords.keySet()) {
            String subSequence = word.split(":")[0];
            WsdClassifier tree = lexicon.get(subSequence);
            if (tree == null) {
                List<String> list = classes.get(subSequence);
                // it's the first time we train for the lemma:
                tree = weka ? new WekaClassifier(list, structure[0]) : new BayesTree(structure,
                        patternFactor, list);
                lexicon.put(subSequence, tree);
            }
            int j = 0;
            // for each index...
            for (Integer integer : indicesForAmbiguousWords.get(word)) {
                // create the numerical context representation:
                float[] features = feat.getFeatures(integer, text, tree.featureSize());
                if (ClassifierPreferences.getInstance().debug) {
                    System.out.println("Training: " + word + " with context: "
                            + ArrayTools.format(features, ClassifierPreferences.getInstance().digits)
                            + textualContext(text, integer, tree));
                }
                // learn the surrounding pattern:
                String correct = word.split(":")[1];
                tree.train(features, correct);
                Integer freq = lower.get(correct);
                if (freq == null)
                    lower.put(correct, 1);
                else {
                    lower.put(correct, lower.get(correct) + 1);
                }
                j++;
            }
            if (ClassifierPreferences.getInstance().debug) {
                System.out.println();
            }
            i++;
        }
        mostFreq = "";
        int max = Integer.MIN_VALUE;
        for (String string : lower.keySet()) {
            if (lower.get(string) > max) {
                max = lower.get(string);
                mostFreq = string;
            }
        }
    }

    public WsdClassifier treeForLemma(String word) {
        WsdClassifier tree = lexicon.get(word);
        if (tree == null) {
            throw new IllegalStateException("No tree in lexicon for: " + word);
        }
        return tree;
    }

    /**
     * @param text The text containing the words to disambiguate
     */
    public void disambiguateText(List<String> text0) {
        List<String> text = new ArrayList<String>();
        for (String string2 : text0) {
            if (string2.trim().length() > 0)
                text.add(string2);
        }
        int cor = 0;
        int incorrect = 0;
        int lowerCor = 0;
        int lowerIncorrect = 0;
        Map<String, List<Integer>> ambiguousWords = collectAmbiguous(text);
        // for each ambiguous word...
        for (String word : ambiguousWords.keySet()) {
            String sub = word.split(":")[0];
            WsdClassifier tree = treeForLemma(sub);
            List<Integer> list = ambiguousWords.get(word);
            // in all the indices it occurs...
            for (Integer integer : list) {
                // create a numerical context representation:
                float[] features = feat.getFeatures(integer, text, tree.featureSize());
                // disambiguate by classifying the context in the tree:

                String result = tree.classify(features);
                if (ClassifierPreferences.getInstance().debug) {
                    System.out.print("Classified: " + word + " with context: "
                            + ArrayTools.format(features, ClassifierPreferences.getInstance().digits)
                            + " with: ");
                    System.out.println("as: " + result + textualContext(text, integer, tree));
                }
                String correct = word.split(":")[1];
                if (correct.equals(result)) {
                    cor++;
                } else {
                    incorrect++;
                }

                if (correct.equals(mostFreq)) {
                    lowerCor++;
                } else {
                    lowerIncorrect++;
                }
                tree.resetClassify();
            }
            tree.resetClassify();
            if (ClassifierPreferences.getInstance().debug) {
                System.out.println();
            }
        }
        System.out.println("Correct: "
                + ArrayTools.numberFormat((double) cor / (cor + incorrect),
                        ClassifierPreferences.getInstance().digits));
        System.out.println("Lower Bound: "
                + ArrayTools.numberFormat((double) lowerCor / (lowerCor + lowerIncorrect),
                        ClassifierPreferences.getInstance().digits));

    }

    private String textualContext(List<String> text, Integer integer, WsdClassifier tree) {
        String textualContext = " ("
                + text.subList(Math.max(0, integer - tree.featureSize()),
                        Math.min(integer + tree.featureSize() + 1, text.size())) + ")";
        return textualContext;
    }

    private Map<String, List<Integer>> collectAmbiguous(List<String> text) {
        Map<String, List<Integer>> ambiguousWords = new HashMap<String, List<Integer>>();
        // collect ambiguous, annotated words:
        for (int i = 0; i < text.size(); i++) {
            if (text.get(i).contains(":")) {
                List<Integer> indices = ambiguousWords.get(text.get(i));
                if (indices == null) {
                    indices = new ArrayList<Integer>();
                }
                indices.add(i);
                ambiguousWords.put(text.get(i), indices);
            }
        }
        return ambiguousWords;
    }

    public void train(List<Ambiguity> samples) {
        final Map<String, Map<String, List<Ambiguity>>> lists = Collections
                .synchronizedMap(group(samples));
        List<Thread> threads = new Vector<Thread>();
        long start = System.currentTimeMillis();
        for (final String lemma : lists.keySet()) {

            Thread thread = new Thread(new Runnable() {
                public void run() {
                    Map<String, List<Ambiguity>> map = Collections.synchronizedMap(lists.get(lemma));
                    System.out.println("Training lemma: " + lemma + " with " + map.keySet().size()
                            + " different senses.");
                    for (final String sense : map.keySet()) {
                        List<Ambiguity> name = map.get(sense);
                        WsdClassifier tree = lexicon.get(lemma);
                        System.out.println("\tTraining " + name.size() + " instances of " + sense);
                        for (Ambiguity ambiguity : name) {
                            // get the applicable tree:
                            if (tree == null) {
                                // it's the first time we train for the
                                // lemma:
                                List<String> list = classes.get(ambiguity.getLemma());
                                tree = weka ? new WekaClassifier(list, structure[0])
                                        : new BayesTree(structure, patternFactor, list);
                                lexicon.put(ambiguity.getLemma(), tree);
                            }
                            float[] features = feat.getFeatures(ambiguity.getContext().target,
                                    ambiguity.getContext().all, tree.featureSize());
                            if (ClassifierPreferences.getInstance().debug) {
                                System.out.println("Training: "
                                        + ambiguity
                                        + " with context: "
                                        + ArrayTools.format(features,
                                                ClassifierPreferences.getInstance().digits));
                            }
                            // train the applicable tree:
                            tree.train(features, ambiguity.getCorrect());
                        }
                        // done training one sense:
                        if (ClassifierPreferences.getInstance().debug) {
                            System.err.println("Training sense done.");
                        }
                    }

                }
            });
            // we train all samples for each lemma in a thread of its own:
            // System.out.println("Spinning thread for learning instances of: "
            // + lemma);
            if (ClassifierPreferences.getInstance().parallel)
                thread.start();
            else
                thread.run();
            threads.add(thread);

        }
        System.out.print("Trained networks with " + lists.keySet().size() + " lemmata, "
                + samples.size() + " samples...");
        if (ClassifierPreferences.getInstance().parallel)
            while (!(allDone(threads) && threads.size() == lists.keySet().size())) {
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        long l = System.currentTimeMillis() - start;
        System.out.print(" took " + l + " ms. (" + l / 1000 + " sec.)" + "\n");
    }

    private boolean allDone(List<Thread> threads) {
        synchronized (threads) {
            for (Thread thread : threads) {
                if (thread.isAlive())
                    return false;
            }
            return true;
        }
    }

    private Map<String, Map<String, List<Ambiguity>>> group(List<Ambiguity> samples) {
        final Map<String, Map<String, List<Ambiguity>>> maps = new HashMap<String, Map<String, List<Ambiguity>>>();
        for (Ambiguity a : samples) {
            Map<String, List<Ambiguity>> map = maps.get(a.getLemma());
            if (map == null) {
                map = new HashMap<String, List<Ambiguity>>();
                maps.put(a.getLemma(), map);
            }
            List<Ambiguity> list = map.get(a.getCorrect());
            if (list == null) {
                list = new Vector<Ambiguity>();
                map.put(a.getCorrect(), list);
            }
            list.add(a);
        }
        return maps;
    }

    public void disambiguate(List<Ambiguity> samples) throws IOException {
        String out = "files/senseval.result";
        BufferedWriter fw = new BufferedWriter(new FileWriter(out));
        final List<String> results = new Vector<String>();
        final Map<String, Map<String, List<Ambiguity>>> lists = group(samples);
        List<Thread> threads = new Vector<Thread>();
        long start = System.currentTimeMillis();
        for (final String lemma : lists.keySet()) {
            Thread thread = new Thread(new Runnable() {
                public void run() {
                    Map<String, List<Ambiguity>> map = lists.get(lemma);
                    for (final String sense : map.keySet()) {
                        List<Ambiguity> name = map.get(sense);
                        WsdClassifier tree = lexicon.get(lemma);
                        if (tree == null) {
                            throw new IllegalStateException("No tree in lexicon for: " + lemma);
                        }
                        System.out.println("\tDisambiguating " + name.size() + " instances of "
                                + lemma);
                        for (Ambiguity ambiguity : name) {
                            float[] features = feat.getFeatures(ambiguity.getContext().target,
                                    ambiguity.getContext().all, tree.featureSize());

                            String result = tree.classify(features);
                            String string = ambiguity.getLemma() + " " + ambiguity.getID() + " "
                                    + result;
                            results.add(string);
                            if (ClassifierPreferences.getInstance().debug) {
                                System.out.print("Classified: "
                                        + ambiguity
                                        + " with context: "
                                        + ArrayTools.format(features,
                                                ClassifierPreferences.getInstance().digits) + " as: ");
                                System.out.println(result);
                            }
                        }
                        // after classifying all instances of a sense, we clear
                        // input sensors (discourse change)
                        if (ClassifierPreferences.getInstance().debug) {
                            System.err.println("Classification reset.");
                        }
                        tree.resetClassify();
                    }
                }
            });
            // we train all samples for each lemma in a thread of its own:
            if (ClassifierPreferences.getInstance().parallel)
                thread.start();
            else
                thread.run();
            threads.add(thread);

        }
        System.out.print("For " + lists.keySet().size() + " lemmata, classified " + samples.size()
                + " instances...");
        while (!(allDone(threads) && threads.size() == lists.keySet().size())) {
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        long l = System.currentTimeMillis() - start;
        System.out.print(" took " + l + " ms. (" + l / 1000 + " sec.)" + "\n");
        // The Senseval scoring app requires sorted output:
        Collections.sort(results);
        for (String r : results) {
            fw.write(r + "\n");
        }
        fw.close();
        System.out
                .println("_____________________________________________________________________\n"
                        + "Result written to: " + out
                        + "\n(see http://www.senseval.org/senseval3/scoring for usage)\n"
                        + "_____________________________________________________________________");
    }
}

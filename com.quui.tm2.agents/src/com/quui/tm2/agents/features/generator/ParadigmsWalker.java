package com.quui.tm2.agents.features.generator;

import java.util.SortedSet;
import java.util.TreeSet;

import com.quui.tm2.agents.features.suffixtree.AlphanumericSuffixTree;
import com.quui.tm2.agents.features.suffixtree.WordSuffixTree;
import com.quui.tm2.agents.features.suffixtree.node.Node;

public class ParadigmsWalker {
	// private AlphanumericSuffixTree forwardTree;

	// private AlphanumericSuffixTree backwardTree;

	// public SortedSet<Paradigm> pardigmsInText;

	// Set<Set<String>> derivedParadigms;

	// HashMap<String, List<Paradigm>> m;
	SortedSet<Paradigm> paradigms = null;

	// SortedSet<Paradigm> bootstrappedParadigms;

	/**
	 * @param text
	 *            The text to bootstrap paradigms from.
	 */
	public ParadigmsWalker(String text) {
		// m = new HashMap<String, List<Paradigm>>();
		paradigms = new TreeSet<Paradigm>();
		// derivedParadigms = new HashSet<Set<String>>();
		// forward: "the man. the boy." --> [man, boy]
		System.out.println("Building suffix trees for paradigm retrieval...");
		WordSuffixTree tree = new WordSuffixTree(text, false, false);
		System.out.println("Built forward tree, retrieving paradigms...");
		bootstrapParadigms(tree);
		// System.out.println("We have " + m.keySet().size() + " words and "
		// + m.values().size() + " lists of paradigms.");
		System.out
				.println("Retrieved paradigms from first tree, building backward tree...");
		// backward: "a man. the man." --> [a, the]
		tree = null;
		tree = new WordSuffixTree(text, true, false);
		System.out.println("Built backward tree, retrieving paradigms...");
		bootstrapParadigms(tree);
		// System.out.println("We have " + m.keySet().size() + " words and "
		// + m.values().size() + " lists of paradigms.");
		System.out.println("Retrieved paradigms from second tree.");
	}

	private void bootstrapParadigms(AlphanumericSuffixTree tree) {
		// Set<Set<String>> paradigmsInText = new HashSet<Set<String>>();
		/*
		 * a mapping to remember which words are already in a paradigm, for
		 * deriving paradigms:
		 */
		// Map<String, Set<String>> map = new HashMap<String, Set<String>>();
		// SortedSet<Paradigm> sortedPs = new TreeSet<Paradigm>();
		// int count = 0;
		for (Node node : tree.getAllNodes()) {
			if (node.isInternal()) {
				// the current paradigm: all children of an inner node
				Paradigm p = new Paradigm();
				// count++;
				// Set<String> paradigm = new HashSet<String>();
				// System.out.println("Getting paradigm from internal node...");
				for (Node child : node.getChildren()) {
					/*
					 * the first word of the edge label in a suffix tree is a
					 * member of the paradigm:
					 */
					String paradigmMember = tree.getIncomingEdgeLabel(
							child).split(" ")[0];
					if (!paradigmMember.trim().equals("")) {
						// paradigm.add(paradigmMember);
						p.add(paradigmMember);
						// List<Paradigm> indices = m.get(paradigmMember);
						// if (indices == null) {
						// indices = new Vector<Paradigm>();
						// m.put(paradigmMember, indices);
						// }
						// if (!indices.contains(p))
						// indices.add(p);
					}

					/*
					 * derive new paradigms: if a word is already in another
					 * paradigm, add all the members of that other paradigm to
					 * the current:
					 */
					// if (map.containsKey(paradigmMember)) {
					// Set<String> pp = new HashSet<String>(paradigm);
					// pp.addAll(map.get(paradigmMember));
					// derivedParadigms.add(pp);
					// }
					// map.put(paradigmMember, paradigm);
					// System.err.println("Contains mittel 1: " +
					// m.keySet().contains("mittel"));
				}
				paradigms.add(p);
				// System.err.println("Contains mittel 2: " +
				// m.keySet().contains("mittel"));
				// paradigm = StopwordsFilter.getInstance().filter(paradigm);
				// if (paradigm != null)
				// paradigm = StopwordsFilter.getInstance().filter(paradigm);
				// TODO nur hier hilft es nicht viel!
				// if (paradigm != null && paradigm.size() > 1) {
				// paradigmsInText.add(paradigm);
				// sortedPs.add(p);
				// }
			}
		}
		// derivedParadigms.removeAll(paradigmsInText);
		// int c = 1;
		// int max = sortedPs.size();
		// for (Paradigm paradigm : sortedPs) {
		// paradigm.value = 1.0f / max * c;
		// c++;
		// }
		// return sortedPs;
	}

	/* Below: Unused methods for demonstration purpose */

	// /**
	// * @param forwardTree
	// * The tree to compute the paradigm from
	// * @return Return the paradigm found in the text
	// */
	// public Set<Set<String>> getSimpleParadigms(
	// AlphanumericSuffixTree forwardTree) {
	// Set<Set<String>> cf = new HashSet<Set<String>>();
	// for (Node node : forwardTree.getAllNodes()) {
	// if (node.isInternal()) {
	// Set<String> p = new HashSet<String>();
	// for (Node child : node.getChildren()) {
	// p.add(forwardTree.getIncomingEdgeLabel(child));
	// }
	// cf.add(p);
	// }
	// }
	// return cf;
	// }
	//
	// public Set<Set<String>> getParadigms(AlphanumericSuffixTree tree) {
	// Set<Set<String>> paradigms = new HashSet<Set<String>>();
	// for (Node node : tree.getAllNodes()) {
	// if (node.isInternal()) {
	// Set<String> paradigm = new HashSet<String>();
	// for (Node child : node.getChildren()) {
	// paradigm.add(tree.getIncomingEdgeLabel(child));
	// }
	// paradigms.add(paradigm);
	// }
	// }
	// return paradigms;
	// }
	//
	// /**
	// * brute-force way of deriving paradigms from given paradigms (NOTE: just
	// * for demonstration, the derived paradigms are computed efficiently after
	// * construction, and thus can be used without any method call)
	// *
	// * @return The paradigm derived from the paradigm found in the text
	// */
	// public Set<Set<String>> getNaiveDerivedParadigms() {
	// Set<Set<String>> paradigms = new HashSet<Set<String>>();
	// for (Set<String> set1 : pardigmsInText) {
	// for (Set<String> set2 : pardigmsInText) {
	// if (!set1.equals(set2)) {
	// Set<String> s1 = new HashSet<String>(set1);
	// Set<String> s2 = new HashSet<String>(set1);
	// for (String string1 : set1) {
	// for (String string2 : set2) {
	// if (string1.equals(string2)) {
	// s1.addAll(set2);
	// s2.addAll(set1);
	// if (!pardigmsInText.contains(s1))
	// paradigms.add(s1);
	// if (!pardigmsInText.contains(s2))
	// paradigms.add(s2);
	// }
	// }
	// }
	//
	// }
	// }
	// }
	// return paradigms;
	// }
}

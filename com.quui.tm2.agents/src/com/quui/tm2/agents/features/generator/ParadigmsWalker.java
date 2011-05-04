package com.quui.tm2.agents.features.generator;

import java.util.SortedSet;
import java.util.TreeSet;

import com.quui.tm2.agents.features.suffixtree.AlphanumericSuffixTree;
import com.quui.tm2.agents.features.suffixtree.WordSuffixTree;
import com.quui.tm2.agents.features.suffixtree.node.Node;

public class ParadigmsWalker {
	SortedSet<Paradigm> paradigms = null;

	/**
	 * @param text
	 *            The text to bootstrap paradigms from.
	 */
	public ParadigmsWalker(String text) {
		paradigms = new TreeSet<Paradigm>();
		// forward: "the man. the boy." --> [man, boy]
		System.out.println("Building suffix trees for paradigm retrieval...");
		WordSuffixTree tree = new WordSuffixTree(text, false, false);
		System.out.println("Built forward tree, retrieving paradigms...");
		bootstrapParadigms(tree);
		System.out
				.println("Retrieved paradigms from first tree, building backward tree...");
		// backward: "a man. the man." --> [a, the]
		tree = null;
		tree = new WordSuffixTree(text, true, false);
		System.out.println("Built backward tree, retrieving paradigms...");
		bootstrapParadigms(tree);
		System.out.println("Retrieved paradigms from second tree.");
	}

	private void bootstrapParadigms(AlphanumericSuffixTree tree) {
		for (Node node : tree.getAllNodes()) {
			if (node.isInternal()) {
				// the current paradigm: all children of an inner node
				Paradigm p = new Paradigm();
				for (Node child : node.getChildren()) {
					/*
					 * the first word of the edge label in a suffix tree is a
					 * member of the paradigm:
					 */
					String paradigmMember = tree.getIncomingEdgeLabel(
							child).split(" ")[0];
					if (!paradigmMember.trim().equals("")) {
						p.add(paradigmMember);
					}
				}
				paradigms.add(p);
			}
		}
	}
}

package com.quui.tm2.agents.features.suffixtree;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.quui.tm2.agents.features.suffixtree.node.Node;
import com.quui.tm2.agents.features.suffixtree.node.SimpleNodeAccessor;



/**
 * A directed acyclic graph, a compact suffix tree
 * 
 * @author fsteeg
 * 
 */
public class DAG {
    /**
     * The DAG
     */
    public NumericSuffixTree graph;

    /**
     * @param tree
     *            The suffix tree to transform into a DAG
     */
    public DAG(NumericSuffixTree tree) {
        this.graph = tree;
        compact();
    }

    /**
     * Implementation of Gusfield, p. 131
     */
    private void compact() {
        Set<Pair> Q = identifyQ();
        for (Pair pair : Q) {
            if (inGraph(pair))
                merge(pair);
        }

    }

    private boolean inGraph(Pair pair) {
        ArrayList<Node> nodes = new ArrayList<Node>();
        nodes = graph.getAllNodes(graph.getRoot(), nodes, false);
        return nodes.contains(pair.p) && nodes.contains(pair.q);
    }

    // TODO correct indices are not preserved
    private void merge(Pair pair) {
        Map<Long, Node> children = graph.accessor.getChildren(pair.p);
        // System.out.println();
        // System.out.println("p: " +
        // ((WordSuffixTree)graph).mapper.getTranslatedEdgeLabel(pair.p));
        // for (Long id : children.keySet()) {
        // Node child = children.get(id);
        // System.out.println("Child: " +
        // ((WordSuffixTree)graph).mapper.getTranslatedEdgeLabel(child));
        // }
        // merge p into q: remove edges out of p
        List<Node> parents = graph.accessor.getParents(pair.p);
        for (Node parentOfP : parents) {
            graph.accessor.getParents(pair.q).add(parentOfP);

        }
        children.clear();
        // clear parent of p
        graph.accessor.getParents(pair.p).clear();
    }

    private Set<Pair> identifyQ() {
        Set<Pair> result = new HashSet<Pair>();
        List<Node> nodes = new ArrayList<Node>();
        nodes = graph.getAllNodes(graph.getRoot(), (ArrayList<Node>) nodes,
                false);
        // nodes.add(tree.getRoot());
        for (Node p : nodes) {
            for (Node q : nodes) {
                if (!p.equals(q))
                    if (links(p, q) && equalsLeafNum(p, q))
                        result.add(new Pair(p, q));
            }
        }
        // System.out.println("Pairs: " + result.size());
        return result;
    }

    private boolean links(Node p, Node q) {
        Node linkedByP = graph.accessor.getSuffixLink(p);
        if (linkedByP != null)
            if (linkedByP.equals(q))
                return true;
        return false;
    }

    private boolean equalsLeafNum(Node p, Node q) {
        ArrayList<Node> pLeaves = new ArrayList<Node>();
        ArrayList<Node> qLeaves = new ArrayList<Node>();
        int leavesP = graph.getAllNodes(p, pLeaves, true).size();
        int leavesQ = graph.getAllNodes(q, qLeaves, true).size();
        return leavesP == leavesQ;
    }

    public void exportDot(String string) {
        if (graph instanceof AlphanumericSuffixTree)
            ((AlphanumericSuffixTree) graph).exportDot(string);
        else
            new Mapper(graph, new SimpleNodeAccessor()).exportDot(string);

    }
}

class Pair {

    Node q;

    Node p;

    public Pair(Node p, Node q) {
        this.p = p;
        this.q = q;
    }
}

/** 
 Project Suffix Trees for Natural Language (STNL) (C) 2006 Fabian Steeg

 This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation; either version 2.1 of the License, or (at your option) any later version.

 This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details.

 You should have received a copy of the GNU Lesser General Public License along with this library; if not, write to the Free Software Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 */
package com.quui.tm2.agents.features.suffixtree;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import com.quui.tm2.agents.features.suffixtree.node.Node;
import com.quui.tm2.agents.features.suffixtree.node.NodeAccessor;
import com.quui.tm2.agents.features.suffixtree.node.SuffixNode;



/**
 * <p>
 * A suffix tree is an efficient method for encoding the frequencies of motifs
 * in a sequence. They are sometimes used to quickly screen for similar
 * sequences. For instance, all motifs of length up to 2 in the sequence
 * <code>AAGT</code> could be encoded as:
 * </p>
 * 
 * <pre>
 *                                           root(4)
 *                                           |
 *                                           A(2)--------G(1)-----T(1)
 *                                           |           |
 *                                           A(1)--G(1)  T(1)
 * </pre>
 * 
 * <p>
 * It supports addition of elements both as String and SymbolList. They should
 * not be mixed together. The strings are also terminated internally, so it is
 * possible to go and add more than one string to the suffix tree.
 * </p>
 * <p/> Some more work need to be done on how data should be generated from this
 * class. If you need something that's not in there, please e-mail the list at
 * biojava-dev@biojava.org and I'll add it in there. <p/> <b>Note:</b> (Fabian
 * Steeg) BioJava API-Code has been removed from the Implementation. The stuff
 * from BioJava might be interesting for modding this to allow larger alphabets
 * than 65535 through IntegerAlphabet. But also modding the String sto int[]
 * might work here (while it didn't for the ottawa-impl, that went quadratic)
 * 
 * @author Francois Pepin (original version from BioJava API), fsteeg (switched
 *         to longs)
 */
public class NumericSuffixTree<T extends Node> {

    public static final Long TERMINATION_SYMBOL = -1L;

    public static final int TO_A_LEAF = -1;

    private int e;

    private SequenceAccessor sequences;

    protected NodeAccessor<T> accessor;

    /**
     * Describes the rule that needs to be applied after walking down a tree.
     * Put as a class variable because it can only return a single object (and I
     * don't want to extend Node any further. rule 1: ended up at a leaf. rule
     * 2: need to extend an internalNode. rule 3: would split an edge. rule 4:
     * ended up in the middle of an edge. rule 5: ended up at an InternalNode
     * 
     * Production 5 counts as rule 4 when adding a sequence, but the rules are
     * also used to when searching the tree.
     */
    private int rule;

    /**
     * Initializes a new <code>UkkonenSuffixTree</code> instance with the
     * given accessors.
     */
    protected void initialize(NodeAccessor<T> nodeAccessor,
            SequenceAccessor sequenceAccessor) {
        this.accessor = nodeAccessor;
        this.sequences = sequenceAccessor;
        accessor.createRootNode();
        e = 0;
    }

    /**
     * Initializes a new <code>UkkonenSuffixTree</code> instance with a text
     * and the given accessors.
     * 
     * @param seqs
     *            The text to add
     */
    public NumericSuffixTree(List<Long> seqs, NodeAccessor<T> accessor,
            SequenceAccessor sequenceAccessor) {
        this(accessor, sequenceAccessor);
        addSequences(seqs, 1, false);
    }

    /**
     * Empty Constructor that does not do anything. NOTE: You <b>must</b> call
     * <code>initialize</code> when using this Constructor!
     */
    public NumericSuffixTree() {

    }

    /**
     * Initializes a new <code>UkkonenSuffixTree</code> instance with the
     * given accessors.
     * 
     * @param seqs
     *            The text to add
     */
    public NumericSuffixTree(NodeAccessor<T> accessor,
            SequenceAccessor sequenceAccessor) {
        initialize(accessor, sequenceAccessor);
    }

    /**
     * Add a sequence into the tree. If there are more sequences, they should be
     * separated by a terminationChar ($ by default). If none exist, it is
     * assumed that there is only 1 continuous sequence to be added.
     * 
     * @param seq
     *            the sequence/sequences to be added into the tree.
     * @param number
     *            The text number, for generalized trees
     * @param doNotTerminate
     *            whether we should terminate the sequence if it's
     *            non-terminated.
     */
    public void addSequences(List<Long> seq, int number, boolean doNotTerminate) {
        int i;
        int start, end;

        ArrayList<List<Long>> toBeAdded = new ArrayList<List<Long>>();
        Iterator<List<Long>> iterator;
        List<Long> subseq;
        if (seq == null || seq.size() == 0)
            return;

        // terminate the String if it's not terminated.
        if (!doNotTerminate && seq.get(seq.size() - 1) != TERMINATION_SYMBOL)
            seq.add(TERMINATION_SYMBOL);
        for (i = 0; seq.subList(i, seq.size()).indexOf(TERMINATION_SYMBOL) != -1;) {
            end = i + seq.subList(i, seq.size()).indexOf(TERMINATION_SYMBOL);
            toBeAdded.add(seq.subList(i, end + 1));
            i = i + seq.subList(i, seq.size()).indexOf(TERMINATION_SYMBOL) + 1;

        }
        addSequences(toBeAdded, number);
    }

    public void addSequences(List<List<Long>> toBeAdded, int number) {
        Iterator<List<Long>> iterator = toBeAdded.iterator();
        while (iterator.hasNext()) {
            List<Long> subseq = iterator.next();
            addSingleSequence(subseq, number);
        }
    }

    /**
     * Add a single sequence into the tree.
     * 
     * @param seq
     *            a <code>String</code> value
     * @param number
     */
    protected void addSingleSequence(List<Long> seq, int number) {
        int i, gammaStart;
        int j = 0;
        T oldNode = null, newNode;
        T currentNode;
        boolean canLinkJump = false;
        Map map = new HashMap();
        int matchesUntil = 0;
            BufferedWriter bufferedWriter = new BufferedWriter(new PrintWriter(
                    System.out));
            map = match(seq, accessor.getRoot(), 0, bufferedWriter, 0, false);
            matchesUntil = map.get("index") != null ? (Integer) map
                    .get("index") : 0;

        // Puts i at the end of the previous sequences
        i = sequences.size();

        int k = i;
        j = i;
        sequences.addAll(seq);

        currentNode = accessor.getRoot();

        // phase i
        for (; i < sequences.size(); i++) {

            e += 1;
            // extension j;
            for (; j <= i; j++) {

                // reset a couple of things...
                newNode = null;

                // find first node v at or above s[j-1,i] that is root or has a
                // suffixLink
                while (!currentNode.equals(accessor.getRoot())
                        && accessor.getSuffixLink(currentNode) == null
                        && canLinkJump)
                    currentNode = accessor.getParents(currentNode).get(0);

                if (currentNode.equals(accessor.getRoot())) {
                    currentNode = jumpTo(accessor.getRoot(), j, i + 1);
                } else {
                    if (canLinkJump)
                        currentNode = accessor.getSuffixLink(currentNode);
                    gammaStart = j + getPathLength(currentNode);

                    currentNode = jumpTo(currentNode, gammaStart, i + 1);
                }

                if (rule == 1)
                    addPositionToLeaf(j, currentNode);
                if (rule == 2)
                    doRule2(currentNode, i, j, number, i - k);
                if (rule == 3) {
                    newNode = doRule3(currentNode, i, j, number, i - k);
                    currentNode = newNode;

                }

                if (rule == 1 || rule == 4 || rule == 5)
                    currentNode = accessor.getParents(currentNode).get(0);

                if (oldNode != null) {
                    if (accessor.isTerminal(currentNode))
                        currentNode = accessor.getParents(currentNode).get(0);
                    accessor.setSuffixLink(oldNode, currentNode);

                }
                oldNode = newNode;
                newNode = null;

                if (rule == 1 || rule == 4 || rule == 5) {
                    oldNode = null;
                    canLinkJump = false;
                    break;
                } else
                    canLinkJump = true;
            }// for phase i
        }// for extension j
        finishAddition();
    }

    private Map match(List<Long> seq, T root, int result,
            BufferedWriter writer, int depth, boolean matched) {
        Map map = new HashMap();
        try {
            tab(writer, depth);
        } catch (IOException e) {
            e.printStackTrace();
        }
        Map<Long, T> children = accessor.getChildren(root);
        List<Long> label = getLabel(root);
        try {
            writer.write(label.toString() + "\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (children != null) {
            depth++;

            for (T child : children.values()) {
                // boolean matched = false;
                label = getEdgeLabel(child);
                for (int i = 0; i < seq.size() && i < label.size(); i++) {

                    if (seq.get(i).equals(label.get(i))) {
                        result++;
                        matched = true;
                    } else if (matched) {
                        map.put("index", result);
                        return map;
                    } else {
                        matched = false;
                        break;

                    }

                }
                if (matched && result <= seq.size())
                    return match(seq.subList(result, seq.size()), (T) child,
                            result, writer, depth, matched);

            }
        } else {
        }
        return map;
    }

   
    /**
     * Just like walkTo, but faster when used during tree construction, as it
     * assumes that a mismatch can only occurs with the last character of the
     * target string.
     * 
     * @param starting
     *            the root of the subtree we're walking down form.
     * @param source
     *            a superstring that contains the string we're using to walking
     *            down. source.subtring(from,to) should give the string we're
     *            walking down from.
     * @param from
     *            the start position (inclusive) of the target string in the
     *            source.
     * @param to
     *            the end position (exclusive) of the target string in the node.
     * @return a <code>SuffixNode</code> that the walk stopped at. If the walk
     *         stopped inside an edge. (check the rule variable to see where it
     *         stopped).
     */
    public T jumpTo(T starting, int from, int to) {
        T currentNode;
        T arrivedAt;
        boolean canGoDown = true;
        int edgeLength;
        int original = from;
        T originalNode = starting;
        // int i = 0;

        currentNode = starting;
        arrivedAt = starting;

        rule = 0;

        if (from == to) {
            rule = 5;
            return starting;
        }
        while (canGoDown) {
            if (accessor.isTerminal(currentNode)) {
                System.out.println("ARRGH! at "
                        + sequences.subList(original, to) + "(" + from + ","
                        + original + "," + to + ") from "
                        + getLabel(originalNode));
                // Something truly awful happened if this line is ever reached.
                // This bug should be dead, but it it came back from the dead a
                // couple
                // of times already.
            }
            Long key = sequences.get(from);
            arrivedAt = (T) accessor.getChildren(currentNode).get(key);
            if (arrivedAt == null) {
                canGoDown = false;
                arrivedAt = currentNode;

                rule = 2;
                break;
            }

            edgeLength = getEdgeLength(arrivedAt);
            if (edgeLength >= to - from) {
                int after = getPathEnd(arrivedAt) - edgeLength + to - from - 1;
                if (sequences.get(after).equals(sequences.get(to - 1))) {
                    if (edgeLength == to - from) {
                        if (accessor.isTerminal(arrivedAt))
                            rule = 1;
                        else
                            rule = 5;
                    } else
                        rule = 4;
                } else
                    rule = 3;
                canGoDown = false;
                break;
            }
            from += edgeLength;
            currentNode = arrivedAt;

        }// while canGoDOwn

        return arrivedAt;
    }

    /***************************************************************************
     * Tree navigation methods
     **************************************************************************/

    public int getEdgeLength(T child) {
        int parentLength, childLength;
        T parent;
        if (child.equals(accessor.getRoot())
                || accessor.getParents(child).size() == 0)
            return 0;
        parent = accessor.getParents(child).get(0);
        parentLength = getPathLength(parent);
        childLength = getPathLength(child);
        if (childLength - parentLength <= 0) {
        }

        return childLength - parentLength;
    }

    protected List<Long> getEdgeLabel(T child) {
        return sequences.subList(child.getLabelStart()
                + (getPathLength(child) - getEdgeLength(child)), (child
                .getLabelEnd() == TO_A_LEAF) ? e : child.getLabelEnd());
    }

    public int getPathLength(T node) {
        return getPathEnd(node) - node.getLabelStart();
    }

    public int getPathEnd(T node) {
        return node.getLabelEnd() == TO_A_LEAF ? e : node.getLabelEnd();
    }

    public List<Long> getLabel(T node) {
        if (node.equals(accessor.getRoot()))
            return new Vector<Long>();
        else
            return sequences.subList(node.getLabelStart(),
                    (node.getLabelEnd() == TO_A_LEAF) ? e : node.getLabelEnd());
    }

    /**
     * @param root
     *            The node to start from
     * @param list
     *            Give an empty List when using, used for recursive storage
     * @param leavesOnly
     *            if true, only leaves will be retuned
     * @return The nodes in the specified subtree
     */
    public ArrayList<T> getAllNodes(T root, ArrayList<T> list,
            boolean leavesOnly) {
        Iterator iterator;
        if (list == null)
            list = new ArrayList<T>();
        if (!leavesOnly || (leavesOnly && accessor.isTerminal(root)))
            list.add(root);
        if (!accessor.isTerminal(root)) {
            iterator = accessor.getChildren(root).values().iterator();
            while (iterator.hasNext())
                list = getAllNodes((T) iterator.next(), list, leavesOnly);
        }

        return list;
    }

    public T getRoot() {
        return accessor.getRoot();
    }

    /***************************************************************************
     * End Tree Navigation Methods
     **************************************************************************/

    /***************************************************************************
     * Tree modification methods
     **************************************************************************/
    private void addPositionToLeaf(int pos, T leaf) {
        int[] moreLabels;
        int[] additionalLabels = leaf.getAdditionalLabels();
        if (additionalLabels == null)
            additionalLabels = new int[] { pos };
        else {
            moreLabels = new int[additionalLabels.length + 1];
            System.arraycopy(additionalLabels, 0, moreLabels, 0,
                    additionalLabels.length);
            moreLabels[moreLabels.length - 1] = pos;
            additionalLabels = moreLabels;
        }
        accessor.setAdditionalLabels(leaf, additionalLabels);
    }

    private void doRule2(T parent, int splittingPos, int suffixStart,
            int number, int suffixIndex) {
        Vector<T> v = new Vector<T>();
        v.add(parent);
        T leaf = accessor.createLeafNode(v, suffixStart, number, suffixIndex);
        Long splitPos = sequences.get(splittingPos);
        accessor.addChild(parent, splitPos, leaf);

    }

    private T doRule3(T child, int splittingPos, int suffixStart, int number,
            int suffixIndex) {
        T parent = accessor.getParents(child).get(0);
        Vector<T> v = new Vector<T>();
        v.add(parent);
        T middle = accessor.createInternalNode(v, suffixStart, splittingPos);
        int edgeLength = getEdgeLength(child);
        long x = sequences.get(child.getLabelStart() + getPathLength(child)
                - edgeLength);

        long y = sequences.get(child.getLabelStart() + getPathLength(child)
                - edgeLength + getEdgeLength(middle));

        accessor.addChild(parent, x, middle);
        accessor.addChild(middle, y, child);
        Vector<T> m = new Vector<T>();
        m.add(middle);
        accessor.setParents(child, m);
        doRule2(middle, splittingPos, suffixStart, number, suffixIndex);
        return middle;
    }

    private void finishAddition() {
        T leaf;
        ArrayList<T> leaves = getAllNodes(accessor.getRoot(), null, true);
        for (int i = 0; i < leaves.size(); i++) {
            leaf = leaves.get(i);
            if (leaf.getLabelEnd() == TO_A_LEAF)
                accessor.setLabelEnd(leaf, e);
        }

    }

    public void printTree(PrintWriter destination) {
        ArrayList<T> allNodes = getAllNodes(accessor.getRoot(), null, false);
        for (int i = 0; i < allNodes.size(); i++) {
            T node = allNodes.get(i);
            if (node.equals(accessor.getRoot()))
                destination.write("root");
            else {
                List<Long> thisLabel = getLabel(node);
                List<Long> parentLabel = getLabel(accessor.getParents(node)
                        .get(0));
                destination.write("NodeImpl " + i + " label: \t" + thisLabel
                        + " attached to: \t" + parentLabel + "\n");
            }
        }
    }

    /***************************************************************************
     * end Tree modification methods
     **************************************************************************/

    /**
     * Writes the tree as a dot text file to disk
     * 
     * <p/> TODO get this out of here, and into a class DotUtils, using a thin
     * interface like SuffixNode
     * 
     * @param root
     *            The root {@link SuffixNode} to export
     * @param dest
     *            The location in the file system to write to (eg. "out.dot")
     */
    public void exportDot(String dest) {
        try {
            String string = dest;
            BufferedWriter fileWriter = new BufferedWriter(new FileWriter(
                    new File(string)));
            fileWriter
                    .write("/* this is a generated dot file: www.graphviz.org */\n"
                            + "digraph suffixtree {\n"
                            + "\trankdir=LR\nnode[shape=box]");
            printDotBody(accessor.getRoot(), null, false, fileWriter, 0);
            fileWriter.write("}");
            fileWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private ArrayList<Node> printDotBody(T root, ArrayList<Node> list,
            boolean leavesOnly, BufferedWriter writer, int depth)
            throws IOException {
        tab(writer, depth);
        List<T> parents = accessor.getParents(root);
        if (parents != null) {
            for (Node node : parents) {
                String string = node.getId() + "->" + root.getId() + "\n";
                writer.write(string);
                tab(writer, depth);
                writer.write("[" + (parents.size() > 1 ? " style=dashed " : "")
                        + "label=\"" + getEdgeLabel(root).toString().trim()
                        + ",\\n Text: " + root.getTextNumber() + ", Suffix: "
                        + ((Node) root).getSuffixIndex() + "\"];\n");
            }
        }
        Iterator iterator;
        if (list == null) {
            list = new ArrayList<Node>();
        }
        if (!leavesOnly || (leavesOnly && accessor.isTerminal((T) root)))
            list.add(root);
        if (!accessor.isTerminal(root)) {
            iterator = accessor.getChildren(root).values().iterator();
            depth = depth + 1;
            while (iterator.hasNext()) {
                Node next = (Node) iterator.next();
                list = printDotBody((T) next, list, leavesOnly, writer, depth);
            }
        }
        return list;
    }

    /**
     * @param writer
     *            The writer to write tabs to
     * @param depth
     *            The current depth in the tree
     * @throws IOException
     *             If writing goes wrong
     */
    private void tab(BufferedWriter writer, int depth) throws IOException {
        for (int i = 0; i <= depth; i++) {
            writer.write("\t");
        }
    }

}

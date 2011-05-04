package com.quui.tm2.agents.features.suffixtree;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.quui.tm2.agents.features.suffixtree.node.Node;
import com.quui.tm2.agents.features.suffixtree.node.NodeAccessor;
import com.quui.tm2.agents.features.suffixtree.node.SuffixNode;

/**
 * Mapping of symbols in texts to symbols in the tree. Also provides the
 * dot-output.
 * 
 * @author fsteeg
 * 
 */
public class Mapper {
    private Map<Long, String> map;

    private NumericSuffixTree tree;

    private NodeAccessor accessor;

    public Mapper(NumericSuffixTree tree, NodeAccessor accessor) {
        this.tree = tree;
        map = new HashMap<Long, String>();
        this.accessor = accessor;
    }

    /**
     * Translates from the char-based internal representation to the actual word
     * based text
     * 
     * @param map
     *            The mapping of the symbols to the words
     * @param label
     *            The label to translate
     * @param cut
     *            If true the result is cut after 10 words
     * @return Returns the actual label, containing blank-separated words
     */
    public String translate(Map<Long, String> map, List<Long> label, boolean cut) {
        String res = "";
        int orig = label.size();
        if (label.size() != 0) {
            if (cut)
                label = label.subList(0, Math.min(10, label.size()));
            StringBuilder builder = new StringBuilder();
            for (int j = 0; j < label.size(); j++) {
                String string = map.get(label.get(j));
                if (label.get(j) == -1L)
                    string = "$";
                if (string == null) {
                    string = "" + label.get(j);
                }
                builder.append(string + " ");
            }
            res = builder.toString().trim();
        } else {
            res = "root";
        }
        if (cut && orig > 10)
            res += " [...]";
        return res;

    }

    public CharSequence getTranslatedEdgeLabel(Node child) {
        return translate(map, tree.getEdgeLabel(child), true);
    }

    public CharSequence getTranslatedLabel(Node node) {
        return translate(map, tree.getLabel(node), true);
    }

    public void put(long counter, String word) {
        map.put(counter, word);

    }

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
            printDotBody(tree.getRoot(), null, false, fileWriter, 0);
            fileWriter.write("}");
            fileWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private ArrayList<Node> printDotBody(Node root, ArrayList<Node> list,
            boolean leavesOnly, BufferedWriter writer, int depth)
            throws IOException {
        tab(writer, depth);
        List<Node> parents = accessor.getParents(root);
        if (parents != null) {
            for (Node node : parents) {
                String string = node.getId() + "->" + root.getId() + "\n";
                writer.write(string);
                tab(writer, depth);
                writer.write("["
                        + (parents.size() > 1 ? " style=dashed " : "")
                        + "label=\""
                        + getTranslatedEdgeLabel(root).toString().trim()
                        + ",\\n Text: " + root.getTextNumber() + ", Suffix: "
                        + ((Node) root).getSuffixIndex() + "\"];\n");
            }
        }
        Iterator iterator;
        if (list == null) {
            list = new ArrayList<Node>();
        }
        if (!leavesOnly || (leavesOnly && accessor.isTerminal(root)))
            list.add(root);
        if (!accessor.isTerminal(root)) {
            iterator = accessor.getChildren(root).values().iterator();
            depth = depth + 1;
            while (iterator.hasNext()) {
                Node next = (Node) iterator.next();
                list = printDotBody(next, list, leavesOnly, writer, depth);
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

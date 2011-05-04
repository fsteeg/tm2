package com.quui.tm2.agents.classifier.bayestree;

import java.io.FileWriter;
import java.io.IOException;

/**
 * Static export to a GraphViz/Dot file of the tree, showing the CPDs of the
 * nodes and the latest activation of each node
 * 
 * @author fsteeg
 * 
 */
public class GraphvizExport {
	/**
	 * @param root
	 *            The root node of the tree to export
	 * @param location
	 *            The location to save the exported file to
	 * @param digits
	 *            The number of digits to use when formatting double values
	 * @param compact
	 */
	public static void export(BayesNode root, String location, int digits,
			boolean compact) {
		String all = "digraph{rankdir=TD\n node[shape="
				+ (compact ? "point" : "record") + " style=rounded]\n edge["
				+ (compact ? "arrowhead=none" : "dir=back") + "] \n BODY }";
		String body = exportNodes(root, new StringBuilder(), digits)
				+ exportEdges(root, new StringBuilder());
		all = all.replaceAll("BODY", body);
		try {
			FileWriter writer = new FileWriter(location);
			writer.write(all);
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static String exportNodes(BayesNode node, StringBuilder builder,
			int digits) {
		String additional = "index: " + node.index + "\\\\n layer: "
				+ node.layer + " \\\\n";
		boolean verbose = false;
		String absCPDText = node.isRoot() ? "" : formatMatrix(node.absCPD, 0);
		String cpdText = formatMatrix(node.cpd, digits);
		String nodeText = node.index + "[label=\"{"
				+ (verbose ? additional : "") + "" + node.activation
				+ "\\\\n\\\\n" + absCPDText + "|" + cpdText
				+ /* "|" + absCPDText */"}\"]\n";
		builder.append(nodeText);
		for (BayesNode n : node.children) {
			if (n != null) {
				builder.append(exportNodes(n, new StringBuilder(), digits));
			}
		}
		return builder.toString();
	}

	private static String formatMatrix(float[][] cpd, int digits) {
		Float[][] nums = new Float[cpd.length][cpd[0].length];
		for (int i = 0; i < nums.length; i++) {
			for (int j = 0; j < nums[i].length; j++) {
				nums[i][j] = cpd[i][j];
			}
		}
		return formatMatrix(nums, digits);
	}

	private static String formatMatrix(int[][] cpd, int digits) {
		Integer[][] nums = new Integer[cpd.length][cpd[0].length];
		for (int i = 0; i < nums.length; i++) {
			for (int j = 0; j < nums[i].length; j++) {
				nums[i][j] = cpd[i][j];
			}
		}
		return formatMatrix(nums, digits);
	}

	private static String exportEdges(BayesNode node, StringBuilder builder) {
		if (node.parent != null)
			builder.append(node.parent.index + "->");
		builder.append(node.index + "\n");
		for (BayesNode n : node.children) {
			if (n != null) {
				builder.append(exportEdges(n, new StringBuilder()));
			}
		}
		return builder.toString();
	}

	private static String formatMatrix(Number[][] cpd, int digits) {
		StringBuilder buf = new StringBuilder();
		for (Number[] ds : cpd) {
			for (Number d : ds) {
				buf.append(ArrayTools.numberFormat(d, digits) + " ");
			}
			buf.append("\\\\n");
		}
		return buf.toString();
	}
}

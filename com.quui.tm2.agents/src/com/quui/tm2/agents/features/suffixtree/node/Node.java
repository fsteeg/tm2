package com.quui.tm2.agents.features.suffixtree.node;

import java.util.List;


public interface Node {

	public static final int A_LEAF = -1;
	
	long getId();
	
	int getTextNumber();

	int getLabelStart();
    
    void setLabelStart(int s);

	int getLabelEnd();
    
    void setLabelEnd(int end);

	int[] getAdditionalLabels();
    
    void setAdditionalLabels(int[] labels);

	//HashMap<Long, Node> getChildren();

	int getSuffixIndex();

	int getDfs();

    List<Node> getChildren();

    boolean isInternal();
	
}



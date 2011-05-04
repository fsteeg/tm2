package com.quui.tm2.doc;

import java.util.ArrayList;
import java.util.List;

import com.quui.tm2.Analysis;
import com.quui.tm2.Synthesis;

class ExperimentInfo {
    List<Analysis<?>> flows = new ArrayList<Analysis<?>>();
    List<Synthesis<?,?>> trains = new ArrayList<Synthesis<?,?>>();
    String title;
    String corpusLocation;
    String resultAnnotationsLocation;
    List<String> agentSources = new ArrayList<String>();
    List<AgentInfo> agentNames = new ArrayList<AgentInfo>();
    List<String> referencedFiles = new ArrayList<String>();
    String evalAgent;
    String evalResult;
    long took;
    public String toDot(){
        return new ExperimentDotTemplate().generate(this);
    }
}

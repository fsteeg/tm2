package com.quui.tm2.doc;

import java.util.*;
import java.io.*;
import com.quui.tm2.*;
import com.quui.tm2.doc.ExportHelper.*;
import com.quui.tm2.util.*;

public class ExperimentDotTemplate
{
  protected static String nl;
  public static synchronized ExperimentDotTemplate create(String lineSeparator)
  {
    nl = lineSeparator;
    ExperimentDotTemplate result = new ExperimentDotTemplate();
    nl = null;
    return result;
  }

  public final String NL = nl == null ? (System.getProperties().getProperty("line.separator")) : nl;
  protected final String TEXT_1 = "";
  protected final String TEXT_2 = NL + NL + "digraph experiment {" + NL + "\trankdir=TD" + NL + "\tnode[shape=box style=filled width=1.8]" + NL + "\tsubgraph cluster_0 {" + NL + "\t" + NL + "\t\t";
  protected final String TEXT_3 = "subgraph cluster_";
  protected final String TEXT_4 = " {" + NL + "\t\t\t\t\t\t";
  protected final String TEXT_5 = " -> ";
  protected final String TEXT_6 = "[style=dashed label=\"";
  protected final String TEXT_7 = "\"];" + NL + "\t\t\t\t\t\t";
  protected final String TEXT_8 = " -> ";
  protected final String TEXT_9 = "[style=dashed label=\"";
  protected final String TEXT_10 = "\"]" + NL + "\t\t\t\t\t\tlabel=\"Synthesis(";
  protected final String TEXT_11 = ",";
  protected final String TEXT_12 = ")\"" + NL + "\t\t\t\t  }" + NL + "\t\t\t\t";
  protected final String TEXT_13 = NL + "\t" + NL + "\t\t";
  protected final String TEXT_14 = NL + "\t\t\t\t";
  protected final String TEXT_15 = " -> ";
  protected final String TEXT_16 = "[style=solid label=\"";
  protected final String TEXT_17 = "\"]" + NL + "\t\t";
  protected final String TEXT_18 = NL + "\t\t" + NL + "\t\tlabel=\"";
  protected final String TEXT_19 = ":\"" + NL + "\t}" + NL + "\teval[style=filled fillcolor=green label=\"Result:\\n";
  protected final String TEXT_20 = "\" width=1.5]" + NL + "\t";
  protected final String TEXT_21 = " -> eval[style=dashed arrowhead=open]" + NL + "\t" + NL + "}";

  public String generate(Object argument)
  {
    final StringBuffer stringBuffer = new StringBuffer();
    stringBuffer.append(TEXT_1);
     
ExperimentInfo meta = (ExperimentInfo) argument;
List<Analysis<?>> list = meta.flows;
List<Synthesis<?,?>> trainings = meta.trains;
 
    stringBuffer.append(TEXT_2);
    
		for (Synthesis t : trainings) {
			for(Object obs : t.data()) {
				for(Object inf : t.info()) {
					String obsName = (obs.getClass().getSimpleName().length()==0?obs.getClass().toString():obs.getClass().getSimpleName()).replace("$","").replaceAll("\\d","");
					String infName = (inf.getClass().getSimpleName().length()==0?inf.getClass().toString():inf.getClass().getSimpleName()).replace("$","").replaceAll("\\d","");
						String so = t.model().getClass().toString().replace("$","");
				
    stringBuffer.append(TEXT_3);
    stringBuffer.append(Math.abs(inf.hashCode()+obs.hashCode()));
    stringBuffer.append(TEXT_4);
    stringBuffer.append(obsName.substring(obsName.lastIndexOf(".")+1,obsName.length()));
    stringBuffer.append(TEXT_5);
    stringBuffer.append(so.substring(so.lastIndexOf(".")+1,so.length()));
    stringBuffer.append(TEXT_6);
    stringBuffer.append(t.getInfoTypeClass()==null||!Preferences.edgeLabels?"":" "+t.getInfoTypeClass().getSimpleName());
    stringBuffer.append(TEXT_7);
    stringBuffer.append(infName.substring(infName.lastIndexOf(".")+1,infName.length()));
    stringBuffer.append(TEXT_8);
    stringBuffer.append(so.substring(so.lastIndexOf(".")+1,so.length()));
    stringBuffer.append(TEXT_9);
    stringBuffer.append(t.getDataTypeClass()==null||!Preferences.edgeLabels?"":" "+t.getDataTypeClass().getSimpleName());
    stringBuffer.append(TEXT_10);
    stringBuffer.append(obsName.substring(obsName.lastIndexOf(".")+1,obsName.length()));
    stringBuffer.append(TEXT_11);
    stringBuffer.append(infName.substring(infName.lastIndexOf(".")+1,infName.length()));
    stringBuffer.append(TEXT_12);
    	
				}
			}
		}
		
    stringBuffer.append(TEXT_13);
    
		for (Analysis f : list) {
			for(Object sii : f.sources()) {
				String si = sii.getClass().getSimpleName().replace("$","").replaceAll("\\d","");
				for(Object soo : f.targets()) {
				String so = soo.getClass().getSimpleName().replace("$","").replaceAll("\\d","");
		
    stringBuffer.append(TEXT_14);
    stringBuffer.append(si.substring(si.lastIndexOf(".")+1,si.length()));
    stringBuffer.append(TEXT_15);
    stringBuffer.append(so.substring(so.lastIndexOf(".")+1,so.length()));
    stringBuffer.append(TEXT_16);
    stringBuffer.append(f.getTypeClass()==null||!Preferences.edgeLabels?"":" " + f.getTypeClass().getSimpleName());
    stringBuffer.append(TEXT_17);
    	
				}
			}
		}
		
    stringBuffer.append(TEXT_18);
    stringBuffer.append(meta.title);
    stringBuffer.append(TEXT_19);
    stringBuffer.append(meta.evalResult!=null?meta.evalResult.split(";")[0]:"[Not available]");
    stringBuffer.append(TEXT_20);
    stringBuffer.append(meta.evalAgent);
    stringBuffer.append(TEXT_21);
    return stringBuffer.toString();
  }
}

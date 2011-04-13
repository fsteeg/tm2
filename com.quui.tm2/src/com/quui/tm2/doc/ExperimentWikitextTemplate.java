package com.quui.tm2.doc;

import java.util.*;
import com.quui.tm2.*;
import com.quui.tm2.doc.ExportHelper.*;

public class ExperimentWikitextTemplate
{
  protected static String nl;
  public static synchronized ExperimentWikitextTemplate create(String lineSeparator)
  {
    nl = lineSeparator;
    ExperimentWikitextTemplate result = new ExperimentWikitextTemplate();
    nl = null;
    return result;
  }

  public final String NL = nl == null ? (System.getProperties().getProperty("line.separator")) : nl;
  protected final String TEXT_1 = "=";
  protected final String TEXT_2 = "=" + NL + "" + NL + "==Resources==" + NL + "" + NL + "{|style=\"text-align:center\"" + NL + "! Corpus Data !! Result Annotations !! Evaluation Result !! Gold Location" + NL + "|-" + NL + "| [";
  protected final String TEXT_3 = "] " + NL + "| [";
  protected final String TEXT_4 = "]" + NL + "| ";
  protected final String TEXT_5 = NL + "| [";
  protected final String TEXT_6 = "]" + NL + "|}" + NL + "" + NL + "==TM2 Setup==" + NL + "" + NL + "[[Image:";
  protected final String TEXT_7 = "]]" + NL + "" + NL + "==Agent Details==" + NL;
  protected final String TEXT_8 = NL + NL + "===";
  protected final String TEXT_9 = "===" + NL + "[[Image:";
  protected final String TEXT_10 = "|right]]" + NL + "''Source'': [";
  protected final String TEXT_11 = "] <br/>" + NL + "''Config'': <nowiki>";
  protected final String TEXT_12 = "</nowiki>" + NL;
  protected final String TEXT_13 = NL + NL + "''This documentation was generated on ";
  protected final String TEXT_14 = " by the TM2 system.''";

  public String generate(Object argument)
  {
    final StringBuffer stringBuffer = new StringBuffer();
     
ExperimentInfo meta = (ExperimentInfo) argument;
List<Analysis<?>> list = meta.flows;
List<AgentInfo> names = meta.agentNames;
Iterator<AgentInfo> iter = names.iterator();
 
    stringBuffer.append(TEXT_1);
    stringBuffer.append(meta.title);
    stringBuffer.append(TEXT_2);
    stringBuffer.append(meta.corpusLocation.replaceAll("[\\[\\]]",""));
    stringBuffer.append(TEXT_3);
    stringBuffer.append(meta.resultAnnotationsLocation.replaceAll("[\\[\\]]",""));
    stringBuffer.append(TEXT_4);
    stringBuffer.append(meta.evalResult!=null?meta.evalResult.split(";")[0]:"[ not available ]");
    stringBuffer.append(TEXT_5);
    stringBuffer.append(meta.evalResult!=null?meta.evalResult.split(";")[1]:"[ not available ]");
    stringBuffer.append(TEXT_6);
    stringBuffer.append(meta.referencedFiles.get(0));
    stringBuffer.append(TEXT_7);
     int c = 0; for (Iterator i = meta.agentSources.iterator(); i.hasNext(); c++) { 
    AgentInfo info = iter.next();
    stringBuffer.append(TEXT_8);
    stringBuffer.append(info.getName());
    stringBuffer.append(TEXT_9);
    stringBuffer.append(meta.referencedFiles.get(c+1));
    stringBuffer.append(TEXT_10);
    stringBuffer.append(i.next());
    stringBuffer.append(TEXT_11);
    stringBuffer.append(info.getConfig());
    stringBuffer.append(TEXT_12);
     } 
    stringBuffer.append(TEXT_13);
    stringBuffer.append(new Date());
    stringBuffer.append(TEXT_14);
    return stringBuffer.toString();
  }
}

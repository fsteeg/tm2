package com.quui.tm2.doc;

import java.util.*;
import java.io.*;
import com.quui.tm2.*;

public class AgentDotTemplate
{
  protected static String nl;
  public static synchronized AgentDotTemplate create(String lineSeparator)
  {
    nl = lineSeparator;
    AgentDotTemplate result = new AgentDotTemplate();
    nl = null;
    return result;
  }

  public final String NL = nl == null ? (System.getProperties().getProperty("line.separator")) : nl;
  protected final String TEXT_1 = "";
  protected final String TEXT_2 = NL + NL + "digraph{" + NL + "\t/* Global settings */" + NL + "\tsize=\"3,8\"" + NL + "\trankdir=LR" + NL + "\t//label=\"";
  protected final String TEXT_3 = "\"" + NL + "\tnode[shape=record width=0.5]" + NL + "\tedge[arrowhead=open style=dashed]" + NL + "\t/* Nodes */" + NL + "\tsource[style=dashed label=\"\"];" + NL + "\tagent[label=\"{";
  protected final String TEXT_4 = "}\" width=1.5];" + NL + "\ttarget[style=dashed label=\"\"];" + NL + "\t/* Edges */" + NL + "\tsource->agent[label=\"";
  protected final String TEXT_5 = "\"];" + NL + "\tagent->target[label=\"";
  protected final String TEXT_6 = "\"];" + NL + "}";

  public String generate(Object argument)
  {
    final StringBuffer stringBuffer = new StringBuffer();
    stringBuffer.append(TEXT_1);
     
AgentInfo agent = (AgentInfo) argument;
 
    stringBuffer.append(TEXT_2);
    stringBuffer.append(agent.config);
    stringBuffer.append(TEXT_3);
    stringBuffer.append(agent.name);
    stringBuffer.append(TEXT_4);
    stringBuffer.append(agent.input);
    stringBuffer.append(TEXT_5);
    stringBuffer.append(agent.output);
    stringBuffer.append(TEXT_6);
    return stringBuffer.toString();
  }
}

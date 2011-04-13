package com.quui.amas.doc;

import java.util.*;
import com.quui.amas.*;
import com.quui.amas.doc.Export.TemplateAttribute;

public class ExperimentLatexTemplate
{
  protected static String nl;
  public static synchronized ExperimentLatexTemplate create(String lineSeparator)
  {
    nl = lineSeparator;
    ExperimentLatexTemplate result = new ExperimentLatexTemplate();
    nl = null;
    return result;
  }

  public final String NL = nl == null ? (System.getProperties().getProperty("line.separator")) : nl;
  protected final String TEXT_1 = "";
  protected final String TEXT_2 = NL + NL + "\\documentclass[notitlepage,a4paper,abstracton]{scrartcl}" + NL + "\\usepackage{natbib}" + NL + "\\usepackage[colorlinks, urlcolor=blue, linkcolor=black]{hyperref}" + NL + "\\usepackage[utf8]{inputenc}" + NL + "\\usepackage[]{paralist}" + NL + "\\usepackage[]{epsfig}" + NL + "\\usepackage[inner=2cm,outer=2cm,top=2cm,bottom=3.5cm]{geometry}" + NL + "\\usepackage[automark,headsepline,headinclude]{scrpage2}" + NL + "\\setlength\\parskip{\\smallskipamount} \\setlength\\parindent{0pt}" + NL + "\\usepackage{listings}" + NL + "" + NL + "\\begin{document}" + NL + "%\\thispagestyle{empty}" + NL + "\\section{";
  protected final String TEXT_3 = "}" + NL + "" + NL + "\\subsection{Overview}" + NL + "This is generated documentation for the experiment ``";
  protected final String TEXT_4 = "''. Corpus is crawled from ``";
  protected final String TEXT_5 = "'', results are written to ``";
  protected final String TEXT_6 = "''. This experiment consists of ";
  protected final String TEXT_7 = " flows. This document was generated on ";
  protected final String TEXT_8 = "." + NL + "\\begin{center}" + NL + "\\includegraphics[width=14cm]{";
  protected final String TEXT_9 = "}" + NL + "\\end{center}" + NL + "" + NL + "\\subsection{Data}" + NL + "" + NL + "Results were optained from following input (in file \\lstinline!";
  protected final String TEXT_10 = ")!:" + NL + "" + NL + "\\emph{";
  protected final String TEXT_11 = "}" + NL + "" + NL + "\\subsection{Agents}" + NL;
  protected final String TEXT_12 = NL + "\\subsubsection{Agent ";
  protected final String TEXT_13 = "}" + NL + "\\includegraphics[width=12cm]{";
  protected final String TEXT_14 = "}" + NL + "\\lstset{language={Java}, basicstyle=\\scriptsize, commentstyle=\\normalsize}" + NL + "\\begin{lstlisting}";
  protected final String TEXT_15 = NL;
  protected final String TEXT_16 = NL + "\\end{lstlisting}";
  protected final String TEXT_17 = NL + NL + "\\subsection{Results}" + NL + "" + NL + "The agents produced following annotations:" + NL + "" + NL + "\\subsubsection{Human-readable}";
  protected final String TEXT_18 = NL;
  protected final String TEXT_19 = NL + NL + "\\subsubsection{XML}" + NL + "" + NL + "\\lstset{language={XML}, basicstyle=\\scriptsize}" + NL + "\\begin{lstlisting}";
  protected final String TEXT_20 = NL;
  protected final String TEXT_21 = NL + "\\end{lstlisting}" + NL;
  protected final String TEXT_22 = NL + NL + "\\subsection{Evaluation}" + NL + "" + NL + "The result has been evaluated against the following gold standard (read from \\lstinline!";
  protected final String TEXT_23 = "!):" + NL + "" + NL + "\\lstset{language={XML}, basicstyle=\\scriptsize}" + NL + "\\begin{lstlisting}";
  protected final String TEXT_24 = NL;
  protected final String TEXT_25 = NL + "\\end{lstlisting}" + NL + "" + NL + "The evaluation resulted in the following result: ";
  protected final String TEXT_26 = NL + NL + "\\end{document}";
  protected final String TEXT_27 = NL;

  public String generate(Object argument)
  {
    final StringBuffer stringBuffer = new StringBuffer();
    stringBuffer.append(TEXT_1);
     
Map<String, List> map = (Map<String,List>) argument;
List<Interaction> list = (List<Interaction>) map.get(TemplateAttribute.FLOWS.toString());
List<String> names = (List<String>)map.get(TemplateAttribute.NAMES.toString());
Iterator<String> iter = names.iterator();
 
    stringBuffer.append(TEXT_2);
    stringBuffer.append(map.get(TemplateAttribute.TITLE.toString()).get(0));
    stringBuffer.append(TEXT_3);
    stringBuffer.append(map.get(TemplateAttribute.TITLE.toString()));
    stringBuffer.append(TEXT_4);
    stringBuffer.append(map.get(TemplateAttribute.CORPUS_LOCATION.toString()));
    stringBuffer.append(TEXT_5);
    stringBuffer.append(map.get(TemplateAttribute.RESULT_LOCATION.toString()));
    stringBuffer.append(TEXT_6);
    stringBuffer.append(list.size());
    stringBuffer.append(TEXT_7);
    stringBuffer.append(new Date());
    stringBuffer.append(TEXT_8);
    stringBuffer.append(map.get(TemplateAttribute.FILES.toString()).get(0));
    stringBuffer.append(TEXT_9);
    stringBuffer.append(map.get(TemplateAttribute.CORPUS_LOCATION.toString()).get(0));
    stringBuffer.append(TEXT_10);
    stringBuffer.append(map.get(TemplateAttribute.CORPUS_TEXT.toString()).get(0));
    stringBuffer.append(TEXT_11);
     int c = 0; for (Iterator i = map.get(TemplateAttribute.SOURCES.toString()).iterator(); i.hasNext(); c++) { 
    stringBuffer.append(TEXT_12);
    stringBuffer.append(iter.next().replace("$","\\$"));
    stringBuffer.append(TEXT_13);
    stringBuffer.append(map.get(TemplateAttribute.FILES.toString()).get(c+1));
    stringBuffer.append(TEXT_14);
    stringBuffer.append(TEXT_15);
    stringBuffer.append(i.next());
    stringBuffer.append(TEXT_16);
     } 
    stringBuffer.append(TEXT_17);
    stringBuffer.append(TEXT_18);
    stringBuffer.append(map.get(TemplateAttribute.ANNOTATED.toString()));
    stringBuffer.append(TEXT_19);
    stringBuffer.append(TEXT_20);
    stringBuffer.append(map.get(TemplateAttribute.RESULT_TEXT.toString()).get(0));
    stringBuffer.append(TEXT_21);
    
List evalResult = map.get(TemplateAttribute.EVALUATION_RESULT.toString());
 
    stringBuffer.append(TEXT_22);
    stringBuffer.append(evalResult!=null?evalResult.get(0):"[not available]");
    stringBuffer.append(TEXT_23);
    stringBuffer.append(TEXT_24);
    stringBuffer.append(evalResult!=null?evalResult.get(1):"[not available]");
    stringBuffer.append(TEXT_25);
    stringBuffer.append(evalResult!=null?evalResult.get(2):"[not available]");
    stringBuffer.append(TEXT_26);
    stringBuffer.append(TEXT_27);
    return stringBuffer.toString();
  }
}

package com.quui.tm2.ui;

import java.io.Serializable;
import java.util.List;

import com.quui.tm2.Agent;
import com.quui.tm2.Annotation;
import com.quui.tm2.Retrieval;

public class ConsoleRetrieval {
  private static final String SYNTAX = "Synatx: file [ list (agent) | find \"term\" agent1 agent2]";

  public static void main(String[] args) {
    if (args.length < 2) {
      System.out.println(SYNTAX);
      System.exit(-1);
    }
    Retrieval r = new Retrieval(args[0]);
    if (args[1].equals("list")) {
      if (args.length == 2) {
        System.out.println("Available agents:");
        List<String> agents = r.getAgents();
        for (String string : agents) {
          System.out.println(string);
        }
      } else {
        System.out.println("Annotations for agent " + args[2] + ":");
        List<Annotation<String>> annotations = getAnnotations(args[2], r);
        for (Annotation<String> annotation : annotations) {
          System.out.println(annotation.getValue());
        }
      }
    } else if (args[1].equals("find")) {
      if (args.length < 5) {
        System.out.println(SYNTAX);
        System.exit(-1);
      }
      List<Annotation<String>> search = search(args[3], args[2], args[4], r);
      System.out.println("Annotations of " + args[3] + " matching '" + args[2]
          + "' by " + args[4]);
      boolean context = true;
      for (Annotation<String> annotation : search) {
        System.out.println(annotation.getValue()
            + (context ? " in context: " + context(annotation, 20, r.getData())
                : ""));
      }
    } else {
      System.out.println(SYNTAX);
    }
  }

  public static <T extends Comparable<T> & Serializable> List<Annotation<T>> getAnnotations(
      String agentName, Retrieval r) {
    List<Annotation<T>> l;
    Class<? extends Agent<?, T>> agentClass = null;
    try {
      /* TODO Can we avoid this cast? I don't think so. */
      agentClass = (Class<? extends Agent<?, T>>) Class.forName(agentName);
    } catch (ClassNotFoundException e) {
      throw wrap(agentName, e);
    }
    if (agentClass != null) {
      l = r.annotationsOf(agentClass);
      return l;
    }
    return null;
  }

  public static <S extends Comparable<S> & Serializable, R extends Comparable<R> & Serializable> List<Annotation<R>> search(
      String searchIn, S searchValue, String returnFrom, Retrieval r) {
    /* TODO test this for non-string-producing agents */
    Class<Agent<?, S>> searchInC = reflectiveInstantiation(searchIn);
    Class<Agent<?, R>> returnFromC = reflectiveInstantiation(returnFrom);
    List<Annotation<R>> result = r.search(searchInC, searchValue, returnFromC);
    return result;
  }

  private static <T extends Comparable<T> & Serializable> Class<Agent<?, T>> reflectiveInstantiation(
      String agentName) {
    try {
      /* TODO Can we avoid this cast? I don't think so. */
      return (Class<Agent<?, T>>) Class.forName(agentName);
    } catch (ClassNotFoundException e) {
      throw wrap(agentName, e);
    }
  }

  private static String context(Annotation<?> annotation, int window,
      String data) {
    String result = data.substring(Math.max(annotation.getStart().intValue()
        - window, 0), annotation.getStart().intValue())
        + "["
        + data.substring(annotation.getStart().intValue(), annotation.getEnd()
            .intValue())
        + "]"
        + data.substring(annotation.getEnd().intValue(), Math.min(annotation
            .getEnd().intValue()
            + window, data.length() - 1));
    return "..." + result + "...";
  }

  private static IllegalArgumentException wrap(String name,
      ClassNotFoundException e) {
    IllegalArgumentException exception = new IllegalArgumentException(
        "Unknown Agent: " + name);
    exception.initCause(e);
    return exception;
  }
}

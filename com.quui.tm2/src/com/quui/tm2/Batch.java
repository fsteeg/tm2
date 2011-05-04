package com.quui.tm2;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URI;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.eclipse.mylyn.wikitext.core.parser.MarkupParser;
import org.eclipse.mylyn.wikitext.textile.core.TextileLanguage;

import com.quui.tm2.doc.WikitextExport;
import com.quui.tm2.util.Preferences;

/**
 * @author Fabian Steeg (fsteeg)
 */
public final class Batch {

  /**
   */
  private Batch() {}

  /**
   * @param experiments The experiments to run
   */
  public static void run(final List<Experiment> experiments) {
    run(experiments.toArray(new Experiment[] {}));
  }

  /**
   * @param experiments The experiments to run
   */
  // TODO Better with a list? To avoid arrays altogether?
  public static void run(final Experiment... experiments) {
	  //TODO replace with JET template
    StringBuilder builder = new StringBuilder(
        "<style type=\"text/css\"> #tableborders td {border: 1px solid #ccc;  padding: .1em .25em;} </style>\n\n");
    builder
        .append("h1. Batch Evaluation\n\n"
            + "table(#tableborders){ border: 2px solid #ccc; border-collapse: collapse; border-spacing: 0; width:100%;}.\n"
            + "| *Result* | *Details* | *Syntheses* | *Models* | *Time* | *Evaluation* | \n"); // *Analyses*
    ExecutorService exec = Executors.newFixedThreadPool(1/*experiments.length*/);
    for (Experiment experiment : experiments) {
      if (experiment == null) {
        throw new IllegalArgumentException("Null experiment");
      }
      exec.execute(experiment);
    }
    exec.shutdown();
    try {
      exec.awaitTermination(Long.MAX_VALUE, TimeUnit.SECONDS);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
    SynchronizedExperiment max = (SynchronizedExperiment) experiments[0];
    for (Experiment experiment : experiments) {
      WikitextExport.of(experiment); // can't call dot concurrently
      builder.append(experiment).append("\n");
      Evaluation evaluation = ((SynchronizedExperiment) experiment).getEvaluation();
	  if (max != null && evaluation != null
			&& evaluation.getF() > max.getEvaluation().getF()) {
		max = (SynchronizedExperiment) experiment;
	  }
    }
    File output = writeResult(builder);
    
    //TODO what to return? batch location? best result?
    System.out.println("Wrote batch result to: " + output.getAbsolutePath());
    System.out.println("Best result: " + max);
  }

  private static File writeResult(StringBuilder builder) {
    MarkupParser markupParser = new MarkupParser();
    markupParser.setMarkupLanguage(new TextileLanguage());
    String htmlContent = markupParser.parseToHtml(builder.toString());
    File output = new File(new File(URI.create(Preferences.get(Preferences.Default.ROOT))),
        "batch-result.html");
    try {
      FileWriter writer = new FileWriter(output);
      writer.write(htmlContent);
      writer.close();
    } catch (IOException e) {
      e.printStackTrace();
    }
    return output;
  }
}

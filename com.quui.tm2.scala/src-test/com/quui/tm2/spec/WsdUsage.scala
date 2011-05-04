package com.quui.tm2.spec

import org.junit.runner.RunWith
import org.scalatest.Spec
import org.scalatest.junit.JUnitRunner
import org.scalatest.matchers.ShouldMatchers
import com.quui.tm2.agents.classifier.console.Pseudowords
import com.quui.tm2.agents.classifier.Classifier
import com.quui.tm2.agents.features.Features
import scala.collection.JavaConversions._
import com.quui.tm2.agents._
import com.quui.tm2.Exp._
import com.quui.tm2.RichAgent._
import com.quui.tm2.RichAnalysis._
import com.quui.tm2.RichSynthesis._
import com.quui.tm2.agents._
import com.quui.tm2._
import com.quui.tm2._
import com.quui.tm2.agents._
import com.quui.tm2.types._
import java.math.BigInteger
import weka.classifiers.bayes._
import weka.classifiers.functions._
import weka.classifiers.misc._

@RunWith(classOf[JUnitRunner])
class WsdUsage extends Spec with ShouldMatchers {
  describe("A WSD evaluation") {
    it("it can contain a single Pseudoword setup") {
      import java.util.ArrayList
      import java.util.Arrays._

      class Ambig extends PseudoAmbig("and", "the")
      class Gold extends PseudoAmbig("and", "the")
      val (corpus, ambiguity, gold, tokenizer, evaluation) = (new Corpus,
        new Ambig,
        new Gold,
        new Tokenizer,
        new SimpleEvaluation[String])
      val (features, classifier) = (new Features(new ArrayList(List() ++ Set() ++ corpus.text.split(Pseudowords.split)), "paradigms", 4), // paradigms -> suffix-tree-based
        new Classifier(asList(8), 1f, /*new SMO*/null, "S0", "S1")) // null -> BayesTree

      print(String.format(
        "Exp with corpus [%s], ambiguity [%s], gold [%s], tokenizer [%s], features [%s] " +
        "classifier [%s], evaluation [%s]\n",
        corpus, ambiguity, gold, tokenizer, features, classifier, evaluation))

      // The actual experiment:
      val x =
        /* We tokenize the corpus and create both features and pseudo-ambiguity for the tokens: */
        corpus -> tokenizer | tokenizer -> ambiguity | tokenizer + ambiguity -> features | ambiguity -> features |
          /* We use the features and the pseudo-ambiguity to train the WSD classifier: */
          features + ambiguity -> classifier |
          /* And finally classify the same features, evaluating the result against the pseudowords: */
          features -> classifier | tokenizer -> gold | classifier + gold -> evaluation !

      x.name("Pseudoword-Evaluation")
      println(x)
      println((evaluation).getResultString)
    }
  }
}

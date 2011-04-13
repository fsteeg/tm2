package com.quui.tm2.usage

import com.quui.tm2.agents.classifier.Classifier
import com.quui.tm2.agents.classifier.console.Pseudowords
import com.quui.tm2.agents.features.Features
import scala.collection.JavaConversions._
import scala.collection.mutable.ListBuffer
import com.quui.tm2.agents._
import com.quui.tm2.doc._
import com.quui.tm2.agents._
import com.quui.tm2.RichAgent._
import com.quui.tm2.RichAnalysis._
import com.quui.tm2.agents._
import com.quui.tm2._
import com.quui.tm2._
import com.quui.tm2.Batch._
import java.util.ArrayList
import com.quui.tm2.spec._
import com.quui.tm2.Exp._
//import weka.classifiers._
import weka.classifiers.bayes._
import weka.classifiers.functions._
import weka.classifiers.misc._
//TODO move all implicit conversions to an AmasPreamble
object WsdUsageMulti extends Application {
  override def main(args: Array[String]): Unit = {
    val title = "Pseudoword-Evaluation"
    class Ambig extends PseudoAmbig("and", "the")
    class Gold extends PseudoAmbig("and", "the")
    run {
      for {
        cPseudo <- List(
          "and the",
          "in of",
          "text data",
          "the text"
          ) // TODO "and the in of"

        cTrain <- List(
          AmasSpec.Corpus //,//"http://quui.de/data/faust-1.txt", 
          //AmasSpec.Corpus)//"http://quui.de/data/faust-2.txt"
          )

        cTest <- List(
          AmasSpec.Corpus //,//"http://quui.de/data/faust-1.txt", 
          //AmasSpec.Corpus
          ) //"http://quui.de/data/faust-2.txt")

        cFeat <- List(
          "word",
          "length",
          "3-gram",
          "7-gram" //,
          //              "paradigms"
          )

        cCont <- List[(Int, java.util.List[Integer])](
          (2, java.util.Arrays.asList(4)),
          (4, java.util.Arrays.asList(8))) //TODO support multi-layer with weka, need context
        //              (8, java.util.Arrays.asList(2,8)), 
        //              (8, java.util.Arrays.asList(16)))

        pFact <- List(1f) // .5f, 1.5f

        cClass <- List(new BayesNet) //new BayesNet, new NaiveBayes, new SMO, new HyperPipes

        cor = new Corpus
        amb = new Ambig
        gol = new Gold
        tok = new Tokenizer
        fea = new Features(
          new ArrayList(List() ++ Set() ++ cor.text.split(Pseudowords.split)),
          cFeat, cCont._1)
        val s: String = ""
        wsd = new Classifier(cCont._2, pFact, cClass, "S0", "S1")
        eva = new SimpleEvaluation[String]

      } yield {
        import com.quui.tm2.RichAnalysis.analysisWrapper // FIXME why import req. here?
        import com.quui.tm2.RichSynthesis.synthesisWrapper

        /* We tokenize the corpus and create both features and pseudo-ambiguity for the tokens: */
        cor -> tok | tok -> amb | tok + amb -> fea | amb -> fea |
          /* We use the features and the pseudo-ambiguity to train the WSD classifier: */
          fea + amb -> wsd |
          /* And finally classify the same features, evaluating the result against the pseudowords: */
          fea -> wsd | tok -> gol | wsd + gol -> eva

      }
    }
  }

}

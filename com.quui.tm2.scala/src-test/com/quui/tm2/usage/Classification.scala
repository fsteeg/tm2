package com.quui.tm2.usage
import com.quui.tm2.agents.features.Features

/** The agents we use are mostly Java implementations of the Agent interface: */
import com.quui.tm2.agents._
/** Import the implicit conversion from Agent to RichAgent, to add support for the
 *  DSL syntax: */
import com.quui.tm2.Exp._
import com.quui.tm2.RichAnalysis._
import com.quui.tm2.Exp
import com.quui.tm2.agents.classifier.Classifier
/**A more realistic complex setup, for agents that are not implemented yet:*/
object Classification extends Application {
  /** With for expressions: */
  for {
    /** The different configs: */
    
    text <- List(com.quui.tm2.spec.Tm2Spec.Corpus, com.quui.tm2.spec.Tm2Spec.Corpus)
    feat <- List("Term", "Vector")
    algo <- List("NaiveBayes", "SVM")
    
    /** For each combination, we create our agents: */
    
    c_corp = new Corpus(text)
    c_toks = new Tokenizer()
    c_feat = new Features(feat)
    c_clas:Classifier = new Classifier(algo)
    
    /** And construct an experiment with them: */
    
    x = (
      c_corp -> c_toks | 
      c_toks -> c_feat | 
      c_feat -> c_clas )
    .name("Classification experiment with " 
           + feat + ", " + algo)
    
  } yield x.run
}
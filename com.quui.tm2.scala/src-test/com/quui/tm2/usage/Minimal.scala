package com.quui.tm2.usage
/** The agents we use are mostly Java implementations of the Agent interface: */
import com.quui.tm2.agents._
import com.quui.tm2.agents._
/** Import the implicit conversion from Agent to RichAgent, to add support for the
 *  DSL syntax: */
import com.quui.tm2.Exp._
import com.quui.tm2.RichAnalysis._
import com.quui.tm2.Exp
import com.quui.tm2.Analysis
import com.quui.tm2.util.Preferences
import com.quui.tm2.util.Preferences.Default
object Minimal extends Application {
  
    //-----------------------------------------------------------------------------------------------
  
    /** We have a rich agent wrapper that defines a > method, 
     *  which returns a typed interaction between a source agent
     *  and one or more target agents: */
      
    new Corpus() -> new Tokenizer() : Analysis[String]
    
    //-----------------------------------------------------------------------------------------------
    
    val corpus = new Corpus("http://www.gutenberg.org/files/14591/14591-8.txt")
    val tokenizer = new Tokenizer
    val gazetteer = new Gazetteer
    
    //-----------------------------------------------------------------------------------------------
    
    /** Don't specify any resources, take all default values from the properties file: */
    Exp ( corpus -> tokenizer, tokenizer -> gazetteer )().run
    /** Or, shorter: */
    corpus -> tokenizer | tokenizer -> gazetteer !
    
   //-----------------------------------------------------------------------------------------------
    
    /** Full configuration of an experiment: */
    import com.quui.tm2.spec._
    Exp( corpus -> tokenizer, tokenizer -> gazetteer,// + new Reverser(), 
        gazetteer -> new SimpleEvaluation[String](Preferences.get(Default.GOLD)))().
      /** Experiment metadata (optional): */
      name("Sample Scala Amas Experiment").
      root(Preferences.get(Default.ROOT)).
      /** As in the simple case, in the end we run the experiment: */
      run
    /** Or, shorter: */
    (corpus -> tokenizer | tokenizer -> gazetteer | gazetteer -> new SimpleEvaluation[String](
         Preferences.get(Default.GOLD))).
    name("Sample Scala Amas Experiment").
    root(Preferences.get(Default.ROOT)).
    !
    //-----------------------------------------------------------------------------------------------
}

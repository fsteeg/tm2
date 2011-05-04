package com.quui.tm2.usage
/** The agents we use are mostly Java implementations of the Agent interface: */
import com.quui.tm2.agents._
import com.quui.tm2.spec.Tm2Spec
/** But not all: */
import com.quui.tm2.agents._
/** Import the implicit conversion from Agent to RichAgent, to add support for the DSL syntax: */
import com.quui.tm2.Exp._
import com.quui.tm2.Exp
import com.quui.tm2.util.Preferences
import com.quui.tm2.util.Preferences.Default
object ComplexAlternative extends Application{
  /** Using for expressions, we can easily define multiple experiments with different configurations: */
  for {
    /** The different configurations to try: */
    corpus <- List(
      "http://www.gutenberg.org/files/14591/14591-8.txt",
      "http://www.gutenberg.org/files/14460/14460-8.txt")
    regexp <- List(" ","\\W")
    /** The agents of one configuration: */
    agentCorpus = new Corpus(corpus)
    agentTokens = new Tokenizer()
    agentGazett = new Gazetteer()
  }
  /** The following happens once for each configuration described above: */
  
  yield 
    /** The experiment setup for each configuration: */
    Exp(
      agentCorpus -> agentTokens, 
      agentTokens -> agentGazett,
      agentGazett -> new Tokenizer(),//+ new Reverser(),
      agentGazett -> new SimpleEvaluation[String](Preferences.get(Default.GOLD))
    )().
    /* Optional part begins; we directly specify fixed values and use variable values from above: */
    name ( "Sample Scala Amas Experiment" ).
    root ( Preferences.get(Default.ROOT) ).
    run
}
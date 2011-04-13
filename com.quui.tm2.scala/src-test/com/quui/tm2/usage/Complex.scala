package com.quui.tm2.usage
/** The agents we use are mostly Java implementations of the Agent interface: */
import com.quui.tm2.agents._
/** Import the implicit conversion from Agent to RichAgent, to add support for the DSL syntax: */
import com.quui.tm2.Exp._
import com.quui.tm2.Exp
object Complex extends Application{
  /** Using for expressions, we can easily define multiple experiments with different configurations: */
  for {
    /** The different configurations to try: */
    corpus <- List(
      "http://www.gutenberg.org/files/14591/14591-8.txt",
      "http://www.gutenberg.org/files/14460/14460-8.txt")
    /** We can configure anything: */
    // regexp <- List(" ","\\W")
    /** The agents of one configuration: */
    agentCorpus = new Corpus(corpus)
    agentTokens = new Tokenizer() // e.g. give regexp here
    agentGazett = new Gazetteer()
    /** Experiment setup: */
    val x = Exp(
      agentCorpus -> agentTokens, 
      agentTokens -> agentGazett
      // etc.
    )().
    /* Optional part begins; we directly specify fixed values and use variable values from above: */
    name("Faust Sample Scala Amas Experiment")
  } yield x.run
}
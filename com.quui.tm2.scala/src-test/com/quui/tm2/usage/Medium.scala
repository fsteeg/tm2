package com.quui.tm2.usage
import com.quui.tm2.agents._
import com.quui.tm2.Exp._
import com.quui.tm2.Exp
object Medium extends Application{
  for {
    /** Config */
    corpus <- List(
      ("Faust-14591", "http://www.gutenberg.org/files/14591/14591-8.txt"), 
      ("Faust-14460", "http://www.gutenberg.org/files/14460/14460-8.txt"))
    val id = corpus._1 + "-" + System.currentTimeMillis
    /** Agents */
    agentTokens = new Tokenizer()
    agentGazetteer = new Gazetteer()
    /** Experiment */
    val x = Exp( new Corpus(corpus._2) -> agentTokens, agentTokens -> agentGazetteer )().
    /** Metadata */
      name( id )
  } yield x.run
}
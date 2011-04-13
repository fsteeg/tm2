package com.quui.tm2.usage

import scala.collection.JavaConversions._
import com.quui.tm2.agents._
import com.quui.tm2.RichAgent._
import com.quui.tm2.AgentList._
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
import com.quui.tm2.Batch._
import com.quui.tm2.Exp._ // implicit conversion from tuple of agents to analysis and synthesis
object MinimalGazetteerUsage extends Application {
  override def main(args: Array[String]): Unit = {
	  
	// A couple of agents:
  class Gold extends Gazetteer // evaluate Gezatteer against itself
	val (corpus, tok, gaz, gold, eval)
		= (new Corpus, new Tokenizer, new Gazetteer, new Gold, new SimpleEvaluation[String])
		
	// Define and run a simple setup (no evaluation):
	val exp1 = corpus -> tok | tok -> gaz !
	
	// Find all annotations by a specific agent:
	val res1 = exp1 ? gaz
	
	// Find determiners tagged by gazetteer, returning the corresponding tokenizer annotations:
	val res2 = exp1 ? ("determiner" -> gaz -> tok)
	
	// Define, run and search from above in one step:
	val res3 = (corpus -> tok | tok -> gaz!) ? ("determiner" -> gaz -> tok)

	// A complex setup, including evaluation:
	val exp2 = corpus -> tok | tok -> (gaz, gold) | (gaz, gold) -> eval!
	
	// println(exp1) // TODO fix for experiments without evaluation
	println(exp2)
	println(res1)
	println(res2)
	println(res3)
  }
}

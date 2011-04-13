package com.quui.tm2.spec

import org.junit.runner.RunWith
import org.scalatest.Spec
import org.scalatest.matchers.ShouldMatchers
import scala.collection.JavaConversions._
import org.scalatest.junit.JUnitRunner
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
@RunWith(classOf[JUnitRunner])
class GazetteerUsage extends Spec with ShouldMatchers {
  describe("A Gazetteer experiment") {
    it("it can contain a single Pseudoword setup") {

      // A couple of agents:
      val (corpus, tokenizer, gaz, gold, eval) = (new Corpus, new Tokenizer, new Gazetteer, new Gazetteer, new SimpleEvaluation[String])

      // These are actually typed to their input and output:
      val tok: Agent[String, String] = tokenizer // TODO should be [String, Token]?

      // Define and run a simple setup linking compatible agents:
      val exp1 = corpus -> tok | tok -> gaz !

      // Find all annotations by a specific agent:
      val res0 = exp1 ? tok

      // These are actually typed to agent's output type:
      val res1: List[Annotation[String]] = res0 // TODO should be List[Annotation[Token]]?

      // Find determiners tagged by gazetteer, returning the corresponding tokenizer annotations:
      val res2 = exp1 ? ("determiner" -> gaz -> tok)

      // Define, run and search from above in one step:
      val res3 = (corpus -> tok | tok -> gaz!) ? ("determiner" -> gaz -> tok)

      // A single complex setup, including evaluation:
      val exp2 = corpus -> tok | tok -> (gaz, gold) | (gaz, gold) -> eval!

      // Define a multi-experiment setup including evaluation:
      val exps = for {
        tok <- new Tokenizer("a") / new Tokenizer("b") // combine each of these...
        gaz <- new Gazetteer("a") / new Gazetteer("b") // ...with each of these...
      } yield corpus -> tok | tok -> (gaz, gold) | (gaz, gold) -> eval // ...in this setup

      // Use the Batch class for concurrent execution and an overview table of results:
      Batch.run { exps }

      // println(exp1) // TODO fix for experiments without evaluation
      println(exp2)
      println(res1)
      println(res2)
      println(res3)
    }
  }
}

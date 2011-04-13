package com.quui.tm2.spec
import org.scalatest.Spec
import org.scalatest.matchers.ShouldMatchers
import com.quui.tm2.agents._
import com.quui.tm2.Exp._
import com.quui.tm2.RichAgent._
import com.quui.tm2.Analysis
import org.scalatest.junit.JUnitRunner
import com.quui.tm2._
import org.junit.runner.RunWith

@RunWith(classOf[JUnitRunner])
class AnalysisSpec extends Spec with ShouldMatchers{
  describe("An Analysis") {
    it("is typed to the exchanged data") {
      val analysis : Analysis[String] = new Corpus -> new Tokenizer
      analysis should not be null
      analysis.sources should have size 1
      analysis.targets should have size 1
    }
    it("consists of a single source and one or many targets") {
      val analysis : Analysis[String] = new Corpus -> new Tokenizer / new Gazetteer / new Tokenizer
      analysis should not be null
      analysis.sources should have size 1
      analysis.targets should have size 3
    }
    it("can combine agents defined in Java and Scala") {
      val analysis : Analysis[String] = 
        new Corpus -> new com.quui.tm2.agents.Reverser
      analysis should not be null
      analysis.sources should have size 1
      analysis.targets should have size 1
    }
    it("uses implicits to convert tuple syntax") {
      val t : (Agent[_, String], Agent[String, _]) = new Corpus -> new Tokenizer
      val a : Analysis[String] = new Corpus -> new Tokenizer
      val analysis : Analysis[String] = t
      analysis should not be null
      analysis.sources should have size 1
      analysis.targets should have size 1
    }
  }
}

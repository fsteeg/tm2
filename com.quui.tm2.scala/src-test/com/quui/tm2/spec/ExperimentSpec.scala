package com.quui.tm2.spec
import org.scalatest.Spec
import org.scalatest.matchers.ShouldMatchers
import com.quui.tm2.agents._
import com.quui.tm2.agents._
import com.quui.tm2.Exp._
import com.quui.tm2.RichAgent._
import com.quui.tm2.AgentList._
import com.quui.tm2.AgentList
import com.quui.tm2.RichAnalysis._
import com.quui.tm2.Exp

import org.scalatest.junit.JUnitRunner
import org.junit.runner.RunWith

@RunWith(classOf[JUnitRunner])
class ExperimentSpec extends Spec with ShouldMatchers{
  describe("An Experiment") {
      
    it("consists of one or many interactions") {
      val x:Exp = Exp(new Corpus -> new Tokenizer)()
      x should not be null
    }
    
    it("can be defined and run in one line, given the agents to use"){
      val (tok, test, gold) = (new Tokenizer, new Gazetteer("a"), new Gazetteer("b"))
      println(new Corpus -> tok | tok -> test/gold | test+gold -> new SimpleEvaluation[String]!)
    }
    
   it("can be used with concise syntax in for expressions to define the agents to combine") {
        val x = for {
         tok <- new Tokenizer("a") / new Tokenizer("b") // combine each of these
         gaz <- new Gazetteer("a") / new Gazetteer("b") // with each of these
         gold = new Gazetteer("c")                      // and this one
        } 
        yield {
          new Corpus -> tok | tok -> gaz/gold | gaz+gold -> new SimpleEvaluation[String]
        }
    }
    
    it("can be constructed using the normal tuple syntax, factories and currying") {
      val x = for {
       tok <- List( new Tokenizer("a"), new Tokenizer("b") ) // combine each of these
       gaz <- List( new Gazetteer("a"), new Gazetteer("b") ) // with each of these
       gold = new Gazetteer("c")                             // and this one
      } 
      yield {
        Exp((new Corpus, tok), (tok, List(gaz, gold)))(((gaz, gold), new SimpleEvaluation[String]))
      }
    }
    
    it("can use remote files") { // commented out for tests
      val corpus = new Corpus//("http://www.gutenberg.org/files/14591/14591-8.txt")
      val tokenizer = new Tokenizer
      val gazetteer = new Gazetteer
      val gold = new Gazetteer
      val eval = new SimpleEvaluation[String]
      val x = ( corpus -> tokenizer | tokenizer -> gazetteer/gold | gazetteer+gold -> eval ).
      name("Sample Scala Amas Experiment")
      x should not be null
      x.run
      val docu = x.documentation
      x
    }
  }
}
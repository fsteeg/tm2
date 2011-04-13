package com.quui.tm2.usage

import java.io.IOException
import java.io.FileInputStream
import java.util.Properties
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
import com.quui.tm2.Exp._
import java.io.Serializable
import scala.util.matching.Regex._

object FullSimpleEval extends Application {

  case class Token(form: String) extends Comparable[Token] with Serializable {
    def compareTo(that: Token) = this.form.compareTo(that.form)
  }
  class Tokenizer extends Agent[String, Token] {
    def process(t: java.util.List[Annotation[String]]): java.util.List[Annotation[Token]] =
      ("\\p{L}+".r findAllIn t.get(0).getValue()).matchData.map((m: Match) =>
        ImmutableAnnotation.getInstance[Token](
          classOf[Tokenizer], Token(m.matched), m.start, m.end)).toList
  }

  case class Sense(form: String) extends Comparable[Sense] with Serializable {
    def compareTo(that: Sense) = this.form.compareTo(that.form)
  }
  class Gazetteer(dict: String) extends AbstractAgent[Token, Sense] {
    def process(t: Token): Sense = {
      val prop = new Properties(); prop.load(new FileInputStream(dict))
      Sense(prop.getOrElse(t.form, ""))
    }
  }
  class Gold(dict: String) extends Gazetteer(dict)

  run {
    for {
      dict <- List("files/dict1.properties", "files/dict2.properties")
      gold <- List("files/dict2.properties", "files/dict1.properties")
      c = new Corpus
      t = new Tokenizer //TODO issue with Tokenizer config
      g = new Gazetteer(dict)
      o = new Gold(gold)
      e = new SimpleEvaluation[Sense]
    } yield {
      c -> t | t -> (g, o) | (g, o) -> e
    }
  }
}

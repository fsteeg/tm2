package com.quui.tm2.agents

import com.quui.tm2.agents.senseval.Ambiguity
import com.quui.tm2.agents.senseval.Context
import com.quui.tm2.agents.senseval.STAXSensevalDataReader
import com.quui.tm2.ImmutableAnnotation
import java.io.File
import com.quui.tm2.RichAgent
import com.quui.tm2.Agent
import com.quui.tm2.{ Annotation => Anno }
import scala.collection.JavaConversions._

abstract class SensevalData(s: String) extends Agent[String, Context] {
  val file = new File(s)
  val trainDataReader = new STAXSensevalDataReader(
    file);
  trainDataReader.load();
  val samples: List[Ambiguity] = trainDataReader.getAmbiguities.toList
  val words: java.util.List[String] = trainDataReader.getWords
  def process(input: java.util.List[Anno[String]]) = {
    samples.map(asAnnotation(_))
    //val train:List[String] = trainDataReader.getWords.toList
  }
  def asAnnotation(amb: Ambiguity): Anno[Context] = {
    val r = ImmutableAnnotation.getInstance[Context](
      classOf[SensevalData],
      amb.getContext:Context,
      amb.getContext.targetStart:Int,
      amb.getContext.targetEnd:Int)
//    println(r)
    r
  }
  override def toString = file.toURL.toString
}
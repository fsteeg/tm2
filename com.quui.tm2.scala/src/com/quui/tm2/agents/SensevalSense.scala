package com.quui.tm2.agents

import com.quui.tm2.agents.senseval.STAXSensevalDataReader
import com.quui.tm2.agents.senseval.Context
import com.quui.tm2.agents.senseval.Ambiguity
import java.io.File
import com.quui.tm2.ImmutableAnnotation
import com.quui.tm2.RichAgent
import com.quui.tm2.Agent
import com.quui.tm2.{ Annotation => Anno }
import scala.collection.JavaConversions._

class SensevalSense(s: String) extends Agent[Context, Ambiguity] {
  val words: List[String] = Nil
  val trainDataReader = new STAXSensevalDataReader(
    new File(s));
  trainDataReader.load();
  val samples: List[Ambiguity] = trainDataReader.getAmbiguities.toList
  def process(input: java.util.List[Anno[Context]]) = {
    samples.map(sense(_)) // FIXME not samples...
    //val train:List[String] = trainDataReader.getWords.toList
  }
  def sense(amb: Ambiguity): Anno[Ambiguity] = {
    ImmutableAnnotation.getInstance[Ambiguity](
      classOf[SensevalData],
      amb:Ambiguity,
      amb.getContext.targetStart:Int,
      amb.getContext.targetEnd:Int)
  }
  override def toString = "SensevalSense("+s+")"
}
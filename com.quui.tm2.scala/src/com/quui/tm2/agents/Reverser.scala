package com.quui.tm2.agents
import java.util.Collections
import com.quui.tm2.RichAgent
import com.quui.tm2.Annotation

/* EXPERIMENTAL, WORK-IN-PROGRESS */
class Reverser extends RichAgent[String,String]{
  val backingAgent = this
  def process(input : String) = { input.reverse } 
  def process(input : java.util.List[Annotation[String]]) = { input }
}
object Reverser {def apply() : Reverser = {new Reverser()}}
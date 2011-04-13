package com.quui.tm2.agents

import com.quui.tm2.Evaluation
import java.io.StringWriter
import java.io.File
import java.io.FileWriter
import com.quui.tm2.types.Sense
import com.quui.tm2.RichAgent
import com.quui.tm2.Agent
import com.quui.tm2.{ Annotation => Anno }
import scala.collection.JavaConversions._

class SensevalEval(s: String) extends Agent[Sense, String] with Evaluation {
  var result = "Not implemented"
  var response = "No response"
  def getResultString = getF.toString
  def process(input: java.util.List[Anno[Sense]]) = {
    val sensevalOutput = for (anno <- input; sense = anno.getValue) yield sense.lemma + " " + sense.id + " " + sense.correct
    val res = sensevalOutput.sorted.mkString("\n")
//    println(res)
    val file = new File("files/senseval.result")
    val fw = new FileWriter(file)
    fw.write(res); fw.close()
    result = file.getCanonicalPath
    println("Calling Senseval scorer2... ")
    val process = java.lang.Runtime.getRuntime()
    	.exec("files/scorer2 files/senseval.result files/EnglishLS.test.key files/EnglishLS.sensemap -g "+ (if (s=="fine") "" else s))
    println(scala.io.Source.fromInputStream(process.getErrorStream).getLines.mkString("\n"))
    response = scala.io.Source.fromInputStream(process.getInputStream).getLines.mkString("\n")
    println(response)
    println(getF)
    Nil
  }
  def getF: Float = {
    // In our setup P is always = R, and thus F, too
    val r = """precision: ([^ ]+) """.r
    val Some(res) = r findFirstIn response
    val r(p) = res
    p.toFloat
  }
  override def toString="Senseval (%s)".format(s)
  
}
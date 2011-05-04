package com.quui.tm2
import java.io.Serializable
import com.quui.tm2.{Agent,Analysis,Experiment,Synthesis, Model}
import com.quui.tm2.doc.ExportHelper
import com.quui.tm2.AbstractAgent

/**A rich training that supports the Scala DSL syntax:*/
abstract class RichSynthesis[V<:Comparable[V] with Serializable, C<:Comparable[C] with Serializable] 
    extends Synthesis[V,C]{
  val backingTraining : Synthesis[V,C]
  def | (next: Analysis[_]): Exp = {
    val x : Experiment.Builder = new Experiment.Builder
    x.synthesis(backingTraining)
    x.analysis(next)
    new Exp(x)
  }
}

object RichSynthesis{
  /** Implicit conversion from Java trainings to rich Scala trainings: */
  implicit def synthesisWrapper [V<:Comparable[V] with Serializable, C<:Comparable[C] with Serializable] 
     (training : Synthesis[V,C])(implicit m1: Manifest[V], m2: Manifest[C]) =
        new RichSynthesis[V,C] {
          val backingTraining = training
          def run (board : java.util.Map[java.lang.Class[_ <: com.quui.tm2.Agent[_, _]],java.util.List[com.quui.tm2.Annotation[_]]])
          : java.util.Map[java.lang.Class[_ <: com.quui.tm2.Agent[_, _]],java.util.List[com.quui.tm2.Annotation[_]]] = {
            backingTraining.run(board)
          }
          def data : java.util.List[Agent[_,V]] = backingTraining.data
          def info : java.util.List[Agent[_,C]] = backingTraining.info
          def model : Model[V,C] = backingTraining.model
          def apply = { this }
          def getDataTypeClass:Class[V] = m1.erasure.asInstanceOf[Class[V]]
          def getInfoTypeClass:Class[C] = m2.erasure.asInstanceOf[Class[C]]
        } 
      
  def apply = {this}
  def apply ( s : String ) = { this }
}    
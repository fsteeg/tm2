package com.quui.tm2
import java.io.Serializable
import com.quui.tm2.{Agent,Analysis,Experiment, Synthesis}
import com.quui.tm2.doc.ExportHelper
import com.quui.tm2.AbstractAgent

/**A rich interaction that supports the Scala DSL syntax:*/
abstract class RichAnalysis[T<:Comparable[T] with Serializable] (implicit val m: Manifest[T])
    extends Analysis[T]{
    
  val backingInteraction :  Analysis[T]

  def | (next: Analysis[_]): Exp = {
    val x : Experiment.Builder = new Experiment.Builder
    x.analysis(backingInteraction)
    x.analysis(next)
    new Exp(x)
  }
  
  def | [V<:Comparable[V] with Serializable, C<:Comparable[C] with Serializable] (next: Synthesis[V,C]): Exp = {
    val x : Experiment.Builder = new Experiment.Builder
    x.analysis(backingInteraction)
    x.synthesis(next)
    new Exp(x)
  }
  
}

object RichAnalysis{
  /** Implicit conversion from Java agents to rich Scala agents: */
  implicit def analysisWrapper [T<:Comparable[T] with Serializable] (interaction : Analysis[T])(implicit m: Manifest[T]) = 
        new RichAnalysis[T] {
          val backingInteraction = interaction
          def run (board : java.util.Map[java.lang.Class[_ <: com.quui.tm2.Agent[_, _]],java.util.List[com.quui.tm2.Annotation[_]]])
          : java.util.Map[java.lang.Class[_ <: com.quui.tm2.Agent[_, _]],java.util.List[com.quui.tm2.Annotation[_]]] = {
            backingInteraction.run(board)
          }
          def sources : java.util.List[Agent[_,T]] = backingInteraction.sources
          def targets : java.util.List[Agent[T,_]] = backingInteraction.targets
          def apply = { this }
          def getTypeClass:Class[T] = m.erasure.asInstanceOf[Class[T]] //Class.forName(m.toString).asInstanceOf[Class[T]]
        } 
      
  def apply = {this}
  def apply ( s : String ) = { this }
}    
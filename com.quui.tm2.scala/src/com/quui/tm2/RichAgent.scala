package com.quui.tm2
import java.io.Serializable
import com.quui.tm2.{Agent, Analysis, Annotation}
import com.quui.tm2.doc.ExportHelper
import com.quui.tm2.AbstractAgent

/**A rich agent that supports the Scala DSL syntax:*/
abstract class RichAgent[I<:Comparable[I] with Serializable,O<:Comparable[O] with Serializable] 
    extends Agent[I,O]{
  val backingAgent :  Agent[I,O]
  def / (a: Agent[I,O]): AgentList[I,O] = new AgentList(List(backingAgent, a))
  def + [O2<:Comparable[O2] with Serializable, I2<:Comparable[I2] with Serializable] 
    (a: Agent[I2,O2]): (Agent[I,O], Agent[I2,O2]) = (backingAgent, a)
}

object RichAgent{
  /** Implicit conversion from Java agents to rich Scala agents: */
  implicit def agentWrapper [I<:Comparable[I] with Serializable,O<:Comparable[O] with Serializable]
     (agent : Agent[I,O]) = {
        new RichAgent[I,O]{
          val backingAgent = agent
          def process(input : java.util.List[Annotation[I]]) = { 
            agent.process(input) 
          }
          def apply = { this }
        } 
      }
  def apply = {this}
  def apply ( s : String ) = { this }
}    
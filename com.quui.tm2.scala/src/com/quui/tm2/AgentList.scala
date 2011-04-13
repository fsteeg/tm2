package com.quui.tm2
import java.io.Serializable
import com.quui.tm2.{Agent, Analysis, Annotation, Model, Synthesis}
import com.quui.tm2.doc.ExportHelper
import com.quui.tm2.AbstractAgent

/**A list of equally typed agents:*/
class AgentList[I<:Comparable[I] with Serializable,O<:Comparable[O] with Serializable]
      (list : List[Agent[I,O]]) {
  val backingAgentList = list
  def / (a: Agent[I,O]): AgentList[I,O] = new AgentList(a::backingAgentList)
}

object AgentList {
  def apply[I<:Comparable[I] with Serializable,O<:Comparable[O] with Serializable]
  (agents : Agent[I,O]*) : AgentList[I,O] = {
    new AgentList[I,O](List() ++ agents)
  }
  
  // for monadic behaviour in for-expressions: a <- new TokenizerA / new TokenizerB / new TokenizerC
  implicit def toList[I<:Comparable[I] with Serializable,O<:Comparable[O] with Serializable]
                      (agentList:AgentList[I,O]):List[Agent[I,O]] = agentList.backingAgentList
}
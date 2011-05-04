package com.quui.tm2
import java.io.Serializable
import com.quui.tm2.{Agent,Analysis, Annotations}
import com.quui.tm2.doc.WikitextExport
import com.quui.tm2.util.{Preferences,TM2Logger}
import scala.collection.JavaConversions._
import RichAgent._
import com.quui.tm2.{Agent, Analysis, Annotation, Model, Synthesis}
import RichSynthesis._
/** The main reason for this class: factory to create Experiments via companion object. */
object Exp {
  def apply(interactions : Analysis[_]*)(syntheses:Synthesis[_,_]*) : Exp = {
    val x = new com.quui.tm2.Experiment.Builder()
    interactions.foreach(i => x.analysis(i))
    new Exp(x)
  }

  //TODO move all implicit conversions here? Tm2Pramble?
  
  implicit def expsToExperiments(exps:List[Exp]):java.util.List[com.quui.tm2.Experiment] =
      exps.map(_.experiment)
  
  // Implicit conversion from tuples of agents to rich analysis and synthesis wrappers
      
  // ALLOWS: a1:Agent[_,D] -> a2:Agent[D,_] : RichAnalysis[D]
  implicit def agentTupleToAnalysis
    [D<:Comparable[D] with Serializable]
    (tuple:(Agent[_, D], Agent[D, _]))(implicit m: Manifest[D]) : RichAnalysis[D] = {
      val interaction = new Analysis.Builder[D]
      interaction.source(tuple._1)
      interaction.target(tuple._2)
      RichAnalysis.analysisWrapper(interaction.build)
  }
  
  // ALLOWS: a1:Agent[_,D] -> (a2:Agent[D,_], a3:Agent[D,_]) : RichAnalysis[D]
  implicit def agentTupleTupleToAgentList
    [I<:Comparable[I] with Serializable, D<:Comparable[D] with Serializable, O<:Comparable[O] with Serializable]
    (tuple:(Agent[I, D], (Agent[D,_], Agent[D,_])))(implicit m: Manifest[D]) : RichAnalysis[D] = {
	  val interaction = new Analysis.Builder[D]
      interaction.source(tuple._1)
      interaction.target(tuple._2._1)
      interaction.target(tuple._2._2)
      RichAnalysis.analysisWrapper(interaction.build)
  }
  
  // ALLOWS: a1:Agent[_,D] -> List(a2:Agent[D,O], a3:Agent[D,O]) : RichAnalysis[D]
  implicit def listOfAgentsToAgentList
    [I<:Comparable[I] with Serializable, D<:Comparable[D] with Serializable, O<:Comparable[O] with Serializable]
    (tuple:(Agent[I, D], List[Agent[D,O]]))(implicit m: Manifest[D]) : RichAnalysis[D] 
                                = agentTupleToAgentList((tuple._1, new AgentList[D,O](tuple._2)))
  
  // ALLOWS: a1:Agent[_,D] -> a2:Agent[D,_] / a3:Agent[D,_]  : RichAnalysis[D]
  implicit def agentTupleToAgentList
    [O1<:Comparable[O1] with Serializable, O2<:Comparable[O2] with Serializable]
    (tuple:(Agent[_, O1], AgentList[O1, O2])) (implicit m: Manifest[O1]): RichAnalysis[O1] = {
      val interaction = new Analysis.Builder[O1]
      interaction.source(tuple._1)
      tuple._2.backingAgentList.foreach(interaction.target(_))
      RichAnalysis.analysisWrapper(interaction.build)
  }
  
  // ALLOWS: a1:Agent[V,C] + a2:Agent[V,C] -> m:Model[V,C] : RichSynthesis[V,C]
  implicit def agentPairModelTupleToSynthesis
    [V<:Comparable[V] with Serializable, C<:Comparable[C] with Serializable] 
    (tuple: ((Agent[_,V], Agent[_,C]), Model[V,C]))(implicit m1: Manifest[V], m2: Manifest[C]) : RichSynthesis[V,C] = {
      val training = new Synthesis.Builder[V,C](tuple._2)
      training.data(tuple._1._1)
      training.info(tuple._1._2)
      RichSynthesis.synthesisWrapper(training.build)
  }
  
}

/** Wrapper class for Experiment.Builder, builds on-the-fly in the run and eval methods. */
class Exp(x:com.quui.tm2.Experiment.Builder) {
  
    def name(name:String):Exp = {x.name(name);this}
    def root(root:String):Exp = {x.root(root);this}
    def agent(agent:String):Exp = {x.agent(agent);this}
    var documentation : String = null
    var experiment : com.quui.tm2.Experiment = x.build
  
    /** Run and export an experiment. */
  def run : Exp = {
        experiment.run
        this
    } 
  def ?
  [S<:Comparable[S] with Serializable, 
   R<:Comparable[R] with Serializable,
   I1<:Comparable[I1] with Serializable,
   I2<:Comparable[I2] with Serializable] 
	  (pair:((S, Agent[I1,S]), Agent[I2,R])): List[Annotation[R]] = {
	  val list = experiment.getAnnotations()
	  val ((searchValue, searchInAgent), returnFromAgent) = pair
	  val hits = for(
    		a1 <- list; 
    		a2 <- list; 
    		if a1 != a2;
    		if Annotations.fullOverlap(a1, a2);
    		if a1.author == searchInAgent.getClass;
    		if a1.getValue() == searchValue;
    		if a2.author == returnFromAgent.getClass
    ) yield a2.asInstanceOf[Annotation[R]]
    hits.toList
  }
  def ?
  [I<:Comparable[I] with Serializable, 
   O<:Comparable[O] with Serializable]
	  (agent:Agent[I,O]): List[Annotation[O]] = {
	  val list = experiment.getAnnotations()
	  val hits = for(
    		a1 <- list; 
    		if a1.author == agent.getClass
    ) yield a1.asInstanceOf[Annotation[O]]
    hits.toList
  }
  def ! : Exp = { val r = run; documentation = WikitextExport.of(experiment); r} 
  override def toString : String = 
  		"Result: %s, Details: %s".format(if(experiment.getEvaluation != null)experiment.getEvaluation.getResultString else "No evaluation", documentation)
  
  def | (next: Analysis[_]): Exp = {
        x.analysis(next)
        new Exp(x)
  }
  def | [V<:Comparable[V] with Serializable, C<:Comparable[C] with Serializable] (next: RichSynthesis[V,C]): Exp = {
    x.synthesis(next)
    new Exp(x)
  }
}
package com.quui.tm2.spec

import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import org.scalatest.Spec
import org.scalatest.matchers.ShouldMatchers
import com.quui.tm2.agents.features.ContextFeatures
import weka.classifiers.`lazy`.IBk
import com.quui.tm2.agents.senseval.SensevalClassifier
import com.quui.tm2.agents.SensevalEval
import com.quui.tm2.agents.SensevalData
import scala.collection.JavaConversions._
import com.quui.tm2.agents._
import com.quui.tm2.RichAgent._
import com.quui.tm2.RichAnalysis._
import com.quui.tm2.RichSynthesis._
import com.quui.tm2.agents._
import com.quui.tm2._
import com.quui.tm2._
import com.quui.tm2.agents._
import com.quui.tm2.types._
import java.math.BigInteger
import com.quui.tm2.agents.senseval._
import com.quui.tm2.Batch._
import weka.classifiers.bayes._
import weka.classifiers.functions._
import weka.classifiers.misc._
import com.quui.tm2.Exp._

@RunWith(classOf[JUnitRunner])
class WsdUsageSensevalAnnotated extends Spec with ShouldMatchers {
  describe("A Senseval evaluation") {
    it("it can contain multiple setups and native evaluation") {
      import java.util.ArrayList
      import java.util.Arrays._

      println("_____________________________________________________________________\n"
        + "Running evaluation on the SENSEVAL-3 ENGLISH LEXICAL SAMPLE TASK data"
        + "\n(see http://www.cse.unt.edu/~rada/senseval/senseval3 for details)\n"
        + "_____________________________________________________________________");

      val files = "files"
      class TrainData extends SensevalData(files + "/EnglishLS.train.xml")
      class TestData extends SensevalData(files + "/EnglishLS.test.xml")
      class TrainSense extends SensevalSense(files + "/EnglishLS.train.xml")

      val corpus: Agent[String, String] = new Corpus
      val trainData: Agent[String, Context] = new TrainData()
      val testData: Agent[String, Context] = new TestData()
      val trainSense: Agent[Context, Ambiguity] = new TrainSense()

      class TrainFeatures(s: String, i: Int) extends ContextFeatures(trainData.asInstanceOf[SensevalData].words, s, i)
      class TestFeatures(s: String, i: Int) extends ContextFeatures(testData.asInstanceOf[SensevalData].words, s, i) //TrainFeatures//ContextFeatures(testData.words, "3-gram", 8)

      Batch.run {
        for {
          /* Configurations: */
          algo: weka.classifiers.Classifier <- List(new NaiveBayes); //, new BayesNet, new SMO, new HyperPipes);//, new IBk, null
          feat: String <- List("3-gram"); //, "7-gram", "word", "length")
          grain: String <- List("fine"); //, "mixed", "coarse")
          context: Int <- List(2, 4); //,8,16)
          /* Agents: */
          trainFeat: Agent[Context, FeatureVector] = new TrainFeatures(feat, context)
          testFeat: Agent[Context, FeatureVector] = new TestFeatures(feat, context)
          classifier = new SensevalClassifier(asList(context * 2), 2f, algo, "S0", "S1")
          classifierAgent: Agent[FeatureVector, Sense] = classifier
          classifierModel: Model[FeatureVector, Ambiguity] = classifier
          evaluation: Agent[Sense, String] = new SensevalEval(grain)
          /* Interactions: */
          corpusData: Analysis[String] = corpus -> (trainData, testData)
          corpusContext: Analysis[Context] = trainData -> (trainFeat, trainSense)
          trainClassifier: Synthesis[FeatureVector, Ambiguity] = (trainFeat, trainSense) -> classifierModel
          testContext: Analysis[Context] = testData -> testFeat
          classify: Analysis[FeatureVector] = testFeat -> classifierAgent
          evaluate: Analysis[Sense] = classifierAgent -> evaluation
        } /* Workflow: */ yield corpusData | corpusContext | trainClassifier | testContext | classify | evaluate
      }
    }
  }
}

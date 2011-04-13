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
import com.quui.tm2.Batch._
import weka.classifiers.bayes._
import weka.classifiers.functions._
import weka.classifiers.misc._
import com.quui.tm2.Exp._

@RunWith(classOf[JUnitRunner])
class WsdUsageSenseval extends Spec with ShouldMatchers {
  describe("A Senseval evaluation") {
    it("it can contain multiple setups and native evaluation") {
      import java.util.ArrayList
      import java.util.Arrays._

      println("_____________________________________________________________________\n"
        + "Running evaluation on the SENSEVAL-3 ENGLISH LEXICAL SAMPLE TASK data"
        + "\n(see http://www.cse.unt.edu/~rada/senseval/senseval3 for details)\n"
        + "_____________________________________________________________________");

      val files = "files"
      object trainData extends SensevalData(files + "/EnglishLS.train.xml")
      object testData extends SensevalData(files + "/EnglishLS.test.xml")
      object trainSense extends SensevalSense(files + "/EnglishLS.train.xml")
      val corpus = new Corpus

      class TrainFeatures(s:String,i:Int) extends ContextFeatures(trainData.words, s, i)
      class TestFeatures(s:String, i:Int) extends ContextFeatures(testData.words, s, i)

      Batch.run {
        for {
          algo <- List(new NaiveBayes);//, new BayesNet, new SMO, new HyperPipes, new IBk, null);
          feat <- List("3-gram");//, "7-gram", "word", "length")
          grain <- List("fine");//, "mixed", "coarse")
          context <- List(2,4);//,8,16)
          trainFeat = new TrainFeatures(feat, context)
          testFeat = new TestFeatures(feat, context)
          classifier = new SensevalClassifier(asList(context*2), 2f, algo, "S0", "S1")
          evaluation = new SensevalEval(grain)
        } yield 
        ( corpus -> (trainData,testData)
        | trainData -> (trainFeat, trainSense)
        | (trainFeat, trainSense) -> classifier 
        | testData -> testFeat 
        | testFeat -> classifier 
        | classifier -> evaluation
        )
      }
    }
  }
}

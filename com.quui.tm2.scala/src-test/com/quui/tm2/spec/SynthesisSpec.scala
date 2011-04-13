package com.quui.tm2.spec

import com.quui.tm2.agents.classifier.Classifier
import org.scalatest.Spec
import org.scalatest.matchers.ShouldMatchers
import com.quui.tm2.agents._
import com.quui.tm2._
import com.quui.tm2.RichAgent._
import com.quui.tm2.Exp._
import com.quui.tm2.Analysis
import org.scalatest.junit.JUnitRunner
import org.junit.runner.RunWith

@RunWith(classOf[JUnitRunner])
class SynthesisSpec extends Spec with ShouldMatchers{
  describe("A Synthesis") {
    it("is typed to the exchanged data") {
      val synthesis : Synthesis[String, String] = 
          new Classifier(java.util.Arrays.asList(2,8), 1f, null, "S0", "S1") +
          new PseudoGold("and", "the") -> new SimpleEvaluation[String]
      synthesis should not be null
      synthesis.data should have size 1 // TODO should not be list
      synthesis.data.get(0).getClass should be (classOf[Classifier])
      synthesis.info should have size 1
      synthesis.info.get(0).getClass should be (classOf[PseudoGold])
      synthesis.model.getClass should be (classOf[SimpleEvaluation[String]])
    }
  }
}

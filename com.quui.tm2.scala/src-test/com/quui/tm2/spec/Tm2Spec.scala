package com.quui.tm2.spec
object Tm2Spec {
  import com.quui.tm2.util.Preferences
  import com.quui.tm2.util.Preferences.Default
  val Corpus = Preferences.get(Default.CORPUS);
  val Corpus1 = Preferences.get(Default.CORPUS);
  val Corpus2 = Preferences.get(Default.CORPUS);
  // Alternative entry point for running the tests (vs. AllAmasSuite)
  def main(args:Array[String]):Unit={
    (new AnalysisSpec).execute()
    (new SynthesisSpec).execute()
    (new ExperimentSpec).execute()
  }
}

package com.quui.tm2.usage
import com.quui.tm2.agents._
import com.quui.tm2.agents._
import com.quui.tm2.Exp._
import com.quui.tm2.RichAnalysis._
import com.quui.tm2.spec.Tm2Spec._
object Simple extends Application {
    //-----------------------------------------------------------------------------------------------------------
    println( new Corpus -> new Tokenizer | new Tokenizer -> new Gazetteer | new Gazetteer -> new SimpleEvaluation[String] ! )
    //-----------------------------------------------------------------------------------------------------------
    
    for {
    
      corpus <- List(Corpus1, Corpus2)
      
      (cor, tok, gaz, eva) = (new Corpus(corpus), new Tokenizer, new Gazetteer, new SimpleEvaluation[String])

    } yield cor -> tok | tok -> gaz | gaz -> eva !
    
    //-----------------------------------------------------------------------------------------------------------
    
}

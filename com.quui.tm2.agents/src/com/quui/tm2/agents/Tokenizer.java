package com.quui.tm2.agents;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
import java.util.regex.MatchResult;

import com.quui.tm2.Agent;
import com.quui.tm2.Annotation;
import com.quui.tm2.ImmutableAnnotation;

/**
 * The tokenizer splits the text into discrete tokens using a regular expression which supports Unicode-sensitive word
 * splitting.
 */
public class Tokenizer implements Agent<String, String> {
  
  public Tokenizer(String s){
    
  }
  public Tokenizer(){
    
  }

    enum Pattern {
        /** International in-word alphanumeric letter sign. */
        LETTER("\\p{L}"),
        /** Split at one or more non-letter signs. */
        DELIM("[^" + LETTER.val + "]+"),
        /** A word: one or more in-word letters. */
        WORD(LETTER.val + "+");
        String val;

        @Override
        public String toString() {
            return String.format("@%s=%s@", this.name(), this.val);
        }

        Pattern(String pattern) {
            this.val = pattern;
        }
    }

    /**
     * {@inheritDoc}
     * @see com.quui.tm2.Agent#process(java.util.List)
     */
    public List<Annotation<String>> process(List<Annotation<String>> text) {
        List<Annotation<String>> writtenAnnotations = new ArrayList<Annotation<String>>();
        Scanner s = new Scanner(text.get(0).getValue());
        s.useDelimiter(Pattern.DELIM.val);
        while (s.hasNext(Pattern.WORD.val)) {
            MatchResult result = s.match();
            Annotation<String> a = ImmutableAnnotation.getInstance(getClass(), s.next(Pattern.WORD.val), BigInteger
                    .valueOf(result.start()), BigInteger.valueOf(result.end()));
            writtenAnnotations.add(a);
        }
        return writtenAnnotations;
    }

    /**
     * {@inheritDoc}
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return getClass().getSimpleName() + " " + Arrays.asList(Pattern.values());
    }
}

/** 
 Project Suffix Trees for Natural Language (STNL) (C) 2006 Fabian Steeg

 This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation; either version 2.1 of the License, or (at your option) any later version.

 This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details.

 You should have received a copy of the GNU Lesser General Public License along with this library; if not, write to the Free Software Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 */
package com.quui.tm2.agents.features.suffixtree;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.quui.tm2.agents.features.suffixtree.node.NodeAccessor;
import com.quui.tm2.agents.features.suffixtree.node.SimpleNodeAccessor;

/**
 * Implementation based on the linear-time algorithm for constructing word-based
 * suffix trees as described by Andersson, Larsson & Swansson in Algorithmica
 * 23, 1999
 * 
 * <p/>
 * 
 * Subclasses a modified {@link NumericSuffixTree} class from BioJava API,
 * builds a word based tree with symbols representing the words which are stored
 * separately in a map
 * 
 * <p/>
 * 
 * TODO implement LCE over LCA (then kmismatch an wildcards are running in
 * linear time), see {@link LCE}
 * 
 * <p/>
 * 
 * TODO implement adding of texts atfer instantiation for more flexibility
 * 
 * @author Fabian Steeg (fsteeg)
 */
public class WordSuffixTree extends AlphanumericSuffixTree {

    public WordSuffixTree(String text, boolean reverse, boolean generalized,
            NodeAccessor accessor) {
        super(text, reverse, generalized, accessor);
    }

    /**
     * TODO
     * @param text
     * @param reverse
     * @param generalized
     */
    public WordSuffixTree(String text, boolean reverse, boolean generalized) {
        this(text,reverse,generalized,new SimpleNodeAccessor());
    }

    public WordSuffixTree(String text) {
        this(text, false, true);
    }

    /**
     * 
     */
    void construct() {

        /**
         * steps 1: construct a trie for the types in the input (ok, i'm using a
         * map but its nice and fast):
         */
        long counter = 0;
        Map<String, Long> map = new HashMap<String, Long>();
        mapper = new Mapper(this, accessor);
        String[] sentences = text.split("[\\.!?;:]");
        Set<String> sentencesSet = new HashSet<String>();
        // if not generalized, we add the text as one
        if (!generalized)
            sentencesSet.add(text);
        // else we'll split it into sentences and later add these separately
        // (FIXME this causes runtime problems, turns generalized trees quadratic, see runtime for generalized suffix tree, see gusfield,
        // page 116)
        else {
            for (String s : sentences) {
                if (!s.equals(" "))
                    sentencesSet.add(s.trim());
            }
        }
        /** step 2: number those types */
        int sentenceCount = 1;
        List<Long> all = new ArrayList<Long>();
        for (String sentence : sentencesSet) {
            // split each sentence into words
            String[] tokens = sentence.split("[^a-zA-Z0-9öäüß]");
            if (reverse) {
                List l = Arrays.asList(tokens);
                String[] tokRev = new String[tokens.length];
                int k = tokRev.length - 1;
                for (Object object : l) {
                    tokRev[k] = (String) object;
                    k--;
                }
                tokens = tokRev;
            }
            List<Long> seq = new ArrayList<Long>();
            for (String word : tokens) {
                if (!map.containsKey(word)) {
                    // TODO attention, here we skip a $ in the weird-chars
                    // string, which would cause the tree to think it should
                    // terminate:
                    if (((char) counter) == '$')
                        counter++;
                    // map a word to a number:
                    map.put(word, counter);
                    // TODO is there a better way do achieve this:
                    // map the number to the word:
                    mapper.put(counter, word);
                    counter++;
                }
            }
            /**
             * step 3: assign the values from the trie to the words in the input
             * string:
             */
            long[] ids = new long[tokens.length];

            for (int i = 0; i < ids.length; i++) {
                String rec = tokens[i];
                Object val = map.get(rec);
                ids[i] = (Long) val;
                seq.add(ids[i]);
            }

            /**
             * step 4: build a traditional suffix tree for the string of
             * numbers:
             */
            all.addAll(seq);
            all.add(NumericSuffixTree.TERMINATION_SYMBOL);
            
            
            sentenceCount++;
        }
        super.addSequences(all, sentenceCount, false);

        /**
         * step 5: expand the tree for words: not present, the tree takes care
         * of the translation on request, using the map
         */

    }

    /**
     * Minimal run of WordSuffixTree. For further tests see
     * {@link TestWordSuffixTree}.
     * 
     * @param args
     *            Not used
     */
    public static void main(String[] args) {
        String text = "Hallo Welt Hallo Rest";
        new WordSuffixTree(text, false, true, new SimpleNodeAccessor());
        System.out.println("Done.");
    }
}

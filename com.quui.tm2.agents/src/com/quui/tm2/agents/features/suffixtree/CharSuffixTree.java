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
 * TODO implement adding of texts atfer instantiation for more flexibility
 * 
 * @author Fabian Steeg (fsteeg)
 */
public class CharSuffixTree extends AlphanumericSuffixTree {

    /**
     * @param text
     *            The input text
     * @param reverse
     *            If true the tree is built for the reversed text
     * @param generalized
     *            If true the tree is generalized
     * @param accessor
     *            The accessor to use (memory vs DB)
     */
    public CharSuffixTree(String text, boolean reverse, boolean generalized,
            NodeAccessor accessor) {
        super(text, reverse, generalized, accessor);
    }

    /**
     * @param text
     *            The input text
     * @param reverse
     *            If true the tree is built for the reversed text
     * @param generalized
     *            If true the tree is generalized
     */
    public CharSuffixTree(String text, boolean reverse, boolean generalized) {
        this(text, reverse, generalized, new SimpleNodeAccessor());
    }

    /**
     * Builds a forward, generalized tree for text
     * 
     * @param text
     *            The input text
     */
    public CharSuffixTree(String text) {
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
        Map<Character, Long> trie = new HashMap<Character, Long>();
        mapper = new Mapper(this, accessor);
        String[] sentences = text.split("[\\.!?;:\\s]");
        Set<String> wordsSet = new HashSet<String>();
        // if not generalized, we add the text as one
        if (!generalized)
            wordsSet.add(text);
        // else we'll split it into sentences and later add these separately
        // (FIXME this currently give runtime problems, turn quadratic)
        else {
            for (String s : sentences) {
                if (!s.equals(" "))
                    wordsSet.add(s.trim());
            }
        }
        /** step 2: number those types */
        System.out.print(wordsSet.size() + " Words, ");
        int sentenceCount = 1;
        for (String word : wordsSet) {
            // split each sentence into words
            char[] tokens = word.toCharArray();
            if (reverse) {
                Character[] cs = new Character[tokens.length];
                for (int i = 0; i < cs.length; i++) {
                    cs[i] = new Character(tokens[i]);
                }
                List l = Arrays.asList(cs);
                char[] tokRev = new char[tokens.length];
                int k = tokRev.length - 1;
                for (Object object : l) {
                    tokRev[k] = (Character) object;
                    k--;
                }
                tokens = tokRev;
            }
            List<Long> builder = new ArrayList<Long>();
            for (char letter : tokens) {
                if (!trie.containsKey(letter)) {
                    // TODO attention, here we skip a $ in the weird-chars
                    // string, which would cause the tree to think it should
                    // terminate:
                    if (((char) counter) == '$')
                        counter++;
                    // map a word to a number:
                    trie.put(letter, counter);
                    // TODO is there a better way do achieve this:
                    // map the number to the word:
                    mapper.put(counter, letter + "");
                    counter++;
                }
            }
            /**
             * step 3: assign the values from the trie to the words in the input
             * string:
             */
            long[] ids = new long[tokens.length];

            for (int i = 0; i < ids.length; i++) {
                char rec = tokens[i];
                Object val = trie.get(rec);
                ids[i] = (Long) val;
                builder.add(ids[i]);
            }

            /**
             * step 4: build a traditional suffix tree for the string of
             * numbers:
             */
            super.addSequences(builder, sentenceCount, false);
            sentenceCount++;
        }
        System.out.print(counter + " Types, ");

        /**
         * step 5: expand the tree for words: not present, the tree takes care
         * of the translation on request, using the map
         */

        // TODO fix quadratic runtime for generalized suffix tree, see gusfield,
        // page 116
    }

    /**
     * Minimal run of CharSuffixTree. For further tests see
     * {@link TestCharSuffixTree}.
     * 
     * @param args
     *            Not used
     */
    public static void main(String[] args) {
        String text = "gehen geht geher";
        new CharSuffixTree(text, false, true, new SimpleNodeAccessor());
        System.out.println("Done.");
    }

}

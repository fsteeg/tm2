package com.quui.tm2;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.Test;

import com.quui.tm2.ImmutableAnnotation;

public final class AnnotationTest {
    @Test
    public void string() {
        /*
         * We rely on a transformability of the value object to and from String
         * objects, this works for the typical values that implement comparable:
         */
        Float f = 3.2f;
        assertEquals(f, new Float(f.toString()));
        Double d = 3.009;
        assertEquals(d, new Double(d.toString()));
        BigInteger b = BigInteger.valueOf(55);
        assertEquals(b, new BigInteger(b.toString()));
        String s = "Hi";
        assertEquals(s, new String(s.toString()));
    }

    /**
     * Equal in value, start and end.
     */
    @Test
    public void equals() {
        ImmutableAnnotation<String> s1 = ImmutableAnnotation.getInstance(null,
                "Test", 0, 1);
        ImmutableAnnotation<String> s2 = ImmutableAnnotation.getInstance(null, 
                "Test", 0, 1);
        assertEquals("Equal annotations are not equal!", s1, s2);
    }

    /**
     * Different start index.
     */
    @Test
    public void nonEqual1() {
        ImmutableAnnotation<String> s1 = ImmutableAnnotation.getInstance(null,
                "Test", 0, 1);
        ImmutableAnnotation<String> s2 = ImmutableAnnotation.getInstance(null,
                "Test", 1, 1);
        assertFalse("Unequal annotations are equal!", s1.equals(s2));
    }

    /**
     * Different end index.
     */
    @Test
    public void nonEqual2() {
        ImmutableAnnotation<String> s1 = ImmutableAnnotation.getInstance(null,
                "TesT", 0, 1);
        ImmutableAnnotation<String> s2 = ImmutableAnnotation.getInstance(null,
                "Test", 0, 2);
        assertFalse("Unequal annotations are equal!", s1.equals(s2));
    }

    /**
     * Different value.
     */
    @Test
    public void nonEqual3() {
        ImmutableAnnotation<String> s1 = ImmutableAnnotation.getInstance(null,
                "Tesl", 0, 1);
        ImmutableAnnotation<String> s2 = ImmutableAnnotation.getInstance(null,
                "Test", 0, 1);
        assertFalse("Unequal annotations are equal!", s1.equals(s2));
    }

    /**
     * Different value type.
     */
    @Test
    public void notComparable() {
        ImmutableAnnotation<String> s1 = ImmutableAnnotation.getInstance(null,
                "Test", 0, 1);
        ImmutableAnnotation<Integer> s2 = ImmutableAnnotation.getInstance(null,
                1, 0, 1);
        assertFalse("Unequal annotations are equal!", s1.equals(s2));
    }

    /**
     * Equal elements in a set are not duplicated (tests hashCode).
     */
    @Test
    public void set() {
        Set<ImmutableAnnotation<String>> set = new HashSet<ImmutableAnnotation<String>>();
        set.add(ImmutableAnnotation.getInstance(null, "Test", 0, 1));
        set.add(ImmutableAnnotation.getInstance(null, "Test", 0, 1));
        assertEquals("Set contains duplicate entries;", 1, set.size());
    }

    /**
     * Sorting order using Collections.sort()
     */
    @Test
    // TODO Also test sorted collections, as they work differently
    public void sort() {
        List<ImmutableAnnotation<String>> list = new ArrayList<ImmutableAnnotation<String>>();
        /* We create in the reverse order: */
        ImmutableAnnotation<String> a0 = ImmutableAnnotation.getInstance(null,
                "Test2", 0, 1);
        ImmutableAnnotation<String> a1 = ImmutableAnnotation.getInstance(null,
                "Test1", 1, 1);
        ImmutableAnnotation<String> a2 = ImmutableAnnotation.getInstance(null,
                "Test1", 0, 2);
        ImmutableAnnotation<String> a3 = ImmutableAnnotation.getInstance(null,
                "Test1", 0, 1);
        list.add(a0);
        list.add(a1);
        list.add(a2);
        list.add(a3);
        Collections.sort(list);
        String message = "List not sorted correctly;";
        assertEquals(message, a3, list.get(0));
        assertEquals(message, a2, list.get(1));
        assertEquals(message, a1, list.get(2));
        assertEquals(message, a0, list.get(3));
    }
}

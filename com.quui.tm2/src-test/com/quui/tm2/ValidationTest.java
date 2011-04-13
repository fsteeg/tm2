package com.quui.tm2;

import java.io.File;
import java.net.MalformedURLException;

import org.junit.Test;

import com.quui.tm2.AnnotationReader;
import com.quui.tm2.util.Preferences;
import com.quui.tm2.util.Preferences.Default;

public class ValidationTest {
    private static String ROOT = Preferences.get(Default.ROOT);

    @Test
    public void testValid() {
        AnnotationReader reader = new AnnotationReader(ROOT + "../files/valid.xml");
    }

    @Test( expected = Exception.class )
    public void testInvalid() {
        AnnotationReader reader = new AnnotationReader(ROOT + "../files/invalid.xml");
    }
}

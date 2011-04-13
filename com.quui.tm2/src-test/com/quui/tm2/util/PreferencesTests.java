package com.quui.tm2.util;

import junit.framework.Assert;

import org.junit.Test;

import com.quui.tm2.util.Preferences;
import com.quui.tm2.util.Preferences.Default;

public class PreferencesTests {

    @Test
    public void test(){
        String string = Preferences.get(Default.CORPUS);
        Assert.assertNotNull(string);
        System.out.println(string);
    }
}

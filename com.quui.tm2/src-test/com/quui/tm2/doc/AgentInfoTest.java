package com.quui.tm2.doc;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.quui.tm2.AbstractAgent;
import com.quui.tm2.doc.AgentInfo;

public class AgentInfoTest {
    @Test
    public void test() {
        String config = new AbstractAgent() {
			@Override
			public Comparable process(Comparable input) {
				return input;
			}
		}.toString();
        AgentInfo ai = new AgentInfo("Counter", config, "implements Agent<String, Integer> {");
        System.out.println(ai);
        assertEquals("Name should match", "Counter", ai.name);
        assertEquals("Input type should match", "String", ai.input);
        assertEquals("Output type should match", "Integer", ai.output);
        assertEquals(config, ai.config);
    }
}

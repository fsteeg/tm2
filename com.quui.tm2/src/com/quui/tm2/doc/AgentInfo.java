package com.quui.tm2.doc;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringEscapeUtils;

public class AgentInfo {

    @Override
    public String toString() {
        return String.format("Agent %s, input %s, output %s", name, input,
                output);
    }

    String name;
    String output;
    String input;
    String config;

    public String getName() {
        return name;
    }

    public String getOutput() {
        return output;
    }

    public String getInput() {
        return input;
    }

    public String getConfig() {
        return config;
    }
    
    public String toDot(){
       return new AgentDotTemplate().generate(this);
    }

    public AgentInfo(String name, String config, String source) {
        this.name = name;
        this.config = config;
        Matcher matcher = Pattern.compile("Agent<(.+),(.+)>").matcher(source);
        if (matcher.find()) {
            this.input = matcher.group(1).trim();
            this.output = matcher.group(2).trim();
        }
    }

    public AgentInfo(String name, String config, String input, String output) {
        this.name = name;
        this.input = input;
        this.output = output;
        this.config = config;
    }
}

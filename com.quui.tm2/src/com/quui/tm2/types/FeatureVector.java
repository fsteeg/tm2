package com.quui.tm2.types;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class FeatureVector implements Comparable<FeatureVector>, Serializable {

    private List<Float> values = new ArrayList<Float>();
    public String lemma;
    public String id;

    public FeatureVector(float[] fs, String lemma, String id) {
    	this.lemma = lemma;
    	this.id = id;
        for (float f : fs) {
            values.add(f);
        }
    }

    /***/
    private static final long serialVersionUID = 5005010477393474435L;

    public int compareTo(FeatureVector o) {
        return values.toString().compareTo(o.values.toString());
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof FeatureVector && ((FeatureVector) obj).values.equals(values);
    }

    @Override
    public int hashCode() {
        return values.hashCode();
    }
    
    public float[] getValues(){
        float[] res = new float[values.size()];
        for (int i = 0; i < res.length; i++) {
            res[i] = values.get(i).floatValue();
        }
        return res;
    }
    
    @Override public String toString() {
        return values.toString();
    }

}

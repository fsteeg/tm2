package com.quui.tm2.lab;

import java.util.List;

/* Nachteil: erzwingt state... Besser: unten*/

class I {
    interface One<T> {
        void in(T i);
    }

    interface Two<T1, T2> {
        void in(T1 t1, T2 t2);
    }
}

class O {
    interface One<T> {
        T out();
    }

    interface Two<T1, T2> {
        T1 out1();
        T2 out2();
        /* Wir brauche gar kein multi-output! */
    }
}

class Wsd1 implements I.Two<Float, String>, O.One<String> {

    public void in(Float t1, String t2) {
    // TODO Auto-generated method stub

    }

    public String out() {
        // TODO Auto-generated method stub
        return null;
    }

}

/* Was man eigentlich will: "class x implements Agent<Float>, Agent<String>", stattdessen: */

interface A<I, O> {
    O process(I i);

    interface I1<I, O> {
        O process(I i);
    }

    interface I2<I1, I2, O> {
        O process(I1 i1, I2 i2);
    }

    interface I3<I1, I2, I3, O> {
        O process(I1 i1, I2 i2, I3 i3);
    }

    interface I4<I1, I2, I3, I4, O> {
        O process(I1 i1, I2 i2, I3 i3, I4 i4);
    }

    interface I5<I1, I2, I3, I4, I5, O> {
        O process(I1 i1, I2 i2, I3 i3, I4 i4, I5 i5);
    }
}

class Tokenizer implements A<String, String> {

    public String process(String i) {
        // TODO Auto-generated method stub
        return null;
    }

}

class Wsd implements A.I2<String, Float, String> {
    public String process(String t1, Float t2) {
        // TODO Auto-generated method stub
        return null;
    }
}

class Inter<T> {
    List<A<?, T>> sources;
    List<A<T, ?>> targets;
}

class Usage {
    public static void main(String[] args) {
        Inter<String> inter = new Inter<String>();
        inter.sources.add(new Tokenizer());
        // inter.targets.add(new Wsd()); // won't compile
    }
}

interface Input<T> {}

interface StringInput extends Input<String> {}

interface FeaturesInput extends Input<Float> {}

// class WsdX implements StringInput, FeaturesInput{} // won't compile

class XsdY implements A<FeatsAmbigs, String> {

    public String process(FeatsAmbigs i) {
        // TODO Auto-generated method stub
        return null;
    }
}

class FeatsAmbigs {
    Float feat;
    String ambigs;
}

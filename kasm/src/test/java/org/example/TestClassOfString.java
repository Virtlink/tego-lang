package org.example;

// Generic top-level class
public abstract class TestClassOfString extends TestClass<String> {

    // Generic inner class
    public class NOfInt extends N<Integer> {
        // Generic inner-inner class
        public class NNOfBool extends NN<Boolean> {
        }
    }
    // Generic inner class with two parameters
    public class N2OfIntObj extends N2<Integer, Object> {
        // Generic inner-inner class with two parameters
        public class NN2OfBoolObj extends NN2<Boolean, Object> {

        }
    }
    // Generic static nested class
    public static class SOfString extends S<String> {
        // Generic inner class in static nested class
        public class NOfInt extends N<Integer> {
        }
    }
    // Generic static nested class with two parameters
    public static class S2OfStringObj extends S2<String, Object> {

    }
}

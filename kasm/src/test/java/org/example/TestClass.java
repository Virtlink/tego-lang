package org.example;

// Generic top-level class
public class TestClass<T> {
    // Method with generic parameters
    void m(T t) {}
    // Method with generic parameters and generic return type
    T f(T t) { return t; }

    // Generic inner class
    public class N<U> {
        // Generic inner-inner class
        public class NN<V> {
            // Method with generic parameters
            void m(T t, U u, V v) {}
            // Method with generic parameters and generic return type
            T f(T t, U u, V v) { return t; }
        }
    }
    // Generic inner class with two parameters
    public class N2<U1, U2> {
        // Generic inner-inner class with two parameters
        public class NN2<V1, V2> {
            // Method with generic parameters
            void m(T t, U1 u1, U2 u2, V1 v1, V2 v2) {}
            // Method with generic parameters and generic return type
            T f(T t, U1 u1, U2 u2, V1 v1, V2 v2) { return t; }
        }
    }
    // Generic static nested class
    public static class S<X> {
        // Method with generic parameters
        void m(X x) {}
        // Method with generic parameters and generic return type
        X f(X x) { return x; }
        // Generic inner class in static nested class
        public class N<V> {
            // Method with generic parameters
            void m(X x, V v) {}
            // Method with generic parameters and generic return type
            X f(X x, V v) { return x; }
        }
    }
    // Generic static nested class with two parameters
    public static class S2<X1, X2> {
        // Method with generic parameters
        void m(X1 x1, X2 x2) {}
        // Method with generic parameters and generic return type
        X1 f(X1 x1, X2 x2) { return x1; }
    }
}

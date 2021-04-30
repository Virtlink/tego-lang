# Tego

## Strategies
A strategy in Tego is a function that always takes an implicit argument and produces a lazy (possibly async) computation of one or multiple values. When the lazy computation returned from a strategy doesn't produce a value, it is said to have _failed_.

Additionally, strategies can be cancelled (interrupted), report progress, and be debugged. Strategy results are memoized.


## Defining a Strategy
To define a strategy, start with a declaration of the strategy's signature. For example, the following declares a strategy `fib` that takes an integer and produces an integer:

```
decl fib: Int -> Int
```

The definition would be:

```
fib 0 = !0
fib 1 = !1
fib n = <add> (<dec; fib> n, <dec; dec; fib> n)
```

Note that each of the definitions must be independent.

The corresponding Java code would be:

```java
class FibStrategy extends Strategy<Integer, Lazy<Integer>> {

    Integer eval(Context ctx, Integer n) throws InterruptedException {
        if (n == 0) {
            // !0
            return 0
        } else if (n == 1) {
            // !1
            return 1
        } else {
            Strategy<Integer, Integer> dec = Dec.getInstance();
            Strategy<Integer, Integer> fib = Fib.getInstance();
            Strategy<Strategy<Integer, Integer>, Strategy<Integer, Integer>, Integer, Integer> seq = Seq.getInstance();
            Strategy<Integer, Integer, Object, Tuple<Integer, Integer>> buildTuple2 = BuildTuple2.getInstance();
            Strategy<Tuple<Integer, Integer>, Integer> add = Add.getInstance();

            // n1 := <dec; fib> n
            Strategy<Integer, Integer> s1 = seq.apply(dec, fib);
            Integer n1 = s1.eval(ctx, n);
            if (n1 == null) return null;
            // n2 := <dec; dec; fib> n
            Strategy<Integer, Integer> s2 = seq.apply(dec, seq(dec, fib));
            Integer n2 = s2.eval(ctx, n);
            if (n2 == null) return null;

            // t := !(n1, n2)
            Tuple<Integer, Integer> t = buildTuple2.apply(n1, n2)

            // n3 := <add> t
            Integer n3 = add.eval(ctx, t);
            if (n3 == null) return null;

            return n3;
        }
    }

}
```

A strategy can return a lazy computation (a single result; or no result in case of failure),
or it can return a lazy sequence of values (multiple results; or no results in case of failure).


A strategy produces a lazy (sequence of) result(s). The `glc` strategy is built-in, and defined as:

```
decl glc<T, U, V>(T -> [U], U -> [V], T -> [V]): T -> [V]
```

```java
class GlcStrategy<T, U, V> extends Strategy3<Strategy<T, U>, Strategy<U, V>, Strategy<T, V>, T, V> {

    Seq<V> eval(Context ctx, Strategy<T, U> sc, Strategy<U, V> st, Strategy<T, V> se, T input) throws InterruptedException {
        return sequence {
            // ...
        };
    }

}
```
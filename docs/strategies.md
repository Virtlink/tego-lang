# Strategies
A strategy producing one or more results lazily. To achieve this, they are implemented using coroutines and state machines.
It may call other strategies lazily, when their results are needed.

/*
Let's look at the Fibonacci sequence as an example. The Tego following definition of `fib` returns an infinite sequence of Fibonacci numbers,
starting with `0` and `1`:

```
decl fib: Any -> [Int]
fib = <generate(fib_next); fst> (0, 1).

decl fib_next: (Int, Int) -> (Int, Int)
fib_next n1, n2 = !(n2, <add> (n1, n2)).
```

Its definition uses the `!` operator, which is syntactic sugar for the built-in `build` strategy that takes a value and wraps it in a strategy.
The strategy also uses the `add` and `fst` strategies, which add two integers in a tuple and project the first component of a tuple, respectively.
Their signatures are:

```
decl build<T>(T): Any -> T
decl fst<T>: (T, Any) -> T
decl add: (Int, Int) -> Int
```

Finally, the strategy uses the special `generate` strategy, which produces an infinite sequence by applying a given strategy to the current value.
It is defined as:
*/

Let's look at the `generate` strategy. This strategy takes an initial value and a strategy, and produces an infinite sequence of values.
It is defined as follows:

```
decl generate<T>(T -> T): T -> [T]
generate(s) i = v := <s> i; ![v | <generate(s)> v].
```

??? note "Definition of `generate`"
    The `generate` strategy is defined recursively, building the head of the sequence and recursively building the tail of the sequence.








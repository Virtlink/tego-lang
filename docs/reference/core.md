# Core Expressions
The following are core expressions:

- `x` — Use a value from a variable `x` of type `T`.
- `@x` — Capture a value in a variable `x` of type `T`, which is an expression of type `T -> T`.
- `__eval(s, a1, .., an, v)` — Evaluate a strategy `s(A1, .., An): T -> R` on values `a1 : A1`, .., `an : An` and `v : T`, resulting in a value of type `R`.
- `__maybeN(s)` — Lifts a strategy `s(..): T -> R` to `(..): T? -> R`, skipping evaluation if the input argument is `null`.
- `s(a1, a2)` — Apply a strategy `s(A1, A2) : T -> R` on arguments `a1 : A1` and `a2 : A2`, resulting in a value of type `T -> R`.
- `\(p1, p2) = s.\` — A lambda of type `(A1, A2) : T -> R` with parameters `p1 : A1` and `p2 : A2` and a strategy body `s` of type `T -> R`.
- `let x = e in b` — Defines a variable `x : X` with expression `e : X` in the scope of body `b : T -> R`. The expression has type `T -> R`.

## Built-In Strategies

- `flatMap<T, R>(T -> [R]): [T] -> [R]` — Monadic reverse `=<<` bind on a lazy sequence.
- `is<reified C>(): Any -> C?` — Attempts to cast the input to the specified type.
- `seq<T, U, R>(T -> U?, U -> R?): T -> R?` — Monadic `>=>` pipe on an Option type.

## Other Strategies

```
def where<T, R>(s: T -> R?): T -> T? =
    {x: ?x; s; !x}

def where<T, R>(s: T -> R?): T -> T? :- i ->
    let x = i in
        s; !x
```

```
def seq<T, U, R>(f: T -> U?, g: U -> R?): T -> R? :- x ->
    do
        y <- <f> x
        <g> y
```

```
def seq<T, U, R>(f: T -> U?, g: U -> R?): T -> R? :- x ->
    <x(\y -> <g> y\)> <f> x
```
# Tego Compiler
Each strategy is compiled to a Java class that returns its results lazily. To achieve this, the compiler performs the following steps:

1. Convert to ANF
2. Build state machine
3. Generate bytecode

We use this example:

```
decl generate<T>(T -> T): T -> [T]
generate(s) i = v := <s> i; ![v | <generate(s)> v].
```

## Convert to ANF
The first step of the compilation algorithm is to convert the strategy expressions into an intermediate representation known as Administrative Normal Form (ANF). This ensures almost all expressions only operate on immediate expressions. Immediate expressions are expressions for which no further computation is necessary, such as a constant value or a variable reference.

To represent the expressions in ANF, we define the following IR expressions:

- `v`\
  Returns the value of variable `v`.
- `let v = e in b`\
  Evaluates `e`, assigns it to variable `v` in the scope of `b`, and returns the value of evaluating `b`.
- `lam (p1: T1, p2: T2, ..) TO = b`\
  Returns a lambda function with parameters `p1`, `p2`, ... (of types `T1`, `T2`, ...) and body `b` that  returns a value of type `TO`.
- `[lazy] strategy (p1: T1, p2: T2, ..) i: TI -> TO = b`\
  Returns a lambda strategy with parameters `p1`, `p2`, ... (of types `T1`, `T2`, ...) and body `b` that takes a value of type `TI` and returns a value of type `TO`. When the strategy is marked `lazy`, it returns its results lazily.
- `s(a1, a2, ..)`\
  Applies arguments `a1`, `a2`, ... to strategy `s`, returning a strategy of type `TI -> TO`.
- `<s> i`\
  (Maybe lazy.) Evaluates strategy `s` with input argument `i`, returning a value of the strategy's return type.
- `yield(v)`\
  (Lazy.) Yields the value of `v`.
- `yieldAll(vs)`\
  (Lazy.) Yields the values of `v`.

The `generate` strategy is represented in ANF as follows:

```
strategy (s: T -> T) i: T -> [T] =
  let v = <s> i in
  let tl = <generate(s)> v in
  let l = [v | lt] in
  let r = build(l) in
  let sr = <r> i in
  sr
```

This computes the sequence `sr` eagerly and returns it. However, to perform lazy evaluation, instead of computing the sequence and returning it, we should return a sequence that knows how to compute itself.

The `generate` strategy is represented in ANF as follows:

```
lazy strategy (s: T -> T) i: T -> [T] =
  let v = <s> i in
  let tl = <generate(s)> v in
  let l = [v | lt] in
  let r = build(l) in
  let sr = <r> i in
  yieldAll(sr)
```

!!! note ""
Note that we end the strategy by applying `r` to `i` and yielding its results. This forces evaluation of the strategy `r` that we built, and returns the sequence of results.

## Continuations
The trick to lazy evaluation is continuations. The above lazy strategy can be rewritten to accept a continuation, which is a function that takes the result of the strategy and do something with it.

```
strategy (s: T -> T, cont: (T) -> ()) i: T -> [T] =
  let v = <s> i in
  let tl = <generate(s)> v in
  let l = [v | lt] in
  let r = build(l) in
  let sr = <r> i in
  yieldAll(sr, cont)
```

## Build a State Machine
The above ANF is sufficient to generate an eager implementation of the strategy. But to generate a lazy implementation, we need to recognize that every time we return a result, the computation can be suspended. To achieve this, we use coroutines and a state machine.

Instead of computing the whole list of values at once (which is impossible for our infinitely recursive definition of `generate`), we compute the list lazily. We dictate that strategies can be _suspended_ (using Kotlin terminology) and a given strategy run sequentially from suspension point to suspension point (that is, from strategy evaluation to strategy evaluation).

This is the above example ANF with its suspension points indicated:

```
lazy strategy (s: T -> T) i: T -> [T] =
  let v = <s> i in                  // suspension point
  let tl = <generate(s)> v in       // suspension point
  let l = [v | lt] in
  let r = build(l) in
  let sr = <r> i in                 // suspension point
  yieldAll(sr)                      // suspension point
```

The state machine generates a label for the start of each sequential block.

```
lazy strategy (s: T -> T) i: T -> [T] =
  label l0:
  let v = <s> i in                  // suspension point
  label l1:
  let tl = <generate(s)> v in       // suspension point
  label l2:
  let l = [v | lt] in
  let r = build(l) in
  let sr = <r> i in                 // suspension point
  label l3:
  yieldAll(sr)                      // suspension point
  label l4:
```

And a con
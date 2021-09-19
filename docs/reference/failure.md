# Failure
A strategy that returns a nullable type can fail, and when it returns `null` it is considered to have failed. In this example, `indexOf` (through its calls to `indexOf'`) will fail if the character is not found in the input string:

```tego
public def indexOf(char: Char): String -> Int?
= indexOf'(char, 0)

def indexOf'(char: Char, i: Int): String -> Int? = match
  [] -> fail
  [char|_] -> i
  [_|cs] -> <indexOf'(char, <inc> i)> cs
```

Failure just means the strategy returns `null`, but since a strategy cannot take `null` as its implicit argument, this means the whole strategy failed.

For example, the following composition will attempt to apply `fail: Any -> Nothing?` to `10 : Int`.
This will result in `null`. Subsequently, we cannot apply `debug: Any -> Any` to `null`, as we explicitly disallow feeling `null` as the implicit argument into a strategy. Therefore, `debug` never gets executed and the whole strategy fails:

```tego
def foo: Int -> Int? :- i ->
    <fail ; debug> i
```

This is desugared to:

```tego
def foo: Int -> Int? :- i ->
    <debug> <fail> i
```

This is compiled to:

```tego
def foo: Int -> Int? :- i ->
    let v1: Int = i in
    let s2: Any? -> Nothing? = __maybe(fail) in
    let r2: Nothing? = __eval(s2, v1) in
    let s3: Nothing? -> Nothing? = __maybe(debug) in
    let r3: Nothing? = __eval(s3, r2) in
    r3
```

!!! note ""
    Note how the `__maybe` lifts the strategies to be able to accept `null`. The `__maybe` strategy never invokes the strategy when the input value is `null`.

    ```tego
    public extern pure def __maybe<T, R>(s: T -> R?): T? -> R?
    ```

!!! warning "Evaluation and `__maybe`"
    Any evaluation of a strategy (that is, in between angled brackets or desugared to it),
    will also apply the `__maybe` strategy around it. It is desugared as follows:

    ```tego
    // Original:
    <s(a1, a2)> e

    // Desugared:
    __eval(__maybe(s), a1, a2, e)
    ```

    The special built-in strategy `__eval(strategy, args*, input)` takes the
    strategy, its applied arguments, and the implicit argument, and evaluates it.
    The built-in `__maybe(strategy)` lifts a strategy to be able to deal with `null`.

This is compiled to something like the following Java code:

```java
public @Nullable Integer foo(int i) {
    final int v1 = i;
    final Strategy<@Nullable Object, @Nullable Void> s2 = MaybeStrategy.apply(FailStrategy);
    final @Nullable Void r2 = engine.eval(s2, v1);
    final Strategy<@Nullable Void, @Nullable Void> s3 = MaybeStrategy.apply(DebugStrategy);
    final @Nullable Void r3 = engine.eval(s3, r2);
    return r3;
}
```

We can optimize evaluating `__maybe(_)` on a value that is never `null` to just evaluate the strategy.

```java
public @Nullable Integer foo(int i) {
    final int v1 = i;
    final Strategy<Object, @Nullable Void> s2 = FailStrategy;
    final @Nullable Void r2 = engine.eval(s2, v1);
    final Strategy<@Nullable Void, @Nullable Void> s3 = MaybeStrategy.apply(DebugStrategy);
    final @Nullable Void r3 = engine.eval(s3, r2);
    return r3;
}
```

If `fail` is a `pure` strategy (and it is, it has no side-effects), then we can remove its call and replace it with its result, which is `null`. However, note that `debug` is not a pure strategy.

```java
public @Nullable Integer foo(int i) {
    final @Nullable Void r2 = null;
    final Strategy<@Nullable Void, @Nullable Void> s3 = MaybeStrategy.apply(DebugStrategy);
    final @Nullable Void r3 = engine.eval(s3, r2);
    return r3;
}
```

Similarly, we can optimize the case of evaluating `__maybe(_)` on a value that is `null` to just return `null`.

```java
public @Nullable Integer foo(int i) {
    final @Nullable Void r3 = null;
    return r3;
}
```

Finally, note that `Void` in Java is not a subtype of all types, therefoew we cannot cast it to our desired return type `Integer`. However, since `@Nullable Void` can have only one possible value, `null`, we can use that value explicitly everywhere, and remove all captured variables:

```java
public @Nullable Integer foo(int i) {
    return null;
}
```

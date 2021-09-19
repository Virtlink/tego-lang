# Strategy Composition
The most basic way to compose three strategies `s1: A -> B`, `s2: B -> C`,
and `s3: C -> D` is to apply when in order on a value `a : A`.
The result will be of type `D`:

```tego
<s3> <s2> <s1> a
```


## Option Monad
Many strategies return a value that may be absent. While this can be represented
in several ways, e.g., as `null` or as an empty list, in Tego these are all
implementations of the `Option<T>` monad. It has a monadic bind operator (`>>=`)
called `maybe`, with the following signature:

```tego
def maybe<T, R>(T -> Option<R>): Option<T> -> Option<R>
```

Using `maybe` directly is kind of cumbersome. For example, three strategies
`s1: A -> Option<B>`, `s2: B -> Option<C>` and `s3: C -> Option<D>` can be
composed using `maybe` and applied to `a: A` as follows:

```tego
<maybe(s3)> <maybe(s2)> <s1> a      // Option<D>
```

As syntactic sugar, Tego provides the semi-colon operator `;`, which allows you
to write instead:

```tego
<s1 ; s2 ; s3> a                    // Option<D>
```

??? summary "Implementation"
    The actual implementation of `maybe` is as follows:

    ```tego
    def maybe<T, R>(s: T -> Option<R>): Option<T> -> Option<R> :- x -> when(x)
        \None -> None\
        \Some(x') -> <s> x'\
    ```

    !!! warning "This is not definitive syntax, the `when` operator is not yet supported"


## Sequence Monad
Many strategies return a lazy sequence of values. This is an implementation of
the `Seq<T>` monad. It has a monadic bind operator (`>>=`) called `flatMap`,
with the following signature:

```tego
def flatMap<T, R>(T -> Seq<R>): Seq<T> -> Seq<R>
```

Like with `maybe`, using `flatMap` directly is not very nice. For example,
tree strategies `s1: A -> Seq<B>`, `s2: B -> Seq<C>` and `s3: C -> Seq<D>` can be
composed using `flatMap` and applied to `a: A` as follows:

```tego
<flatMap(s3)> <flatMap(s2)> <s1> a                  // Seq<D>
```

As syntactic sugar, Tego provides the bind operator `|>`, which allows
you to write instead:

```tego
<s1 |> s2 |> s3> a
```

## Do-notation
When you want to name the arguments in the bind operator, you'll have to write
something like this:

```tego
<flatMap(\x -> <flatMap(s3)> <s2> x\)> <s1> a       // Seq<D>
```

Instead, you can write this easier using [do-notation](do-notation.md).
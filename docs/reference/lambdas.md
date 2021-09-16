# Lambdas
A lambda is an anonymous function or strategy. Lambdas are written as a rule or strategy definition between backslashes. For example, the following lambda adds 1 to the argument `a`.

```tego
\a -> <add> (a, 1)\
```

A lambda can have more parameters:

```tego
\(i) a -> <add> (a, i)\
```

A lambda can be written as a strategy:

```tego
\(i) = !(<id>, i); add\
```

Even when it doesn't have any parameters:

```tego
\= !(<id>, 1); add\
```

!!! note "Lambda Mnemonic"
    To remember which way the slash should go, think of the Greek letter lambda `λ`. Note that a lambda in Tego ends with the same backslash.

## Types
When the compiler cannot figure out the type of the lambda, you can specify it explicitly:

```tego
\: Int -> Int :- a -> <add> (a, 1)\
\(i: Int): Int -> Int :- a -> <add> (a, i)\

\= !(<id>, 1); add\
\(i: Int): Int -> Int = !(<id>, i); add\
```

!!! warning "Not sure yet about this type syntax"
    Basically, we'd have to allow specifying a type for any expression,
    using the syntax `e : T`. Maybe not a bad idea?

## See Also
- Background: [Lambdas](../background/lambdas.md)
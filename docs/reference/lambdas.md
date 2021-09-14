# Lambdas
A lambda is an anonymous function or strategy. Lambdas are written as a rule or strategy definition between backslashes. For example, the following lambda adds 1 to the argument `a`.

```tego
\a -> <add> (a, 1).\
```

A lambda can have more arguments:

```tego
\(i) a -> <add> (a, i).\
```

A lambda can be written as a strategy:

```tego
\(i) = !(<id>, i); add.\
```

Even when it doesn't have any arguments:

```tego
\= !(<id>, 1); add.\
```

!!! note "Lambda Mnemonic"
    To remember which way the slash should go, think of the Greek letter lambda `λ`. Note that a lambda in Tego ends with the same backslash, and that the expression within the lambda should terminate with a period (`.`), like any expression.

## See Also
- Background: [Lambdas](../background/lambdas.md)
# Lambdas
Whenever you use a lambda (anonymous function or strategy) in your code, it is compiled to a _nonymous_ strategy and the lambda is replaced with the new name. The types of the nonymous declaration are inferred from the surrounding code.

For example, the following:

```tego
val bar: Int -> Int = \a -> <add> (a, 1).\.
```

Is desugared into:

```tego
val bar: Int -> Int = __lambda1.

def __lambda1: Int -> Int
__lambda1 :- a -> <add> (a, 1).
```

## See Also
- Reference: [Lambdas](../reference/lambdas.md)
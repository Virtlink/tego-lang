# Strategies
A strategy in Tego is a function that takes an implicit argument and performs an operation on it to produce a value. A strategy needs to be declared, after which it can be defined. For example, the following declares a strategy `stringLength` that takes a `String` and produces an `Int`.

```tego
def stringLength: String -> Int
```

The strategy is defined to use the `String.length()` instance method (in Java) to return the length of the string:

```tego
stringLength = String#length.
```

A single strategy declaration can have multiple strategy definitions. Strategies are evaluated in the order they appear in the module, and the first definition that succeeds determines the final result. For example, the following strategy `inc` increments an integer or floating-point value:

```tego
def inc<T: Number>: T -> T?
inc = as-Int; Integer#sum(<id>, 1.0)
inc = as-Float; Float#sum(<id>, 1.0)
```

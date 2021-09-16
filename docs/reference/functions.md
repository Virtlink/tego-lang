# Functions
Like strategies, functions perform an operations. However, unlike strategies, they don't have an implicit parameter. Generally, the only functions you will use are Java static method calls and Java constructors.

In a context of type `T`, calling a function that returns a value of that type works as expected:

```tego
def createList: Any -> List<Int> :- _ ->
  Collection#emptyList<Int>
```

However, when in a strategy context of type `T -> R`, calling a function would be a type mismatch:

```tego
// INVALID
def createList: Any -> List<Int> =
  Collection#emptyList<Int>
```

Instead, you can use the _build_ operator to call a function in a strategy context:

```tego
// valid
def createList: Any -> List<Int> =
  !Collection#emptyList<Int>
```

This works because the build operator is syntactic sugar for a lambda, where any implicit strategy applications are applied to the input argument:

```tego
def createList: Any -> List<Int> =
  \_ -> Collection#emptyList<Int>\
```

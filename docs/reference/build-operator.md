# Build Operator
The build operator `!` allows a value to be built in a context where a strategy
is expected. For example:

```tego
!MyClass(<inc> i, "x", MyOtherClass(<id>))
```

Its type is `Int -> MyClass`.

Breakdown:

```tego
  MyClass                                        : MyClass(Int, String, MyOtherClass)
                i                                : Int
           inc                                   : Int -> Int
          <inc> i                                : Int
                   "x"                           : String
                        MyOtherClass             : MyOtherClass(Int)
                                      id         : Int -> Int
                                     <id>        : Int
                        MyOtherClass(<id>)       : MyOtherClass
  MyClass(<inc> i, "x", MyOtherClass(<id>))      : MyClass
 !MyClass(<inc> i, "x", MyOtherClass(<id>))      : Any -> MyClass
<!MyClass(<inc> i, "x", MyOtherClass(<id>))>     : Int -> MyClass
<!MyClass(<inc> i, "x", MyOtherClass(<id>))> 3   : MyClass
```

In the build operator, any strategy application is applied to the implicit input
of the build operator. In the example above, `<id>` is applied to the implicit
input `3`, even though it is nested deep inside some constructors.

If, instead, you want to apply a strategy to a particular value other than
the implicit input, write it with an explicit term, like `<inc>` in the example
above.

## Desugaring
The build operator is desugared into a lambda like this:

```
!MyClass(<inc> i, "x", MyOtherClass(<id>))
  : Int -> MyClass

                   -->

\a -> MyClass(<inc> i, "x", MyOtherClass(<id> a)).\
  : Int -> MyClass
```

Desugaring the build operator introduces a name for the implicit receiver
and adds it to any strategy applications that don't have an explicit argument.

!!! note ""
    Theoretically a rule `a -> b` is just sugar for a strategy `?a; !b`.
    However, practically, rules are not desugared and instead strategies are
    desugared into rules.

    When a rule has a complex pattern on the left-hand side, desugaring
    associates a fresh name to the pattern and inserts that for any strategy
    applications on the right-hand side that have no argument. Thus:

    ```
    (c1, c2) -> (<fst; inc>, <snd; dec>).
    ```

    Becomes:

    ```
    (c1, c2) ; @__input -> (<fst; inc> __input, <snd; dec> __input).
    ```

## Invoking a Function
Given a function `f(String): Int`, you cannot use it in a strategy context. One way is to write it in a lambda that ignores the input argument:

```tego
<\(s) :- _ -> f(s)\("mystring")> x
```

However, the build operator can be used as a short-hand for calling functions:

```tego
<!f("mystring")> x
```

This is desugared into the above.
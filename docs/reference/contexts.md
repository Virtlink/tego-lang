# Contexts
In Tego, there are a few different contexts: the strategy context,
the value context, and the pattern context.

## Strategy Context
The strategy context is of type `T -> R`. It always has an implicit argument
of type `T`, which we'll call the _receiver_, and produces a value of type `R`.

To switch to a value context, use the _build_ operator `!`. Its signature is:

```tego
operator def build<R>(v: R): Any -> R
```

Note how the first argument is a value context of type `T`.

To switch to a pattern context, use the _match_ operator `?`. Its signature is:

```tego
operator def match<T, R>(p: Pattern<T>): T -> R
```

Note how the first argument is a pattern context of type `Pattern<T>`.

## Example
```tego
!(<id>, 1); add         : Int -> Int
!(<id>, 1)              : Unit -> Pair<Int, Int>
            add         : Pair<Int, Int> -> Int
  <id>                  : Int
   id                   : Int -> Int
        1               : Int
```

## Calling Methods
We can distinguish the following kinds of methods and fields:

- constructors
- instance fields
- static fields
- instance properties
- static properties
- instance methods
- static methods
- equality and comparity

### Instance Methods, Fields, and Properties
An instance method `Ret MyClass.method(Arg)` has this signature:

```tego
MyClass#method(Arg): MyClass -> Ret
```

!!! note "Returning `void`"
    A Java method that returns `void` returns `Unit()` in Tego.

Fields are similar, but obviously don't have any arguments.
Properties are similar, but they are derived from the _getters_ in a class.

### Static Methods, Fields, and Properties
A static method `Ret MyClass.function(Arg)` has this signature:

```tego
MyClass#function(Arg): Class<MyClass> -> Ret
```

!!! note ""
    Instance methods are applied to an instance.
    Static methods are applied to a class.

### Constructors
A constructor `MyClass(Arg)` has this signature:

```tego
MyClass(Arg): MyClass
```

# Match Operator
The match operator `?` allows a value to be matched in a context where a strategy
is expected.

## Type Match
The simplest use is to verify the type of the value:

```tego
?MyClass()
```

Desugared:

```tego
is<MyClass>
```

## Complex Match (on Decomposable Type)
However, we can also match more complex expressions. If the type supports
decomposition, the individual components are matched as well:

```tego
?MyClass(10, "x")
```

Desugared:

```tego
is<MyClass> ;
where(MyClass#component1 ; ?10) ;
where(MyClass#component2 ; ?"x")
```

Desugared more:

```tego
is<MyClass> ;
where(MyClass#component1 ; is<Int> ; Int#equals(10)) ;
where(MyClass#component2 ; is<String> ; String#equals("x"))
```

## Complex Match (on Opaque Type)
However, if the type does not support decomposition, the value is compared using
the `equals` method on another instance of the object. Note that in this case
it is not possible to project part of the value, nor can part of the value be
captured into a variable. For example:

```tego
?MyClass(10, "x")
```

Desugared:

```tego
is<MyClass> ; MyClass#equals(MyClass(10, "x"))
```

## Using Variables
A given bound variable can be used in place of a value.

```tego
x := 10
y := "x"

?MyClass(x, y)
```

## Capturing Variables
A value can be captured in a variable by using `@x` in a match. For example:

```tego
?MyClass(@x, @y)
```

Desugared:

```tego
{x, y:
    is<MyClass> ;
    where(MyClass#component1 ; @x) ;
    where(MyClass#component2 ; @y)
}
```

To simply capture a value:

```tego
!10 ; ?@x
```

For which this is a short-hand (and also the desugared version,
where the type is `T -> T`):

```tego
!10 ; @x
```

## Projecting
When applying a strategy without an argument in a match, its result is
projected out of the match. Note, however, that only _one_ projection
is allowed.

```tego
?MyClass(<id>, _)
```

Desugared:

```tego
is<MyClass> ;
where(MyClass#component1 ; id ; @__proj) ;
where(MyClass#component2 ; _) ;
__proj
```


## Wildcards
Wildcards are written as underscore `_`.

-----




For example:

```tego
?MyClass(<inc> i, "x", MyOtherClass(<id>))
```

Its type is `Any -> Int`.

Breakdown:

```tego
                                     id         : Int -> Int
                                    <id>        : Pattern<Int>
                       MyOtherClass(<id>)       : Pattern<MyOtherClass>
                  "x"                           : Pattern<String>
               i                                : Int
          inc                                   : Int -> Int
         <inc> i                                : Pattern<Int>
 MyClass(<inc> i, "x", MyOtherClass(<id>))      : Pattern<MyClass>
?MyClass(<inc> i, "x", MyOtherClass(<id>))      : Any -> Int
```

Desugared:

The match operator is desugared into a bunch of variable captures,
and equality comparisons:

```tego
  is<MyClass> ; mc@_ ;
  <MyClass#component1 ; where(Int#equals(<inc> i))> mc ;
  <MyClass#component2 ; where(String#equals("x"))> mc ;
  <MyClass#component3 ; is<MyOtherClass> ; moc@_ ;
    <MyOtherClass#component1 ; id> moc
  > mc
```

```tego
inline def is<reified C>(): Any -> C?
```

!!! note "Reified"
    A reified type parameter is one that is also added as a `Class` parameter
    to the strategy declaration. This is only allowed for inlined strategies.

## Where
The built-in `where` strategy takes a predicate and returns the input value when
the predicate returns `true`, but fails when the predicate returns `false`.

```tego
def where<T>(s: T -> Bool): T?
```

!!! note ""
    This definition of `where` is different from Stratego.
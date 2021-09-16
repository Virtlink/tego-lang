# Strategies
A strategy in Tego is a function that takes an implicit parameter and performs an operation on it to produce a result. A strategy is generally declared and defined at the same time (except for `extern` strategies, whose definition is somewhere else).

For example, the following declares a strategy `stringLength` that takes a `String` and produces an `Int`. The strategy is defined to use the `String.length()` instance method (in Java) to return the length of the string:

```tego
import java/lang::String

def stringLength: String -> Int = String#length
```


## Syntax
The basic syntax for a strategy definition is:

```tego
// Strategy
def $name <$type-params> ($params) $inputType -> $outputType = $body

// Rule
def $name <$type-params> ($params) $inputType -> $outputType :- $input -> $output
```

And when using a lambda:

```tego
// Strategy
def $name <$type-params> ($params) $inputType -> $outputType = $body

// Rule
def $name <$type-params> ($params) $inputType -> $outputType :- $input -> $output
```

With an implicit input parameter:

```tego
// Generic
def foo1 = id
def foo1<T>(): T -> T = id

def foo2(a) = !a
def foo2<T>(a: T): Any -> T = !a

// Non-generic
def foo3 = stringLength
def foo3(): String -> Int = stringLength

def foo4(a) = <stringLength> a
def foo4(a: String): Any -> Int = !(<stringLength> a
```

With an explicit input parameter:

```tego
// Generic
def foo1 :- input -> input
def foo1<T>(): T -> T :- input -> input

def foo2(a) :- input -> a
def foo2<T>(a: T): Any -> T :- input -> a

// Non-generic
def foo3 :- input -> <stringLength> input
def foo3(): String -> Int :- input -> <stringLength> input

def foo4(a) :- input -> <stringLength> a
def foo4(a: String): Any -> Int :- input -> <stringLength> a
```

As a lambda with an implicit input parameter:

```tego
// Generic
/= id/
/<T>: T -> T = id/

/(a)= !a/
/<T>(a: T): Any -> T = !a/

// Non-generic
/= stringLength/
/: String -> Int = stringLength/

/(a) = <stringLength> a/
/(a: String): Any -> Int = !(<stringLength> a)/
```

As a lambda with explicit input parameter:

```tego
// Generic
/:- input -> input/
/T -> T :- input -> input/

/(a) :- input -> a/
/(a: T): Any -> T :- input -> a/

// Non-generic
/:- input -> <stringLength> input/
/: String -> Int :- input -> <stringLength> input/

/(a) :- input -> <stringLength> a/
/(a: String): Any -> Int :- input -> <stringLength> a/
```

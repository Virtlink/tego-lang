# Java Interop
It is possible to call Java methods from Tego and use their results.

## Instance Methods
Java instance methods are implicitly declared as public external strategies that take a value of the receiver type as the implicit input parameter. For example, the `length` method defined on `java/lang::String` is represented in Tego as:

```tego
extern module java/lang
String::length: String -> Int
```

Any additional parameters are parameters to the strategy. For example, the `replace` method defined on `java/lang::String` is declared as:

```tego
extern module java/lang
String::replace(Char, Char): String -> String
```

## Static Methods
Java static methods are implicitly declared as public external strategies that take a value of type `Any` as the implicit input parameter and ignore it. However, the implicit argument can be captured using, say, `<id>` in place of an argument. For example, the `java/util::Collections::emptyList` static method is declared as:

```tego
extern module java/util
Collection::emptyList<T>: Any -> List<T>
```

Again, like with instance methods, any additional parameters are parameters to the strategy.


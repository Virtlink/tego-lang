# Java Interop
It is possible to call Java methods from Tego and use their results.

## Fully-Qualified Names
Java types and members can be referenced using either their fully-qualified name or their shortened name. For example, the `List` type is referenced using its fully-qualified name as:

```
java/util::List
```

Or by using an explicit import:

```
import java/util::List

List
```

Or using an implicit import:

```
import java/util::*

List
```

## Reference
The following are examples to reference Java types and members in various situations:

```
// Types
Thread                      // class
Runnable                    // interface
DayOfWeek                   // enum
Array<String>               // array
ArrayList<T>                // generic class
List<T>                     // generic interface
   $                        // static nested class
   .                        // inner class

// Static Members
DayOfWeek#SUNDAY            // enum field
Collections#emptyList<T>    // generic static function
Int#MAX_VALUE               // static field

// Instance Members
Pair<K, V>#getKey           // instance method
```


## Instance Methods
Java instance methods are implicitly declared as public external strategies that take a value of the receiver type as the implicit input parameter. For example, the `length` method defined on `java/lang::String` is represented in Tego as:

```tego
extern module java/lang
String#length: String -> Int
```

Any additional parameters are parameters to the strategy. For example, the `replace` method defined on `java/lang::String` is declared as:

```tego
extern module java/lang
String#replace(Char, Char): String -> String
```

## Static Methods
Java static methods are implicitly declared as public external strategies that take a value of type `Any` as the implicit input parameter and ignore it. However, the implicit argument can be captured using, say, `<id>` in place of an argument. For example, the `java/util::Collections#emptyList` static method is declared as:

```tego
extern module java/util
Collection#emptyList<T>: Any -> List<T>
```

Again, like with instance methods, any additional parameters are parameters to the strategy.


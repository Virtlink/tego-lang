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


## References
The following are examples to reference Java types and members in various situations:

```
// Types
Thread                      // class
Runnable                    // interface
DayOfWeek                   // enum
Array<String>               // array
ArrayList<T>                // generic class
List<T>                     // generic interface
Map.Entry                   // static nested class/interface
   $                        // inner class
                            // method local class
                            // anonymous class

// Static Members
DayOfWeek#SUNDAY            // enum field
Collections#emptyList<T>    // generic static function
Int#MAX_VALUE               // static field

// Instance Members
Pair<K, V>#getKey           // instance method
```


## Instance Methods
Java instance methods (of classes and interfaces) are implicitly declared as public external strategies that take a value of the receiver type as the implicit input parameter. For example, the `length` method defined on the `java/lang::String` class is represented in Tego as:

```tego
extern module java/lang
  extern def String#length: String -> Int
```

Any additional parameters are parameters to the strategy. For example, the `replace` method defined on `java/lang::String` is declared as:

```tego
extern module java/lang
  extern def String#replace(Char, Char): String -> String
```

??? tip "Implementation"
    Internally, the invocation of non-private instance methods on a class are defined using a call to the `__invokeVirtual` special function, which will be inlined and produce the correct JVM instructions for calling a non-private class instance method.

    ```tego
    extern inline fun __invokeVirtual<R>(
        clsName: String,
        memberName: String,
        signature: String,
        instance: Any,
        vararg args: Any
    ) : T

    // Usage:
    __invokeVirtual<Int>("java/lang/String", "length", "()I", myString)
    ```

    Whereas the JVM can easily locate the correct entry for a method in a class declaration, it cannot do this easily for an interface because a class can implement one or more interfaces in any order. Therefore, when calling an interface method, it is defined using a call to the `__invokeInterface` special function, which will also be inlined and produce the correct JVM instructions for calling an interface method.

    ```tego
    extern inline fun __invokeInterface<R>(
        clsName: String,
        memberName: String,
        signature: String,
        instance: Any,
        vararg args: Any
    ) : T

    // Usage:
    __invokeInterface<Int>("java/util/List", "size", "()I", myList)
    ```

    To optimize calls to instance methods that cannot be overridden (e.g., private methods, super class methods, constructors), these calls are defined using the `__invokeFinal` special function, which will also be inlined and produce the correct JVM instructions for calling a specific instance method or constructor.

    ```tego
    extern inline fun __invokeFinal<R>(
        clsName: String,
        memberName: String,
        signature: String,
        instance: Any,
        vararg args: Any
    ) : T
    ```


## Static Methods
Java static methods are implicitly declared as public external functions. For example, the `java/util::Collections#emptyList` static method is declared as:

```tego
extern module java/util
  extern fun Collection#emptyList<T>: List<T>
```

Again, like with instance methods, any additional parameters are parameters to the function.

??? tip "Implementation"
    Internally, the invocation of static methods is defined using a call to the `__invokeStatic` special function, which will be inlined and produce the correct JVM instructions for calling a static method.

    ```tego
    extern inline fun __invokeStatic<R>(
        clsName: String,
        memberName: String,
        signature: String,
        vararg args: Any
    ) : T

    // Usage:
    __invokeStatic<List<T>>("java/util/Collection", "emptyList", "()List")
    ```



## Constructors
A Java constructor is implicitly declared as a public external function. For example, the constructors of `HashMap` are declared as:

```tego
extern module java/util
  extern fun HashMap<K, V>(): HashMap<K, V>
  extern fun HashMap<K, V>(Int): HashMap<K, V>
  extern fun HashMap<K, V>(Int, Float): HashMap<K, V>
  extern fun HashMap<K, V>(Map<K, V>): HashMap<K, V>
```

??? tip "Implementation"
    Internally, the constructor functions are defined using a call to the `__invokeConstructor` special function, which will be inlined and produce the correct JVM instructions for calling a constructor.

    ```tego
    extern inline fun __invokeConstructor<R>(
        clsName: String,
        memberName: String,
        signature: String,
        vararg args: Any
    ) : R

    // Usage:
    __invokeConstructor<HashMap<K, V>>("java/util/HashMap", "<init>", "()V")
    ```



## Return Values
All functions and strategies return a value, even if they are declared as returning `void` in Java. Instead, in Tego they return the only instance of type `Unit`.


## Null
Any value of type `T` which can also be `null` has the type `Option<T>` in Tego. While the `Nothing` type is the bottom of the type hierarchy, there is also an `Option<Nothing>` whose only possible value is `null`. Similarly, while `Unit` has only one value `Unit()`, there is also an `Option<Unit>` whose possible values are `Unit` and `null`. The syntactic sugar for any `Option<T>` type is `T?`.


## Casts
To cast a value to a type `T`, use the `is<T>` strategy, which will fail when the value cannot be cast:

```tego
extern def is<T>(): Any -> T?

// Usage:
!"abc" ; is<String>     // success
!10 ; is<String>        // fail
fail ; is<String?>      // success
fail ; is<String>       // fail
```


## Java Types to Tego Types
Not all Java types have the same name in Tego.

These are the primitive types:

| Tego Type          | Java Types                             |
| ------------------ | -------------------------------------- |
| `Char`             | `char` and `java/lang::Character`      |
| `Byte`             | `byte` and `java/lang::Byte`           |
| `Short`            | `short` and `java/lang::Short`         |
| `Int`              | `int` and `java/lang::Integer`         |
| `Long`             | `long` and `java/lang::Long`           |
| `Float`            | `float` and `java/lang::Float`         |
| `Double`           | `double` and `java/lang::Double`       |
| `Bool`             | `boolean` and `java/lang::Boolean`     |
| `Unit`             | `void`                                 |

Non-primitive types:

| Tego Type          | Java Types                             |
| ------------------ | -------------------------------------- |
| `Any`              | `object` and `java/lang::Object`       |
| `Array<T>`         | `T[]` and all primitive array types    |

Special types:

| Tego Type          | Description                            |
| ------------------ | -------------------------------------- |
| `Any`              | Top type.                              |
| `Unit`             | Unit type.                             |
| `Nothing`          | Bottom type.                           |
| `[T]`              | `mb/statix/sequences::Seq`             |
| `T?`               | `T` or `null`                          |



## Calling Java code
Generally, you want to call Java code through their generated extern functions and strategies. However, when this is not possible for some reason, Tego provides special expressions that can perform these calls. Note that you should not use these, they are used by the internal implementations of the Java interop.

### Constructor
To call a class constructor:

```tego
extern inline fun __invokeConstructor<T>(clsName: String, memberName: String, signature: String) : T

// Usage:
val hm = __invokeConstructor<HashMap<K, V>>("java/util/HashMap", "<init>", "()V")
```

### Static Method
TBD

### Non-Private Instance Method
TBD

### Private Instance Method
TBD

### Super Method
TBD

### Interface Method
TBD

### Dynamic Method
TBD

### Field Getter
TBD

### Field Setter
TBD
# Java Interop

## Static Functions and Variables
Static Java members like these:

```java
package java.util;
class Collections {
    public static <T> List<T> emptyList();
    public static <T> Set<T> emptySet();
}

class Arrays {
    public static <T> List<T> asList(T... a);
}

package java.lang;
class Integer {
    public static final int MIN_VALUE = 0x80000000;
    public static final int MAX_VALUE = 0x7fffffff;
            
    public static int parseInt(String s);
    public static int parseInt(String s, int radix);

    public static String toString(Integer i);
    public static String toString(Integer i, int radix);
}
```

Are declared in Tego as:

```
module java.util
extern fun Collections.emptyList<T>(): Unit -> List<T>
extern fun Collections.emptySet<T>(): Unit -> Set<T>

extern fun Arrays.asList<T>(a: Array<T>): Unit -> List<T>

module java.lang
extern const Integer.MIN_VALUE: Int
extern const Integer.MAX_VALUE: Int

extern fun Integer.parseInt(s: String): Unit -> Int 
extern fun Integer.parseInt(s: String, radix: Int): Unit -> Int

extern fun Integer.toString(i: Int): Unit -> String 
extern fun Integer.toString(i: Int, radix: Int): Unit -> String  
```

!!! note ""
    Note the difference between the name `Integer.toString` and the receiver type (for static members this is always `Unit`).

And have the types:

```
<T> Unit -> List<T>         // emptyList
<T> Unit -> Set<T>          // emptySet

<T> (Array<T>) Unit -> List<T> // asList

Int                         // MIN_VALUE
Int                         // MAX_VALUE

(String) Unit -> Int        // parseInt
(String, Int) Unit -> Int   // parseInt

(Int) Unit -> String        // toString
(Int, Int) Unit -> String   // toString
```

!!! note "Static functions with no arguments"
    Since there is no type to represent a function with no receiver (i.e., static), its implicit input is of type `Unit`.


## Instance Functions and Variables
Instance Java members like these:

```java
package java.lang;
class Object {
    public int hashCode();
    public boolean equals(Object obj);
    public String toString();
}

class Pair<T1, T2> {
    public T1 c1;
    public T2 c2;
}
```

Are declared in Tego as:

```
module java.lang
extern class Object

extern fun Object.hashCode(): Object -> Int
extern fun Object.equals(obj: Object): Object -> Bool
extern fun Object.toString(): Object -> String

extern class Pair<T1, T2>
extern fun Pair<T1, T2>.c1: Pair<T1, T2> -> T1                  // getter
extern fun Pair<T1, T2>.c1(value: T1): Pair<T1, T2> -> T1       // setter
extern fun Pair<T1, T2>.c2: Pair<T1, T2> -> T2                  // getter
extern fun Pair<T1, T2>.c2(value: T2): Pair<T1, T2> -> T2       // getter
```

And have the types:

```
Class<Object>               // class Object
Object -> Int               // hashCode
(Object) Object -> Bool     // equals
Object -> String            // toString

Class<Pair>                 // class Pair<T1, T2>
Pair<T1, T2> -> T1          // c1 (getter)
(T1) Pair<T1, T2> -> T1     // c1 (setter)
Pair<T1, T2> -> T2          // c2 (getter)
(T2) Pair<T1, T2> -> T2     // c2 (setter)
```

## Build Operator
A value can be turned into a strategy using the build-operator `!`. Similarly, the build operator can turn a strategy that takes `Unit` into a value.

```
internal fun build<T>(v: T): Any -> T
```

Any strategy can be applied to its input value using the angled brackets operator:

```
<s> v           // s: A -> B; v: A, : Any -> B
```

Which is syntactic sugar for:

```
!v; s           // s: A -> B; v: A, !v : Any -> A, : Any -> B
```


A static function with no arguments (or none applied) can be called using the `build` operator `!`.

```
// With explicit number of arguments
fun emptyList(): List<Int>
  = !Collections.emptyList()
  
// Function
fun emptySet(): Any -> Set<Int>
  = !Collections.emptySet
```


## Static Functions with One Argument
The static function must implicitly take the implicit argument as the first (and only) argument.

```
fun dec2int(s: String): Int
  = !s; Integer.parseInt

fun int2dec(i: Int): String
  = !i; Integer.toString
```

!!! note "Overloading"
    When multiple functions with the same signature exist, from all functions with a compatible signature, the shortest one is selected. To specify explicitly how many arguments the function has, you can use the special `..` argument. For example:

    ```
    fun dec2int(s: String): Int
      = !s; Integer.parseInt()       // Explicitly the overload with no other parameters
    
    fun 2int(s: String): Int -> Int
      = !s; Integer.parseInt(..)     // Partial application
      // ...providing the first argument,
      //selecting the overload with two arguments
    ```

## Static Functions with Multiple Arguments
The static function must implicitly take the implicit argument as the first argument. The other arguments can be provided through application.

```
fun hex2int(s: String): Int
 = !s; Integer.parseInt(16)
 
fun int2hex(i: Int): String
 = !s; Integer.toString(16)
```

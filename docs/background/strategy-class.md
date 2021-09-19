# Strategy Class
Every strategy is represented by a class of type `StrategyN`, where `N` is the number of explicit parameters to the strategy. There are several ways to call a strategy, from most flexible but least performant to most performant but least flexible:

1.  Call the generic evaluation method (any number of parameters) on the `ITegoEngine`.
2.  Call the specific evaluation method (specific number of parameters) on the `ITegoEngine`.
3.  Call the generic evaluation method (any number of parameters) on the strategy itself.
4.  Call the specific evaluation method (specific number of parameters) on the strategy itself.
5.  Call the static evaluation function on the strategy class.

Using the `ITegoEngine` allows for debugging, printing entering and exiting strategies, etc, whereas skipping the engine call is more performant but less flexible. Calling the generic evaluation method has to execute some extra code to figure out the specific evaluation method to call, whereas the specific evaluation method is more direct. However, both are still virtual methods, and since a strategy cannot have state, it can implement its evaluation as a static method. The advantage is that a call to a static method is non-virtual and also removes a receiver null-check from the JVM. Another advantage is that the static method can be used directly as a method reference in place of a strategy.

The `StrategyN<A1, .., An, T, R>` interface defines the following function to be implemented:

```kotlin
fun eval(engine: TegoEngine, arg1: A1, .., argN: An, input: T): R
```

Additionally, it has default implementations for some meta-data methods:

```kotlin
interface StrategyN<A1, .., An, T, R> {
    // Arity-specific methods
    fun apply(arg1: A1, .., argN-1: An-1): StrategyN-1

    // Arity-agnostic methods
    fun eval(engine: TegoEngine, args: Array<Any>, input: Any): Any
    fun apply(args: Array<Any>): Strategy

    // Debug printing
    val name: String
    val arity: Int = N
    val isAnonymous: Boolean
    val precedence: Int
    val isAtom: Boolean
    fun getParamName(index: Int): String
    fun writeTo(sb: StringBuilder): StringBuilder
    fun writeArg(sb: StringBuilder, index: Int, arg: Any)
}
```

Since there can only be one instance of a strategy at any one time, the constructor is private and we generate a `getInstance()` method, like this:

```java
    @SuppressWarnings({"rawtypes", "RedundantSuppression"})
    private static final GlcStrategy instance = new GlcStrategy();
    @SuppressWarnings({"unchecked", "unused", "RedundantCast", "RedundantSuppression"})
    public static <CTX, T, U, R> GlcStrategy<CTX, T, U, R> getInstance() { return (GlcStrategy<CTX, T, U, R>)instance; }

    private GlcStrategy() { /* Prevent instantiation. Use getInstance(). */ }
```

Finally, since strategies are stateless, a strategy implementation has a static method to which all other `eval` methods are delegated.

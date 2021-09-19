# Declarations
In a [module](modules.md) you can declare and define members. All definitions require at least a declaration, but not all declarations need to be defined.

!!! note "Declarations vs Definitions"
    A _declaration_ specifies what the member is, whereas a _definition_ specifies how the member works. The compiler needs only the declarations to figure out whether references are valid and what types, parameters, and attributes they have. To execute the compiled code, all declarations require a definition, which can be provided either in Tego or in Java.

## Values
A value is declared using the `val` keyword, followed by the name of the value. You can specify the type, but if you define a value then the type is optional and can be inferred. For example:

```tego
val a = 10
val b: Int = 20
val c: Int
```

!!! note "Either a type or a value is required"
    It is not allowed to declare a `val` without a type and without a value, as the compiler will not be able to infer the type.

## Strategies
Strategies are declared using the `def` keyword. See the page on [Strategies](strategies.md) for more information.

!!! note "Rule Syntax vs Strategy Syntax"
    Whether you _define_ a strategy using rule syntax or strategy syntax does not impact how the strategy is _declared_ using the `def` keyword.



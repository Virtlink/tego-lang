# Implicit Conversions
There is an implicit conversion from `Nullable<T>` to `Seq<T>` called `ntl` (nullable-to-list). When applying a strategy `s: A -> B?` where a strategy `A -> [B]` is expected, the application is wrapped in a `ntl` strategy that returns a singleton sequence of the result of the wrapped strategy if it is not `null`, and an empty sequence otherwise.

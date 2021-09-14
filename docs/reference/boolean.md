# Boolean Operators

## Not
The `not` strategy takes a strategy `s`. When this strategy is applied
to the receiver and it succeeds, this strategy fails.
Otherwise, when the strategy fails, this strategy returns the receiver.

```tego
def not<T, R>(s: T -> R?): T -> T?
= s < fail + id
```

## And
```tego
def and<T, U, V>(s1: T -> U?, s2: T -> V?): T -> (U, V)?
= !(<s1>, <s2>)
```

## Or
```tego
def or<T, R>(s1: T -> R?, s2: T -> R?): T -> R?
= s1 < id + s2
```


## Logical Operators
```tego
def and: (Bool, Bool) -> Bool
def or: (Bool, Bool) -> Bool
def xor: (Bool, Bool) -> Bool

val true: Bool
val false: Bool
```

## Equality and Comparity
Equality is compared using the match operator. For comparisons,
there are built-in strategies `lt`, `le`, `gt`, `ge`, `eq`, `neq`.
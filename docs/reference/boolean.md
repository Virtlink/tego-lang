# Boolean Operators

## Not
The `not` strategy takes a strategy `s`. When this strategy is applied
to the receiver and it succeeds, this strategy fails.
Otherwise, when the strategy fails, this strategy returns the receiver.

```tego
def not<T>(s: T -> Any?): T -> T?
```

## Equality and Comparity
Equality is compared using the match operator. For comparisons,
there are built-in strategies `lt`, `le`, `gt`, `ge`, `eq`, `neq`.
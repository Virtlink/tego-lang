# Expand All Predicates

This is the Tego code for the `expandAllPredicates` strategy:

```tego
def expandAllPredicates(v: ITermVar): SolverState -> [SolverState] =
    SolverState#withExpanded(!Set.Immutable#of) |>
    repeat(
      limit(1, select(CUser::class,
        \(constraint: IConstraint) state: SolverState -> <containsVar(v, constraint); checkNotYetExpanded(constraint)> state\
      )) |>
      expandPredicate(v) |>
      assertValid(v)
    )
```


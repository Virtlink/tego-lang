# Implementing Generics in Statix
The Tego language supports generics (parametric polymorphism), where a type is parameterized by other types that can be instantiated at a later point by substition. For example, the `single` strategy in Tego takes two type parameters `T` and `R`, and is defined as:

```tego
def single<T, R>(T -> [R]): T -> [R]
```

This strategy can be used in different contexts. For example, the following strategy uses `single` where `T` is `String` and `R` is `Int`:

```tego
<single<String, Int>(length; ![<id>])> "some string"
```

Ideally, the type parameters should be able to be inferred in most situations. You would want to write:

```tego
<single(length; ![<id>])> "some string"
```

## Explicit Substitution
Naively, an eager substitution could could create a copy of the scope graph in which all type variables have been replaced by their instantiations. However, here we follow the approach of [van Antwerpen et. al.][1] to create scopes with _explicit substitutions_ that are lazily applied during name resolution.

We will define the `FORALL(typeParams, s)` type constructor that models a type (represented by scope `s`) which may contain occurrences of type parameters (represented by a list of occurrences `typeParams`). The types itself can contain a reference to a type parameter modeled by `TVAR(typeParam)`.

```statix
signature
  sorts TYPE
  constructors
    FORALL : list(occurrence) * scope -> TYPE
    TVAR   : occurrence -> TYPE
```

How can a scope represent a type? We give the scope a `body` relation to the scope that 


## See Also
- van Antwerpen et. al. [Scopes as Types][1] section 2.5, OOPSLA'18
- [System F in Statix](https://github.com/metaborg/nabl/tree/master/statix.integrationtest/lang.sysf)
- [Featherweight Generic Java (FGJ) in Statix](https://github.com/metaborg/nabl/tree/master/statix.integrationtest/lang.fgj)

[1]: https://dl.acm.org/doi/10.1145/3276484
# Parameterized Types
For Tego we need parameterized types, as there are many strategies that are type agnostic and it would be cumbersome to instantiate each strategy for each type.

## Scopes
Every declaration has a declaration scope `s_decl` in which all aspects of the declaration are stored, including the type parameters. An instantiation of a declaration has a scope `s_inst`, in which all aspects of the instantiation are stored.

## Type Parameters
A type parameter is a name with an upper bound and a lower bound.

## Type Arguments
Whenever a type is instantiated, we search the scope hierarchy for type parameters corresponding to type arguments

## Declaring a Generic Type
A generic type can be declared through, say, a generic strategy declaration. Without generics a strategy is declared like this:

```tego
def myNormalStrategy: String -> Int     // : STRATEGY([], STRING(), INT())
```

With generics, it is instead declared like this:

```tego
def myStrategy<T, R>: T -> R      // : FORALL(T, s_all_t)

                                  // s_all_t =P=> s
                                  // s_all_t -var-> T := TVAR(T)
                                  // s_all_t -body-> FORALL(R, s_all_r)

                                  // s_all_r =P=> s_all_t
                                  // s_all_r -var-> R := TVAR(R)
                                  // s_all_r -body-> STRATEGY([], TVAR(T), TVAR(R))
```

Where `s_all_t` and `s_all_r` are the scopes in which the types are existentially quantified over `T` and `R`, respectively.

## Instantiating a Generic Type
Without generics, instantiating a type has the expected semantic type:

```tego
myNormalStrategy                  // : STRATEGY([], STRING(), INT())
```

When we have a generic type `myStrategy<T, R>`, we can do three things:

1.  Give no type arguments. The type is `FORALL(T, s_all_t)`.
2.  Give some type argument. The type is `PROJB(s_inst_t)`.
3.  Give all type arguments. The type is `PROJB(s_inst_t)`.

If we give no type arguments, nothing changes. If we give one type argument `T`, we get:

```tego
myStrategy<String, ..>            // : PROJB(s_inst_t)
                                  // s_inst_t -subst-> T := STRING()
                                  // s_inst_t =L=> s_all_t
```

If we give all type arguments, we get:

```tego
myStrategy<String, Int>           // : PROJB(s_inst_t)
                                  // s_inst_t -subst-> T := STRING()
                                  // s_inst_t =L=> s_all_t

                                  // s_inst_r -subst-> R := INT()
                                  // s_inst_r =L=> ?
```

Its semantic type of `myStrategy` is `FORALL([T, R], s_all)`, and the instantiated expression has type `PROJB(s_inst)`, where `s_inst` is a fresh instantiation scope for the type (related by a `P` edge to the `s_all` scope). It has `subst` edges for each of the substitutions.

The `PROJB` type indicates that we don't resolve the type at this point (by applying the substitution), but instead delay it until it is needed.

## Using/Comparing Generic Type Instances
When we use (or compare) a generic instantiation of a type, we have to figure out the actual type. We do this by making the type _strict_ (i.e., applying all the substitutions). Simply put, if the type is not a `PROJB` type, then we just return the type. However, if it is a `PROJB(s_inst)` type, we resolve all possible type bodies reachable from `s_inst` (which has a relation with `s_all` which has the type body, remember), and then normalize them.

Normalization involves looking at the path from the instantiation scope to the nearest type `body`. The path consists of a series of instantiation scopes `s_inst -> s_inst3 -> s_inst2 -> s_inst1 -> s_all`. Note that instantiation scopes that where created earlier are closer to `s_all`. Therefore, we go over these scopes in reverse order, from close to `s_all` toward the last `s_inst`. We normalize the resolved `body` type with respect to each of these instantiation scopes.

Some basic normalization rules are that if there are no more path elements, or when the type is simple, then we just return the type. If it's a complex type (e.g. a function/strategy type), we recursively normalize its arguments. However, if the type to be normalized is a type 


Of course, it can happen that a type instantiation uses generic type variabeles that have not been instantiated. For example:

```tego
def myOtherStrategy<T, R>: T -> R     // : FORALL([T, R], s_all)
= myStrategy<T, R>                    // : PROJB(s_inst)
```


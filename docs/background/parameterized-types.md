# Parameterized Types
For Tego we need parameterized types, as there are many strategies that are type agnostic and it would be cumbersome to instantiate each strategy for each type.


## Scopes
Every declaration has a declaration scope `s_decl` in which all aspects of the declaration are stored, including the type parameters. An instantiation of a declaration has a scope `s_inst`, in which all aspects of the instantiation are stored.


## Type Parameters
A type parameter is a name with an upper bound and a lower bound. The semantic type and relation are:

```statix
signature
  sorts
    TYPE_PARAM
  constructors
    /**
    * A semantic type parameter.
    *
    * @param scope the scope in which the parameter is declared
    * @param string the name of the parameter
    * @param upper_bound the upper bound type of the type parameter; or `ANY()` (top)
    * @param lower_bound the lower bound type of the type parameter; or `NOTHING()` (bottom)
    */
    TYPE_PARAM : scope * string * TYPE * TYPE -> TYPE_PARAM
  relations
    type_param : TYPE_PARAM
```

The `type_param` relation is from a declaration scope `s_decl`.


## Type Arguments
Whenever a type is instantiated, we search the scope hierarchy for type parameters corresponding to type arguments.  This is done through the `typeOf()` rule, which t
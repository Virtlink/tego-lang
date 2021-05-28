# Declarations and Fully-Qualified Names
To be able to generate Java code, we need to find the fully-qualified names of references, and the declarations they resolve to. These declarations expose information that is used to generate method calls and signatures in Java, including:

- fully-qualified name (including enclosing class and package)
- parameter types (for functions and strategies)
- input type (for strategies)
- return type (for functions and strategies)
- base class (for classes, functions, and strategies)
- base interfaces (for classes, functions, and strategies)
- type parameters (for generic classes, functions, and strategies)

The Tego project uses Statix to resolve references to their declarations, but doesn't provide a way to neatly expose these declarations in the AST. However, using the `@exp.ref := decl` attribute, we can explicitly link a reference to its declaration. And since both the reference and the declaration get _term indices_ (an index that uniquely identifies the term in the AST), we know the term index of the declaration a reference points to. Finally, since often the reference points to the _name_ of the declaration, we need to find the term indices that are all related to the same declaration and use this to find the declaration for a reference.

In the Java backend, we expect an AST of the program annotated with the following information:

- types
- type references
- references
- term indices

## Types and Type References
Type information is provided in Statix using the `@e.type := T` attribute. Any time we have an expression, definition, or declaration for which we determine its type `T`, we associate it with its `@e.type` attribute. When we prepare the AST for use by the Java backend, we use our Stratego strategy `generate/get-type` to get the value of the `@.type` attribute and apply it using an `OfType` annotation. Any term indices on the type are _kept_, so we can associate a type reference with its declaration.

## References
While types and type references are handled as explained previously, this does not handle normal (non-type) references to declarations. For these, we use the Statix `@e.ref := decl` attribute. For any reference to a declaration, we associate it with its `@e.ref` attribute. Similarly to `generate/get-type`, we use our Strategy strategy `generate/get-ref` to get the value of the `@.ref` attribute and apply it using an `OfRef` annotation. Again, term indices are _kept_ so we can associate a reference with its declaration.

## Term Indices
As references can point to any part of the declaration (usually the name of the declaration), we need to associate a list of term indices with each declaration. We do this in the Java backend when we read the AST. For example, the following strategy declaration:

```tego
extern def assertValid(ITermVar): SolverState -> [SolverState]
```

Has the following AST, including type annotations and term indices:

```aterm
StrategyDecl(
  [ExternDecl(){TermIndex("completion-min.tego", 501)}]{TermIndex("completion-min.tego", 504)}
, "assertValid"{TermIndex("completion-min.tego", 505)}
, []{TermIndex("completion-min.tego", 507)}
, [TypeRef("ITermVar"{OfType("ITermVar"{TermIndex("completion-min.tego", 717)}), TermIndex("completion-min.tego", 508)}){TermIndex("completion-min.tego", 509)}]{TermIndex("completion-min.tego", 512)}
, TypeRef("SolverState"{OfType("SolverState"{TermIndex("completion-min.tego", 679)}), TermIndex("completion-min.tego", 513)}){TermIndex("completion-min.tego", 514)}
, ListType(TypeRef("SolverState"{OfType("SolverState"{TermIndex("completion-min.tego", 679)}), TermIndex("completion-min.tego", 515)}){TermIndex("completion-min.tego", 516)}){TermIndex("completion-min.tego", 517)}
){ OfType(
     STRATEGY(
       [CLASS("ITermVar"{TermIndex("completion-min.tego", 717)}){OfSort(SORT("TYPE"))}]
     , CLASS("SolverState"{TermIndex("completion-min.tego", 679)}){OfSort(SORT("TYPE"))}
     , LIST(CLASS("SolverState"{TermIndex("completion-min.tego", 679)}){OfSort(SORT("TYPE"))}){OfSort(SORT("TYPE"))}
     ){OfSort(SORT("TYPE"))}
   )
 , TermIndex("completion-min.tego", 518)
}
```

From this we can extract a cleaned-up version of the AST (without singleton term index annotations applied to every term) and a list of term indices that belong to this declaration.

```aterm
StrategyDecl(
  [ExternDecl()]
, "assertValid"
, []
, [TypeRef("ITermVar"{OfType("ITermVar"{TermIndex("completion-min.tego", 717)})})]
, TypeRef("SolverState"{OfType("SolverState"{TermIndex("completion-min.tego", 679)}))
, ListType(TypeRef("SolverState"{OfType("SolverState"{TermIndex("completion-min.tego", 679)})}))
){ OfType(
     STRATEGY(
       [CLASS("ITermVar"{TermIndex("completion-min.tego", 717)})]
     , CLASS("SolverState"{TermIndex("completion-min.tego", 679)})
     , LIST(CLASS("SolverState"{TermIndex("completion-min.tego", 679)}))
     )
   )
  , TermIndices([
    TermIndex("completion-min.tego", 501),
    TermIndex("completion-min.tego", 504),
    TermIndex("completion-min.tego", 505),
    TermIndex("completion-min.tego", 507),
    TermIndex("completion-min.tego", 508),
    TermIndex("completion-min.tego", 509),
    TermIndex("completion-min.tego", 512),
    TermIndex("completion-min.tego", 513),
    TermIndex("completion-min.tego", 514),
    TermIndex("completion-min.tego", 515),
    TermIndex("completion-min.tego", 516),
    TermIndex("completion-min.tego", 517),
    TermIndex("completion-min.tego", 518),
  ])
}
```

!!! note ""
    If the declaration has a body (i.e., is a definition), then we don't associate the term indices of the body with the declaration, as no references pointing into the body would be expected to resolve to the declaration.

    Similarly, we don't associate the term indices of types (or anything that occurs in an annnotation) with the declaration.

From this result, it's clear to see that all references of `SolverState` reference the same declaration with term index `TermIndex("completion-min.tego", 679)`, and that any reference to any of the term indices in the list are referencing the same declaration of `assertValid`.

Basically, this allows us to identify declarations with their term index only.
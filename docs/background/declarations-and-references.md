# Declarations and References
To be able to generate Java code, we need to find the fully-qualified names of references, and the declarations they resolve to. These declarations expose information that is used to generate method calls and signatures in Java, including:

- fully-qualified name (including enclosing class and package)
- parameter types (for functions and strategies)
- input type (for strategies)
- return type (for functions and strategies)
- base class (for classes, functions, and strategies)
- base interfaces (for classes, functions, and strategies)
- type parameters (for generic classes, functions, and strategies)

The Tego project uses Statix to resolve references to their declarations, but doesn't provide a way to neatly expose these declarations in the AST. However, using the `@t.ref := name` and `@t.decl := name` attributes, we can explicitly link a reference to its declaration. And since both the reference and the declaration get _term indices_ (an index that uniquely identifies the term in the AST), we know the term index of the declaration a reference points to.

In the Java backend, we expect an AST of the program annotated with the following information:

- types
- type references
- references
- term indices

## Types and Type References
Type information is provided in Statix using the `@e.type := T` attribute. Any time we have an expression, definition, or declaration for which we determine its type `T`, we associate it with its `@e.type` attribute. When we prepare the AST for use by the Java backend, we use our Stratego strategy `generate/get-type` to get the value of the `@e.type` attribute and apply it using an `OfType` annotation. Any term indices on the type are _kept_, so we can associate a type reference with its declaration.

## References
While types and type references are handled as explained previously, this does not handle normal (non-type) references to declarations. For these, we use the Statix `@t.ref := name` attribute. For any reference to a declaration, we associate it with its `@t.ref` attribute. Similarly to `generate/get-type`, we use our Strategy strategy `generate/get-ref` to get the value of the `@t.ref` attribute and apply it using an `OfRef` annotation. Again, term indices are _kept_ so we can associate a reference with its declaration.

## Declarations
Similarly, we get the value of the `@t.decl` attribute and apply it using an `OfDecl` annotation. Both the reference `@t.ref` and the declaration `@t.decl` must point to the same entity, usually the name of the declaration.

## Term Indices
While we cannot uniquely associate a name to a reference or declaration, we _can_ use Statix resolution mechanism in combination with term indices. Each term index uniquely identifies a term in the AST.

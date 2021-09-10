# Semantic Specification
The semantic specification of Tego is written in Statix.

## Naming Rules
The rules follow this naming convention:

- `declareX(s: scope, name: ID, ty: TYPE)` — Declares an `X` with name `name` and type `ty` in scope `s`.
- `resolveX(s: scope, name: ID) -> list((path * ?))` — Resolves an `X` with name `name` from scope `s` and returns a list of occurrences that where found.
- `typeOfX(s: scope, name: ID) -> TYPE` — Resolves an `X` with name `name` from scope `s` and returns the type of the single occurrence.

## Naming Variables
Variables can have a prefix followed by an underscore and a camelCase name. The prefixes can also be used stand-alone if there is no need to specify more:

- `s` — A scope (of type `scope`).
- `ty` — A semantic type (of type `TYPE`).
- `tp` — A semantic type parameter (of type `TYPE_PARAM`).
- `ta` — A semantic type argument (of type `TYPE_PARAM * TYPE`).
- `name` — A name (of type `string`).
- `param` — A syntactic parameter.

Variables that are lists end with `s`, such as `tys`.

Occurrences have `occ` in them.
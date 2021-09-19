# Intermediate Representation

The IR for Tego is:

- complete
- sound
- flexible
- robust
- (portable)

It must be easy to transform the IR into byte code. The IR stores the following metadata:

- types
- data flow (ANF)

## Let (Exp)
The `let` instruction defines a name for an expression in a scope.

```
let <name: ID> = <value: Exp> in
  <body: Exp>
```

## If (Exp)
The `if` instruction takes one of two branches depending on a condition.

```
if <condition: Exp> then
  <onTrue: Exp>
else
  <onFalse: Exp>
```

## Var (Exp)
A variable is just named.

```
<name: ID>
```


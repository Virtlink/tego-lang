## Do-notation
Given a definition of monadic bind (and for lists specifically, `flatMap`):

- `bind<M : Monad, A, B>(s: A -> M<B>): M<A> -> M<B>` — Reverse monadic `=<<` bind.
- `flatMap<T, R>(T -> [R]): [T] -> [R]` — Reverse monadic `=<<` bind on a lazy sequence.
- `x<T, R>(T -> R?): T? -> R?` — Reverse monadic `=<<` bind on an Option type.

We can use monadic bind like this:

```tego
<flatMap(\x -> <repeat(x)> x\)> [1, 2, 3]
// [1, 2, 2, 3, 3, 3]
```

Or this:

```tego
<flatMap(\x -> [<inc> x]\)> [1, 2, 3]
// [2, 3, 4]
```

But when we combine them, it gets ugly real fast (especially since we have the
argument on the right-hand side of the strategy application):

```tego
  <flatMap(\x ->
    <flatMap(\y -> <repeat(y)> y\)>
  [<inc> x]\)>
[1, 2, 3]
// [2, 2, 3, 3, 3, 4, 4, 4, 4]
```

In Tego, you can use do-notation like this:

```tego
do
    x <- [1, 2, 3]
    y <- [<inc> x]
    <repeat(y)> y
// [2, 2, 3, 3, 3, 4, 4, 4, 4]
```

It is desugared to the above.

!!! warning "This syntax is not final"
    There is also the issue of the type of the notation. For example, if you
    elide the explicit, do you get the implicit argument? What is the type?
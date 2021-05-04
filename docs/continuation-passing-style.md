# Continuation Passing Style (CPS) and Administrative Normal Form (ANF) 
Continuation passing style (CPS) explicitly encodes the control flow of the program. Instead of returning a value, throwing an exception, or jumping to labels in a loop, a program in CPS calls a _continuation_ function that handles the value or exception. However, writing CPS programs by hand is unintuitive and error-prone, and it considerably increases the size of programs. But CPS can be very useful as the intermediate representation of a program in a compiler. Flanagan et. al. (1993) recognize that typically compilers do a transformation to CPS, followed by a simplification, followed by a transformation to un-CPS, and that these steps can be performed at once using Administrative Normal Form (A-Normal Form, or ANF).

## Continuations
A function in Continuation Passing Style (CPS) never returns its result, but always uses a continuation to communicate the result. The function has an extra continuation parameter, which is a callback function that takes a single argument of the type that the function used to return. Most continuation calls would be tail calls (i.e., the last call in the function, as they replace the function's `return`), and can be optimized in various ways (such as not allocating a new stack frame for the continuation call, or rewriting a tail recursive function into a loop).

While in _direct style_ the caller decides what happens after the called function terminates, in CPS the callee decides this. Note that since the callee calls the continuation to continue but never returns, the call stack only grows.

A function in CPS can take more than one continuation. For example, one continuation for normal control flow (i.e., `return`), another for exceptional control flow (i.e., `throw`).

While in general continuation could be invoked multiple times (_multi-shot_), often it is fine to have continuations that can only be invoked once (_single-shot_ or _one-shot_).

_Call/cc_ (call-with-current-continuation) is a control operator that captures the rest of the program in a continuation. Calling the captured continuation can't return values, because it can only resume the rest of the program until it completes. This is known as an _undelimited continuation_.

An example of a undelimited continuation is in this `product` function, which calls the continuation `k` when it encounters a `0` to break out early. `callCC` captures the current stack, and a call to `k` resumes as if the given value was provided instead of the `callCC`. 

```
public decl product : [Int] -> Int
  product l -> <callCC> \k -> loop(k) l\

decl loop(Int -> Nothing) : [Int] -> Int
  loop(_)     [] -> 1
  loop(k)  [0|_] -> <k> 0
  loop(k) [x|xs] -> xsn := <loop(k)> xs; <mul> (x, xsn)
```

### See Also

- Marijn Haverbeke — [Continuation-Passing Style](https://marijnhaverbeke.nl/cps/) and why JavaScript developers might be interested in it
- Yassine Elouafi — Algebraic Effects in JavaScript
  - [Continuations and Control Transfer](https://gist.github.com/yelouafi/57825fdd223e5337ba0cd2b6ed757f53)
  - [Capturing Continuations with Generators](https://gist.github.com/yelouafi/bbc559aef92f00d9682b8d0531a36503)
  - [Delimited Continuations](https://gist.github.com/yelouafi/7261da07c97c5e6322da3894f6ea60e2)
  - [Implementing Algebraic Effects and Handlers](https://gist.github.com/yelouafi/5f8550b887ab7ffcf3284602330bd37d)


## ANF

In ANF, the arguments to a function must be _immediate_ values. Immediate values do not need any computation; they are constants or can be looked up in the environment. For example, the expression:

    2 + 2 + let x = 1 in f(x)

Is normalized to ANF as:

    let t1 = 2 + 2 in
    let x = 1 in
    let t2 = f(x) in
    t1 + t2

Bytecode generation is easier, since the simple operations can be encoded in a few bytecode instructions:

    // let t1 = 2 + 2 in
    LOADC 2
    LOADC 2
    ADD
    STORE t1

    // let x = 1 in
    LOADC 1
    STORE x

    // let t2 = f(x) in
    LOAD f
    LOAD x
    CALL
    STORE t2

    // t1 + t2
    LOAD t1
    LOAD t2
    ADD

## Control Flow
While most arguments in ANF are immediate values, control flow is modeled using non-immediate values. For example, the `if`-expression:

    if (c) e1 else e2

Here `c` is an immediate value, but `e1` and `e2` are not. For example:

    x = if (f()) a() else b()

Would be translated in ANF to:

    let c = f() in
    let x = if (c) a() else b()

And _not_ into:

    let c = f() in
    let a' = a() in
    let b' = b() in
    let x = if (c) a' else b'

Because in the latter case `a()` and `b()` would both be evaluated.


## See Also
- Flanagan et. al. (PLDI'93) — [The Essence of Compiling with Continuations](http://users.soe.ucsc.edu/~cormac/papers/pldi93.pdf)
- Kennedy (ICFP'07) — [Compiling with Continuations, Continued](https://dl.acm.org/doi/10.1145/1291220.1291179)
- Maurer (2015) — [Intermediate Languages for Optimization](https://www.cs.uoregon.edu/Reports/AREA-201512-Maurer.pdf)
- Maurer et. al. (2016) — [Administrative Normal Form, Continued](https://ix.cs.uoregon.edu/~pdownen/publications/anf-continued.pdf)
- Maurer et. al. (PLDI'17) — [Compiling without Continuations](https://ix.cs.uoregon.edu/~pdownen/publications/pldi17_appendix.pdf)
- Cong et. al. (ICFP'19) — [Compiling with Continuations, or without? Whatever.](https://www.cs.purdue.edu/homes/rompf/papers/cong-icfp19.pdf)
- Might — [A-Normalization: Why and How](http://matt.might.net/articles/a-normalization/)
- Might — [Writing an interpreter, CESK-style](http://matt.might.net/articles/cesk-machines/)
- (Swarthmore) [ANF](https://www.cs.swarthmore.edu/~jpolitz/cs75/s16/n_anf-tutorial.html)
- [Conditionals and A-Normal Form](https://course.ccs.neu.edu/cs4410sp19/lec_anf_notes.html)
- [Revisiting ANF: Encoding A-Normal Form with Types](https://course.ccs.neu.edu/cs4410sp19/lec_anf-redux_notes.html)
- [Program transformations in Hakaru](https://hakaru-dev.github.io/internals/transforms/)
- [Haskell - STG](https://gitlab.haskell.org/ghc/ghc/-/wikis/commentary/compiler/generated-code)
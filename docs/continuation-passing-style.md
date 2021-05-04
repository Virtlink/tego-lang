# Continuation Passing Style (CPS) and Administrative Normal Form (ANF) 
Continuation passing style (CPS) explicitly encodes the control flow of the program. Instead of returning a value, throwing an exception, or jumping to labels in a loop, a program in CPS calls a _continuation_ function that handles the value or exception. However, writing CPS programs by hand is unintuitive and error-prone, and it considerably increases the size of programs. But CPS can be very useful as the intermediate representation of a program in a compiler. Flanagan et. al. (1993) recognize that typically compilers do a transformation to CPS, followed by a simplification, followed by a transformation to un-CPS, and that these steps can be performed at once using Administrative Normal Form (A-Normal Form, or ANF).

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
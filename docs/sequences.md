# Sequences
A sequence is a computation that lazily produces values.

For example, the following computation prints the first ten even fibonacci numbers:

```java
static void Main(String[] args) {
    // Build the computation
    Iterable<Integer> tenEvenFibonacciNumbers = Take(10, EvenNumbersOnly(Fibonacci()));
    
    // Print the results
    for (Integer e : tenEvenFibonacciNumbers) {
        System.out.println(e);
    }
}

static Iterable<Integer> Fibonacci() { return buildSequence(b -> {
    b.yield(1); // First fibonacci numnber
    int currFib = 1;
    int nextFib = 1;
    while (true) {
        b.yield(nextFib);
        int newFib = currFib + nextFib;
        currFib = nextFib;
        nextFib = newFib;
    }
}); }

static Iterable<Integer> EvenNumbersOnly(Iterable<Integer> sequence) { return buildSequence(b -> {
    for (Integer i : sequence) {
        if ((i % 2) == 0) {
            b.yield(i);
        }
    }
}); }

static <T> Iterable<T> Take(int limit, Iterable<T> sequence) { return buildSequence(b -> {
    int count = 0;
    for (T e : sequence) {
        if (count >= limit) return;
        b.yield(e);
        count += 1;
    }
}); }
```

This pseudo-Java code illustrates how we would like to write a lazy computation as imperative code. The `buildSequence()` takes an imperatively written piece of code and turns it into a lazy computation, returning an `Iterable<T>`. The lambda argument to the `buildSequence()` method takes a computation builder `b`, which has a method `yield()` which will suspend the computation and yield the value. Note that in current Java there is no way to implement `buildSequence()` or `yield()` to make this Java code work as intended. However, we will use this to illustrate how we can transform such code into Java code that _is_ executable, and performs the intended lazy computation.

## State Machine
We can represent each computation using a dedicated state machine. The `Fibonacci` function would be implemented as:

```java
static Iterable<Integer> Fibonacci() {
    return new Iterable<Integer>() {
        @NotNull
        @Override
        public Iterator<Integer> iterator() {
            return new Iterator<Integer>() {
                private Fibonacci_State _state = Fibonacci_State.Start;
                private boolean _hasCurrent = false;
                private Integer _current;

                private int currFib;
                private int nextFib;

                @Override
                public boolean hasNext() {
                    if (_state == Fibonacci_State.Start) {
                        // Compute the first element
                        computeNext();
                    }
                    return _hasCurrent;
                }

                @Override
                public Integer next() {
                    if (!_hasCurrent && _state != Fibonacci_State.Done) {
                        // Compute the next element
                        computeNext();
                    }
                    _hasCurrent = false;
                    return _current;
                }

                private void computeNext() {
                    if (_state == Fibonacci_State.Start) goto start;
                    if (_state == Fibonacci_State.Step2) goto step2;
                    if (_state == Fibonacci_State.Step3) goto step3;
                    throw new IllegalStateException();

                    start:
                    _current = 1;
                    _hasCurrent = true;
                    _state = Fibonacci_State.Step2;
                    return;
                    step2:
                    currFib = 1;
                    nextFib = 1;
                    // Fall through
                    loopstart:
                    _current = nextFib;
                    _hasCurrent = true;
                    _state = Fibonacci_State.Step3;
                    return;
                    step3:
                    int newFib = currFib + nextFib;
                    currFib = nextFib;
                    nextFib = newFib;
                    // Goto loopStart
                    _state = Fibonacci_State.LoopStart;
                    goto loopstart;
                    done:
                    return;
                }
            };
        }
    };
}

enum Fibonacci_State {
    Start,
    Step2,
    Step3,
    Done,
}
```

Note that we modeled the control flow using `goto` statements, which don't exist in Java but do illustrate the transformation to a statemachine more clearly. Also note how the loop, which is a goto statement in byte code, is still present in the code in the form of the `loopstart` label; dealing with loops requires no special machinery.

The `yield(e)` calls have been transformed into the sequence:

```java
_current = e;
_hasCurrent = true;
_state = next_state;
```

Where `next_state` is the next state where the state machine should resume.

### Optimizations
We can optimize this a bit by not creating an `enum` but just using integer values for the states. Also, we can reduce the number of local variables we need to store in the iterator by doing some use-def analysis. If they don't cross block bounaries, they do not need to be in the iterator state.

## ANF
Functional Fibonacci sequence in Tego:

```
decl fibs : [Int]
fibs = [0 | 1 | <zipWith(add)> (fibs, <tail> fibs)]
```

In ANF:

```
yield 0
yield 1
let zwa = zipWith(add) in
let tl = <tail> fibs in
let tup = (fibs, tl) in
let l = <zwa> tup in
yieldAll l
```

!!! note "Evaluation vs Application"
    A strategy `s` evaluated with value `v` as input, is written as `<s> v`.
    A strategy `s` applied to arguments `a1`, `a2`, .., `an` is writen as `s(a1, a2, .., an)`.
    Therefore, to apply and evaluate a strategy in one go, is to write `<s(a1, a2, .., an)> v`.

!!! note "Yielding"
    The `yield` construct yields a value for a lazy sequence.

Note that `yieldAll` is actually just a loop and a yield, so we get:

```
yield 0
yield 1
let zwa = zipWith(add) in
let tl = <tail> fibs in
let tup = (fibs, tl) in
let l = <zwa> tup in
let iter = <Iterable.getIterator> l in
let hn = <Iterator.hasNext> iter in
while (hn) {
    let n = <Iterator.next> iter in
    yield n
    hn = <Iterator.hasNext> iter
}
```

Writing the ANF out in a state machine, we get:

```
let state = getState() in
let s0 = (state == 0) in
if (s0) {
    // yield 0
    setState(1)
    setCurrent(0)
} else {
    let s1 = (state == 1) in
    if (s1) {
        // yield 1
        setState(2)
        setCurrent(1)
    } else {
        let s2 = (state == 2) in
        if (s2) {
            let zwa = zipWith(add) in
            let tl = <tail> fibs in
            let tup = (fibs, tl) in
            let l = <zwa> tup in
            
        } else {
            
        }
    }
}
```

```
return new Iterator<Integer>() {
    private Fibonacci_State _state = Fibonacci_State.Start;
    private boolean _hasCurrent = false;
    private Integer _current;

    private int currFib;
    private int nextFib;

    @Override
    public boolean hasNext() {
        if (_state == Fibonacci_State.Start) {
            // Compute the first element
            computeNext();
        }
        return _hasCurrent;
    }

    @Override
    public Integer next() {
        if (!_hasCurrent && _state != Fibonacci_State.Done) {
            // Compute the next element
            computeNext();
        }
        _hasCurrent = false;
        return _current;
    }

    private void computeNext() {
        if (_state == Fibonacci_State.Start) goto start;
        if (_state == Fibonacci_State.Step2) goto step2;
        if (_state == Fibonacci_State.Step3) goto step3;
        throw new IllegalStateException();

        start:
        _current = 1;
        _hasCurrent = true;
        _state = Fibonacci_State.Step2;
        return;
        step2:
        currFib = 1;
        nextFib = 1;
        // Fall through
        loopstart:
        _current = nextFib;
        _hasCurrent = true;
        _state = Fibonacci_State.Step3;
        return;
        step3:
        int newFib = currFib + nextFib;
        currFib = nextFib;
        nextFib = newFib;
        // Goto loopStart
        _state = Fibonacci_State.LoopStart;
        goto loopstart;
        done:
        return;
    }
};
```
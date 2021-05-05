package com.virtlink.tego;

import org.jetbrains.annotations.NotNull;

import java.util.Iterator;
/*
class SeqTest {
    static void Main(String[] args) {
        // Build the computation
        Iterable<Integer> tenEvenFibonacciNumbers = Take(10, EvenNumbersOnly(Fibonacci()));

        // Print the results
        for (Integer e : tenEvenFibonacciNumbers) {
            System.out.println(e);
        }
    }

    static Iterable<Integer> Fibonacci() {
        return buildSequence(b -> {
            // Start:
            b.yield(1); // First fibonacci numnber
            // Step 2:
            int currFib = 1;
            int nextFib = 1;
            // LoopStart:
            while (true) {
                b.yield(nextFib);
                // Step 3:
                int newFib = currFib + nextFib;
                currFib = nextFib;
                nextFib = newFib;
            }
        });
    }

    static Iterable<Integer> Fibonacci_lazy() {
        return new Iterable<Integer>() {
            @NotNull
            @Override
            public Iterator<Integer> iterator() {
                return new Iterator<Integer>() {
                    private Fibonacci_State _state = Fibonacci_State.Start;
                    private boolean _hasCurrent = false;
                    private Integer _current = null;

                    private int currFib = 1;
                    private int nextFib = 1;

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
        LoopStart,
        Step3,
        Done,
    }

    static Iterable<Integer> EvenNumbersOnly(Iterable<Integer> sequence) {
        return buildSequence(b -> {
            for (Integer i : sequence) {
                if ((i % 2) == 0) {
                    b.yield(i);
                }
            }
        });
    }

    static <T> Iterable<T> Take(int limit, Iterable<T> sequence) {
        return buildSequence(b -> {
            int count = 0;
            for (T e : sequence) {
                if (count >= limit) return;
                b.yield(e);
                count += 1;
            }
        });
    }
}
*/

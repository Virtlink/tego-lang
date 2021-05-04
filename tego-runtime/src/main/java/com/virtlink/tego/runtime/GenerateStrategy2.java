package com.virtlink.tego.runtime;

import com.virtlink.tego.strategies.Strategy;
import com.virtlink.tego.strategies.Strategy1;
import kotlin.sequences.Sequence;
import kotlin.sequences.SequencesKt;
import org.jetbrains.annotations.NotNull;
/*
class GenerateStrategy2<CTX, T> implements Strategy1<CTX, Strategy<CTX, T, T>, T, T> {
        private GenerateStrategy2() { }

        private static GenerateStrategy2<?, ?> instance = new GenerateStrategy2<>();
        @SuppressWarnings("unchecked")
        public static <CTX, T> GenerateStrategy2<CTX, T> getInstance() {
                return (GenerateStrategy2<CTX, T>)instance;
        }

        @NotNull
        @Override
        public String getName() {
                return "generate";
        }

        @Override
        public int getArity() {
                return Strategy1.super.getArity();
        }

        @NotNull
        @Override
        public Strategy<CTX, T, T> invoke(Strategy<CTX, T, T> arg1) {
                return Strategy1.super.invoke(arg1);
        }

        @NotNull
        @Override
        public Sequence<T> eval(CTX ctx, Strategy<CTX, T, T> arg1, T input) {
                return null;
        }

        private static class GenerateStrategy2_StateMachine<CTX, T> {
                private GenerateStrategy2_StateMachine(CTX ctx, Strategy<CTX, T, T> s, T i) {
                        this.ctx = ctx;
                        this.s = s;
                        this.i = i;
                }

                enum State { Start, Step1, Step2, Step3, End }

                private final CTX ctx;
                private final Strategy<CTX, T, T> s;
                private final T i;
                private State __state = State.Start;
                private Sequence<T> v;
                private Sequence<T> lt;
                private Sequence<T> l;

                public void Start() {
                        try {
                                if (__state == State.Start) {
                                        // let v = <s> i
                                        v = s.eval(ctx, i);
                                } else if (__state == State.Step1) {
                                        // let lt = <generate(s)> v
                                        lt = SequencesKt.flatMap(v, (it) -> GenerateStrategy2.<CTX, T>getInstance().eval(ctx, s, it));
                                } else if (__state == State.Step2) {
                                        // let l = [v | lt]
                                        l = SequencesKt. sequence {
                                                yieldAll(v)
                                                yieldAll(lt)
                                        }
                                } else if (__state == State.Step3) {

                                }
                        }
                }

        }
}
*/
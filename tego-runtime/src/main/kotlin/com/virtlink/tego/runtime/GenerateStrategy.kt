package com.virtlink.tego.runtime

import com.virtlink.tego.strategies.Strategy
import com.virtlink.tego.strategies.Strategy1
import com.virtlink.tego.strategies.Strategy2

/**
 * Generates a sequence.
 *
 * @param CTX the type of context (invariant)
 * @param T the type of input and results (invariant)
 */
@Suppress("PARAMETER_NAME_CHANGED_ON_OVERRIDE")
class GenerateStrategy<CTX, T> private constructor() : Strategy1<CTX, Strategy<CTX, T, T>, T, T> {

    companion object {
        @JvmStatic
        private val instance: GenerateStrategy<*, *> = GenerateStrategy<Any, Any>()
        @Suppress("UNCHECKED_CAST")
        @JvmStatic
        fun <CTX, T> getInstance(): GenerateStrategy<CTX, T> = instance as GenerateStrategy<CTX, T>
    }

    override val name: String get() = "generate"

    override fun eval(
        ctx: CTX,
        s: Strategy<CTX, T, T>,
        i: T
    ): Sequence<T> = sequence {
        // decl generate<T>(T -> T): T -> [T]
        // generate(s) i = v := <s> i; ![v | <generate(s)> v].
        // generate(s) i = v := <s> i; build([v | <generate(s)> v]).

        // let v = <s> i
        val v = s.eval(ctx, i)
        // let lt = <generate(s)> v
        val lt = v.flatMap { this@GenerateStrategy.eval(ctx, s, it) }
        // let l = [v | lt]
        val l = sequence {
            yieldAll(v)
            yieldAll(lt)
        }
        // let r = build(l)
        val r = l.flatMap { BuildStrategy.getInstance<CTX, T>().eval(ctx, it, Unit) }
        // r
        yieldAll(r)
    }

    override fun toString(): String = "$name(..)"
}

class BuildStrategy<CTX, T> : Strategy1<CTX, T, Any, T> {

    companion object {
        @JvmStatic
        private val instance: BuildStrategy<*, *> = BuildStrategy<Any, Any>()
        @Suppress("UNCHECKED_CAST")
        @JvmStatic
        fun <CTX, T> getInstance(): BuildStrategy<CTX, T> = instance as BuildStrategy<CTX, T>
    }

    override fun eval(ctx: CTX, arg1: T, input: Any): Sequence<T> = sequence {
        yield(arg1)
    }
}

class SeqStrategy<CTX, T, U, V> : Strategy2<CTX, Strategy<CTX, T, U>, Strategy<CTX, U, V>, T, V> {

    companion object {
        @JvmStatic
        private val instance: SeqStrategy<*, *, *, *> = SeqStrategy<Any, Any, Any, Any>()
        @Suppress("UNCHECKED_CAST")
        @JvmStatic
        fun <CTX, T, U, V> getInstance(): SeqStrategy<CTX, T, U, V> = instance as SeqStrategy<CTX, T, U, V>
    }

    override fun eval(ctx: CTX, arg1: Strategy<CTX, T, U>, arg2: Strategy<CTX, U, V>, input: T): Sequence<V> {
        TODO("Not yet implemented")
    }

}
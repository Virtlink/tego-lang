package com.virtlink.tego.runtime

import com.virtlink.tego.strategies.Strategy
import com.virtlink.tego.strategies.Strategy1

/**
 * Attempts to evaluate a strategy.
 *
 * @param CTX the type of context (invariant)
 * @param T the type of input and results (invariant)
 */
@Suppress("PARAMETER_NAME_CHANGED_ON_OVERRIDE")
class TryStrategy<CTX, T> private constructor() : Strategy1<CTX, Strategy<CTX, T, T>, T, T> {

    companion object {
        @JvmStatic
        private val instance: TryStrategy<*, *> = TryStrategy<Any, Any>()
        @Suppress("UNCHECKED_CAST")
        @JvmStatic
        fun <CTX, T> getInstance(): TryStrategy<CTX, T> = instance as TryStrategy<CTX, T>
    }

    override val name: String get() = "try"

    override fun eval(
        ctx: CTX,
        s: Strategy<CTX, T, T>,
        input: T
    ): Sequence<T> = sequence {
        // if(s, id, id)
        // TODO: We can optimize this by moving the getInstance calls outside the sequence body
        val glc = IfStrategy.getInstance<CTX, T, T, T>()
        val id = IdStrategy.getInstance<CTX, T>()
        yieldAll(glc.eval(ctx,
            s, id, id, input
        ))
    }

    override fun toString(): String = "$name(..)"
}
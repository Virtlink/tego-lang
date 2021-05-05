package com.virtlink.tego.runtime

import com.virtlink.tego.strategies.Strategy
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

/**
 * Increment.
 *
 * @param CTX the type of context (covariant)
 */
class IncStrategy<CTX> private constructor() : Strategy<CTX, Int, Int> {

    companion object {
        @JvmStatic
        private val instance: IncStrategy<*> = IncStrategy<Any>()
        @Suppress("UNCHECKED_CAST")
        @JvmStatic
        fun <CTX> getInstance(): IncStrategy<CTX> = instance as IncStrategy<CTX>
    }

    override val name: String get() = "inc"

    override fun eval(ctx: CTX, input: Int): Sequence<Int> = sequence {
        yield(input + 1)
    }

    override fun toString(): String = "$name()"
}
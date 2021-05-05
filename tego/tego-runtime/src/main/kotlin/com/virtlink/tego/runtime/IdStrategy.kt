package com.virtlink.tego.runtime

import com.virtlink.tego.strategies.Strategy
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

/**
 * Identity.
 *
 * @param CTX the type of context (invariant)
 * @param T the type of input and result (invariant)
 */
class IdStrategy<CTX, T> private constructor() : Strategy<CTX, T, T> {

    companion object {
        @JvmStatic
        private val instance: IdStrategy<*, *> = IdStrategy<Any, Any>()
        @Suppress("UNCHECKED_CAST")
        @JvmStatic
        fun <CTX, T> getInstance(): IdStrategy<CTX, T> = instance as IdStrategy<CTX, T>
    }

    override val name: String get() = "id"

    override fun eval(ctx: CTX, input: T): Sequence<T> {
        return sequenceOf(input)
    }

    override fun toString(): String = "$name()"
}
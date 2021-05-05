package com.virtlink.tego.runtime

import com.virtlink.tego.strategies.Strategy
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow

/**
 * Failure.
 *
 * @param CTX the type of context
 * @param T the type of input (contravariant)
 * @param R the type of result (covariant)
 */
class FailStrategy<CTX, in T, out R> private constructor() : Strategy<CTX, T, R> {
    companion object {
        @JvmStatic
        private val instance: FailStrategy<*, *, *> = FailStrategy<Any, Any, Any>()
        @Suppress("UNCHECKED_CAST")
        @JvmStatic
        fun <CTX, T, R> getInstance(): FailStrategy<CTX, T, R> = instance as FailStrategy<CTX, T, R>
    }

    override val name: String get() = "fail"

    override fun eval(ctx: CTX, input: T): Sequence<R> {
        return emptySequence()
    }

    override fun toString(): String = "$name()"
}
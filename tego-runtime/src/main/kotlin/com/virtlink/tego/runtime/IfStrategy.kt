package com.virtlink.tego.runtime

import com.virtlink.tego.strategies.Strategy
import com.virtlink.tego.strategies.Strategy3
import kotlinx.coroutines.flow.*

/**
 * Conditionally evaluates one of two strategies.
 *
 * @param CTX the type of context (invariant)
 * @param T the type of input (invariant)
 * @param U the type of intermediate (invariant)
 * @param R the type of result (invariant)
 */
@Suppress("PARAMETER_NAME_CHANGED_ON_OVERRIDE")
class IfStrategy<CTX, T, U, R> private constructor() : Strategy3<CTX, Strategy<CTX, T, U>, Strategy<CTX, U, R>, Strategy<CTX, T, R>, T, R> {

    companion object {
        @JvmStatic
        private val instance: IfStrategy<*, *, *, *> = IfStrategy<Any, Any, Any, Any>()
        @Suppress("UNCHECKED_CAST")
        @JvmStatic
        fun <CTX, T, U, R> getInstance(): IfStrategy<CTX, T, U, R> = instance as IfStrategy<CTX, T, U, R>
    }

    override val name: String get() = "if"

    override fun eval(
        ctx: CTX,
        sc: Strategy<CTX, T, U>,
        st: Strategy<CTX, U, R>,
        se: Strategy<CTX, T, R>,
        input: T
    ): Sequence<R> = sequence {
        val rc = sc.eval(ctx, input)
        // FIXME: Buffer the input to ensure its iterated only once
        if (rc.any()) {
            yieldAll(rc.flatMap { st.eval(ctx, it) })
        } else {
            yieldAll(se.eval(ctx, input))
        }
    }

    override fun toString(): String = "$name(..)"
}
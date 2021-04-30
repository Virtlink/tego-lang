package com.virtlink.tego.strategies

import kotlinx.coroutines.flow.Flow

/**
 * A strategy with one argument.
 *
 * @param CTX the type of context (invariant)
 * @param A1 the type of the first argument (contravariant)
 * @param T the type of input (contravariant)
 * @param R the type of results (covariant)
 */
@FunctionalInterface
@Suppress("PARAMETER_NAME_CHANGED_ON_OVERRIDE")
fun interface Strategy1<CTX, in A1, in T, out R> : StrategyDecl {

    override val arity get() = 1

    /**
     * Evaluates the strategy.
     *
     * @param ctx the context
     * @param arg1 the first argument
     * @param input the input argument
     * @return the lazy sequence of results; or an empty sequence if the strategy failed
     */
    fun eval(ctx: CTX, arg1: A1, input: T): Sequence<R>

    /**
     * Applies the strategy, providing the first argument.
     *
     * @param arg1 the first argument
     * @return the applied strategy
     */
    operator fun invoke(arg1: A1): Strategy<CTX, T, R> = object: Strategy<CTX, T, R> {
        override val name: String get() = this@Strategy1.name
        override fun eval(ctx: CTX, input: T): Sequence<R>
            = this@Strategy1.eval(ctx, arg1, input)
        override fun toString(): String = "$name($arg1)"
    }

}
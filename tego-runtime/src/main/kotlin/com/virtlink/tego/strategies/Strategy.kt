package com.virtlink.tego.strategies

import kotlinx.coroutines.flow.Flow

/**
 * A strategy.
 *
 * @param CTX the type of context (invariant)
 * @param T the type of input (contravariant)
 * @param R the type of results (covariant)
 */
@FunctionalInterface
@Suppress("PARAMETER_NAME_CHANGED_ON_OVERRIDE")
fun interface Strategy<CTX, in T, out R> : StrategyDecl {

    override val arity get() = 0

    /**
     * Evaluates the strategy.
     *
     * @param ctx the context
     * @param input the input argument
     * @return the lazy sequence of results; or an empty sequence if the strategy failed
     */
    fun eval(ctx: CTX, input: T): Sequence<R>

}

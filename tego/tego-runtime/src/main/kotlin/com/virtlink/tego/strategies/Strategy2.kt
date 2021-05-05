package com.virtlink.tego.strategies

import kotlinx.coroutines.flow.Flow

/**
 * A strategy with two arguments.
 *
 * @param CTX the type of context (invariant)
 * @param A1 the type of the first argument (contravariant)
 * @param A2 the type of the second argument (contravariant)
 * @param T the type of input (contravariant)
 * @param R the type of results (covariant)
 */
@FunctionalInterface
@Suppress("PARAMETER_NAME_CHANGED_ON_OVERRIDE")
fun interface Strategy2<CTX, in A1, in A2, in T, out R> : StrategyDecl {

    override val arity get() = 2

    /**
     * Evaluates the strategy.
     *
     * @param ctx the context
     * @param arg1 the first argument
     * @param arg2 the second argument
     * @param input the input argument
     * @return the lazy sequence of results; or an empty sequence if the strategy failed
     */
    fun eval(ctx: CTX, arg1: A1, arg2: A2, input: T): Sequence<R>

    /**
     * Partially applies the strategy, providing the first argument.
     *
     * @param arg1 the first argument
     * @return the partially applied strategy
     */
    operator fun invoke(arg1: A1): Strategy1<CTX, A2, T, R> = object: Strategy1<CTX, A2, T, R> {
        override val name: String get() = this@Strategy2.name
        override fun eval(ctx: CTX, arg2: A2, input: T): Sequence<R>
            = this@Strategy2.eval(ctx, arg1, arg2, input)
        override fun invoke(arg2: A2): Strategy<CTX, T, R>
            = this@Strategy2.invoke(arg1, arg2)
        override fun toString(): String = "$name($arg1, ..)"
    }

    /**
     * Applies the strategy, providing the first two arguments.
     *
     * @param arg1 the first argument
     * @param arg2 the second argument
     * @return the applied strategy
     */
    operator fun invoke(arg1: A1, arg2: A2): Strategy<CTX, T, R> = object: Strategy<CTX, T, R> {
        override val name: String get() = this@Strategy2.name
        override fun eval(ctx: CTX, input: T): Sequence<R>
            = this@Strategy2.eval(ctx, arg1, arg2, input)
        override fun toString(): String = "$name($arg1, $arg2)"
    }

}
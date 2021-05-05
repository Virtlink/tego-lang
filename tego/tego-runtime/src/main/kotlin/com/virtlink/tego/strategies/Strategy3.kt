package com.virtlink.tego.strategies

import kotlinx.coroutines.flow.Flow

/**
 * A strategy with three arguments.
 *
 * @param CTX the type of context (invariant)
 * @param A1 the type of the first argument (contravariant)
 * @param A2 the type of the second argument (contravariant)
 * @param A3 the type of the third argument (contravariant)
 * @param T the type of input (contravariant)
 * @param R the type of results (covariant)
 */
@FunctionalInterface
@Suppress("PARAMETER_NAME_CHANGED_ON_OVERRIDE")
fun interface Strategy3<CTX, in A1, in A2, in A3, in T, out R> : StrategyDecl {

    override val arity get() = 3

    /**
     * Evaluates the strategy.
     *
     * @param ctx the context
     * @param arg1 the first argument
     * @param arg2 the second argument
     * @param arg3 the third argument
     * @param input the input argument
     * @return the lazy sequence of results; or an empty sequence if the strategy failed
     */
    fun eval(ctx: CTX, arg1: A1, arg2: A2, arg3: A3, input: T): Sequence<R>

    /**
     * Partially applies the strategy, providing the first argument.
     *
     * @param arg1 the first argument
     * @return the partially applied strategy
     */
    operator fun invoke(arg1: A1): Strategy2<CTX, A2, A3, T, R> = object: Strategy2<CTX, A2, A3, T, R> {
        override val name: String get() = this@Strategy3.name
        override fun eval(ctx: CTX, arg2: A2, arg3: A3, input: T): Sequence<R>
                = this@Strategy3.eval(ctx, arg1, arg2, arg3, input)
        override fun invoke(arg2: A2): Strategy1<CTX, A3, T, R>
                = this@Strategy3.invoke(arg1, arg2)
        override fun invoke(arg2: A2, arg3: A3): Strategy<CTX, T, R>
                = this@Strategy3.invoke(arg1, arg2, arg3)
        override fun toString(): String = "$name($arg1, ..)"
    }

    /**
     * Partially applies the strategy, providing the first two arguments.
     *
     * @param arg1 the first argument
     * @param arg2 the second argument
     * @return the partially applied strategy
     */
    operator fun invoke(arg1: A1, arg2: A2): Strategy1<CTX, A3, T, R> = object: Strategy1<CTX, A3, T, R> {
        override val name: String get() = this@Strategy3.name
        override fun eval(ctx: CTX, arg3: A3, input: T): Sequence<R>
                = this@Strategy3.eval(ctx, arg1, arg2, arg3, input)
        override fun invoke(arg3: A3): Strategy<CTX, T, R>
                = this@Strategy3.invoke(arg1, arg2, arg3)
        override fun toString(): String = "$name($arg1, $arg2, ..)"
    }

    /**
     * Applies the strategy, providing the first three arguments.
     *
     * @param arg1 the first argument
     * @param arg2 the second argument
     * @param arg3 the third argument
     * @return the applied strategy
     */
    operator fun invoke(arg1: A1, arg2: A2, arg3: A3): Strategy<CTX, T, R> = object: Strategy<CTX, T, R> {
        override val name: String get() = this@Strategy3.name
        override fun eval(ctx: CTX, input: T): Sequence<R>
                = this@Strategy3.eval(ctx, arg1, arg2, arg3, input)
        override fun toString(): String = "$name($arg1, $arg2, $arg3)"
    }

}
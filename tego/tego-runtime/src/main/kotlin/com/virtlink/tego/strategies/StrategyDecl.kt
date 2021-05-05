package com.virtlink.tego.strategies

/**
 * A strategy declaration.
 */
interface StrategyDecl {

    /**
     * The name of the strategy.
     */
    val name: String get() = this::class.java.simpleName

    /**
     * The arity of the strategy.
     *
     * The arity of a basic strategy `T -> R` is 0.
     *
     * @return the arity of the strategy, not counting the input argument
     */
    val arity: Int

}
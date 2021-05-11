package org.spoofax.tego.ir

/**
 * An IR expression.
 */
interface Exp {

    /** Whether this subtree of the AST is an ANF top-level, compound, or immediate expression. */
    val isAnf: Boolean get() = isComp
    /** Whether this subtree of the AST is an ANF compound or immediate expression. */
    val isComp: Boolean get() = isImm
    /** Whether this subtree of the AST is an ANF immediate expression. */
    val isImm: Boolean get() = false

    /** The type of the expression. */
    val type: Type

}

/**
 * An IR constant.
 */
interface Const : Exp {
    override val isImm: Boolean get() = true
}

/**
 * Composition of two strategies or expressions.
 *
 * ```
 * $e1 ; $e2
 * ```
 */
data class Seq(
    val e1: Exp,
    val e2: Exp,
    override val type: Type,
) : Exp {
    override val isComp: Boolean get() = e1.isImm && e2.isImm
}

/**
 * A let-expression, which binds an expression to a variable
 * and scopes it to the body of the let.
 *
 * ```
 * let $varName = $varExp in
 *   $body
 * ```
 */
data class Let(
    val varName: String,
    val varExp: Exp,            // comp
    val body: Exp,              // anf
    override val type: Type,
) : Exp {
//    override val type: Type get() = body.type

    override val isAnf: Boolean get() = varExp.isComp && body.isAnf
}

/**
 * Applies one or more arguments to a function.
 *
 * ```
 * $function($arguments)
 * ```
 */
data class Apply(
    val function: Exp,          // imm
    val arguments: List<Exp>,   // [imm]
    override val type: Type,
) : Exp {
    override val isComp: Boolean get() = function.isImm && arguments.all { it.isImm }
}

/**
 * Evaluates a strategy.
 *
 * ```
 * <$strategy> $input
 * ```
 */
data class Eval(
    val strategy: Exp,          // imm
    val input: Exp,             // imm
    override val type: Type,
) : Exp {
    override val isComp: Boolean get() = strategy.isImm && input.isImm
}

/**
 * Gets a value from the environment.
 *
 * ```
 * name
 * ```
 */
data class Var(
    val name: String,
    override val type: Type,
) : Exp {
    override val isImm: Boolean get() = true
}

/**
 * A literal integer.
 *
 * ```
 * $value
 * ```
 */
data class IntLit(
    val value: Int,
    override val type: Type,
) : Const

/**
 * A literal string.
 *
 * ```
 * $value
 * ```
 */
data class StringLit(
    val value: String,
    override val type: Type,
) : Const

/**
 * An instance of `object`.
 *
 * ```
 * Any
 * ```
 */
data class AnyInst(
    override val type: Type,
) : Const
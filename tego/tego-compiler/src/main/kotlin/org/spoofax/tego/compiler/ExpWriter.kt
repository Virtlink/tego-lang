package org.spoofax.tego.compiler

import org.spoofax.tego.ir.Exp

/**
 * Writes an expression to Java bytecode.
 */
class ExpWriter {

    /**
     * Writes the expression.
     */
    fun writeExp(exp: Exp, builder: ScopeBuilder) {
        require(exp.isAnf) { "Expression is not in ANF." }


    }

}
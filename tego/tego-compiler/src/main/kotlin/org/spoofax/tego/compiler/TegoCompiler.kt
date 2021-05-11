package org.spoofax.tego.compiler

import org.spoofax.tego.aterm.Term
import org.spoofax.tego.ir.Exp
import org.spoofax.tego.ir.IrBuilder
import org.spoofax.tego.ir.ExpNormalizer

/**
 * Compiles a Tego strategy to a class.
 */
class TegoCompiler(
    private val irBuilder: IrBuilder,
    private val expNormalizer: ExpNormalizer,
    private val expWriter: ExpWriter,
) {

    /**
     * Compiles an expression.
     *
     * @param expTerm the expression term to compile
     * @return
     */
    fun compileExpression(expTerm: Term) {
        val exp = irBuilder.toExp(expTerm)
        //val anfExp = expNormalizer.normalize(exp)


        // TODO: Write to class
    }
}
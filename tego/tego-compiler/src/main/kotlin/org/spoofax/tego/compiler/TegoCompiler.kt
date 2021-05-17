package org.spoofax.tego.compiler

import com.virtlink.kasm.ScopeBuilder
import org.spoofax.tego.aterm.Term
import org.spoofax.tego.ir.Exp
import org.spoofax.tego.ir.IrBuilder
import org.spoofax.tego.ir.ExpNormalizer
import org.spoofax.tego.ir.Let

/**
 * Compiles a Tego strategy to a class.
 */
class TegoCompiler(
    private val irBuilder: IrBuilder,
    private val expNormalizer: ExpNormalizer,
    private val strategyWriter: StrategyWriter,
//    private val expWriter: ExpWriter,
) {

    fun compile(term: Term) {
        //val ir = irBuilder.to
        //strategyWriter.writeStrategy()
    }

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
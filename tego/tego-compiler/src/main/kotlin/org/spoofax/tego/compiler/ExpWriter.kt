package org.spoofax.tego.compiler

import com.virtlink.kasm.ScopeBuilder
import com.virtlink.kasm.iConst
import org.spoofax.tego.ir.*

/**
 * Writes an expression to Java bytecode.
 */
class ExpWriter(
    private val typeManager: JvmTypeManager
) {

    /**
     * Writes the expression.
     */
    fun ScopeBuilder.writeExp(exp: Exp, env: Environment) {
        require(exp.isAnf) { "Expression is not in ANF." }

        when (exp) {
            is Let -> {
                // val v = `exp.body`
                val v = localVar(exp.varName, typeManager[exp.varExp.type])
                writeExp(exp.body, env + v)
                aStore(v)
            }
            is Apply -> {
                TODO()
            }
            is Eval -> {
                TODO()
            }
            is IntLit -> {
                // 42
                iConst(exp.value)
            }
            is StringLit -> {
                // "abc"
                ldc(exp.value)
            }
            is AnyInst -> {
                TODO()
            }
            is Var -> {
                val v = env[exp.name]
                aLoad(v)
            }
            else -> TODO("Unsupported expression of type ${exp::class.java}: $exp")
        }
        TODO()
    }

}
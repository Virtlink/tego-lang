package org.spoofax.tego.compiler

import com.virtlink.kasm.JvmMethodSignature
import com.virtlink.kasm.ScopeBuilder
import com.virtlink.kasm.iConst
import org.spoofax.tego.ir.*

/**
 * Writes an expression to Java bytecode.
 */
class ExpWriter(
    private val typeManager: JvmTypeManager
) {

    class Factory {
        fun create(typeManager: JvmTypeManager): ExpWriter
                = ExpWriter(typeManager)
    }

    /**
     * Writes the expression to this scope.
     *
     * @param exp the expression to write
     * @param env the environment
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
                // s.apply(a1, a2, .., an)
                val strategyType = exp.strategy.type as StrategyType
                val strategyJvmType = typeManager[strategyType]
                val paramJvmTypes = strategyType.paramTypes.map { typeManager[it] }.toTypedArray()

                // We take the inferred type of the application expression here.
                // FIXME: This is not necessary correct, since it might give us a more specific type
                //  than the apply() function returns, e.g., `FooStrategy` instead of `Strategy`.
//                val outputJvmType = typeManager[exp.type as StrategyType]
                // Therefore, we just apply the strategy ourselves here. It should give the same result.
                val outputStrategyType = strategyType.apply(exp.arguments.map { it.type })
                val outputStrategyJvmType = typeManager[outputStrategyType]
                // FIXME: This check doesn't work as long as we cannot get the StrategyType from a ClassType
//                assert(outputStrategyType.arity == (exp.type as StrategyType).arity)

                writeExp(exp.strategy, env)
                exp.arguments.forEach { arg ->
                    writeExp(arg, env)
                }

                invokeVirtual(
                    strategyJvmType, "apply", JvmMethodSignature.of(
                        outputStrategyJvmType, listOf(*paramJvmTypes)
                    )
                )
            }
            is Eval -> {
                // s.eval(ctx, input)
                val strategyType = exp.strategy.type as StrategyType
                val strategyJvmType = typeManager[strategyType]
                val ctxJvmType = env["ctx"].type
                val paramJvmTypes = strategyType.paramTypes.map { typeManager[it] }.toTypedArray()
                val inputJvmType = typeManager[strategyType.inputType]
                val outputJvmType = typeManager[strategyType.outputType]

                writeExp(exp.strategy, env)
                // TODO: In the future we can make a version of Eval that can deal with partially applied strategies, say ApplyEval().
                //  The parser would have to parse `<s(a1, a2, .., an)> i` to this new ApplyEval() instead of a combination of
                //  Apply() and Eval() as it currently does. This would avoid a call to `Strategy.apply()` and the allocation
                //  of a partially applied strategy object.
                check(paramJvmTypes.isEmpty()) { "Expected a fully applied strategy, missing ${paramJvmTypes.size} arguments." }
                writeExp(exp.input, env)

                invokeVirtual(
                    strategyJvmType, "eval", JvmMethodSignature.of(
                        outputJvmType, listOf(
                            ctxJvmType, *paramJvmTypes, inputJvmType
                        )
                    )
                )
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
                TODO("Create an object.")
            }
            is Var -> {
                val v = env[exp.name]
                aLoad(v)
            }
            else -> TODO("Unsupported expression of type ${exp::class.java}: $exp")
        }
    }

}
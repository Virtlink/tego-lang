package org.spoofax.tego.compiler

import com.virtlink.kasm.JvmMethodSignature
import com.virtlink.kasm.LocalVar
import com.virtlink.kasm.ScopeBuilder
import com.virtlink.kasm.iConst
import org.spoofax.tego.ir.*

/**
 * Assembles an expression into Java bytecode.
 */
class ExpAssembler(
    private val typeManager: JvmTypeManager,
    private val symbolTable: SymbolTable,
) {

    class Factory {
        fun create(typeManager: JvmTypeManager, symbolTable: SymbolTable): ExpAssembler
                = ExpAssembler(typeManager, symbolTable)
    }

    /**
     * Assembles the expression into this scope.
     *
     * @param rootExp the expression to write
     * @param env the environment
     */
    fun ScopeBuilder.assembleExp(rootExp: Exp, rootEnv: Environment) {
        require(rootExp.isAnf) { "Expression is not in ANF." }

        val worklist = ArrayDeque<Pair<Exp, Environment>>()
        worklist.addLast(rootExp to rootEnv)

        while (worklist.isNotEmpty()) {
            val (exp, env) = worklist.removeFirst()
            when (exp) {
                is Let -> {
                    // val v = `exp.body`
                    val v = localVar(exp.varName, typeManager[exp.varExp.type])
                    assembleExp(exp.varExp, env)
                    aStore(v)
                    worklist.addLast(exp.body to (env + v))
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

                    assembleExp(exp.strategy, env)
                    exp.arguments.forEach { arg ->
                        assembleExp(arg, env)
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
                    val ctx = env["ctx"]!!
                    val ctxJvmType = ctx.type
                    val paramJvmTypes = strategyType.paramTypes.map { typeManager[it] }.toTypedArray()
                    val inputJvmType = typeManager[strategyType.inputType]
                    val outputJvmType = typeManager[strategyType.outputType]

                    assembleExp(exp.strategy, env)
                    aLoad(ctx)
                    // TODO: In the future we can make a version of Eval that can deal with partially applied strategies, say ApplyEval().
                    //  The parser would have to parse `<s(a1, a2, .., an)> i` to this new ApplyEval() instead of a combination of
                    //  Apply() and Eval() as it currently does. This would avoid a call to `Strategy.apply()` and the allocation
                    //  of a partially applied strategy object.
                    check(paramJvmTypes.isEmpty()) { "Expected a fully applied strategy, missing ${paramJvmTypes.size} arguments." }
                    assembleExp(exp.input, env)

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
                    if (v != null) {
                        loadLocalVar(v)
                    } else {
                        // TODO: Fix package name
                        val d = symbolTable[QName("tego", exp.name)]
                        if (d != null) {
                            loadTypeDecl(d)
                        } else {
                            throw NoSuchElementException("Environment contains no variable or declaration with name '${exp.name}'.")
                        }
                    }
                }
                else -> TODO("Unsupported expression of type ${exp::class.java}: $exp")
            }
        }
    }

    /**
     * Loads a local variable.
     *
     * @param localVar the local variable
     */
    fun ScopeBuilder.loadLocalVar(localVar: LocalVar) {
        aLoad(localVar)
    }

    /**
     * Loads a type declaration.
     *
     * @param decl the type declaration
     */
    fun ScopeBuilder.loadTypeDecl(decl: TypeDecl) {
        when (decl) {
            is StrategyTypeDecl -> {
                // MyStrategy.getInstance()
                val strategyJvmType = typeManager[decl.type]
                invokeStatic(strategyJvmType, "getInstance", JvmMethodSignature.of(strategyJvmType, emptyList()))
            }
            is ClassTypeDecl -> TODO("Cannot load class")
            else -> TODO("Unsupported type declaration: $decl")
        }
    }

}
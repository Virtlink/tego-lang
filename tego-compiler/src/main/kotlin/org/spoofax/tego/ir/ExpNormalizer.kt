package org.spoofax.tego.ir


typealias Context = List<Assignment>
typealias Assignment = Triple<String, Type, Exp>

/**
 * Performs A-normalization on the expression IR.
 *
 * After normalizing the IR to ANF (Administrative Normal Form),
 * almost all expressions operate only on immediate expressions, that is,
 * expressions for which no further computation is necessary, such
 * as a constant value or a variable reference.
 *
 * The computations themselves will have been lifted out of the expression
 * into surrounding `let`-expressions.
 */
class Normalizer {

    class TypeEnvironment(
        private val map: Map<String, Type>
    ) {
        operator fun get(v: String): Type {
            return map[v] ?: throw NoSuchElementException("No type defined for: $v")
        }
        operator fun plus(pair: Pair<String, Type>): TypeEnvironment {
            return with(pair.first, pair.second)
        }
        operator fun plus(pairs: List<Pair<String, Type>>): TypeEnvironment {
            return withAll(pairs)
        }
        fun with(name: String, type: Type): TypeEnvironment {
            return TypeEnvironment(map + (name to type))
        }
        fun withAll(vararg pairs: Pair<String, Type>): TypeEnvironment
            = withAll(pairs.asList())
        fun withAll(pairs: List<Pair<String, Type>>): TypeEnvironment {
            return TypeEnvironment(map + pairs)
        }
    }

    private data class R(
        val exp : Exp,
        val expType : Type,
        val ctx : Context,
    )

    private fun Iterable<R>.unzip(): Triple<List<Exp>, List<Type>, List<Context>> {
        val expectedSize = if (this is Collection<*>) this.size else 10
        val expList = ArrayList<Exp>(expectedSize)
        val typList = ArrayList<Type>(expectedSize)
        val ctxList = ArrayList<Context>(expectedSize)
        for ((e, t, c) in this) {
            expList.add(e)
            typList.add(t)
            ctxList.add(c)
        }
        return Triple(expList, typList, ctxList)
    }


    /**
     * Normalizes the AST to ANF (Administrative Normal Form).
     *
     * @param exp the AST to normalize
     * @return the normalized AST
     */
    fun normalize(exp: Exp, env: TypeEnvironment): Exp {
        val (ans, ansType, ansCtx) = toComp(exp, env)
        val newExp = ansCtx.foldRight(ans) { (bind, bindType, exp), body -> Let(bind, bindType, exp, body) }
        assert(newExp.isAnf)
        return newExp
    }

    /**
     * Normalizes an expression into its compound expression form
     * and a corresponding context that computes the values for the immediate variables.
     *
     * @param exp the expression to normalize
     * @return a pair of a normalized compound expression and a context
     */
    private fun toComp(exp: Exp, env: TypeEnvironment): R {
        val (newExp, newType, newCtx) = when (exp) {
            // Compounds
            is Seq -> TODO()
            is Let -> {
                val (bnd, bndType, bndCtx) = toComp(exp.varExp, env)
                val (bdy, bdyType, bdyCtx) = toComp(exp.body, env + (exp.varName to bndType))
                R(bdy, bdyType, (bndCtx + bdyCtx + Triple(exp.varName, bndType, bnd)))
            }
            is Apply -> {
                val (fnc, fncType, fncCtx) = toImm(exp.function, env)
                val (args, argTypes, argCtxs) = exp.arguments.map { toImm(it, env) }.unzip()
                R(Apply(fnc, args), fncType, (fncCtx + argCtxs.flatten()))
            }
            is Eval -> {
                val (str, strType, strCtx) = toImm(exp.strategy, env)
                val (inp, inpType, inpCtx) = toImm(exp.input, env)
                R(Eval(str, inp), strType, (strCtx + inpCtx))
            }
//            is If -> {
//                val (cnd, cndCtx) = toImm(exp.conditionExp)
//                val (thn, thnCtx) = toComp(exp.onSuccessExp)
//                val (els, elsCtx) = toComp(exp.onFailExp)
//                If(cnd, thn, els) to (thnCtx + elsCtx + cndCtx)
//            }
//            is Lam -> {
//                val (bdy, bdyCtx) = toComp(exp.bodyExp)
//                Lam(exp.paramId, exp.paramType, bdy) to (bdyCtx)
//            }
            // Immediates
            is Const -> toImm(exp, env)
            is Var -> toImm(exp, env)
            else -> TODO("Unsupported expression: $exp")
        }
        assert(newExp.isComp)
        return R(newExp, newType, newCtx)
    }

    /**
     * Normalizes an expression into its immediate expression form
     * and a corresponding context that computes the values for the immediate variables.
     *
     * @param exp the expression to normalize
     * @return a pair of a normalized immediate expression and a context
     */
    private fun toImm(exp: Exp, env: TypeEnvironment): R {
        val (newExp, newType, newCtx) = when (exp) {
            // Constant Immediates
            is IntLit -> R(exp, IntType, listOf())
            is StringLit -> R(exp, StringType, listOf())
            is AnyInst -> R(exp, AnyType, listOf())
            is Const -> TODO("Unsupported constant: $exp")
            // Variable Immediates
            is Var -> R(exp, env[exp.varName], listOf())
            // Compounds
            else -> wrapToImm(exp, env)
        }
        assert(newExp.isImm)
        return R(newExp, newType, newCtx)
    }

    /**
     * Wraps a compound expression in a `let`-expression
     * (implicitly, in the context) to make it into an immediate expression.
     *
     * @param exp the compound expression
     * @return a pair of a fresh immediate variable and a context
     */
    private fun wrapToImm(exp: Exp, env: TypeEnvironment): R {
        val (c, t, ctx) = toComp(exp, env)
        val tmp = fresh()
        return R(Var(tmp), t, (ctx + Triple(tmp, t, c)))
    }

    private var counter: Int = 0

    /**
     * Provides a fresh variable.
     *
     * @return the name of the fresh variable
     */
    private fun fresh() : String {
        val name = "var_$counter"
        counter += 1
        return name
    }
}
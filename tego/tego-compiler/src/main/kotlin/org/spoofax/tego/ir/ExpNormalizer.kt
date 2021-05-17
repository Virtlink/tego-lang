package org.spoofax.tego.ir


typealias Context = List<Assignment>
typealias Assignment = Pair<String, Exp>

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
class ExpNormalizer {

    private data class R(
        val exp : Exp,
        val ctx : Context,
    )

    private fun Iterable<R>.unzip(): Pair<List<Exp>, List<Context>> {
        val expectedSize = if (this is Collection<*>) this.size else 10
        val expList = ArrayList<Exp>(expectedSize)
        val ctxList = ArrayList<Context>(expectedSize)
        for ((e, c) in this) {
            expList.add(e)
            ctxList.add(c)
        }
        return expList to ctxList
    }

    /**
     * Normalizes the AST to ANF (Administrative Normal Form).
     *
     * @param exp the AST to normalize
     * @return the normalized AST
     */
    fun normalize(exp: Exp): Exp {
        val (ans, ansCtx) = toComp(exp)
        val newExp = ansCtx.foldRight(ans) { (bind, exp), body -> Let(bind, exp, body, body.type) }
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
    private fun toComp(exp: Exp): R {
        val (newExp, newCtx) = when (exp) {
            // Compounds
            is Seq -> TODO()
            is Let -> {
                val (bnd, bndCtx) = toComp(exp.varExp)
                val (bdy, bdyCtx) = toComp(exp.body)
                R(bdy, (bndCtx + bdyCtx + (exp.varName to bnd)))
            }
            is Apply -> {
                val (fnc, fncCtx) = toImm(exp.strategy)
                val (args, argCtxs) = exp.arguments.map { toImm(it) }.unzip()
                R(Apply(fnc, args, exp.type), (fncCtx + argCtxs.flatten()))
            }
            is Eval -> {
                val (str, strCtx) = toImm(exp.strategy)
                val (inp, inpCtx) = toImm(exp.input)
                R(Eval(str, inp, exp.type), (strCtx + inpCtx))
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
            is Const -> toImm(exp)
            is Var -> toImm(exp)
            else -> TODO("Unsupported expression: $exp")
        }
        assert(newExp.isComp)
        return R(newExp, newCtx)
    }

    /**
     * Normalizes an expression into its immediate expression form
     * and a corresponding context that computes the values for the immediate variables.
     *
     * @param exp the expression to normalize
     * @return a pair of a normalized immediate expression and a context
     */
    private fun toImm(exp: Exp): R {
        val (newExp, newCtx) = when (exp) {
            // Constant Immediates
            is IntLit -> R(exp, listOf())
            is StringLit -> R(exp, listOf())
            is AnyInst -> R(exp, listOf())
            is Const -> TODO("Unsupported constant: $exp")
            // Variable Immediates
            is Var -> R(exp, listOf())
            // Compounds
            else -> wrapToImm(exp)
        }
        assert(newExp.isImm)
        return R(newExp, newCtx)
    }

    /**
     * Wraps a compound expression in a `let`-expression
     * (implicitly, in the context) to make it into an immediate expression.
     *
     * @param exp the compound expression
     * @return a pair of a fresh immediate variable and a context
     */
    private fun wrapToImm(exp: Exp): R {
        val (c, ctx) = toComp(exp)
        val tmp = fresh()
        return R(Var(tmp, exp.type), (ctx + (tmp to c)))
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
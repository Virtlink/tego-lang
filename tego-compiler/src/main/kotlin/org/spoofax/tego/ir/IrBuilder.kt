package org.spoofax.tego.ir

import org.spoofax.tego.InvalidFormatException
import org.spoofax.tego.aterm.ApplTerm
import org.spoofax.tego.aterm.Term
import org.spoofax.tego.aterm.toJavaString
import org.spoofax.tego.aterm.toList

/**
 * Builds the Intermediate Representation of an expression.
 */
class IrBuilder {

    /**
     * Compiles an expression term into an IR expression.
     */
    fun toExp(exp: Term): Exp {
        require(exp is ApplTerm) { "Expected constructor application term, got: $exp"}

        return when (exp.constructor) {
            "Var" -> Var(exp[0].toJavaString())
            "Int" -> IntLit(exp[0].toJavaString().toIntOrNull() ?: throw InvalidFormatException("Expected integer value, got: ${exp.args[0].toJavaString()}"))
            "String" -> StringLit(exp[0].toJavaString())
            "Object" -> AnyInst
            "Id" -> TODO()  // Should be desugared into a val `id`
            "Apply" -> Apply(toExp(exp[0]), exp[1].toList().map { toExp(it) })
            "Eval" -> Eval(toExp(exp[0]), toExp(exp[1]))
            "Build" -> TODO()   // Should be desugared into an eval `<build> v`?
            "Seq" -> Seq(toExp(exp[0]), toExp(exp[1]))
            else -> TODO("Unsupported expression: $exp")
        }
    }
}
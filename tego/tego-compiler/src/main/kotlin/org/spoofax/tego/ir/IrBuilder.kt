package org.spoofax.tego.ir

import org.spoofax.tego.InvalidFormatException
import org.spoofax.tego.aterm.*

/**
 * Builds the Intermediate Representation of an expression.
 */
class IrBuilder {

    /**
     * Compiles an expression term into an IR expression.
     */
    fun toExp(exp: Term): Exp {
        require(exp is ApplTerm) { "Expected constructor application term, got: $exp"}

        val type = typeOf(exp)
        return when (exp.constructor) {
            "Var" -> Var(exp[0].toJavaString(), type)
            "Int" -> IntLit(exp[0].toJavaInt(), type)
            "String" -> StringLit(exp[0].toJavaString(), type)
            "Object" -> AnyInst(type)
            "Id" -> TODO()  // Should be desugared into a val `id`
            "Apply" -> Apply(toExp(exp[0]), exp[1].toList().map { toExp(it) }, type)
            "Eval" -> Eval(toExp(exp[0]), toExp(exp[1]), type)
            "Build" -> TODO()   // Should be desugared into an eval `<build> v`?
            "Seq" -> Seq(toExp(exp[0]), toExp(exp[1]), type)
            else -> TODO("Unsupported expression: $exp")
        }
    }

    private fun typeOf(t: Term): Type {
        return toType(t.annotations["OfType", 1])
    }

    /**
     * Compiles a type term into an IR type.
     */
    fun toType(type: Term?): Type {
        requireNotNull(type) { "Expected a type, not nothing." }
        require(type is ApplTerm) { "Expected constructor application term, got: $type"}

        return when (type.constructor) {
            "STRATEGY" -> StrategyType(toType(type[0]), toType(type[1]))
            "FUNCTION" -> FunctionType(type[0].toList().map { toType(it) }, toType(type[1]))
            "CLASS" -> ClassType(type[0].toJavaString())
            "LIST" -> ListType(toType(type[0]))

            "BYTE" -> ByteType
            "SHORT" -> ShortType
            "INT" -> IntType
            "LONG" -> LongType
            "UBYTE" -> UByteType
            "USHORT" -> UShortType
            "UINT" -> UIntType
            "ULONG" -> ULongType

            "ANY" -> AnyType
            "STRING" -> StringType

            else -> TODO("Unsupported type: $type")
        }
    }
}
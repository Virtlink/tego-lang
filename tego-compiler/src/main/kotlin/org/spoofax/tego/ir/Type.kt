package org.spoofax.tego.ir

/**
 * A type.
 */
interface Type

object IntType : Type
object StringType : Type
object AnyType : Type

data class FunctionType(
    val paramTypes: List<Type>,
    val returnType: Type,
) : Type

data class StrategyType(
    val inputType: Type,
    val outputType: Type,
) : Type
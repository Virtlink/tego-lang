package org.spoofax.tego.ir

/**
 * A type.
 */
interface Type

object ByteType : Type
object ShortType : Type
object IntType : Type
object LongType : Type
object UByteType : Type
object UShortType : Type
object UIntType : Type
object ULongType : Type

object AnyType : Type
object StringType : Type

data class FunctionType(
    val paramTypes: List<Type>,
    val returnType: Type,
) : Type

data class StrategyType(
    val inputType: Type,
    val outputType: Type,
) : Type

data class ClassType(
    val name: String
) : Type

data class ListType(
    val elementsType: Type
) : Type
package org.spoofax.tego.ir

/**
 * A type.
 */
sealed interface Type

/**
 * A type reference.
 */
sealed interface TypeRef : Type {
//    /** The referenced name. */
//    val name: QName
}

object BoolType : Type
object CharType : Type

object ByteType : Type
object ShortType : Type
object IntType : Type
object LongType : Type

object UByteType : Type
object UShortType : Type
object UIntType : Type
object ULongType : Type

object FloatType : Type
object DoubleType : Type

object AnyType : Type
object NothingType : Type

object UnitType : Type
object StringType : Type

data class StrategyType(
    val paramTypes: List<Type>,
    val inputType: Type,
    val outputType: Type,
) : Type {
    /** The arity of the strategy, excluding the input argument. */
    val arity: Int get() = paramTypes.size

    fun apply(argTypes: List<Type>): StrategyType {
        require(argTypes.size <= paramTypes.size) { "Expected at most ${paramTypes.size} arguments in strategy application, got ${argTypes.size}." }
        val remainingParamTypes = paramTypes.drop(argTypes.size)
        return StrategyType(remainingParamTypes, inputType, outputType)
    }
    fun apply(vararg argTypes: Type): StrategyType
        = apply(argTypes.asList())
}

data class TupleType(
    val componentTypes: List<Type>
) : Type

/**
 * A reference to a class.
 *
 * @property name The name of the class.
 */
data class ClassTypeRef(
    val name: String,
    override val pointer: TermIndex,
) : TypeRef, Reference

/**
 * A reference to a strategy.
 *
 * @property name The name of the strategy.
 */
data class StrategyTypeRef(
    val name: String,
    override val pointer: TermIndex,
) : TypeRef, Reference

data class ListType(
    val elementsType: Type
) : Type

data class TypeError(
    val message: String
) : Type
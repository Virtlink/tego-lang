package org.spoofax.tego.compiler

import com.virtlink.kasm.*
import com.virtlink.kasm.JvmType.Companion.parameterizedBy
import com.virtlink.tego.strategies.Strategy
import com.virtlink.tego.strategies.Strategy1
import com.virtlink.tego.strategies.Strategy2
import com.virtlink.tego.strategies.Strategy3
import org.spoofax.tego.ir.*


/**
 * Manages the JVM types corresponding to Tego types.
 */
class JvmTypeManager(
    private val symbolTable: SymbolTable
) {

    operator fun get(type: Type): JvmType = getJvmType(type)
    operator fun get(decl: TypeDecl): JvmType = getJvmType(decl)

    /**
     * Gets the JVM type for the given type specification.
     *
     * This method is thread-safe.
     *
     * @param type the type specification
     * @return the JVM type
     */
    private fun getJvmType(type: Type): JvmType = when(type) {
        // FIXME: Many of these types will be wrong as a generic type argument
        // or when used to return `null`
        is BoolType -> JvmType.Boolean
        is CharType -> JvmType.Character

        is ByteType -> JvmType.Byte
        is ShortType -> JvmType.Short
        is IntType -> JvmType.Integer
        is LongType -> JvmType.Long

        is UByteType -> JvmType.Byte
        is UShortType -> JvmType.Short
        is UIntType -> JvmType.Integer
        is ULongType -> JvmType.Long

        is FloatType -> JvmType.Float
        is DoubleType -> JvmType.Double

        is AnyType -> JvmTypes.Object
        is NothingType -> JvmTypes.Object    /* TODO: What to do here? */

        is UnitType -> JvmType.Void           /* TODO: Can also be a normal value sometimes. */
        is StringType -> JvmTypes.String

        is StrategyType -> {
            val jvmStrategyType = when (type.paramTypes.size) {
                0 -> JvmType.of(Strategy::class.java)
                1 -> JvmType.of(Strategy1::class.java)
                2 -> JvmType.of(Strategy2::class.java)
                3 -> JvmType.of(Strategy3::class.java)
                else -> TODO("Strategies with ${type.paramTypes.size} parameters are not (yet) supported.")
            }
            jvmStrategyType.parameterizedBy(
                listOf(JvmTypeArg.invariant(JvmTypeVar("CTX"))) +
                    // FIXME: Not all arguments are invariant
                    //  Find the variance in the declaration
                    type.paramTypes.map { JvmTypeArg.invariant(getJvmType(it)) } +
                    listOf(
                        JvmTypeArg.invariant(getJvmType(type.inputType)),
                        JvmTypeArg.invariant(getJvmType(type.outputType))
                    )
            )
        }

        is TupleType -> TODO("Tuple types not supported")
        is ListType -> JvmTypes.List.parameterizedBy(JvmTypeArg.invariant(getJvmType(type.elementsType)))
        is ClassTypeRef -> {
            val decl = (symbolTable[type.pointer] ?: throw NoSuchElementException("No type declaration found for reference $type")) as TypeDecl
            getJvmType(decl)
        }
        is StrategyTypeRef -> TODO("Strategy type references not supported")

//        is Type.Ref -> {
//            val decl = symbolTable[type] ?: throw NoSuchElementException("No type declaration found for reference $type")
//            getJvmType(decl)
//        }
        is TypeError -> TODO("Type errors not supported")
        else -> TODO("Unsupported type of type ${type::class.java}: $type")
    }

    /**
     * Gets the JVM type for the given type declaration.
     *
     * This method is thread-safe.
     *
     * @param decl the type declaration
     * @return the JVM type
     */
    fun getJvmType(decl: TypeDecl): JvmType = decl.getJvmType(this)

    /**
     * Gets the JVM class signature for the given type declaration.
     *
     * This method is thread-safe.
     *
     * @param decl the strategy type declaration
     * @return the JVM class signature
     */
    fun getJvmClassSignature(decl: TypeDecl): JvmClassSignature = decl.getJvmClassSignature(this)

    /**
     * Gets the JVM class signature for the `eval` method of the given strategy type.
     *
     * This method is thread-safe.
     *
     * @param type the strategy type
     * @return the JVM `eval` method signature
     */
    fun getEvalJvmMethodSignature(type: StrategyType): JvmMethodSignature {
        return JvmMethodSignature.of(getJvmType(type.outputType),
            listOf(JvmTypeVar("CTX")) + type.paramTypes.map { getJvmType(it) } + listOf(getJvmType(type.inputType))
        )
    }

    /**
     * Gets the JVM class signature for the `eval` bridge method of the given strategy type.
     *
     * This method is thread-safe.
     *
     * @param type the strategy type
     * @return the JVM `eval` bridge method signature
     */
    fun getEvalJvmBridgeMethodSignature(type: StrategyType): JvmMethodSignature {
        return JvmMethodSignature.of(getJvmType(type.outputType),
            listOf(JvmTypes.Object) + type.paramTypes.map { JvmTypes.Object } + listOf(JvmTypes.Object)
        )
    }

    /**
     * Computes the JVM type for the given strategy type declaration.
     *
     * This implementation is deterministic.
     *
     * @param decl the strategy type declaration
     * @return the JVM type
     */
    private fun computeJvmType(decl: StrategyTypeDecl): JvmType {
        TODO()
//        return JvmObject(
//            getQualifiedClassName(decl),
//            listOf(
//                JvmTypeParam.of("CTX"),
//                JvmTypeParam.of("CTX"),
//                JvmTypeParam.of("CTX"),
//            ),
//        )
    }

    /**
     * Gets the fully qualified JVM class name for the given strategy type declaration.
     *
     * This implementation is deterministic.
     *
     * @param decl the strategy type declaration
     * @return the JVM class name (with `/` as the separator)
     */
    private fun getQualifiedClassName(decl: StrategyTypeDecl): String {
        val pkg = getPackageName(decl).toString()
        val name = getClassName(decl)
        if (pkg.isEmpty()) return name
        return "$pkg/$name"
    }

    /**
     * Gets the class name for the given strategy type declaration.
     *
     * This implementation is deterministic.
     *
     * @param decl the strategy type declaration
     * @return the class name
     */
    private fun getClassName(decl: StrategyTypeDecl): String {
        if (decl.name != null) {
            // Generate name for named strategy
            // TODO: Perhaps rename something like foo_bar to FooBarStrategy?
            //  What about FooBar, foo-bar, XMLBar?
            return decl.name.simpleName + "Strategy"
        } else {
            // Generate name for anonymous strategy
            // (ensure this name is deterministic, somehow)
            TODO("Anonymous strategies are not yet supported.")
        }
    }

    /**
     * Gets the dot-separated package name for the given strategy type declaration.
     *
     * This implementation is deterministic.
     *
     * @param decl the strategy type declaration
     * @return the package name
     */
    private fun getPackageName(decl: StrategyTypeDecl): PackageName {
        if (decl.name != null) {
            // Generate package for named strategy
            return decl.name.packageName
        } else {
            // Generate package for anonymous strategy
            // (ensure this name is deterministic, somehow)
            TODO("Anonymous strategies are not yet supported.")
        }
    }

}
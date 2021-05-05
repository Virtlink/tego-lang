package com.virtlink.kasm

import java.lang.reflect.Constructor
import java.lang.reflect.Method

/**
 * A method type.
 *
 * @property result The result.
 * @property parameters The parameters.
 * @property typeParameters The generic type parameters.
 * @property throwables The checked exceptions.
 */
data class JvmMethodSignature private constructor(
    val result: JvmType,
    val parameters: List<JvmType>,
    val typeParameters: List<JvmTypeParam>,
    val throwables: List<JvmType>,
) {

    companion object {
        fun of(constructor: Constructor<*>): JvmMethodSignature {
            val parameters = constructor.genericParameterTypes.map { JvmType.of(it) }
            val typeParameters = constructor.typeParameters.map { JvmTypeParam.of(it) }
            val throwables = constructor.genericExceptionTypes.map { JvmType.of(it) }
            return of(JvmType.Void, parameters, typeParameters, throwables)
        }

        fun of(method: Method): JvmMethodSignature {
            val result = JvmType.of(method.returnType)
            val parameters = method.genericParameterTypes.map { JvmType.of(it) }
            val typeParameters = method.typeParameters.map { JvmTypeParam.of(it) }
            val throwables = method.genericExceptionTypes.map { JvmType.of(it) }
            return of(result, parameters, typeParameters, throwables)
        }

        fun of(result: JvmType, parameters: List<JvmType>, typeParameters: List<JvmTypeParam> = emptyList(), throwables: List<JvmType> = emptyList()): JvmMethodSignature {
            return JvmMethodSignature(result, parameters, typeParameters, throwables)
        }

//        fun of(returnType: JvmType, vararg parameterTypes: JvmType): JvmMethodSignature
//                = of(returnType, parameterTypes.toList())
    }

    val signature: String get() = StringBuilder().apply {
        if (typeParameters.isNotEmpty()) {
            append('<')
            for (typeParam in typeParameters) {
                append(typeParam.signature)
            }
            append('>')
        }
        append('(')
        for (parameter in parameters) {
            append(parameter.signature)
        }
        append(')')
        append(result.signature)
        for (throwable in throwables) {
            append('^')
            append(throwable.signature)
        }
    }.toString()

    val descriptor: String get() = StringBuilder().apply {
        append('(')
        for (parameter in parameters) {
            append(parameter.descriptor)
        }
        append(')')
        append(result.descriptor)
    }.toString()

    override fun toString(): String = StringBuilder().apply {
        if (typeParameters.isNotEmpty()) {
            append('<')
            append(typeParameters.first())
            for (typeParam in typeParameters.drop(1)) {
                append(',')
                append(typeParam)
            }
            append('>')
        }
        append('(')
        if (parameters.isNotEmpty()) {
            append(parameters.first())
            for (parameter in parameters.drop(1)) {
                append(',')
                append(parameter)
            }
        }
        append(')')
        append(" ")
        append(result)
        if (throwables.isNotEmpty()) {
            append(" throws ")
            append(throwables.first())
            for (throwable in throwables.drop(1)) {
                append(',')
                append(throwable)
            }
        }
    }.toString()

}
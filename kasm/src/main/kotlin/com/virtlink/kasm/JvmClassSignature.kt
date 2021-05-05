package com.virtlink.kasm

/**
 * A class signature.
 *
 * The signature describes the super class, super interfaces, and any type parameters.
 */
data class JvmClassSignature private constructor(
    val superClass: JvmType,
    val superInterfaces: List<JvmType>,
    val typeParameters: List<JvmTypeParam>,
) {

    companion object {
        fun of(
            superClass: JvmType,
            superInterfaces: List<JvmType> = listOf(),
            typeParameters: List<JvmTypeParam> = listOf(),
        ): JvmClassSignature {
            return JvmClassSignature(superClass, superInterfaces, typeParameters)
        }

        fun of(
            superClass: JvmType,
            vararg superInterfaces: JvmType
        ): JvmClassSignature = of(superClass, superInterfaces.toList())

        fun of(type: Class<*>): JvmClassSignature {
            val superClass = JvmType.of(type.genericSuperclass)
            val superInterfaces = type.genericInterfaces.map { JvmType.of(it) }
            val typeParameters = type.typeParameters.map { JvmTypeParam.of(it) }
            return of(superClass, superInterfaces, typeParameters)
        }
    }

    val signature: String get() = StringBuilder().apply {
        if (typeParameters.isNotEmpty()) {
            append('<')
            for (typeParam in typeParameters) {
                append(typeParam.signature)
            }
            append('>')
        }
        append(superClass.signature)
        for (superInterface in superInterfaces) {
            append(superInterface.signature)
        }
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
        append(" extends ")
        append(superClass)
        if (superInterfaces.isNotEmpty()) {
            append(" implements ")
            append(superInterfaces.first())
            for (superInterface in superInterfaces.drop(1)) {
                append(',')
                append(superInterface)
            }
        }
    }.toString()

}


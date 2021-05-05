package com.virtlink.kasm

/**
 * A field signature.
 *
 * @property type The field's type.
 */
data class JvmFieldSignature private constructor(
    val type: JvmType,
) {

    companion object {
        fun of(type: JvmType): JvmFieldSignature {
            return JvmFieldSignature(type)
        }
    }

    val signature: String get() = StringBuilder().apply {
        append(type.signature)
    }.toString()

    val descriptor: String get() = StringBuilder().apply {
        append(type.descriptor)
    }.toString()

    override fun toString(): String = StringBuilder().apply {
        append(type)
    }.toString()

}
package com.virtlink.kasm

/**
 * A method reference.
 */
data class MethodRef(
    val owner: JvmType,
    val name: String,
    val signature: JvmMethodSignature,
) {
    init {
        require(owner.sort == JvmTypeSort.Object) { "Method's owner must be an object, got: $owner" }
    }

    override fun toString(): String {
        return "${owner}::$name(${(signature.parameters.joinToString(", "))} ${signature.result}"
    }
}
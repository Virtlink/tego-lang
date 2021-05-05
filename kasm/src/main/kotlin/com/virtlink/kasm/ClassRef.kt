package com.virtlink.kasm

/**
 * A class reference.
 */
data class ClassRef(
    val type: JvmType,
) {
    init {
        require(type.sort == JvmTypeSort.Object) { "Type must be an object, got: $type" }
    }

    val name: String get() = type.toString()

    override fun toString(): String {
        return type.toString()
    }
}
package com.virtlink.kasm

/**
 * A local variable in a method.
 *
 * @param name the name of the local variable; or `null` when not specified
 * @param type the type of the local variable
 * @param scope the scope of the local variable
 * @param index the zero-based index of the local variable
 */
data class LocalVar(
    val name: String?,
    val type: JvmType,
    val scope: Scope,
    val index: Int,
)
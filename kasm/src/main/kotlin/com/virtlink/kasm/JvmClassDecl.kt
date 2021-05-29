package com.virtlink.kasm

/**
 * Describes everything needed for a JVM type declaration.
 *
 * This describes the type, super classes, super interfaces,
 * type parameters, methods, and fields of a JVM type.
 *
 * @property name The fully-qualified name of the JVM type, with forward slashes as separators.
 * @property superClass The superclass of the JVM type.
 * @property superInterfaces The super interfaces of the JVM type.
 */
data class JvmClassDecl(
    val name: String,
    val superClass: JvmType,
    val superInterfaces: List<JvmType>,

)


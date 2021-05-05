package com.virtlink.kasm

import org.objectweb.asm.Opcodes
import java.util.*

/**
 * Modifiers for fields.
 */
enum class FieldModifier(
    val value: Int
) {
    /** The field is accessible outside its package. */
    Public(Opcodes.ACC_PUBLIC),
    /** The field is accessible only within its defining class and other classes belonging to the same nest. */
    Private(Opcodes.ACC_PRIVATE),
    /** The field is accessible to its subclasses. */
    Protected(Opcodes.ACC_PROTECTED),
    /** The field is static. */
    Static(Opcodes.ACC_STATIC),
    /** The field cannot be assigned after its construction. */
    Final(Opcodes.ACC_FINAL),
    /** The field cannot be cached. */
    Volatile(Opcodes.ACC_VOLATILE),
    /** The field is ignored by serialization. */
    Transient(Opcodes.ACC_TRANSIENT),
    /** The field is not explicitly declared in the source code. */
    Synthetic(Opcodes.ACC_SYNTHETIC),
    /** The field is an element of an enum type. */
    Enum(Opcodes.ACC_ENUM),
    /** The field was not explicitly declared in the source code but implicitly mandated by the specification. */
    Mandated(Opcodes.ACC_MANDATED),
    /** The field is deprecated. ASM-specific. */
    Deprecated(Opcodes.ACC_DEPRECATED);

    infix fun or(other: FieldModifier): FieldModifiers = FieldModifiers.of(this, other)
}

typealias FieldModifiers = EnumSet<FieldModifier>

infix fun FieldModifiers.or(other: FieldModifier): FieldModifiers = FieldModifiers.of(other, *this.toTypedArray())

fun FieldModifiers.toInt(): Int {
    return this.fold(0) { acc, m -> acc or m.value }
}
package com.virtlink.kasm

import org.objectweb.asm.Opcodes
import java.util.*

/**
 * Modifiers for classes.
 */
enum class ClassModifier(
    val value: Int
) {
    /** The class is accessible outside its package. */
    Public(Opcodes.ACC_PUBLIC),
    // Unused?
    Private(Opcodes.ACC_PRIVATE),
    // Unused?
    Protected(Opcodes.ACC_PROTECTED),
    /** The class cannot be subclassed. */
    Final(Opcodes.ACC_FINAL),
    /** Treat superclass methods special when invoked with the `invokespecial` instruction. */
    Super(Opcodes.ACC_SUPER),
    /** The class is an interface, the class is not a class. */
    Interface(Opcodes.ACC_INTERFACE),
    /** The class cannot be instantiated. */
    Abstract(Opcodes.ACC_ABSTRACT),
    /** The class is not explicitly declared in the source code. */
    Synthetic(Opcodes.ACC_SYNTHETIC),
    /** The class is an annotation type. */
    Annotation(Opcodes.ACC_ANNOTATION),
    /** The class is an enum type. */
    Enum(Opcodes.ACC_ENUM),
    /** The class is a record type. */
    Record(Opcodes.ACC_RECORD),
    /** The class is deprecated. ASM-specific. */
    Deprecated(Opcodes.ACC_DEPRECATED);

    infix fun or(other: ClassModifier): ClassModifiers = ClassModifiers.of(this, other)
}

typealias ClassModifiers = EnumSet<ClassModifier>

infix fun ClassModifiers.or(other: ClassModifier): ClassModifiers = ClassModifiers.of(other, *this.toTypedArray())

fun EnumSet<ClassModifier>.toInt(): Int {
    return this.fold(0) { acc, m -> acc or m.value }
}
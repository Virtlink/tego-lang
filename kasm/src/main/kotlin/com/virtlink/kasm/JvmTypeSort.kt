package com.virtlink.kasm

/**
 * Specifies the sort of JVM type.
 */
enum class JvmTypeSort {
    /** Void type. */
    Void,
    /** Boolean type. */
    Boolean,
    /** Char type. */
    Character,
    /** Byte type. */
    Byte,
    /** Short type. */
    Short,
    /** Int type. */
    Integer,
    /** Long type. */
    Long,
    /** Float type. */
    Float,
    /** Double type. */
    Double,
    /** Array type. */
    Array,
    /** Object type. */
    Object,
    /** Type parameter. */
    TypeParam,
    /** Type argument. */
    TypeArg,
}
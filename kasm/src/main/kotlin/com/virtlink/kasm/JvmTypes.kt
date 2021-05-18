package com.virtlink.kasm

import java.lang.NullPointerException

object JvmTypes {
    val Object: JvmType = JvmType.of(java.lang.Object::class.java)
    val Class: JvmType = JvmType.of(java.lang.Class::class.java)
    val String: JvmType = JvmType.of(java.lang.String::class.java)
    val Nullable: JvmType = JvmType.of("org.jetbrains.annotations.Nullable")
    val NotNull: JvmType = JvmType.of("org.jetbrains.annotations.NotNull")
    val AssertionError: JvmType = JvmType.of(java.lang.AssertionError::class.java)
    val NullPointerException: JvmType = JvmType.of(java.lang.NullPointerException::class.java)

    val List: JvmType = JvmType.of(java.util.List::class.java)
}
package com.virtlink.kasm

import org.objectweb.asm.Opcodes

/**
 * Specifies the class version.
 */
@Suppress("unused")
enum class ClassVersion(
    val value: Int
) {
    V1(Opcodes.V1_1),
    V2(Opcodes.V1_2),
    V3(Opcodes.V1_3),
    V4(Opcodes.V1_4),
    V5(Opcodes.V1_5),
    V6(Opcodes.V1_6),
    V7(Opcodes.V1_7),
    V8(Opcodes.V1_8),
    V9(Opcodes.V9),
    V10(Opcodes.V10),
    V11(Opcodes.V11),
    V12(Opcodes.V12),
    V13(Opcodes.V13),
    V14(Opcodes.V14),
    V15(Opcodes.V15),
}
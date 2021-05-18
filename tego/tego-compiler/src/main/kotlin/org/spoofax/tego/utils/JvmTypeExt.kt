package org.spoofax.tego.utils

import com.virtlink.kasm.JvmType
import org.spoofax.tego.ir.QName

/**
 * Gets a JVM type from a qualified name
 *
 * @param name the qualified name
 * @return the JVM type
 */
fun JvmType.Companion.of(name: QName): JvmType {
    return JvmType.of(name.toJvmClassName())
}
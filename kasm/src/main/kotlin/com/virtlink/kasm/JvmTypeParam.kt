package com.virtlink.kasm

import java.lang.reflect.TypeVariable

/**
 * A generic type parameter.
 *
 * @property identifier The parameter's identifier.
 * @property classBound The class upper bound; or `null` when not specified.
 * @property interfaceBounds The interface upper bounds; or an empty list when not specified.
 */
data class JvmTypeParam private constructor(
    val identifier: String,
    val classBound: JvmType?,
    val interfaceBounds: List<JvmType>
) {

    companion object {
        fun of(
            identifier: String,
            classBound: JvmType? = null,
            interfaceBounds: List<JvmType> = emptyList()
        ): JvmTypeParam {
            return JvmTypeParam(identifier, classBound, interfaceBounds)
        }

        fun of(
            identifier: String,
            classBound: JvmType,
            vararg interfaceBounds: JvmType
        ): JvmTypeParam {
            return JvmTypeParam(identifier, classBound, interfaceBounds.toList())
        }

        fun of(
            typeVar: TypeVariable<*>
        ): JvmTypeParam {
            val identifier = typeVar.name

            // Assume that if any bounds are given, the first bound is a class bound. FIXME: Not sure if this is the case
            val classBound: JvmType?
            val interfaceBounds: List<JvmType>
            if (typeVar.bounds.isNotEmpty()) {
                classBound = JvmType.of(typeVar.bounds[0])
                interfaceBounds = typeVar.bounds.drop(1).map { JvmType.of(it) }
            } else {
                classBound = null
                interfaceBounds = emptyList()
            }

            return of(identifier, classBound, interfaceBounds)
        }
    }

    val signature: String get() = StringBuilder().apply {
        append(identifier)
        append(':')
        if (classBound != null) append(classBound.signature)
        for (interfaceBound in interfaceBounds) {
            append(':')
            append(interfaceBound.signature)
        }
    }.toString()

    override fun toString(): String = StringBuilder().apply {
        append(identifier)
        if (classBound != null) {
            append(" extends ")
            append(classBound)
        }
        if (interfaceBounds.isNotEmpty()) {
            append(" implements ")
            append(interfaceBounds.first())
            for (interfaceBound in interfaceBounds.drop(1)) {
                append(',')
                append(interfaceBound)
            }
        }
    }.toString()
}
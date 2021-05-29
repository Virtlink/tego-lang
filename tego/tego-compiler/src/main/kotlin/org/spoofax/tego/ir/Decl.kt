package org.spoofax.tego.ir

/**
 * An IR declaration or definition.
 */
sealed interface Decl {

}

sealed interface Def : Decl

data class ParamDef(
    val name: String,
    val type: Type?
)

/**
 * The target of a reference.
 */
sealed interface Declaration {
    /** Objects that are used as the pointers for this declaration; or an empty list when there are none. */
    val pointers: List<TermIndex>
}

/**
 * The source of a reference.
 */
sealed interface Reference {
    /** Object that is used as the pointer for this reference. */
    val pointer: TermIndex
}
package org.spoofax.tego.ir

/**
 * An IR declaration or definition.
 */
sealed interface Decl {

}

sealed interface Def : Decl {
    var module: Module?
}

data class ParamDef(
    val name: String,
    val type: Type?
)

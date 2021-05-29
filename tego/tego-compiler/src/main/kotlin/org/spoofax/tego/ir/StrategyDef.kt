package org.spoofax.tego.ir

/**
 * A strategy definition.
 */
data class StrategyDef(
    val name: String,
    val params: List<ParamDef>,
    val inputName: String,
    val body: Exp,
    override val pointer: Any
) : Def, Reference {
//    var decl: StrategyTypeDecl? = null
}
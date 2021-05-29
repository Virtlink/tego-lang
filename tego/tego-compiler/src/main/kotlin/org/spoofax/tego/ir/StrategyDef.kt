package org.spoofax.tego.ir

/**
 * A strategy definition.
 */
data class StrategyDef(
    val name: QName,
    val params: List<ParamDef>,
    val inputName: String,
    val body: Exp,
) : Def {
//    var decl: StrategyTypeDecl? = null
}
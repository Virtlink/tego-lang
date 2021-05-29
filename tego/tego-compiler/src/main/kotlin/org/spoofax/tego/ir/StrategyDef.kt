package org.spoofax.tego.ir

/**
 * A strategy definition.
 */
data class StrategyDef(
    val simpleName: String,
    val params: List<ParamDef>,
    val inputName: String,
    val body: Exp,
    override val pointer: TermIndex
) : Def, Reference {

    override var module: Module? = null

}
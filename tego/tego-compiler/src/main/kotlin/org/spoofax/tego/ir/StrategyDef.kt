package org.spoofax.tego.ir

/**
 * A strategy definition.
 */
data class StrategyDef(
    val name: QName,
    val paramNames: List<String>,
    val inputName: String,
    val body: Exp,
) : Def
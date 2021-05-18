package org.spoofax.tego.ir

/**
 * A Tego project.
 */
data class Project(
    val modules: List<Module>,
)

/**
 * A Tego module.
 */
data class Module(
    val name: QName,
    val declarations: List<TypeDecl>,
    val definitions: List<Def>,
)
package org.spoofax.tego.ir

import java.util.*

/** A Tego project. */
data class Project(
    val files: List<File>,
)

/** A Tego file. */
data class File(
    val modules: List<Module>,
)

/** A Tego module. */
data class Module(
    val name: PackageName,
    val declarations: List<TypeDecl>,
    val definitions: List<Def>,
    val modifiers: ModuleModifiers,
)

typealias ModuleModifiers = EnumSet<ModuleModifier>

/**
 * Specifies a module modifier.
 */
enum class ModuleModifier {
    Extern
}

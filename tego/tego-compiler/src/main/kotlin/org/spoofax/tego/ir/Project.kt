package org.spoofax.tego.ir

import java.util.*

/** A Tego project. */
class Project {
    val files: MutableList<File> = Children(this, { it.project }, { it, owner -> it.project = owner })
}

/** A Tego file. */
class File {

    var project: Project? = null
    val modules: MutableList<Module> = Children(this, { it.file }, { it, owner -> it.file = owner })

}

/** A Tego module. */
data class Module(
    val name: PackageName,
    val modifiers: ModuleModifiers,
    override val pointers: List<TermIndex>,
) : Declaration {

    var file: File? = null
    val declarations: MutableList<TypeDecl> = Children(this, { it.module }, { it, owner -> it.module = owner })
    val definitions: MutableList<Def> = Children(this, { it.module }, { it, owner -> it.module = owner })

}

typealias ModuleModifiers = EnumSet<ModuleModifier>

/**
 * Specifies a module modifier.
 */
enum class ModuleModifier {
    Extern
}

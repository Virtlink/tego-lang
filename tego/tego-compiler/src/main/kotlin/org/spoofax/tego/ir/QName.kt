package org.spoofax.tego.ir

/**
 * A fully qualified name.
 *
 * @property packageName The package name, or an empty string for the root package.
 * @property simpleName The simple name.
 */
data class QName(
    val packageName: PackageName,
    val simpleName: String,
) {

    init {
        require(simpleName.matches(Regex("^[a-zA-Z_$][a-zA-Z0-9_$]*$"))) { "Simple name is illegal: $simpleName" }
    }

    override fun toString(): String = "$packageName::$simpleName"
}

/**
 * A forward-slash separated package name.
 *
 * For example, `java.util` becomes `java/util`.
 */
@JvmInline
value class PackageName(val name: String) {
    init {
        require(name.matches(Regex("^[a-z_$][a-z0-9_$]*(/[a-z_\$][a-z0-9_\$]*)*$"))) { "Package name is illegal: $name" }
    }
}
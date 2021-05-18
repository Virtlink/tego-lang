package org.spoofax.tego.ir

/**
 * A fully qualified name.
 *
 * @property packageName The package name, or an empty string for the root package.
 * @property simpleName The simple name.
 */
data class QName(
    val packageName: String,
    val simpleName: String,
) {

    init {
        require(packageName.matches(Regex("^[a-z_$][a-z0-9_$]*(\\.[a-z_\$][a-z0-9_\$]*)*$"))) { "Package name is illegal: $packageName" }
        require(simpleName.matches(Regex("^[a-zA-Z_$][a-zA-Z0-9_$]*$"))) { "Simple name is illegal: $simpleName" }
    }

    /**
     * Gets the JVM class name for this class.
     */
    fun toJvmClassName(): String = "$packageName.$simpleName"

    override fun toString(): String = "$packageName::$simpleName"
}

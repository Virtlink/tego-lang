package org.spoofax.tego.ir

/**
 * A type declaration.
 */
sealed interface TypeDecl : Decl {

    /** The fully-qualified name of the declaration. */
    val name: QName

}

/**
 * A strategy declaration.
 *
 * @property name The name of the strategy.
 * @property type The type of the strategy.
 */
data class StrategyTypeDecl(
    override val name: QName,
    val type: StrategyType,
) : TypeDecl {

    override fun toString(): String  = StringBuilder().apply {
        append(name)
        append(": ")
        append(type)
    }.toString()

}

/**
 * A class declaration.
 *
 * @property name The fully-qualified name of the class.
 * @property superClass The super class of the class; or [Type.Any] by default.
 * @property superInterfaces The super interfaces of the class.
 * @property typeParameters The type parameters of the class.
 */
data class ClassDecl(
    override val name: QName,
    val superClass: Type = AnyType,
    val superInterfaces: List<Type> = emptyList(),
) : TypeDecl {

    init {
        require(superClass is TypeRef || superClass === AnyType) { "Only a type reference or 'Any' is allowed as a superclass." }
        require(superInterfaces.all { it is TypeRef }) { "Only type references are allowed as superinterfaces."}
    }

}

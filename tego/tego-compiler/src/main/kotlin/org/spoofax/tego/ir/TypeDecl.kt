package org.spoofax.tego.ir

import java.util.*

/**
 * A type declaration.
 */
sealed interface TypeDecl : Decl {

    /** The fully-qualified name of the declaration. */
    val name: QName
    /** The modifiers of the declaration. */
    val modifiers: TypeModifiers

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
    override val modifiers: TypeModifiers,
) : TypeDecl {

    override fun toString(): String  = StringBuilder().apply {
        append(name)
        append(": ")
        append(type)
    }.toString()

}

/**
 * A class type declaration.
 *
 * @property name The fully-qualified name of the class.
 */
data class ClassTypeDecl(
    override val name: QName,
    override val modifiers: TypeModifiers,
) : TypeDecl

typealias TypeModifiers = EnumSet<TypeModifier>

/**
 * Specifies a type modifier.
 */
enum class TypeModifier {
    Public,
    Extern
}


///**
// * A class type declaration.
// *
// * @property name The fully-qualified name of the class.
// * @property superClass The super class of the class; or [Type.Any] by default.
// * @property superInterfaces The super interfaces of the class.
// */
//data class ClassTypeDecl(
//    override val name: QName,
//    val superClass: Type = AnyType,
//    val superInterfaces: List<Type> = emptyList(),
//) : TypeDecl {
//
//    init {
//        require(superClass is TypeRef || superClass === AnyType) { "Only a type reference or 'Any' is allowed as a superclass." }
//        require(superInterfaces.all { it is TypeRef }) { "Only type references are allowed as superinterfaces."}
//    }
//
//}

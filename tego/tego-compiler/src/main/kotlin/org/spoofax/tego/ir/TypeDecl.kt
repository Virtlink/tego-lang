package org.spoofax.tego.ir

import com.virtlink.kasm.JvmType
import org.spoofax.tego.compiler.JvmTypeManager
import java.util.*

/**
 * A type declaration.
 */
sealed interface TypeDecl : Decl, Declaration {

    /** The fully-qualified name of the declaration. */
    val name: QName
    /** The modifiers of the declaration. */
    val modifiers: TypeModifiers

    /** The module that contains the declaration. */
    var module: Module?

    /**
     * Gets the JVM type for this declaration.
     */
    fun toJvmType(types: JvmTypeManager): JvmType
}

/**
 * A strategy declaration.
 *
 * @property name The name of the strategy.
 * @property type The type of the strategy.
 */
data class StrategyTypeDecl(
    val simpleName: String,
    val type: StrategyType,
    override val modifiers: TypeModifiers,
    override val pointers: List<TermIndex>,
) : TypeDecl, Declaration {

    override fun toString(): String  = StringBuilder().apply {
        append(name)
        append(": ")
        append(type)
    }.toString()

    override var module: Module? = null

    override val name: QName get() = QName(module?.name!!, this.simpleName)

    override fun toJvmType(types: JvmTypeManager): JvmType {
        return JvmType.fromSignature("L${name.packageName}/${getJvmClassName()};")
    }

    /**
     * Gets the JVM class name of this type.
     */
    private fun getJvmClassName(): String {
        // TODO: Make this smarter about names with underscores, dashes, and special characters
        return name.simpleName.capitalize() + "Strategy"
    }

}

/**
 * A class type declaration.
 *
 * @property name The fully-qualified name of the class.
 */
data class ClassTypeDecl(
    val simpleName: String,
    override val modifiers: TypeModifiers,
    override val pointers: List<TermIndex>,
) : TypeDecl, Declaration {

    override var module: Module? = null

    override val name: QName get() = QName(module?.name!!, this.simpleName)

    override fun toJvmType(types: JvmTypeManager): JvmType {
        return JvmType.fromSignature("L${name.packageName}/${name.simpleName};")
    }

}

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

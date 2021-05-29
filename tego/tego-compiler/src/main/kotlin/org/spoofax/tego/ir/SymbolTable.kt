package org.spoofax.tego.ir

import org.spoofax.tego.utils.putIfAbsentOrThrow

/**
 * Maintains all symbols in a project.
 */
interface SymbolTable {

    /**
     * Resolves a type to its declaration.
     *
     * @param name the name to resolve
     * @return the type declaration; or `null` if not found
     */
    operator fun get(name: QName): TypeDecl? = resolve(name)

//    /**
//     * Resolves a type to its declaration.
//     *
//     * @param type the type to resolve
//     * @return the type declaration; or `null` when not found
//     */
//    operator fun get(type: TypeRef): TypeDecl? = resolve(type)

    /**
     * Resolves a qualified name to its declaration.
     *
     * @param name the name to resolve
     * @return the type declaration; or `null` when not found
     */
    fun resolve(name: QName): TypeDecl?

//    /**
//     * Resolves a type to its declaration.
//     *
//     * @param type the type to resolve
//     * @return the type declaration; or `null` when not found
//     */
//    fun resolve(type: TypeRef): TypeDecl?
//
//    /**
//     * Resolves a strategy type to its declaration.
//     *
//     * @param type the strategy type to resolve
//     * @return the strategy type declaration; or `null` when not found
//     */
//    fun resolve(type: ClassTypeRef): ClassDecl?
//
//    /**
//     * Resolves a strategy type to its declaration.
//     *
//     * @param type the strategy type to resolve
//     * @return the strategy type declaration; or `null` when not found
//     */
//    fun resolve(type: StrategyTypeRef): StrategyTypeDecl?

}


/**
 * Maintains all symbols in a project.
 */
interface MutableSymbolTable : SymbolTable {

    /**
     * Adds the given type declaration.
     *
     * @param decl the type declaration to add
     */
    fun add(decl: TypeDecl)

    /**
     * Adds the given strategy type declarations.
     *
     * @param decls the strategy type declarations to add
     */
    fun addAll(vararg decls: TypeDecl)
            = addAll(decls.asList())

    /**
     * Adds the given strategy type declarations.
     *
     * @param decls the strategy type declarations to add
     */
    fun addAll(decls: Iterable<TypeDecl>) {
        for (decl in decls) {
            add(decl)
        }
    }

}



/**
 * Maintains all symbols in a project.
 */
class SymbolTableImpl : MutableSymbolTable {

    private val decls: MutableMap<QName, TypeDecl> = mutableMapOf()

    override fun resolve(name: QName): TypeDecl? {
        return decls[name]
    }

//    private val classes: MutableMap<QName, ClassDecl> = mutableMapOf()
//    private val strategies: MutableMap<QName, StrategyTypeDecl> = mutableMapOf()

//    override fun resolve(type: TypeRef): TypeDecl? = when (type) {
//        is ClassTypeRef -> resolve(type)
//        is StrategyTypeRef -> resolve(type)
//        else -> TODO("Unsupported type ${type::class.java}: $type")
//    }
//
//    override fun resolve(type: ClassTypeRef): ClassDecl? {
//        return this.classes[type.name]
//    }
//
//    override fun resolve(type: StrategyTypeRef): StrategyTypeDecl? {
//        return this.strategies[type.name]
//    }

    override fun add(decl: TypeDecl) { //= when(decl) {
        this.decls.putIfAbsentOrThrow(decl.name, decl)
//        is ClassDecl -> this.classes.putIfAbsentOrThrow(decl.name, decl)
//        is StrategyTypeDecl -> this.strategies.putIfAbsentOrThrow(decl.name, decl)
//        else -> TODO("Unsupported declaration type ${decl::class.java}: $decl")
    }

}

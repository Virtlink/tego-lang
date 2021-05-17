package org.spoofax.tego.ir

/**
 * Maintains all symbols in a project.
 */
class SymbolTable {

    private val classes: MutableMap<QName, ClassDecl> = mutableMapOf()
    private val strategies: MutableMap<QName, StrategyTypeDecl> = mutableMapOf()

    /**
     * Resolves a type to its declaration.
     *
     * @param type the type to resolve
     * @return the type declaration; or `null` when not found
     */
    operator fun get(type: TypeRef): TypeDecl? = resolve(type)

    /**
     * Resolves a type to its declaration.
     *
     * @param type the type to resolve
     * @return the type declaration; or `null` when not found
     */
    fun resolve(type: TypeRef): TypeDecl? = when (type) {
        is ClassTypeRef -> resolve(type)
        is StrategyTypeRef -> resolve(type)
        else -> TODO("Unsupported type ${type::class.java}: $type")
    }

    /**
     * Resolves a strategy type to its declaration.
     *
     * @param type the strategy type to resolve
     * @return the strategy type declaration; or `null` when not found
     */
    fun resolve(type: ClassTypeRef): ClassDecl? {
        return this.classes[type.name]
    }

    /**
     * Resolves a strategy type to its declaration.
     *
     * @param type the strategy type to resolve
     * @return the strategy type declaration; or `null` when not found
     */
    fun resolve(type: StrategyTypeRef): StrategyTypeDecl? {
        return this.strategies[type.name]
    }

    /**
     * Adds the given type declaration.
     *
     * @param decl the type declaration to add
     */
    fun add(decl: TypeDecl) = when(decl) {
        is ClassDecl -> this.classes[decl.name] = decl
        is StrategyTypeDecl -> this.strategies[decl.name] = decl
        else -> TODO("Unsupported declaration type ${decl::class.java}: $decl")
    }

    /**
     * Adds the given strategy type declarations.
     *
     * @param decls the strategy type declarations to add
     */
    fun addAll(vararg decls: TypeDecl)
        = addAll(decls.toList())

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
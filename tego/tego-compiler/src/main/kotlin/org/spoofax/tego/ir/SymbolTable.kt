package org.spoofax.tego.ir

import org.spoofax.tego.utils.putIfAbsentOrThrow

/**
 * Maintains all symbols in a project.
 */
interface SymbolTable {

    /**
     * Resolves a pointer to a declaration.
     *
     * @param ptr the pointer to resolve
     * @return the declaration; or `null` if not found
     */
    operator fun get(ptr: TermIndex): Declaration? = resolve(ptr)

    /**
     * Resolves a pointer to its declaration.
     *
     * @param ptr the pointer to resolve
     * @return the declaration; or `null` when not found
     */
    fun resolve(ptr: TermIndex): Declaration?

}


/**
 * Maintains all symbols in a project.
 */
interface MutableSymbolTable : SymbolTable {

    /**
     * Adds the given declaration.
     *
     * @param decl the declaration to add
     */
    fun add(decl: Declaration)

    /**
     * Adds the given strategy declarations.
     *
     * @param decls the strategy declarations to add
     */
    fun addAll(vararg decls: Declaration)
            = addAll(decls.asList())

    /**
     * Adds the given strategy declarations.
     *
     * @param decls the strategy declarations to add
     */
    fun addAll(decls: Iterable<Declaration>) {
        for (decl in decls) {
            add(decl)
        }
    }

}



/**
 * Maintains all symbols in a project.
 */
class SymbolTableImpl : MutableSymbolTable {

    private val decls: MutableMap<TermIndex, Declaration> = mutableMapOf()

    override fun resolve(ptr: TermIndex): Declaration? {
        return decls[ptr]
    }

    override fun add(decl: Declaration) {
        for (ptr in decl.pointers) {
            this.decls.putIfAbsentOrThrow(ptr, decl)
        }
    }

}

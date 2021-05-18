package org.spoofax.tego.compiler

import com.virtlink.kasm.JvmClass
import org.spoofax.tego.aterm.Term
import org.spoofax.tego.ir.*

/**
 * Compiles a Tego strategy to a class.
 */
class TegoCompiler(
    private val irBuilder: IrBuilder,
    private val strategyAssemblerFactory: StrategyAssembler.Factory,
    private val classWriter: TegoClassWriter,
) {

    /**
     * Compiles the project in the given term.
     */
    fun compile(term: Term) {
        val project = irBuilder.toProject(term)
        compile(project)
    }

    /**
     * Compiles the given project.
     */
    fun compile(project: Project) {
        val gatherer = Gatherer()
        for (module in project.modules) {
            for (decl in module.declarations) {
                gatherer.addDeclaration(decl)
            }
        }
        val symbolTable: SymbolTable = gatherer.symbolTable

        val compiler = Compiler(symbolTable, strategyAssemblerFactory.create(JvmTypeManager(symbolTable), symbolTable))
        for (module in project.modules) {
            for (def in module.definitions) {
                compiler.compileDefinition(def)
            }
        }
    }

    /**
     * Gathers declarations.
     */
    private inner class Gatherer(
        val symbolTable: MutableSymbolTable = SymbolTableImpl()
    ) {
        /**
         * Adds a declaration to the symbol table.
         *
         * @param decl the declaration to add
         */
        fun addDeclaration(decl: TypeDecl) {
            symbolTable.add(decl)
        }
    }

    /**
     * Compiles definitions.
     */
    private inner class Compiler(
        val symbolTable: SymbolTable,
        val strategyAssembler: StrategyAssembler,
    ) {

        /**
         * Compiles a definition.
         *
         * @param def the definition to compile
         * @return the compiled JVM class
         */
        fun compileDefinition(def: Def): JvmClass = when (def) {
            is StrategyDef -> compileDefinition(def)
            else -> TODO("Unsupported definition: $def")
        }

        /**
         * Compiles a definition.
         *
         * @param def the definition to compile
         * @return the compiled JVM class
         */
        fun compileDefinition(def: StrategyDef): JvmClass {
            val decl = symbolTable[def.name] as StrategyTypeDecl? ?: throw IllegalStateException("No declaration found for definition: ${def.name}")
            require(def.body.isAnf) { "The definition body must be in ANF." }

            val cls = strategyAssembler.assembleStrategy(decl, def)
            classWriter.accept(cls)
            return cls
        }
    }

}
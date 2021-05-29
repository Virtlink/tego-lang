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
     *
     * @return a list of all compiled classes
     */
    fun compile(term: Term): List<JvmClass> {
        val project = irBuilder.toProject(term)
        return compile(project)
    }

    /**
     * Compiles the given project.
     *
     * @return a list of all compiled classes
     */
    fun compile(project: Project): List<JvmClass> {
        val gatherer = Gatherer()
        project.files.forEach { file ->
            file.modules.forEach { module ->
                gatherer.addDeclaration(module)
                module.declarations.filterIsInstance<Declaration>().forEach { decl ->
                    gatherer.addDeclaration(decl)
                }
            }
        }
        val symbolTable: SymbolTable = gatherer.symbolTable

        val compiler = Compiler(symbolTable, strategyAssemblerFactory.create(JvmTypeManager(symbolTable), symbolTable))
        return project.files.flatMap { file ->
            file.modules.flatMap { module ->
                module.definitions.map { def ->
                    compiler.compileDefinition(def)
                }
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
        fun addDeclaration(decl: Declaration) {
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
            val decl = symbolTable[def.pointer] as StrategyTypeDecl? ?: throw IllegalStateException("No declaration found for definition: ${def.simpleName}")
            require(def.body.isAnf) { "The definition body must be in ANF." }

            val cls = strategyAssembler.assembleStrategy(decl, def)
            classWriter.accept(cls)
            return cls
        }
    }

}
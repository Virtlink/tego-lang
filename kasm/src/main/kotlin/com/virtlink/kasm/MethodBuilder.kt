package com.virtlink.kasm

import org.objectweb.asm.MethodVisitor

/**
 * Builds a method's body.
 *
 * @param methodVisitor the method visitor
 */
class MethodBuilder(
    val methodVisitor: MethodVisitor
) {

    private var nextLocalVarIndex = 0
    private val localVars = mutableListOf<LocalVar>()

    /**
     * Gets a fresh local variable index.
     */
    private fun getFreshLocalVarIndex(): Int {
        val index = this.nextLocalVarIndex
        this.nextLocalVarIndex += 1
        return index
    }

    /**
     * Creates and registers a new local variable.
     *
     * @param name the name of the local variable; or `null` when not specified
     * @param type the type of the local variable
     * @param scope the scope
     */
    fun createLocalVar(name: String?, type: JvmType, scope: Scope): LocalVar {
        val index = getFreshLocalVarIndex()
        val localVar = LocalVar(name, type, scope, index)
        localVars.add(localVar)
        return localVar
    }

    /**
     * Start building the method body.
     */
    fun startMethodBody() {
        methodVisitor.visitCode()
    }

    /**
     * Finish building the method body.
     */
    fun endMethodBody() {
        for (localVar in localVars.filter { !it.name.isNullOrEmpty() }) {
            methodVisitor.visitLocalVariable(localVar.name, localVar.type.descriptor, null, localVar.scope.start, localVar.scope.end, localVar.index)
        }
        methodVisitor.visitMaxs(/* Incorrect, but will be fixed by ASM library */ 0, localVars.size)
        methodVisitor.visitEnd()
    }



}
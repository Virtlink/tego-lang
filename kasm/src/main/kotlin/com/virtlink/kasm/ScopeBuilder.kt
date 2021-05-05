package com.virtlink.kasm

import org.objectweb.asm.Type
import org.objectweb.asm.Handle
import org.objectweb.asm.Label
import org.objectweb.asm.Opcodes

/**
 * A scope builder.
 *
 * @param methodBuilder the owning method builder
 */
@Suppress("FunctionName", "UNREACHABLE_CODE", "unused", "UNUSED_PARAMETER", "NOTHING_TO_INLINE")
class ScopeBuilder(
    val methodBuilder: MethodBuilder
) : Scope {
    override val start: Label = Label()
    override val end: Label = Label()

    /**
     * Creates a new scope for local variables.
     */
    fun scope(builder: ScopeBuilder.() -> Unit) {
        val scopeBuilder = ScopeBuilder(methodBuilder)
        methodBuilder.methodVisitor.visitLabel(scopeBuilder.start)
        scopeBuilder.builder()
        methodBuilder.methodVisitor.visitLabel(scopeBuilder.end)
    }

    /**
     * Creates a new local variable.
     *
     * The order in which the local variables are created
     * determines their order in the source. The first local variables
     * to be created should represent the method arguments.
     *
     * @param name the name of the local variable; or `null` when not specified
     * @param type the type of the local variable
     */
    fun localVar(name: String?, type: JvmType): LocalVar = methodBuilder.createLocalVar(name, type, this)




    /////////////////////
    // LOCAL VARIABLES //
    /////////////////////

    /** Loads an integer from a local variable. */
    inline fun iLoad(variable: LocalVar) = TODO() as Unit
    /** Loads a long from a local variable. */
    inline fun lLoad(variable: LocalVar) = TODO() as Unit
    /** Loads a float from a local variable. */
    inline fun fLoad(variable: LocalVar) = TODO() as Unit
    /** Loads a double from a local variable. */
    inline fun dLoad(variable: LocalVar) = TODO() as Unit
    /** Loads a reference from a local variable. */
    inline fun aLoad(variable: LocalVar) = methodBuilder.methodVisitor.visitVarInsn(Opcodes.ALOAD, variable.index)

    /** Stores an integer into a local variable. */
    inline fun iStore(variable: LocalVar) = TODO() as Unit
    /** Stores a long into a local variable. */
    inline fun lStore(variable: LocalVar) = TODO() as Unit
    /** Stores a float into a local variable. */
    inline fun fStore(variable: LocalVar) = TODO() as Unit
    /** Stores a double into a local variable. */
    inline fun dStore(variable: LocalVar) = TODO() as Unit
    /** Stores a reference into a local variable. */
    inline fun aStore(variable: LocalVar) = methodBuilder.methodVisitor.visitVarInsn(Opcodes.ASTORE, variable.index)

    /** Increments an integer local variable by a constant value. */
    inline fun iInc(variable: LocalVar, constant: Byte) = TODO() as Unit


    ///////////
    // STACK //
    ///////////

    /** Pops the top value from the stack. */
    inline fun pop() = TODO() as Unit
    /** Pops the top two values from the stack. */
    inline fun pop2() = TODO() as Unit
    /** Swaps the top two values on the stack. */
    inline fun swap() = TODO() as Unit
    /** Duplicates the top value on the stack. */
    inline fun dup() = methodBuilder.methodVisitor.visitInsn(Opcodes.DUP)
    /** Duplicates the top value up to two values below the top of the stack. */
    inline fun dup_x1() = TODO() as Unit
    /** Duplicates the top value up to three values below the top of the stack. */
    inline fun dup_x2() = TODO() as Unit
    /** Duplicates the top two values on the stack. */
    inline fun dup2() = TODO() as Unit
    /** Duplicates the up to two top values up to three values below the top of the stack. */
    inline fun dup2_x1() = TODO() as Unit
    /** Duplicates the up to two top values up to four values below the top of the stack. */
    inline fun dup2_x2() = TODO() as Unit

    ///////////////
    // CONSTANTS //
    ///////////////

    /** Push constant integer -1 on the stack. */
    inline fun iConst_m1() = methodBuilder.methodVisitor.visitInsn(Opcodes.ICONST_M1)
    /** Push constant integer 0 on the stack. */
    inline fun iConst_0() = methodBuilder.methodVisitor.visitInsn(Opcodes.ICONST_0)
    /** Push constant integer 1 on the stack. */
    inline fun iConst_1() = methodBuilder.methodVisitor.visitInsn(Opcodes.ICONST_1)
    /** Push constant integer 2 on the stack. */
    inline fun iConst_2() = methodBuilder.methodVisitor.visitInsn(Opcodes.ICONST_2)
    /** Push constant integer 3 on the stack. */
    inline fun iConst_3() = methodBuilder.methodVisitor.visitInsn(Opcodes.ICONST_3)
    /** Push constant integer 4 on the stack. */
    inline fun iConst_4() = methodBuilder.methodVisitor.visitInsn(Opcodes.ICONST_4)
    /** Push constant integer 5 on the stack. */
    inline fun iConst_5() = methodBuilder.methodVisitor.visitInsn(Opcodes.ICONST_5)

    /** Push constant long integer 0 on the stack. */
    inline fun lConst_0() = methodBuilder.methodVisitor.visitInsn(Opcodes.LCONST_0)
    /** Push constant long integer 1 on the stack. */
    inline fun lConst_1() = methodBuilder.methodVisitor.visitInsn(Opcodes.LCONST_1)

    /** Push constant float 0 on the stack. */
    inline fun fConst_0() = methodBuilder.methodVisitor.visitInsn(Opcodes.FCONST_0)
    /** Push constant float 1 on the stack. */
    inline fun fConst_1() = methodBuilder.methodVisitor.visitInsn(Opcodes.FCONST_1)
    /** Push constant float 2 on the stack. */
    inline fun fConst_2() = methodBuilder.methodVisitor.visitInsn(Opcodes.FCONST_2)

    /** Push constant double 0 on the stack. */
    inline fun dConst_0() = methodBuilder.methodVisitor.visitInsn(Opcodes.DCONST_0)
    /** Push constant double 1 on the stack. */
    inline fun dConst_1() = methodBuilder.methodVisitor.visitInsn(Opcodes.DCONST_1)

    /** Push a constant byte value on the stack. */
    inline fun biPush(value: Byte) = methodBuilder.methodVisitor.visitIntInsn(Opcodes.BIPUSH, value.toInt())
    /** Push a constant short value on the stack. */
    inline fun siPush(value: Short) = methodBuilder.methodVisitor.visitIntInsn(Opcodes.SIPUSH, value.toInt())
    /** Push a loaded constant on the stack. */
    inline fun ldc(value: Any) {
        val a = if (value is JvmType) Type.getType(value.descriptor) else value
        methodBuilder.methodVisitor.visitLdcInsn(a)
    }
    /** Push constant null on the stack. */
    inline fun aConst_Null() = methodBuilder.methodVisitor.visitInsn(Opcodes.ACONST_NULL)


    //////////////////////////
    // ARITHMETIC and LOGIC //
    //////////////////////////
    
    
    ///////////
    // CASTS //
    ///////////

    /** Checked cast. */
    inline fun checkCast(type: JvmType) = methodBuilder.methodVisitor.visitTypeInsn(Opcodes.CHECKCAST, type.internalName)
    
    //////////////////////////////////
    // OBJECTS, FIELDS, and METHODS //
    //////////////////////////////////
    
    
    ////////////
    // ARRAYS //
    ////////////
    
    
    
    ///////////
    // JUMPS //
    ///////////

    /** Pops the top stack value and goto label if greater than 0. */
    inline fun ifGt(label: Label) = methodBuilder.methodVisitor.visitJumpInsn(Opcodes.IFGT, label)
    /** Pops the top stack value and goto label if greater than or equal to 0. */
    inline fun ifGe(label: Label) = methodBuilder.methodVisitor.visitJumpInsn(Opcodes.IFGE, label)
    /** Pops the top stack value and goto label if equal to 0. */
    inline fun ifEq(label: Label) = methodBuilder.methodVisitor.visitJumpInsn(Opcodes.IFEQ, label)
    /** Pops the top stack value and goto label if not equal to 0. */
    inline fun ifNe(label: Label) = methodBuilder.methodVisitor.visitJumpInsn(Opcodes.IFNE, label)
    /** Pops the top stack value and goto label if less than or equal to 0. */
    inline fun ifLe(label: Label) = methodBuilder.methodVisitor.visitJumpInsn(Opcodes.IFLE, label)
    /** Pops the top stack value and goto label if less than 0. */
    inline fun ifLt(label: Label) = methodBuilder.methodVisitor.visitJumpInsn(Opcodes.IFLT, label)
    /** Goto label if stack top is null. */
    inline fun ifNull(label: Label) = methodBuilder.methodVisitor.visitJumpInsn(Opcodes.IFNULL, label)
    /** Pops the top stack value and goto label if not null. */
    inline fun ifNonNull(label: Label) = methodBuilder.methodVisitor.visitJumpInsn(Opcodes.IFNONNULL, label)

    /** Goto label unconditionally. */
    inline fun goto(label: Label) = methodBuilder.methodVisitor.visitJumpInsn(Opcodes.GOTO, label)

    /////////////////////////////
    // LABELS and LINE NUMBERS //
    /////////////////////////////

    /**
     * Adds a new label for the specified line number.
     *
     * @param lineNumber the one-based line number in the source
     * @return the label
     */
    inline fun lineNumber(lineNumber: Int): Label = lineNumber(lineNumber, newLabel())

    /**
     * Adds an existing label for the specified line number.
     *
     * @param lineNumber the one-based line number in the source
     * @param label the label
     * @return the label
     */
    inline fun lineNumber(lineNumber: Int, label: Label): Label {
        methodBuilder.methodVisitor.visitLineNumber(lineNumber, label)
        return label
    }

//    /**
//     * Adds a new label.
//     *
//     * @return the added label
//     */
    /**
     * Creates a new label.
     */
    inline fun newLabel(): Label = Label()//label(Label())

//    /**
//     * Creates a new label.
//     */
//    fun createLabel(): Label = Label()

    /**
     * Adds an existing label.
     *
     * @return the added label
     */
    inline fun label(label: Label = newLabel()): Label {
        methodBuilder.methodVisitor.visitLabel(label)
        return label
    }


    /////////////////////////////////////////////



    inline fun new(type: JvmType) {
        methodBuilder.methodVisitor.visitTypeInsn(Opcodes.NEW, type.internalName)
    }


    /** Throws the object popped from the top of the stack. */
    inline fun aThrow() = methodBuilder.methodVisitor.visitInsn(Opcodes.ATHROW);




    inline fun aReturn() {
        methodBuilder.methodVisitor.visitInsn(Opcodes.ARETURN)
    }
    inline fun iReturn() {
        methodBuilder.methodVisitor.visitInsn(Opcodes.IRETURN)
    }
    inline fun `return`() {
        methodBuilder.methodVisitor.visitInsn(Opcodes.RETURN)
    }

    inline fun getField(ownerType: JvmType, memberName: String, memberSignature: JvmMethodSignature) {
        methodBuilder.methodVisitor.visitFieldInsn(Opcodes.GETFIELD, ownerType.internalName, memberName, memberSignature.descriptor)
    }

    /**
     * @param ownerIsInterface whether the owner is an interface (such as when calling a `static` method on an interface)
     */
    inline fun invokeStatic(ownerType: JvmType, memberName: String, memberSignature: JvmMethodSignature, ownerIsInterface: Boolean = false) {
        methodBuilder.methodVisitor.visitMethodInsn(
            Opcodes.INVOKESTATIC,
            ownerType.internalName,
            memberName,
            memberSignature.descriptor,
            ownerIsInterface
        )
    }

    inline fun invokeVirtual(ownerType: JvmType, memberName: String, memberSignature: JvmMethodSignature, ownerIsInterface: Boolean = false) {
        methodBuilder.methodVisitor.visitMethodInsn(
            Opcodes.INVOKEVIRTUAL,
            ownerType.internalName,
            memberName,
            memberSignature.descriptor,
            ownerIsInterface
        )
    }

    /**
     * @param ownerIsInterface whether the owner is an interface (such as when calling a `default` method on an interface)
     */
    inline fun invokeSpecial(ownerType: JvmType, memberName: String, memberSignature: JvmMethodSignature, ownerIsInterface: Boolean = false) {
        methodBuilder.methodVisitor.visitMethodInsn(
            Opcodes.INVOKESPECIAL,
            ownerType.internalName,
            memberName,
            memberSignature.descriptor,
            ownerIsInterface
        )
    }

    inline fun invokeConstructor(ownerType: JvmType, signature: JvmMethodSignature)
            = invokeSpecial(ownerType, "<init>", signature, false)

    inline fun invokeInterface(ownerType: JvmType, memberName: String, memberSignature: JvmMethodSignature) {
        methodBuilder.methodVisitor.visitMethodInsn(
            Opcodes.INVOKEINTERFACE,
            ownerType.internalName,
            memberName,
            memberSignature.descriptor,
            true
        )
    }

    inline fun invokeDynamic(methodName: String, methodSignature: JvmMethodSignature, handle: Handle, vararg arguments: Any) {
        methodBuilder.methodVisitor.visitInvokeDynamicInsn(methodName, methodSignature.descriptor, handle, arguments)
    }


    /**
     * Gets the value from a static field of the given type
     * and pushes it onto the stack.
     *
     * @param owner the type that contains the static field
     * @param name the name of the static field
     * @param type the type of the static field
     */
    inline fun getStatic(owner: JvmType, name: String, type: JvmType) = methodBuilder.methodVisitor.visitFieldInsn(Opcodes.GETSTATIC, owner.internalName, name, type.descriptor)

    /**
     * Puts the value from the stack in the static field of the given type.
     *
     * @param owner the type that contains the static field
     * @param name the name of the static field
     * @param type the type of the static field
     */
    inline fun putStatic(owner: JvmType, name: String, type: JvmType) = methodBuilder.methodVisitor.visitFieldInsn(Opcodes.PUTSTATIC, owner.internalName, name, type.descriptor)

}
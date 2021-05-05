package com.virtlink.kasm

import org.objectweb.asm.Handle
import org.objectweb.asm.Label
import org.objectweb.asm.Opcodes

/**
 * Builds an assert statement.
 *
 * @param cls the class that contains the assert
 * @param builder adds the instruction for the assert condition,
 * and jumps to the given label when the assert is not triggered
 */
fun ScopeBuilder.assert(cls: JvmType, builder: ScopeBuilder.(Label) -> Unit) {
    val endLabel = Label()
    getStatic(cls, "\$assertionsDisabled", JvmType.Boolean)
    ifNe(endLabel)
    builder(endLabel)
    // Code produced by `builder()` should have jumped to `endLabel`
    // when the assertion is true. Otherwise, we throw `AssertionError`:
    new(JvmTypes.AssertionError)
    dup()
    invokeConstructor(JvmTypes.AssertionError, JvmMethodSignature.of(JvmType.Void, emptyList()))
    aThrow()
    label(endLabel)
}

/**
 * Pushes a constant integer value on the stack.
 *
 * @param value the value to push
 */
fun ScopeBuilder.iConst(value: Int) {
    when (value) {
        -1 -> iConst_m1()
        0 -> iConst_0()
        1 -> iConst_1()
        2 -> iConst_2()
        3 -> iConst_3()
        4 -> iConst_4()
        5 -> iConst_5()
        else -> if (Byte.MIN_VALUE <= value && value <= Byte.MAX_VALUE) {
            biPush(value.toByte())
        } else if (Short.MIN_VALUE <= value && value <= Short.MAX_VALUE) {
            siPush(value.toShort())
        } else {
            ldc(java.lang.Integer(value))
        }
    }
}

/**
 * Pushes a constant long value on the stack.
 *
 * @param value the value to push
 */
fun ScopeBuilder.lConst(value: Long) {
    when (value) {
        0L -> lConst_0()
        1L -> lConst_1()
        else -> ldc(java.lang.Long(value))
    }
}

/**
 * Pushes a constant float value on the stack.
 *
 * @param value the value to push
 */
fun ScopeBuilder.fConst(value: Float) {
    when (value) {
        0.0f -> fConst_0()
        1.0f -> fConst_1()
        2.0f -> fConst_2()
        else -> ldc(java.lang.Float(value))
    }
}

/**
 * Pushes a constant double value on the stack.
 *
 * @param value the value to push
 */
fun ScopeBuilder.dConst(value: Double) {
    when (value) {
        0.0 -> dConst_0()
        1.0 -> dConst_1()
        else -> ldc(java.lang.Double(value))
    }
}

/**
 * Instantiates and pushes a lambda instance.
 *
 * @param interfaceType the type of interface the lambda is cast to
 * @param interfaceMemberName the name of the interface member the lambda is cast to
 * @param interfaceMemberSignature the signature of the interface member the lambda is cast to
 * @param lambdaOwnerType the type owning of the lambda function
 * @param lambdaName the name of the lambda function
 * @param lambdaSignature the signature of the lambda function, preceded by any captured variables
 * @param capturedVars array of captured variables
 * @param lambdaOwnerIsInterface whether the lambda owner is an interface
 */
fun ScopeBuilder.newLambda(interfaceType: JvmType, interfaceMemberName: String, interfaceMemberSignature: JvmMethodSignature, lambdaOwnerType: JvmType, lambdaName: String, lambdaSignature: JvmMethodSignature, capturedVars: List<JvmType> = emptyList(), lambdaOwnerIsInterface: Boolean = false) {
    invokeDynamic(interfaceMemberName, JvmMethodSignature.of(interfaceType, capturedVars),
        Handle(Opcodes.H_INVOKESTATIC, "java/lang/invoke/LambdaMetafactory", "metafactory", "(Ljava/lang/invoke/MethodHandles\$Lookup;Ljava/lang/String;Ljava/lang/invoke/MethodType;Ljava/lang/invoke/MethodType;Ljava/lang/invoke/MethodHandle;Ljava/lang/invoke/MethodType;)Ljava/lang/invoke/CallSite;", false),
        // samMethodType: Signature and return type (with type erasure)
        interfaceMemberSignature /* (Type-erased) signature */ /* TODO: This must be the type erased signature */,
        // implMethod: Describes the implementation method which should be called (with suitable adaptation
        // of argument types, return types, and with captured arguments prepended to the invocation arguments)
        // at invocation time.
        Handle(Opcodes.H_INVOKESTATIC, lambdaOwnerType.internalName, lambdaName, lambdaSignature.descriptor, lambdaOwnerIsInterface),
        // instantiatedMethodType: Signature and return type (including generics).
        interfaceMemberSignature /* Signature including generic type arguments */
    )
}

/**
 * Pushes a method reference.
 *
 * @param interfaceType the type of interface the method is cast to
 * @param interfaceMemberName the name of the interface member the method is cast to
 * @param interfaceMemberSignature the signature of the interface member the method is cast to
 * @param methodOwnerType the type owning of the method
 * @param methodName the name of the method
 * @param methodSignature the signature of the method
 * @param methodOwnerIsInterface whether the method owner is an interface
 */
fun ScopeBuilder.newMethodRef(interfaceType: JvmType, interfaceMemberName: String, interfaceMemberSignature: JvmMethodSignature, methodOwnerType: JvmType, methodName: String, methodSignature: JvmMethodSignature, methodOwnerIsInterface: Boolean = false) {
    newLambda(interfaceType, interfaceMemberName, interfaceMemberSignature, methodOwnerType, methodName, methodSignature, lambdaOwnerIsInterface = methodOwnerIsInterface)
}
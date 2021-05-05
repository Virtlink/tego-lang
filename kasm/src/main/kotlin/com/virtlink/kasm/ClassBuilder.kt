package com.virtlink.kasm

import org.objectweb.asm.*
import java.util.*

/**
 * @param type the type representing the class
 */
class ClassBuilder(
    val type: JvmType,
    val classVisitor: ClassVisitor
) {

    init {
        require(type.sort == JvmTypeSort.Object) { "Class type must be an object type, got: $type" }
    }

    /**
     * Creates a method.
     */
    fun method(
        modifiers: MethodModifiers,
        name: String,
        signature: JvmMethodSignature,
        exceptions: Array<String>? = null,
        head: (MethodHeadBuilder.() -> Unit)? = null,
        builder: ScopeBuilder.() -> Unit
    ): MethodRef {
        val methodVisitor = classVisitor.visitMethod(modifiers.toInt(), name, signature.descriptor, signature.signature, exceptions)
        val methodBuilder = MethodBuilder(methodVisitor)
        head?.invoke(MethodHeadBuilder(methodBuilder))
        methodBuilder.startMethodBody()
        val scopeBuilder = ScopeBuilder(methodBuilder)
        methodBuilder.methodVisitor.visitLabel(scopeBuilder.start)
        scopeBuilder.builder()
        methodBuilder.methodVisitor.visitLabel(scopeBuilder.end)
        methodBuilder.endMethodBody()
        return MethodRef(type, name, signature)
    }

    /**
     * Creates a bridge method.
     */
    fun bridge(
        modifiers: MethodModifiers,
        memberName: String,
        fromSignature: JvmMethodSignature,
        toSignature: JvmMethodSignature,
        ownerType: JvmType,
        exceptions: Array<String>? = null,
        ownerIsInterface: Boolean = false,
    ): MethodRef {
        require(fromSignature.parameters.size == toSignature.parameters.size) { "Signatures must have the same number of parameters: $fromSignature -> $toSignature" }
        return method(modifiers or MethodModifier.Bridge or MethodModifier.Synthetic, memberName, fromSignature, exceptions) {
            val `this` = localVar("this", ownerType)
            aLoad(`this`)
            val localVars = fromSignature.parameters.map { localVar(null, it) }
            for ((l, p) in localVars zip toSignature.parameters) {
                aLoad(l)
                if (l.type != p) checkCast(p)
            }
            invokeVirtual(ownerType, memberName, toSignature, ownerIsInterface)
            aReturn()
        }
    }


    /**
     * Creates a lambda.
     */
    fun lambda(
        name: String,
        signature: JvmMethodSignature,
        capturedVars: List<JvmType> = emptyList(),
        exceptions: Array<String>? = null,
        head: (MethodHeadBuilder.() -> Unit)? = null,
        builder: ScopeBuilder.() -> Unit
    ): MethodRef {
        val lambdaName = getFreshLambdaName(name)
        val lambdaSignature = getLambdaSignature(signature, capturedVars)
        return method(
            MethodModifiers.of(MethodModifier.Private, MethodModifier.Static, MethodModifier.Synthetic),
            lambdaName, lambdaSignature, exceptions, head, builder)
    }


    /**
     * Creates an instance class constructor.
     */
    fun constructor(
        modifiers: MethodModifiers,
        signature: JvmMethodSignature,
        exceptions: Array<String>? = null,
        head: (MethodHeadBuilder.() -> Unit)? = null,
        builder: ScopeBuilder.() -> Unit
    ): MethodRef = method(modifiers, "<init>", signature, exceptions, head, builder)

    /**
     * Creates a static class constructor.
     */
    fun constructorStatic(
        head: (MethodHeadBuilder.() -> Unit)? = null,
        builder: ScopeBuilder.() -> Unit
    ): MethodRef = method(EnumSet.of(MethodModifier.Static), "<clinit>", JvmMethodSignature.of(JvmType.Void, listOf()), null, head, builder)


    /**
     * Creates a field.
     */
    fun field(
        modifiers: FieldModifiers,
        name: String,
        type: JvmType,
        signature: JvmFieldSignature? = null,
        value: Any? = null,
        builder: (FieldBuilder.() -> Unit)? = null
    ) {
        val fieldVisitor = classVisitor.visitField(modifiers.toInt(), name, type.descriptor, signature?.signature, value)
        val fieldBuilder = FieldBuilder(fieldVisitor)
        builder?.invoke(fieldBuilder)
        fieldVisitor.visitEnd()
    }

    private val lambdaNames: MutableMap<String, Int> = mutableMapOf()

    /**
     * Gets a fresh lambda name in this class scope.
     *
     * @param baseName the base name
     * @return the fresh lambda name
     */
    private fun getFreshLambdaName(baseName: String): String {
        val counter = lambdaNames.compute(baseName) { _, i -> (i ?: -1) + 1 }
        return "lambda\$$baseName\$$counter"
    }

    /**
     * Returns the signature of a lambda with the specified signature and captures variables.
     */
    private fun getLambdaSignature(signature: JvmMethodSignature, capturedVars: List<JvmType>): JvmMethodSignature {
        val paramTypes = signature.parameters
        val returnType = signature.result
        return JvmMethodSignature.of(returnType, capturedVars + paramTypes)
    }

}
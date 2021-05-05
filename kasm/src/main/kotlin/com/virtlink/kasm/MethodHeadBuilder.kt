package com.virtlink.kasm

import org.objectweb.asm.Attribute

class MethodHeadBuilder(
    private val methodBuilder: MethodBuilder
) {

    private val v = this.methodBuilder.methodVisitor

    /**
     * Adds a method's attribute.
     *
     * @param attribute the attribute
     */
    fun attribute(attribute: Attribute) {
        v.visitAttribute(attribute)
    }

    /**
     * Annotates a method.
     *
     * @param parameterCount the number of annotatable parameters
     * @param visible whether defining the number of parameters that are visible/invisible at runtime
     */
    fun annotatableParameters(parameterCount: Int, visible: Boolean) {
        v.visitAnnotableParameterCount(parameterCount, visible)
    }

    /**
     * Annotates a method.
     *
     * @param type the type of annotation
     * @param visible whether the annotation is visible at runtime
     */
    fun annotateMethod(type: JvmType, visible: Boolean, builder: (AnnotationBuilder.() -> Unit)? = null) {
        val annotationVisitor = v.visitAnnotation(type.descriptor, visible)
        val annotationBuilder = AnnotationBuilder(annotationVisitor)
        builder?.invoke(annotationBuilder)
        annotationVisitor.visitEnd()
    }

    /**
     * Annotates a method's parameter.
     *
     * @param param the parameter being annotated
     * @param type the type of annotation
     * @param visible whether the annotation is visible at runtime
     */
    fun annotateParameter(param: LocalVar, type: JvmType, visible: Boolean, builder: (AnnotationBuilder.() -> Unit)? = null)
            // FIXME: param.index is incorrect when the method is an instance method,
            // since the first parameter is `this` and param.index should be 0 for the first non-this parameter
        = annotateParameter(param.index, type, visible, builder)

    /**
     * Annotates a method's parameter.
     *
     * @param paramIndex the parameter index being annotated
     * @param type the type of annotation
     * @param visible whether the annotation is visible at runtime
     */
    // TODO: Remove this overload, but how do we get the LocalVar for the parameter?
    fun annotateParameter(paramIndex: Int, type: JvmType, visible: Boolean, builder: (AnnotationBuilder.() -> Unit)? = null) {
        val annotationVisitor = v.visitParameterAnnotation(paramIndex, type.descriptor, visible)
        val annotationBuilder = AnnotationBuilder(annotationVisitor)
        builder?.invoke(annotationBuilder)
        annotationVisitor.visitEnd()
    }
}
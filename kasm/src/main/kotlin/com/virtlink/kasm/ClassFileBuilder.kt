package com.virtlink.kasm

import org.objectweb.asm.ClassWriter

object ClassFileBuilder {

    fun `class`(
        modifiers: ClassModifiers,
        type: JvmType,
        signature: JvmClassSignature?,
        extendsClass: JvmType,
        implementsInterfaces: List<JvmType>,
        computeMaxs: Boolean = true,
        computeFrames: Boolean = true,
        version: ClassVersion = ClassVersion.V8,
        builder: ClassBuilder.() -> Unit
    ): ClassWriter {
        var classFlags = 0
        if (computeMaxs) classFlags = classFlags or ClassWriter.COMPUTE_MAXS
        if (computeFrames) classFlags = classFlags or ClassWriter.COMPUTE_FRAMES
        val classWriter = ClassWriter(classFlags)
        classWriter.visit(
            version.value,
            modifiers.toInt(),
            type.internalName,
            signature?.signature,
            extendsClass.internalName,
            implementsInterfaces.map{it.internalName}.toTypedArray()
        )
        val classBuilder = ClassBuilder(type, classWriter)
        classBuilder.builder()
        classWriter.visitEnd()
        return classWriter
    }

}
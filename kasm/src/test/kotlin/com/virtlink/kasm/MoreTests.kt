package com.virtlink.kasm

import org.example.TestClass
import org.junit.jupiter.api.Test

class MoreTests {
    @Test
    fun x() {
        val clsses = listOf(
            TestClass.S::class.java,
            TestClass.N.NN::class.java,
            Void.TYPE,
            Integer.TYPE,
            java.lang.Void::class.java,
            java.lang.Integer::class.java,
            IntArray::class.java,
            Array<Int>::class.java,
            Array<MoreTests>::class.java,
        )
        for (cls in clsses) {
            printCls(cls)
        }
    }

    fun printCls(cls: Class<*>) {
        println("-----------------------------------------------")
        println(cls.toString())
        println("annotatedInterfaces: " + cls.annotatedInterfaces)
        println("annotatedSuperclass: " + cls.annotatedSuperclass)
//        println("classLoader: " + cls.classLoader)
//        println("classes: " + cls.classes)
//        println("componentType: " + cls.componentType)
//        println("constructors: " + cls.constructors)
//        println("declaredClasses: " + cls.declaredClasses)
//        println("declaredConstructors: " + cls.declaredConstructors)
//        println("declaredFields: " + cls.declaredFields)
//        println("declaredMethods: " + cls.declaredMethods)
//        println("declaringClass: " + cls.declaringClass)
        println("enclosingClass: " + cls.enclosingClass)
//        println("enclosingConstructor: " + cls.enclosingConstructor)
//        println("enclosingMethod: " + cls.enclosingMethod)
//        println("enumConstants: " + cls.enumConstants)
//        println("fields: " + cls.fields)
        println("genericInterfaces: " + cls.genericInterfaces)
        println("genericSuperclass: " + cls.genericSuperclass)
        println("interfaces: " + cls.interfaces)
        println("isAnnotation: " + cls.isAnnotation)
        println("isAnonymous: " + cls.isAnonymousClass)
        println("isArray: " + cls.isArray)
        println("isEnum: " + cls.isEnum)
        println("isArray: " + cls.isArray)
        println("isInterface: " + cls.isInterface)
        println("isLocalClass: " + cls.isLocalClass)
        println("isMemberClass: " + cls.isMemberClass)
        println("isPrimitive: " + cls.isPrimitive)
        println("isSynthetic: " + cls.isSynthetic)
//        println("methods: " + cls.methods)
        println("modifiers: " + cls.modifiers)
        println("name: " + cls.name)
        println("canonicalName: " + cls.canonicalName)
        println("simpleName: " + cls.simpleName)
        println("package: " + cls.`package`)
//        println("signers: " + cls.signers)
        println("superclass: " + cls.superclass)
        println("typeName: " + cls.typeName)
        println("typeParameters: " + cls.typeParameters)
    }
}
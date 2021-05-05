package com.virtlink.kasm

import org.example.ListOfListOfString
import org.example.ListOfString
import org.example.TestClassOfString
import org.junit.jupiter.api.*
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assumptions.assumeTrue
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type

/**
 * Tests the [JvmType] class.
 */
@Suppress("RemoveRedundantQualifierName")
class JvmTypeTests {

    data class TestData(
        val type: JvmType,
        val name: String,
        val simpleName: String?,
        val cls: Type?,
        val descriptor: String?,
        val signature: String,
        val internalName: String?,
        val isPrimitive: Boolean,
        val isArray: Boolean,
        val arrayDimensionCount: Int?,
        val elementType: JvmType?,
        val enclosingType: JvmType?,
        val isGeneric: Boolean,
        val genericParameters: List<JvmType>?,
        val isTypeParameter: Boolean,
    )

    private fun createJvmType(sort: JvmTypeSort, signature: String): JvmType {
        val type = JvmType.fromSignature(signature)
        assert(type.sort == sort)
        return type
    }


    @Suppress("BooleanLiteralArgument", "PLATFORM_CLASS_MAPPED_TO_KOTLIN")
    val testCases: List<TestData> = listOf(
        // @formatter:off
        // Primitives
        /*  1 */ TestData(JvmType.Void,      "void",    "void",    java.lang.Void.TYPE,      "V", "V", null, true, false, null, null, null, false, null, false),
        /*  2 */ TestData(JvmType.Boolean,   "boolean", "boolean", java.lang.Boolean.TYPE,   "Z", "Z", null, true, false, null, null, null, false, null, false),
        /*  3 */ TestData(JvmType.Character, "char",    "char",    java.lang.Character.TYPE, "C", "C", null, true, false, null, null, null, false, null, false),
        /*  4 */ TestData(JvmType.Byte,      "byte",    "byte",    java.lang.Byte.TYPE,      "B", "B", null, true, false, null, null, null, false, null, false),
        /*  5 */ TestData(JvmType.Short,     "short",   "short",   java.lang.Short.TYPE,     "S", "S", null, true, false, null, null, null, false, null, false),
        /*  6 */ TestData(JvmType.Integer,   "int",     "int",     java.lang.Integer.TYPE,   "I", "I", null, true, false, null, null, null, false, null, false),
        /*  7 */ TestData(JvmType.Long,      "long",    "long",    java.lang.Long.TYPE,      "J", "J", null, true, false, null, null, null, false, null, false),
        /*  8 */ TestData(JvmType.Float,     "float",   "float",   java.lang.Float.TYPE,     "F", "F", null, true, false, null, null, null, false, null, false),
        /*  9 */ TestData(JvmType.Double,    "double",  "double",  java.lang.Double.TYPE,    "D", "D", null, true, false, null, null, null, false, null, false),
        // Primitive Arrays
        /* 10 */ TestData(createJvmType(JvmTypeSort.Array, "[Z"), "boolean[]", "boolean[]", BooleanArray::class.java,      "[Z", "[Z", null, false, true, 1, JvmType.Boolean,   null, false, null, false),
        /* 11 */ TestData(createJvmType(JvmTypeSort.Array, "[C"), "char[]",    "char[]",    CharArray::class.java,         "[C", "[C", null, false, true, 1, JvmType.Character, null, false, null, false),
        /* 12 */ TestData(createJvmType(JvmTypeSort.Array, "[B"), "byte[]",    "byte[]",    ByteArray::class.java,         "[B", "[B", null, false, true, 1, JvmType.Byte,      null, false, null, false),
        /* 13 */ TestData(createJvmType(JvmTypeSort.Array, "[S"), "short[]",   "short[]",   ShortArray::class.java,        "[S", "[S", null, false, true, 1, JvmType.Short,     null, false, null, false),
        /* 14 */ TestData(createJvmType(JvmTypeSort.Array, "[I"), "int[]",     "int[]",     IntArray::class.java,          "[I", "[I", null, false, true, 1, JvmType.Integer,   null, false, null, false),
        /* 15 */ TestData(createJvmType(JvmTypeSort.Array, "[J"), "long[]",    "long[]",    LongArray::class.java,         "[J", "[J", null, false, true, 1, JvmType.Long,      null, false, null, false),
        /* 16 */ TestData(createJvmType(JvmTypeSort.Array, "[F"), "float[]",   "float[]",   FloatArray::class.java,        "[F", "[F", null, false, true, 1, JvmType.Float,     null, false, null, false),
        /* 17 */ TestData(createJvmType(JvmTypeSort.Array, "[D"), "double[]",  "double[]",  DoubleArray::class.java,       "[D", "[D", null, false, true, 1, JvmType.Double,    null, false, null, false),
        // Multi-dimensional Primitive Arrays
        /* 18 */ TestData(createJvmType(JvmTypeSort.Array, "[[[Z"), "boolean[][][]", "boolean[][][]", Class.forName("[[[Z"), "[[[Z", "[[[Z", null, false, true, 3, JvmType.arrayOf(JvmType.arrayOf(JvmType.Boolean)),   null, false, null, false),
        /* 19 */ TestData(createJvmType(JvmTypeSort.Array, "[[[C"), "char[][][]",    "char[][][]",    Class.forName("[[[C"), "[[[C", "[[[C", null, false, true, 3, JvmType.arrayOf(JvmType.arrayOf(JvmType.Character)), null, false, null, false),
        /* 20 */ TestData(createJvmType(JvmTypeSort.Array, "[[[B"), "byte[][][]",    "byte[][][]",    Class.forName("[[[B"), "[[[B", "[[[B", null, false, true, 3, JvmType.arrayOf(JvmType.arrayOf(JvmType.Byte)),      null, false, null, false),
        /* 21 */ TestData(createJvmType(JvmTypeSort.Array, "[[[S"), "short[][][]",   "short[][][]",   Class.forName("[[[S"), "[[[S", "[[[S", null, false, true, 3, JvmType.arrayOf(JvmType.arrayOf(JvmType.Short)),     null, false, null, false),
        /* 22 */ TestData(createJvmType(JvmTypeSort.Array, "[[[I"), "int[][][]",     "int[][][]",     Class.forName("[[[I"), "[[[I", "[[[I", null, false, true, 3, JvmType.arrayOf(JvmType.arrayOf(JvmType.Integer)),   null, false, null, false),
        /* 23 */ TestData(createJvmType(JvmTypeSort.Array, "[[[J"), "long[][][]",    "long[][][]",    Class.forName("[[[J"), "[[[J", "[[[J", null, false, true, 3, JvmType.arrayOf(JvmType.arrayOf(JvmType.Long)),      null, false, null, false),
        /* 24 */ TestData(createJvmType(JvmTypeSort.Array, "[[[F"), "float[][][]",   "float[][][]",   Class.forName("[[[F"), "[[[F", "[[[F", null, false, true, 3, JvmType.arrayOf(JvmType.arrayOf(JvmType.Float)),     null, false, null, false),
        /* 25 */ TestData(createJvmType(JvmTypeSort.Array, "[[[D"), "double[][][]",  "double[][][]",  Class.forName("[[[D"), "[[[D", "[[[D", null, false, true, 3, JvmType.arrayOf(JvmType.arrayOf(JvmType.Double)),    null, false, null, false),
        // Boxed Primitives
        /* 26 */ TestData(createJvmType(JvmTypeSort.Object, "Ljava/lang/Void;"),      "java.lang.Void",      "Void",      java.lang.Void::class.java,      "Ljava/lang/Void;",      "Ljava/lang/Void;",      "java/lang/Void",      false, false, null, null, null, false, null, false),
        /* 27 */ TestData(createJvmType(JvmTypeSort.Object, "Ljava/lang/Boolean;"),   "java.lang.Boolean",   "Boolean",   java.lang.Boolean::class.java,   "Ljava/lang/Boolean;",   "Ljava/lang/Boolean;",   "java/lang/Boolean",   false, false, null, null, null, false, null, false),
        /* 28 */ TestData(createJvmType(JvmTypeSort.Object, "Ljava/lang/Character;"), "java.lang.Character", "Character", java.lang.Character::class.java, "Ljava/lang/Character;", "Ljava/lang/Character;", "java/lang/Character", false, false, null, null, null, false, null, false),
        /* 29 */ TestData(createJvmType(JvmTypeSort.Object, "Ljava/lang/Byte;"),      "java.lang.Byte",      "Byte",      java.lang.Byte::class.java,      "Ljava/lang/Byte;",      "Ljava/lang/Byte;",      "java/lang/Byte",      false, false, null, null, null, false, null, false),
        /* 30 */ TestData(createJvmType(JvmTypeSort.Object, "Ljava/lang/Short;"),     "java.lang.Short",     "Short",     java.lang.Short::class.java,     "Ljava/lang/Short;",     "Ljava/lang/Short;",     "java/lang/Short",     false, false, null, null, null, false, null, false),
        /* 31 */ TestData(createJvmType(JvmTypeSort.Object, "Ljava/lang/Integer;"),   "java.lang.Integer",   "Integer",   java.lang.Integer::class.java,   "Ljava/lang/Integer;",   "Ljava/lang/Integer;",   "java/lang/Integer",   false, false, null, null, null, false, null, false),
        /* 32 */ TestData(createJvmType(JvmTypeSort.Object, "Ljava/lang/Long;"),      "java.lang.Long",      "Long",      java.lang.Long::class.java,      "Ljava/lang/Long;",      "Ljava/lang/Long;",      "java/lang/Long",      false, false, null, null, null, false, null, false),
        /* 33 */ TestData(createJvmType(JvmTypeSort.Object, "Ljava/lang/Float;"),     "java.lang.Float",     "Float",     java.lang.Float::class.java,     "Ljava/lang/Float;",     "Ljava/lang/Float;",     "java/lang/Float",     false, false, null, null, null, false, null, false),
        /* 34 */ TestData(createJvmType(JvmTypeSort.Object, "Ljava/lang/Double;"),    "java.lang.Double",    "Double",    java.lang.Double::class.java,    "Ljava/lang/Double;",    "Ljava/lang/Double;",    "java/lang/Double",    false, false, null, null, null, false, null, false),
        // Boxed Primitive Arrays
        /* 35 */ TestData(createJvmType(JvmTypeSort.Array, "[Ljava/lang/Void;"),      "java.lang.Void[]",      "Void[]",      Array<java.lang.Void>::class.java,      "[Ljava/lang/Void;",      "[Ljava/lang/Void;",      null, false, true, 1, createJvmType(JvmTypeSort.Object, "Ljava/lang/Void;"),      null, false, null, false),
        /* 36 */ TestData(createJvmType(JvmTypeSort.Array, "[Ljava/lang/Boolean;"),   "java.lang.Boolean[]",   "Boolean[]",   Array<java.lang.Boolean>::class.java,   "[Ljava/lang/Boolean;",   "[Ljava/lang/Boolean;",   null, false, true, 1, createJvmType(JvmTypeSort.Object, "Ljava/lang/Boolean;"),   null, false, null, false),
        /* 37 */ TestData(createJvmType(JvmTypeSort.Array, "[Ljava/lang/Character;"), "java.lang.Character[]", "Character[]", Array<java.lang.Character>::class.java, "[Ljava/lang/Character;", "[Ljava/lang/Character;", null, false, true, 1, createJvmType(JvmTypeSort.Object, "Ljava/lang/Character;"), null, false, null, false),
        /* 38 */ TestData(createJvmType(JvmTypeSort.Array, "[Ljava/lang/Byte;"),      "java.lang.Byte[]",      "Byte[]",      Array<java.lang.Byte>::class.java,      "[Ljava/lang/Byte;",      "[Ljava/lang/Byte;",      null, false, true, 1, createJvmType(JvmTypeSort.Object, "Ljava/lang/Byte;"),      null, false, null, false),
        /* 39 */ TestData(createJvmType(JvmTypeSort.Array, "[Ljava/lang/Short;"),     "java.lang.Short[]",     "Short[]",     Array<java.lang.Short>::class.java,     "[Ljava/lang/Short;",     "[Ljava/lang/Short;",     null, false, true, 1, createJvmType(JvmTypeSort.Object, "Ljava/lang/Short;"),     null, false, null, false),
        /* 40 */ TestData(createJvmType(JvmTypeSort.Array, "[Ljava/lang/Integer;"),   "java.lang.Integer[]",   "Integer[]",   Array<java.lang.Integer>::class.java,   "[Ljava/lang/Integer;",   "[Ljava/lang/Integer;",   null, false, true, 1, createJvmType(JvmTypeSort.Object, "Ljava/lang/Integer;"),   null, false, null, false),
        /* 41 */ TestData(createJvmType(JvmTypeSort.Array, "[Ljava/lang/Long;"),      "java.lang.Long[]",      "Long[]",      Array<java.lang.Long>::class.java,      "[Ljava/lang/Long;",      "[Ljava/lang/Long;",      null, false, true, 1, createJvmType(JvmTypeSort.Object, "Ljava/lang/Long;"),      null, false, null, false),
        /* 42 */ TestData(createJvmType(JvmTypeSort.Array, "[Ljava/lang/Float;"),     "java.lang.Float[]",     "Float[]",     Array<java.lang.Float>::class.java,     "[Ljava/lang/Float;",     "[Ljava/lang/Float;",     null, false, true, 1, createJvmType(JvmTypeSort.Object, "Ljava/lang/Float;"),     null, false, null, false),
        /* 43 */ TestData(createJvmType(JvmTypeSort.Array, "[Ljava/lang/Double;"),    "java.lang.Double[]",    "Double[]",    Array<java.lang.Double>::class.java,    "[Ljava/lang/Double;",    "[Ljava/lang/Double;",    null, false, true, 1, createJvmType(JvmTypeSort.Object, "Ljava/lang/Double;"),    null, false, null, false),
        // Multi-dimensional Boxed Primitive Arrays
        /* 44 */ TestData(createJvmType(JvmTypeSort.Array, "[[[Ljava/lang/Void;"),      "java.lang.Void[][][]",      "Void[][][]",      Array<Array<Array<java.lang.Void>>>::class.java,      "[[[Ljava/lang/Void;",      "[[[Ljava/lang/Void;",      null, false, true, 3, createJvmType(JvmTypeSort.Array, "[[Ljava/lang/Void;"),      null, false, null, false),
        /* 45 */ TestData(createJvmType(JvmTypeSort.Array, "[[[Ljava/lang/Boolean;"),   "java.lang.Boolean[][][]",   "Boolean[][][]",   Array<Array<Array<java.lang.Boolean>>>::class.java,   "[[[Ljava/lang/Boolean;",   "[[[Ljava/lang/Boolean;",   null, false, true, 3, createJvmType(JvmTypeSort.Array, "[[Ljava/lang/Boolean;"),   null, false, null, false),
        /* 46 */ TestData(createJvmType(JvmTypeSort.Array, "[[[Ljava/lang/Character;"), "java.lang.Character[][][]", "Character[][][]", Array<Array<Array<java.lang.Character>>>::class.java, "[[[Ljava/lang/Character;", "[[[Ljava/lang/Character;", null, false, true, 3, createJvmType(JvmTypeSort.Array, "[[Ljava/lang/Character;"), null, false, null, false),
        /* 47 */ TestData(createJvmType(JvmTypeSort.Array, "[[[Ljava/lang/Byte;"),      "java.lang.Byte[][][]",      "Byte[][][]",      Array<Array<Array<java.lang.Byte>>>::class.java,      "[[[Ljava/lang/Byte;",      "[[[Ljava/lang/Byte;",      null, false, true, 3, createJvmType(JvmTypeSort.Array, "[[Ljava/lang/Byte;"),      null, false, null, false),
        /* 48 */ TestData(createJvmType(JvmTypeSort.Array, "[[[Ljava/lang/Short;"),     "java.lang.Short[][][]",     "Short[][][]",     Array<Array<Array<java.lang.Short>>>::class.java,     "[[[Ljava/lang/Short;",     "[[[Ljava/lang/Short;",     null, false, true, 3, createJvmType(JvmTypeSort.Array, "[[Ljava/lang/Short;"),     null, false, null, false),
        /* 49 */ TestData(createJvmType(JvmTypeSort.Array, "[[[Ljava/lang/Integer;"),   "java.lang.Integer[][][]",   "Integer[][][]",   Array<Array<Array<java.lang.Integer>>>::class.java,   "[[[Ljava/lang/Integer;",   "[[[Ljava/lang/Integer;",   null, false, true, 3, createJvmType(JvmTypeSort.Array, "[[Ljava/lang/Integer;"),   null, false, null, false),
        /* 50 */ TestData(createJvmType(JvmTypeSort.Array, "[[[Ljava/lang/Long;"),      "java.lang.Long[][][]",      "Long[][][]",      Array<Array<Array<java.lang.Long>>>::class.java,      "[[[Ljava/lang/Long;",      "[[[Ljava/lang/Long;",      null, false, true, 3, createJvmType(JvmTypeSort.Array, "[[Ljava/lang/Long;"),      null, false, null, false),
        /* 51 */ TestData(createJvmType(JvmTypeSort.Array, "[[[Ljava/lang/Float;"),     "java.lang.Float[][][]",     "Float[][][]",     Array<Array<Array<java.lang.Float>>>::class.java,     "[[[Ljava/lang/Float;",     "[[[Ljava/lang/Float;",     null, false, true, 3, createJvmType(JvmTypeSort.Array, "[[Ljava/lang/Float;"),     null, false, null, false),
        /* 52 */ TestData(createJvmType(JvmTypeSort.Array, "[[[Ljava/lang/Double;"),    "java.lang.Double[][][]",    "Double[][][]",    Array<Array<Array<java.lang.Double>>>::class.java,    "[[[Ljava/lang/Double;",    "[[[Ljava/lang/Double;",    null, false, true, 3, createJvmType(JvmTypeSort.Array, "[[Ljava/lang/Double;"),    null, false, null, false),
        // Classes
        /* 53 */ TestData(createJvmType(JvmTypeSort.Object, "Ljava/lang/Object;"),             "java.lang.Object",             "Object", java.lang.Object::class.java, "Ljava/lang/Object;",             "Ljava/lang/Object;",             "java/lang/Object",             false, false, null, null, null,                                                false, null, false),
        /* 54 */ TestData(createJvmType(JvmTypeSort.Object, "Ljava/util/List;"),               "java.util.List",               "List",   null,                         "Ljava/util/List;",               "Ljava/util/List;"      ,         "java/util/List",               false, false, null, null, null,                                                false, null, false),
        /* 55 */ TestData(createJvmType(JvmTypeSort.Object, "Lorg/example/TestClass\$S;"),     "org.example.TestClass\$S",     "S",      null,                         "Lorg/example/TestClass\$S;",     "Lorg/example/TestClass\$S;",     "org/example/TestClass\$S",     false, false, null, null, JvmType.fromSignature("Lorg/example/TestClass;"),    false, null, false),
        /* 56 */ TestData(createJvmType(JvmTypeSort.Object, "Lorg/example/TestClass.N.NN;"),   "org.example.TestClass.N.NN",   "NN",     null,                         "Lorg/example/TestClass.N.NN;",   "Lorg/example/TestClass.N.NN;",   "org/example/TestClass.N.NN",   false, false, null, null, JvmType.fromSignature("Lorg/example/TestClass.N;"),  false, null, false),
        /* 57 */ TestData(createJvmType(JvmTypeSort.Object, "Lorg/example/TestClass\$S2;"),    "org.example.TestClass\$S2",    "S2",     null,                         "Lorg/example/TestClass\$S2;",    "Lorg/example/TestClass\$S2;",    "org/example/TestClass\$S2",    false, false, null, null, JvmType.fromSignature("Lorg/example/TestClass;"),    false, null, false),
        /* 58 */ TestData(createJvmType(JvmTypeSort.Object, "Lorg/example/TestClass.N2.NN2;"), "org.example.TestClass.N2.NN2", "NN2",    null,                         "Lorg/example/TestClass.N2.NN2;", "Lorg/example/TestClass.N2.NN2;", "org/example/TestClass.N2.NN2", false, false, null, null, JvmType.fromSignature("Lorg/example/TestClass.N2;"), false, null, false),
        // Object Class Arrays
        // Multi-dimensional Object Class Arrays
        // Generic Classes
        /* 59 */ TestData(createJvmType(JvmTypeSort.Object, "Ljava/util/List<TE;>;"),                                   "java.util.List<E>",                             "List", java.util.List::class.java,              "Ljava/util/List;",               "Ljava/util/List<TE;>;",                                   "java/util/List",               false, false, null, null, null,                                                               true, listOf(JvmType.fromSignature("TE;")),                                 false),
        /* 60 */ TestData(createJvmType(JvmTypeSort.Object, "Lorg/example/TestClass\$S<TX;>;"),                         "org.example.TestClass\$S<X>",                   "S",    org.example.TestClass.S::class.java,     "Lorg/example/TestClass\$S;",     "Lorg/example/TestClass\$S<TX;>;",                         "org/example/TestClass\$S",     false, false, null, null, JvmType.fromSignature("Lorg/example/TestClass;"),                   true, listOf(JvmType.fromSignature("TX;")),                                 false),
        /* 61 */ TestData(createJvmType(JvmTypeSort.Object, "Lorg/example/TestClass\$S<TX;>.N<TV;>;"),                  "org.example.TestClass\$S<X>.N<V>",              "N",    org.example.TestClass.S.N::class.java,   "Lorg/example/TestClass\$S.N;",   "Lorg/example/TestClass\$S<TX;>.N<TV;>;",                  "org/example/TestClass\$S.N",   false, false, null, null, JvmType.fromSignature("Lorg/example/TestClass\$S<TX;>;"),           true, listOf(JvmType.fromSignature("TV;")),                                 false),
        /* 62 */ TestData(createJvmType(JvmTypeSort.Object, "Lorg/example/TestClass<TT;>.N<TU;>.NN<TV;>;"),             "org.example.TestClass<T>.N<U>.NN<V>",           "NN",   org.example.TestClass.N.NN::class.java,  "Lorg/example/TestClass.N.NN;",   "Lorg/example/TestClass<TT;>.N<TU;>.NN<TV;>;",             "org/example/TestClass.N.NN",   false, false, null, null, JvmType.fromSignature("Lorg/example/TestClass<TT;>.N<TU;>;"),       true, listOf(JvmType.fromSignature("TV;")),                                 false),
        /* 63 */ TestData(createJvmType(JvmTypeSort.Object, "Lorg/example/TestClass\$S2<TX1;TX2;>;"),                   "org.example.TestClass\$S2<X1,X2>",              "S2",   org.example.TestClass.S2::class.java,    "Lorg/example/TestClass\$S2;",    "Lorg/example/TestClass\$S2<TX1;TX2;>;",                   "org/example/TestClass\$S2",    false, false, null, null, JvmType.fromSignature("Lorg/example/TestClass;"),                   true, listOf(JvmType.fromSignature("TX1;"), JvmType.fromSignature("TX2;")), false),
        /* 64 */ TestData(createJvmType(JvmTypeSort.Object, "Lorg/example/TestClass<TT;>.N2<TU1;TU2;>.NN2<TV1;TV2;>;"), "org.example.TestClass<T>.N2<U1,U2>.NN2<V1,V2>", "NN2",  org.example.TestClass.N2.NN2::class.java,"Lorg/example/TestClass.N2.NN2;", "Lorg/example/TestClass<TT;>.N2<TU1;TU2;>.NN2<TV1;TV2;>;", "org/example/TestClass.N2.NN2", false, false, null, null, JvmType.fromSignature("Lorg/example/TestClass<TT;>.N2<TU1;TU2;>;"), true, listOf(JvmType.fromSignature("TV1;"), JvmType.fromSignature("TV2;")), false),
        // Parameterized Classes
        /* 65 */ TestData(createJvmType(JvmTypeSort.Object, "Ljava/util/List<Ljava/util/String;>;"),                                                                                             "java.util.List<java.util.String>",                                                                                       "List", ListOfString::class.java.getParameterizedSuper(),                             "Ljava/util/List;",               "Ljava/util/List<Ljava/util/String;>;",                                                                                             "java/util/List",               false, false, null, null, null,                                                                                                           true, listOf(JvmType.fromSignature("Ljava/util/String;")),                                               false),
        /* 66 */ TestData(createJvmType(JvmTypeSort.Object, "Ljava/util/List<Ljava/util/List<Ljava/util/String;>;>;"),                                                                           "java.util.List<java.util.List<java.util.String>>",                                                                       "List", ListOfListOfString::class.java.getParameterizedSuper(),                       "Ljava/util/List;",               "Ljava/util/List<Ljava/util/List<Ljava/util/String;>;>;",                                                                           "java/util/List",               false, false, null, null, null,                                                                                                           true, listOf(JvmType.fromSignature("Ljava/util/List<Ljava/util/String;>;")),                             false),
        /* 67 */ TestData(createJvmType(JvmTypeSort.Object, "Lorg/example/TestClass\$S<Ljava/util/String;>;"),                                                                                   "org.example.TestClass\$S<java.util.String>",                                                                             "S",    TestClassOfString.SOfString::class.java.getParameterizedSuper(),              "Lorg/example/TestClass\$S;",     "Lorg/example/TestClass\$S<Ljava/util/String;>;",                                                                                   "org/example/TestClass\$S",     false, false, null, null, JvmType.fromSignature("Lorg/example/TestClass;"),                                                               true, listOf(JvmType.fromSignature("Ljava/util/String;")),                                               false),
        /* 68 */ TestData(createJvmType(JvmTypeSort.Object, "Lorg/example/TestClass\$S<Ljava/util/String;>.N<Ljava/util/Integer;>;"),                                                            "org.example.TestClass\$S<java.util.String>.N<java.util.Integer>",                                                        "N",    TestClassOfString.SOfString.NOfInt::class.java.getParameterizedSuper(),       "Lorg/example/TestClass\$S.N;",   "Lorg/example/TestClass\$S<Ljava/util/String;>.N<Ljava/util/Integer;>;",                                                            "org/example/TestClass\$S.N",   false, false, null, null, JvmType.fromSignature("Lorg/example/TestClass\$S<Ljava/util/String;>;"),                                        true, listOf(JvmType.fromSignature("Ljava/util/Integer;")),                                              false),
        /* 69 */ TestData(createJvmType(JvmTypeSort.Object, "Lorg/example/TestClass<Ljava/util/String;>.N<Ljava/util/Integer;>.NN<Ljava/util/Boolean;>;"),                                       "org.example.TestClass<java.util.String>.N<java.util.Integer>.NN<java.util.Boolean>",                                     "NN",   TestClassOfString.NOfInt.NNOfBool::class.java.getParameterizedSuper(),        "Lorg/example/TestClass.N.NN;",   "Lorg/example/TestClass<Ljava/util/String;>.N<Ljava/util/Integer;>.NN<Ljava/util/Boolean;>;",                                       "org/example/TestClass.N.NN",   false, false, null, null, JvmType.fromSignature("Lorg/example/TestClass<Ljava/util/String;>.N<Ljava/util/Integer;>;"),                    true, listOf(JvmType.fromSignature("Ljava/util/Boolean;")),                                              false),
        /* 70 */ TestData(createJvmType(JvmTypeSort.Object, "Lorg/example/TestClass\$S2<Ljava/util/String;Ljava/util/Object;>;"),                                                                "org.example.TestClass\$S2<java.util.String,java.util.Object>",                                                           "S2",   TestClassOfString.S2OfStringObj::class.java.getParameterizedSuper(),          "Lorg/example/TestClass\$S2;",    "Lorg/example/TestClass\$S2<Ljava/util/String;Ljava/util/Object;>;",                                                                "org/example/TestClass\$S2",    false, false, null, null, JvmType.fromSignature("Lorg/example/TestClass;"),                                                               true, listOf(JvmType.fromSignature("Ljava/util/String;"), JvmType.fromSignature("Ljava/util/Object;")),  false),
        /* 71 */ TestData(createJvmType(JvmTypeSort.Object, "Lorg/example/TestClass<Ljava/util/String;>.N2<Ljava/util/Integer;Ljava/util/Object;>.NN2<Ljava/util/Boolean;Ljava/util/Object;>;"), "org.example.TestClass<java.util.String>.N2<java.util.Integer,java.util.Object>.NN2<java.util.Boolean,java.util.Object>", "NN2",  TestClassOfString.N2OfIntObj.NN2OfBoolObj::class.java.getParameterizedSuper(),"Lorg/example/TestClass.N2.NN2;", "Lorg/example/TestClass<Ljava/util/String;>.N2<Ljava/util/Integer;Ljava/util/Object;>.NN2<Ljava/util/Boolean;Ljava/util/Object;>;", "org/example/TestClass.N2.NN2", false, false, null, null, JvmType.fromSignature("Lorg/example/TestClass<Ljava/util/String;>.N2<Ljava/util/Integer;Ljava/util/Object;>;"), true, listOf(JvmType.fromSignature("Ljava/util/Boolean;"), JvmType.fromSignature("Ljava/util/Object;")), false),
        // Type parameters
        /* 72 */ TestData(createJvmType(JvmTypeSort.TypeArg,    "+Ljava/util/Object;"), "? extends java.util.Object", null, null /* TODO */, null, "+Ljava/util/Object;", null, false, false, null, JvmType.fromSignature("Ljava/util/Object;"), null, false, null, true),
        /* 73 */ TestData(createJvmType(JvmTypeSort.TypeArg,    "-Ljava/util/Object;"), "? super java.util.Object",   null, null /* TODO */, null, "-Ljava/util/Object;", null, false, false, null, JvmType.fromSignature("Ljava/util/Object;"), null, false, null, true),
        /* 74 */ TestData(createJvmType(JvmTypeSort.TypeArg,    "*"),                   "?",                          null, null /* TODO */, null, "*",                   null, false, false, null,                                              null, null, false, null, true),
        /* 75 */ TestData(createJvmType(JvmTypeSort.TypeParam,  "TT;"),                 "T",                          "T",  null /* TODO */, null, "TT;",                 null, false, false, null,                                              null, null, false, null, true),
        // @formatter:on
    )

    @TestFactory
    fun `of(Type) should return expected value`() = testCases.map { case ->
        DynamicTest.dynamicTest("of(${case.cls}) should return ${case.type}") {
            val cls = case.cls
            if (cls !is Class<*>) {
                // TODO: Change JvmType.of() to accept Type
                return@dynamicTest
            }

            // Act
            val result = JvmType.of(cls)

            // Assert
            assertEquals(case.type, result)
        }
    }

    @TestFactory
    @Disabled("Not working yet")
    fun `of(String) should return expected value`() = testCases.map { case ->
        DynamicTest.dynamicTest("of(${case.name}) should return ${case.type}") {
            // Act
            val result = JvmType.of(case.name)

            // Assert
            assertEquals(case.type, result)
        }
    }

    @TestFactory
    fun `arrayOf() should return expected value`() = listOf(
        JvmType.Integer to createJvmType(JvmTypeSort.Array, "[I"),
        createJvmType(JvmTypeSort.Array, "[[I") to createJvmType(JvmTypeSort.Array, "[[[I"),
    ).map { (type, expected) ->
        DynamicTest.dynamicTest("arrayOf($type) should return $expected") {
            // Act
            val result = JvmType.arrayOf(type)

            // Assert
            assertEquals(expected, result)
        }
    }

    @TestFactory
    fun `fromSignature() should return expected value`() = testCases.map { case ->
        DynamicTest.dynamicTest("fromSignature(${case.signature}) should return ${case.type}") {
            // Assume
            //assumeTrue(case.type.internalSort != JvmTypeSort.Internal)

            // Act
            val result = JvmType.fromSignature(case.signature)

            // Assert
            assertEquals(case.type, result)
        }
    }

    @TestFactory
    fun `fromDescriptor() should return expected value`() = testCases.map { case ->
        DynamicTest.dynamicTest("fromDescriptor(${case.descriptor}) should return ${case.type}") {
            // Assume
            assumeTrue(case.descriptor != null)

            // Act
            val result = JvmType.fromDescriptor(case.descriptor!!)

            // Assert
            // We compare descriptors, because it's not possible to describe generic parameters in a descriptor
            assertEquals(case.type.descriptor, result.descriptor)
        }
    }

    @TestFactory
    fun `signature should return expected value`() = testCases.map { case ->
        DynamicTest.dynamicTest("${case.type}.signature should return ${case.signature}") {
            // Act
            val result = case.type.signature

            // Assert
            assertEquals(case.signature, result)
        }
    }

    @TestFactory
    fun `descriptor should return expected value or throw`() = testCases.map { case ->
        DynamicTest.dynamicTest("${case.type}.descriptor should " + if (case.descriptor != null) "return ${case.descriptor}" else "throw") {
            val expected = case.descriptor
            if (expected == null) {
                // Act/Assert
                assertThrows<IllegalStateException> {
                    case.type.descriptor
                }
            } else {
                // Act
                val result = case.type.descriptor

                // Assert
                assertEquals(expected, result)
            }
        }
    }

    @TestFactory
    fun `simpleName should return expected value`() = testCases.map { case ->
        DynamicTest.dynamicTest("${case.type}.simpleName should return ${case.simpleName}") {
            // Assume
            assumeTrue(case.simpleName != null)

            // Act
            val result = case.type.simpleName

            // Assert
            assertEquals(case.simpleName!!, result)
        }
    }

    @TestFactory
    fun `internalName should return expected value or throw`() = testCases.map { case ->
        DynamicTest.dynamicTest("${case.type}.internalName should " + if (case.internalName != null) "return ${case.internalName}" else "throw") {
            val expected = case.internalName
            if (expected == null) {
                // Act/Assert
                assertThrows<IllegalStateException> {
                    case.type.internalName
                }
            } else {
                // Act
                val result = case.type.internalName

                // Assert
                assertEquals(expected, result)
            }
        }
    }

    @TestFactory
    fun `isPrimitive should return expected value`() = testCases.map { case ->
        DynamicTest.dynamicTest("${case.type}.isPrimitive should return ${case.isPrimitive}") {
            // Act
            val result = case.type.isPrimitive

            // Assert
            assertEquals(case.isPrimitive, result)
        }
    }

    @TestFactory
    fun `isArray should return expected value`() = testCases.map { case ->
        DynamicTest.dynamicTest("${case.type}.isArray should return ${case.isArray}") {
            // Act
            val result = case.type.isArray

            // Assert
            assertEquals(case.isArray, result)
        }
    }

    @TestFactory
    fun `arrayDimensionCount should return expected value`() = testCases.map { case ->
        DynamicTest.dynamicTest("${case.type}.arrayDimensionCount should return ${case.arrayDimensionCount ?: 0}") {
            // Act
            val result = case.type.dimensionCount

            // Assert
            assertEquals(case.arrayDimensionCount ?: 0, result)
        }
    }

    @TestFactory
    fun `elementType should return expected value`() = testCases.map { case ->
        DynamicTest.dynamicTest("${case.type}.elementType should return ${case.elementType}") {
            // Assume
            assumeTrue(case.elementType != null)

            // Act
            val result = case.type.elementType

            // Assert
            assertEquals(case.elementType!!, result)
        }
    }

    @TestFactory
    fun `enclosingType should return expected value`() = testCases.map { case ->
        DynamicTest.dynamicTest("${case.type}.enclosingType should return ${case.enclosingType}") {
            // Assume
            assumeTrue(case.enclosingType != null)

            // Act
            val result = case.type.enclosingType

            // Assert
            assertEquals(case.enclosingType!!, result)
        }
    }

    @TestFactory
    fun `isGeneric should return expected value`() = testCases.map { case ->
        DynamicTest.dynamicTest("${case.type}.isGeneric should return ${case.isGeneric}") {
            // Act
            val result = case.type.isGeneric

            // Assert
            assertEquals(case.isGeneric, result)
        }
    }

    @TestFactory
    fun `genericParameters should return expected value`() = testCases.map { case ->
        DynamicTest.dynamicTest("${case.type}.genericParameters should return ${case.genericParameters}") {
            // Assume
            assumeTrue(case.genericParameters != null)

            // Act
            val result = case.type.typeParameters

            // Assert
            assertEquals(case.genericParameters!!, result)
        }
    }

    @TestFactory
    fun `genericPisTypeParameterarameters should return expected value`() = testCases.map { case ->
        DynamicTest.dynamicTest("${case.type}.isTypeParameter should return ${case.isTypeParameter}") {
            // Act
            val result = case.type.isTypeParameter

            // Assert
            assertEquals(case.isTypeParameter, result)
        }
    }

    @TestFactory
    fun `toString() should return expected value`() = testCases.map { case ->
        DynamicTest.dynamicTest("${case.type}.toString() should return ${case.name}") {
            // Act
            val result = case.type.toString()

            // Assert
            assertEquals(case.name, result)
        }
    }

//    @Test
//    @Disabled
//    fun equalsContract() {
//        EqualsVerifier.forClass(JvmType::class.java)
//            .withCachedHashCode(
//                "cachedHashCode", "computeHashCode",
//                createJvmType(JvmTypeSort.Object, "Ljava/lang/Object;")
//            )
//            .withNonnullFields("sort", "buffer")
//            .withIgnoredFields("arrayDimensionCount")
//            .verify()
//    }

    /**
     * Gets the (first) generic parameterized super class or super interface of the specified class.
     *
     * Because of type erasure, it is not possible to specify something like `List<String>::class.java`
     * and expect a Java [Class] object with a generic argument [String] (represented by [ParameterizedType]
     * in Java reflection). However, generic arguments are retained for super classes and super interfaces.
     * So we use this trick where we define a class `ListOfString : List<String>` and get the parameterized
     * supertype from it instead.
     *
     * @return the first generic parameterized type
     */
    private fun Class<*>.getParameterizedSuper(): ParameterizedType {
        val superCls = this.genericSuperclass as? ParameterizedType
        if (superCls != null) return superCls
        return this.genericInterfaces.filterIsInstance(ParameterizedType::class.java).first()
    }
}

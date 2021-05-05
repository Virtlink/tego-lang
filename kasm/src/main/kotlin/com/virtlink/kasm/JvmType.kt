package com.virtlink.kasm

import java.lang.reflect.*

/**
 * A type or type parameter.
 */
interface JvmType {
    companion object {
        /** The `void` type. */
        val Void: JvmType = JvmVoid
        /** The `boolean` type. */
        val Boolean: JvmType = JvmBool
        /** The `char` type. */
        val Character: JvmType = JvmChar
        /** The `byte` type. */
        val Byte: JvmType = JvmByte
        /** The `short` type. */
        val Short: JvmType = JvmShort
        /** The `int` type. */
        val Integer: JvmType = JvmInt
        /** The `long` type. */
        val Long: JvmType = JvmLong
        /** The `float` type. */
        val Float: JvmType = JvmFloat
        /** The `double` type. */
        val Double: JvmType = JvmDouble

        /**
         * Gets the JVM type of a type.
         *
         * @param type the type
         * @return the JVM type
         */
        fun of(type: Type): JvmType {
            return when (type) {
                is ParameterizedType -> TODO()
                is GenericArrayType -> TODO()
                is TypeVariable<*> -> of(type)
                is WildcardType -> TODO()
                is Class<*> -> of(type)
                else -> TODO("Unsupported type ${type::class.java}: $type")
            }
        }

        /**
         * Gets the JVM type of a type variable.
         *
         * @param typeVar the type variable
         * @return the JVM type
         */
        fun of(typeVar: TypeVariable<*>): JvmTypeVar {
            return JvmTypeVar(typeVar.name)
        }

        /**
         * Gets the JVM type of a class.
         *
         * @param cls the class
         * @return the JVM type
         */
        fun of(cls: Class<*>): JvmType {
            return when {
                cls.isPrimitive -> {
                    // Primitive type
                    when (cls) {
                        // @formatter:off
                        java.lang.Void.TYPE      -> JvmVoid
                        java.lang.Boolean.TYPE   -> JvmBool
                        java.lang.Character.TYPE -> JvmChar
                        java.lang.Byte.TYPE      -> JvmByte
                        java.lang.Short.TYPE     -> JvmShort
                        java.lang.Integer.TYPE   -> JvmInt
                        java.lang.Long.TYPE      -> JvmLong
                        java.lang.Float.TYPE     -> JvmFloat
                        java.lang.Double.TYPE    -> JvmDouble
                        // @formatter:on
                        else -> throw AssertionError("Unhandled primitive: $cls")
                    }
                }
                cls.isArray -> {
                    // Array type
                    JvmArray(of(cls.componentType))
                }
                else -> {
                    // Object type
                    val enclosingClass: JvmObject?
                    val isInnerClass: Boolean
                    if (cls.enclosingClass != null) {
                        isInnerClass = !Modifier.isStatic(cls.modifiers)
                        // Strip the type parameters from the enclosing class,
                        // when this is a static class
                        enclosingClass = stripTypeParameters(of(cls.enclosingClass) as JvmObject, !isInnerClass)
                    } else {
                        enclosingClass = null
                        isInnerClass = false
                    }
                    val name = if (cls.enclosingClass != null) cls.simpleName else cls.name.replace('.', '/')
                    val typeParams = cls.typeParameters.map { of(it) }
                    JvmObject(name, typeParams, enclosingClass, isInnerClass)
                }
            }
        }

        /**
         * Gets the JVM type of a class specified by its Java name.
         *
         * This method accepts Java names, such as:
         * ```
         * com.example.Foo
         * java.util.List<T>
         * java.util.Map<java.lang.String, java.util.List<java.lang.Integer>>
         * java.util.Set<java.lang.String>
         * java.lang.String[]
         * com.example.MyClass$MyInnerClass
         * com.example.MyClass.MyNestedClass
         * ```
         *
         * @param className the Java name
         * @return the JVM type
         */
        fun of(className: String): JvmType {
            return when (className) {
                // @formatter:off
                java.lang.Void.TYPE.name      -> JvmVoid
                java.lang.Boolean.TYPE.name   -> JvmBool
                java.lang.Character.TYPE.name -> JvmChar
                java.lang.Byte.TYPE.name      -> JvmByte
                java.lang.Short.TYPE.name     -> JvmShort
                java.lang.Integer.TYPE.name   -> JvmInt
                java.lang.Long.TYPE.name      -> JvmLong
                java.lang.Float.TYPE.name     -> JvmFloat
                java.lang.Double.TYPE.name    -> JvmDouble
                // @formatter:on
                else -> {
                    if (className.endsWith("[]")) {
                        JvmArray(of(className.substring(0, className.length - 2)))
                    } else if (className.endsWith(">")) {
                        val typeParamsStart = className.indexOf('<')
                        val typeParamsStr = className.substring(typeParamsStart + 1, className.length - 1)
                        // TODO: Support generics
                        TODO()
                    } else {
                        // FIXME: Cannot distinguish between packages and nested classes
                        JvmObject(className.replace('.', '/'), emptyList(), null, false)
                    }
                }
            }
        }

        /**
         * Returns the specified type parameterized with the given type parameters.
         */
        fun JvmType.parameterizedBy(typeArguments: List<JvmTypeArg>): JvmObject {
            require(this is JvmObject) { "Expected an object type." }
            require(typeArguments.size == this.typeParameters.size) { "Expected ${this.typeParameters.size} type arguments, got ${typeArguments.size}."}

            return JvmObject(this.name, typeArguments, this.enclosingType, this.isInnerClass)
        }

        fun JvmType.parameterizedBy(vararg typeParameters: JvmTypeArg): JvmObject = parameterizedBy(typeParameters.toList())

        /**
         * Returns the specified type as a raw type, with wildcards for type parameters (if any).
         *
         * @return the raw type
         */
        fun JvmType.asRawType(): JvmType {
            if (this !is JvmObject || this.typeParameters.isEmpty() || this.typeParameters.all { (it as? JvmTypeArg)?.paramSort == JvmTypeParamSort.Wildcard }) return this
            return JvmObject(this.name, typeParameters.map { JvmTypeArg.wildcard() }, this.enclosingType, this.isInnerClass)
        }

        /**
         * Returns a type representing an array of the specified type.
         *
         * @param type the type to wrap in the array
         * @return the array type
         */
        fun arrayOf(type: JvmType): JvmType = JvmArray(type)

        /**
         * Returns a type representing the class with the specified signature.
         *
         * @param signature the signature
         * @return the type
         */
        fun fromSignature(signature: String): JvmType {
            return fromSignature(signature, 0, signature.length, false)
        }

        /**
         * Returns a type representing the class with the specified descriptor.
         *
         * Note that it's not possible to specify generic parameters or arguments in a descriptor.
         *
         * @param descriptor the descriptor
         * @return the type
         */
        fun fromDescriptor(descriptor: String): JvmType {
            return fromSignature(descriptor, 0, descriptor.length, true)
        }

        /**
         * Parses the specified range in the buffer as a single signature.
         *
         * @param buffer the signature (or descriptor)
         * @param start the start of the signature (or descriptor) in the buffer
         * @param end the maximum end of the signature (or descriptor) in the buffer
         * @param isDescriptor `true` to parse a descriptor instead of a signature; otherwise, `false`
         * @return the type
         */
        internal fun fromSignature(buffer: String, start: Int = 0, end: Int = buffer.length, isDescriptor: Boolean = false): JvmType {
            val (type, newEnd) = fromPartialSignature(buffer, start, end, isDescriptor)
            if (newEnd != end)
                throw IllegalArgumentException("Incomplete signature: " + buffer.substring(start, end))
            return type
        }

        /**
         * Parses the specified range in the buffer as a partial signature.
         *
         * @param buffer the signature (or descriptor)
         * @param start the start of the signature (or descriptor) in the buffer
         * @param end the maximum end of the signature (or descriptor) in the buffer
         * @param isDescriptor `true` to parse a descriptor instead of a signature; otherwise, `false`
         * @return the type
         */
        internal fun fromPartialSignature(buffer: String, start: Int = 0, end: Int = buffer.length, isDescriptor: Boolean = false): Pair<JvmType, Int> {
            return when (buffer[start]) {
                'V' -> JvmVoid to start + 1
                'Z' -> JvmBool to start + 1
                'C' -> JvmChar to start + 1
                'B' -> JvmByte to start + 1
                'S' -> JvmShort to start + 1
                'I' -> JvmInt to start + 1
                'J' -> JvmLong to start + 1
                'F' -> JvmFloat to start + 1
                'D' -> JvmDouble to start + 1
                '[' -> {
                    val (elementType, newEnd) = fromPartialSignature(buffer, start + 1 /* Skip `[` */, end, isDescriptor)
                    JvmArray(elementType) to newEnd
                }
                // FIXME: We cannot distinguish here between an invariant type argument (JvmTypeArg)
                // and an object (JvmObject). Basically, for a type argument list, the JvmObject should
                // be wrapped in a JvmTypeArg.
                'L' -> {
                    var currentEnclosingType: JvmObject? = null
                    val currentName = StringBuilder()
                    var currentParams = mutableListOf<JvmType>()
                    var currentIsInnerClass = false

                    var cursor = start + 1  // After `L`
                    while (cursor < end && buffer[cursor] != ';') {
                        // Walk through the signature
                        when (buffer[cursor]) {
                            '.' -> {
                                // Everything before here is an enclosing class (and the next class is an innner class)
                                // Build the enclosing class
                                currentEnclosingType = JvmObject(currentName.toString(), currentParams, currentEnclosingType, currentIsInnerClass)
                                // Start the next class
                                currentName.clear()
                                currentParams = mutableListOf()
                                currentIsInnerClass = true
                                cursor += 1 // Skip over `.`
                            }
                            '$' -> {
                                // Everything before here is an enclosing class (and the next class is a static class)
                                // Build the enclosing class
                                currentEnclosingType = JvmObject(currentName.toString(), currentParams, currentEnclosingType, currentIsInnerClass)
                                // Start the next class
                                currentName.clear()
                                currentParams = mutableListOf()
                                currentIsInnerClass = false
                                cursor += 1 // Skip over `$`
                            }
                            '<' -> {
                                // Generic type parameters
                                cursor += 1 /* Skip over `<` */
                                while (cursor < end && buffer[cursor] != '>') {
                                    val (paramType, paramEnd) = fromPartialSignature(
                                        buffer,
                                        cursor,
                                        end,
                                        isDescriptor
                                    )
                                    currentParams.add(paramType)
                                    cursor = paramEnd
                                }
                                if (cursor >= end) throw IllegalArgumentException("Malformed signature: " + buffer.substring(start, end))
                                cursor += 1 // Skip over `>`
                            }
                            else -> {
                                currentName.append(buffer[cursor])
                                cursor += 1 // Goto next character
                            }
                        }
                    }
                    // Build the class
                    return JvmObject(currentName.toString(), currentParams, currentEnclosingType, currentIsInnerClass) to (cursor + 1) /* Skip over `;` */
                }
                'T' -> {
                    val currentName = StringBuilder()

                    var cursor = start + 1  // After `T`
                    while (cursor < end && buffer[cursor] != ';') {
                        currentName.append(buffer[cursor])
                        cursor += 1 // Goto next character
                    }
                    if (cursor >= end) throw IllegalArgumentException("Malformed signature: " + buffer.substring(start, end))

                    JvmTypeVar(currentName.toString()) to (cursor + 1) /* Skip over `;` */
                }
                '+' -> {
                    val (paramType, newEnd) = fromPartialSignature(buffer, start + 1 /* Skip over `+` */, end, isDescriptor)
                    JvmTypeArg.covariant(paramType) to newEnd
                }
                '-' -> {
                    val (paramType, newEnd) = fromPartialSignature(buffer, start + 1 /* Skip over `-` */, end, isDescriptor)
                    JvmTypeArg.contravariant(paramType) to newEnd
                }
                '*' -> JvmTypeArg.wildcard() to (start + 1)
                '(' -> throw IllegalArgumentException("Method signature is not supported.")
                else -> throw IllegalArgumentException("Signature type is not supported: " + buffer.substring(start, end))
            }
        }

        /**
         * Strips the type parameters from the type.
         *
         * @param type the type to strip from
         * @param strip whether to strip
         * @retyrn the new type
         */
        private fun stripTypeParameters(type: JvmObject?, strip: Boolean): JvmObject? {
            if (!strip) return type
            if (type == null) return null
            return JvmObject(type.name, emptyList(), stripTypeParameters(type.enclosingType, true), false)
        }

    }
    val simpleName: String
    val internalName: String
    val classPath: String
    val descriptor: String
    val signature: String
    val sort: JvmTypeSort
    val isPrimitive: Boolean
    val isArray: Boolean
    val isGeneric: Boolean
    val isTypeParameter: Boolean
    val elementType: JvmType? get() = null
    val dimensionCount: Int get() = 0
    val typeParameters: List<JvmType>? get() = null
    val enclosingType: JvmType? get() = null
    val isInnerClass: Boolean get() = false
}

object JvmVoid : JvmType {
    override val simpleName: String get() = "void"
    override val internalName: String get() = throw IllegalStateException("Void primitive does not have an internal name")
    override val classPath: String get() = throw IllegalStateException("Void primitive does not have a class path")
    override val descriptor: String get() = "V"
    override val signature: String get() = "V"
    override val sort: JvmTypeSort get() = JvmTypeSort.Void
    override val isPrimitive: Boolean get() = true
    override val isArray: Boolean get() = false
    override val isGeneric: Boolean get() = false
    override val isTypeParameter: Boolean get() = false
    override val dimensionCount: Int get() = 0
    override fun toString(): String = "void"
}
object JvmBool : JvmType {
    override val simpleName: String get() = "boolean"
    override val internalName: String get() = throw IllegalStateException("Bool primitive does not have an internal name")
    override val classPath: String get() = throw IllegalStateException("Bool primitive does not have a class path")
    override val descriptor: String get() = "Z"
    override val signature: String get() = "Z"
    override val sort: JvmTypeSort get() = JvmTypeSort.Boolean
    override val isPrimitive: Boolean get() = true
    override val isArray: Boolean get() = false
    override val isGeneric: Boolean get() = false
    override val isTypeParameter: Boolean get() = false
    override val dimensionCount: Int get() = 0
    override fun toString(): String = "boolean"
}
object JvmChar : JvmType {
    override val simpleName: String get() = "char"
    override val internalName: String get() = throw IllegalStateException("Char primitive does not have an internal name")
    override val classPath: String get() = throw IllegalStateException("Char primitive does not have a class path")
    override val descriptor: String get() = "C"
    override val signature: String get() = "C"
    override val sort: JvmTypeSort get() = JvmTypeSort.Character
    override val isPrimitive: Boolean get() = true
    override val isArray: Boolean get() = false
    override val isGeneric: Boolean get() = false
    override val isTypeParameter: Boolean get() = false
    override val dimensionCount: Int get() = 0
    override fun toString(): String = "char"
}
object JvmByte : JvmType {
    override val simpleName: String get() = "byte"
    override val internalName: String get() = throw IllegalStateException("Byte primitive does not have an internal name")
    override val classPath: String get() = throw IllegalStateException("Byte primitive does not have a class path")
    override val descriptor: String get() = "B"
    override val signature: String get() = "B"
    override val sort: JvmTypeSort get() = JvmTypeSort.Byte
    override val isPrimitive: Boolean get() = true
    override val isArray: Boolean get() = false
    override val isGeneric: Boolean get() = false
    override val isTypeParameter: Boolean get() = false
    override val dimensionCount: Int get() = 0
    override fun toString(): String = "byte"
}
object JvmShort : JvmType {
    override val simpleName: String get() = "short"
    override val internalName: String get() = throw IllegalStateException("Short primitive does not have an internal name")
    override val classPath: String get() = throw IllegalStateException("Short primitive does not have a class path")
    override val descriptor: String get() = "S"
    override val signature: String get() = "S"
    override val sort: JvmTypeSort get() = JvmTypeSort.Short
    override val isPrimitive: Boolean get() = true
    override val isArray: Boolean get() = false
    override val isGeneric: Boolean get() = false
    override val isTypeParameter: Boolean get() = false
    override val dimensionCount: Int get() = 0
    override fun toString(): String = "short"
}
object JvmInt : JvmType {
    override val simpleName: String get() = "int"
    override val internalName: String get() = throw IllegalStateException("Int primitive does not have an internal name")
    override val classPath: String get() = throw IllegalStateException("Int primitive does not have a class path")
    override val descriptor: String get() = "I"
    override val signature: String get() = "I"
    override val sort: JvmTypeSort get() = JvmTypeSort.Integer
    override val isPrimitive: Boolean get() = true
    override val isArray: Boolean get() = false
    override val isGeneric: Boolean get() = false
    override val isTypeParameter: Boolean get() = false
    override val dimensionCount: Int get() = 0
    override fun toString(): String = "int"
}
object JvmLong : JvmType {
    override val simpleName: String get() = "long"
    override val internalName: String get() = throw IllegalStateException("Long primitive does not have an internal name")
    override val classPath: String get() = throw IllegalStateException("Long primitive does not have a class path")
    override val descriptor: String get() = "J"
    override val signature: String get() = "J"
    override val sort: JvmTypeSort get() = JvmTypeSort.Long
    override val isPrimitive: Boolean get() = true
    override val isArray: Boolean get() = false
    override val isGeneric: Boolean get() = false
    override val isTypeParameter: Boolean get() = false
    override val dimensionCount: Int get() = 0
    override fun toString(): String = "long"
}
object JvmFloat : JvmType {
    override val simpleName: String get() = "float"
    override val internalName: String get() = throw IllegalStateException("Float primitive does not have an internal name")
    override val classPath: String get() = throw IllegalStateException("Float primitive does not have a class path")
    override val descriptor: String get() = "F"
    override val signature: String get() = "F"
    override val sort: JvmTypeSort get() = JvmTypeSort.Float
    override val isPrimitive: Boolean get() = true
    override val isArray: Boolean get() = false
    override val isGeneric: Boolean get() = false
    override val isTypeParameter: Boolean get() = false
    override val dimensionCount: Int get() = 0
    override fun toString(): String = "float"
}
object JvmDouble : JvmType {
    override val simpleName: String get() = "double"
    override val internalName: String get() = throw IllegalStateException("Double primitive does not have an internal name")
    override val classPath: String get() = throw IllegalStateException("Double primitive does not have a class path")
    override val descriptor: String get() = "D"
    override val signature: String get() = "D"
    override val sort: JvmTypeSort get() = JvmTypeSort.Double
    override val isPrimitive: Boolean get() = true
    override val isArray: Boolean get() = false
    override val isGeneric: Boolean get() = false
    override val isTypeParameter: Boolean get() = false
    override val dimensionCount: Int get() = 0
    override fun toString(): String = "double"
}

data class JvmArray(
    override val elementType: JvmType
) : JvmType {
    override val simpleName: String get() = "${elementType.simpleName}[]"
    override val internalName: String get() = throw IllegalStateException("Array type does not have an internal name")
    override val classPath: String get() = throw IllegalStateException("Array primitive does not have a class path")
    override val descriptor: String get() = "[${elementType.descriptor}"
    override val signature: String get() = "[${elementType.signature}"
    override val sort: JvmTypeSort get() = JvmTypeSort.Array
    override val isPrimitive: Boolean get() = false
    override val isArray: Boolean get() = true
    override val isGeneric: Boolean get() = false
    override val isTypeParameter: Boolean get() = false
    override val dimensionCount: Int get() = 1 + elementType.dimensionCount
    override fun toString(): String = "$elementType[]"
}

data class JvmObject(
    val name: String,
    override val typeParameters: List<JvmType>,
    override val enclosingType: JvmObject? = null,
    override val isInnerClass: Boolean = false,
) : JvmType {
    init {
        require(!isInnerClass || enclosingType != null) { "Enclosing class not specified." }
    }
    override val simpleName: String get() {
        val start = name.lastIndexOf('/')
        if (start == -1) return name
        return name.substring(start + 1)
    }
    override val internalName: String get() {
        return if (enclosingType != null) {
            if (isInnerClass) {
                "${enclosingType.internalName}.$name"
            } else {
                "${enclosingType.internalName}\$$name"
            }
        } else {
            name
        }
    }
    override val classPath: String get() {
        val sb = StringBuilder()
        if (enclosingType != null) {
            if (isInnerClass) {
                // An inner class is enclosed in a class with type parameters
                sb.append(enclosingType.toString())
                sb.append('.')
            }
            else {
                // A static class is enclosed in a class without type parameters
                sb.append(enclosingType.internalName.replace('/', '.'))
                sb.append('$')
            }
        }
        sb.append(name.replace('/', '.'))
        return sb.toString()
    }
    override val descriptor: String get() = "L$internalName;"
    override val signature: String get() = "L$cleanSignature;"
    private val cleanSignature: String get() {
        val sb = StringBuilder()
        if (enclosingType != null) {
            if (isInnerClass) {
                // An inner class is enclosed in a class with type parameters
                sb.append(enclosingType.cleanSignature)
                sb.append('.')
            }
            else {
                // A static class is enclosed in a class without type parameters
                sb.append(enclosingType.internalName)
                sb.append('$')
            }
        }
        sb.append(name)
        if (typeParameters.isNotEmpty()) {
            sb.append('<')
            for (typeParam in typeParameters) {
                sb.append(typeParam.signature)
            }
            sb.append('>')
        }
        return sb.toString()
    }
    override val sort: JvmTypeSort get() = JvmTypeSort.Object
    override val isPrimitive: Boolean get() = false
    override val isArray: Boolean get() = false
    override val isGeneric: Boolean get() = typeParameters.isNotEmpty()
    override val isTypeParameter: Boolean get() = false
    override val dimensionCount: Int get() = 0
    override fun toString(): String {
        val sb = StringBuilder()
        if (enclosingType != null) {
            if (isInnerClass) {
                // An inner class is enclosed in a class with type parameters
                sb.append(enclosingType.toString())
                sb.append('.')
            }
            else {
                // A static class is enclosed in a class without type parameters
                sb.append(enclosingType.internalName.replace('/', '.'))
                sb.append('$')
            }
        }
        sb.append(name.replace('/', '.'))
        if (typeParameters.isNotEmpty()) {
            sb.append('<')
            sb.append(typeParameters.first().toString())
            for (typeParam in typeParameters.drop(1)) {
                sb.append(',')
                sb.append(typeParam.toString())
            }
            sb.append('>')
        }
        return sb.toString()
    }
}

//interface JvmTypeParam2 {
//    val signature: String
//}

data class JvmTypeVar(
    val name: String,
) : JvmType {
    override val simpleName: String get() = name
    override val internalName: String get() = throw IllegalStateException("A type parameter does not have an internal name.")
    override val classPath: String get() = throw IllegalStateException("A type parameter does not have a class path.")
    override val descriptor: String get() = JvmTypes.Object.descriptor  // Method type erasure (TODO: bounds)
    override val signature: String get() = "T$name;"
    override val sort: JvmTypeSort get() = JvmTypeSort.TypeParam
    override val isPrimitive: Boolean get() = false
    override val isArray: Boolean get() = false
    override val isGeneric: Boolean get() = false
    override val isTypeParameter: Boolean get() = true
    override val dimensionCount: Int get() = 0
    override fun toString(): String = name
}

data class JvmTypeArg private constructor(
    override val elementType: JvmType?,
    val paramSort: JvmTypeParamSort,
) : JvmType {

    companion object {
        fun invariant(type: JvmType): JvmTypeArg {
            return JvmTypeArg(type, JvmTypeParamSort.Invariant)
        }
        fun covariant(type: JvmType): JvmTypeArg {
            return JvmTypeArg(type, JvmTypeParamSort.Covariant)
        }
        fun contravariant(type: JvmType): JvmTypeArg {
            return JvmTypeArg(type, JvmTypeParamSort.Contravariant)
        }
        fun wildcard(): JvmTypeArg {
            return JvmTypeArg(null, JvmTypeParamSort.Wildcard)
        }
    }

    init {
        require((paramSort == JvmTypeParamSort.Wildcard) == (elementType == null)) { "Element type must be specified when it is not a wildcard type param, and vice versa." }
    }

    override val simpleName: String get() = throw IllegalStateException("A type argument does not have an simple name.")
    override val classPath: String get() = throw IllegalStateException("A type argument does not have a class path.")
    override val internalName: String get() = throw IllegalStateException("A type argument does not have an internal name.")
    override val descriptor: String get() = throw IllegalStateException("A type argument does not have a descriptor.")

    override val signature: String get() = when (paramSort) {
        JvmTypeParamSort.Invariant -> elementType!!.signature
        JvmTypeParamSort.Covariant -> "+${elementType!!.signature}" // Out
        JvmTypeParamSort.Contravariant -> "-${elementType!!.signature}" // In
        JvmTypeParamSort.Wildcard -> "*"
    }

    override val sort: JvmTypeSort get() = JvmTypeSort.TypeArg
    override val isPrimitive: Boolean get() = false
    override val isArray: Boolean get() = false
    override val isGeneric: Boolean get() = false
    override val isTypeParameter: Boolean get() = true
    override val dimensionCount: Int get() = 0

    override fun toString(): String {
        return when (paramSort) {
            JvmTypeParamSort.Invariant -> "$elementType"
            JvmTypeParamSort.Covariant -> "? extends $elementType" // Out
            JvmTypeParamSort.Contravariant -> "? super $elementType" // In
            JvmTypeParamSort.Wildcard -> "?"
        }
    }
}

enum class JvmTypeParamSort {
    Invariant,
    Covariant,      // Out, +
    Contravariant,  // In, -
    Wildcard
}
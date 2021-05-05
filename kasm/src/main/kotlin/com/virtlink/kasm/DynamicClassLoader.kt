package com.virtlink.kasm

/**
 * A dynamic class loader.
 */
class DynamicClassLoader(parent: ClassLoader? = null) : ClassLoader(parent) {

    /**
     * Adds a class from its binary representation.
     *
     * @param name the name of the class (i.e., "org.example.MyClass")
     * @param b the bytes of the class
     * @return the class
     */
    @Throws(ClassFormatError::class)
    fun defineClass(name: String, b: ByteArray): Class<*> {
        return defineClass(name, b, 0, b.size)
    }

}
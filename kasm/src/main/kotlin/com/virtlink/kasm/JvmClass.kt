package com.virtlink.kasm

import org.objectweb.asm.util.TraceClassVisitor
import org.objectweb.asm.ClassReader
import org.objectweb.asm.ClassWriter
import org.objectweb.asm.util.CheckClassAdapter
import java.io.PrintWriter
import java.io.StringWriter
import java.nio.file.Files
import java.nio.file.Path

/**
 * A compiled JVM class.
 *
 * @param className the class name
 * @param packageName the package name
 * @param classBytes the class bytes
 */
class JvmClass(
    val className: String,
    val packageName: String,
    private val classBytes: ByteArray,
) {

    companion object {
        fun fromType(type: JvmType, classBytes: ByteArray): JvmClass {
            // FIXME: Is this correct for nested classes?
            val fullName = type.classPath
            val packageSeparator = fullName.lastIndexOf('.')
            val packageName = fullName.substring(0, if (packageSeparator >= 0) packageSeparator else 0)
            val className = fullName.substring(if (packageSeparator >= 0) packageSeparator + 1 else 0)
            return JvmClass(className, packageName, classBytes)
        }
    }

    /**
     * Sanity checks the class.
     */
    fun check() {
        val classReader = ClassReader(classBytes)
        val classWriter = ClassWriter(classReader, ClassWriter.COMPUTE_MAXS or ClassWriter.COMPUTE_FRAMES)
        val checkClassAdapter = CheckClassAdapter(classWriter)
        classReader.accept(checkClassAdapter, ClassReader.EXPAND_FRAMES)
    }

    /**
     * Loads the class.
     *
     * @param classLoader the class loader to use
     * @param T the type of class
     * @return the loaded class
     */
    @Suppress("UNCHECKED_CAST")
    fun <T> load(classLoader: ClassLoader = JvmClass::class.java.classLoader): Class<T> {
        val clsLoader = DynamicClassLoader(classLoader)
        return clsLoader.defineClass("$packageName.$className", classBytes) as Class<T>
    }

    /**
     * Instantiates the class.
     *
     * @param classLoader the class loader to use
     * @param T the type of class
     * @return the instance
     */
    fun <T> instantiate(classLoader: ClassLoader = JvmClass::class.java.classLoader): T {
        val cls = load<T>(classLoader)
        return cls.getDeclaredConstructor().newInstance()
    }

    /**
     * Writes the JVM class to a file.
     *
     * @param path the path of the file
     */
    fun writeToFile(path: Path) {
        Files.write(path, classBytes)
    }

    /**
     * Writes the JVM class to a file and directory
     * corresponding to the class name and package, respectively.
     *
     * @param root the root path
     */
    fun writeInPackage(root: Path) {
        val targetDir = root.resolve(toPackageDirectory(packageName))
        val targetFile = targetDir.resolve(toClassFilename(className))
        Files.createDirectories(targetDir)
        writeToFile(targetFile)
    }

    /**
     * Returns a string representation of the byte code.
     */
    override fun toString(): String {
        val cr = ClassReader(classBytes)
        val stringWriter = StringWriter()
        cr.accept(TraceClassVisitor(PrintWriter(stringWriter)), 0)
        return stringWriter.toString()
    }

    /**
     * Makes a class name into a filename.
     *
     * @param className the class name
     * @return the filename
     */
    private fun toClassFilename(className: String): String {
        // FIXME: This basically reduces the classname to a valid Java class name
        return className.replace(Regex("[^A-Za-z0-9_$]"), "") + ".class"
    }

    /**
     * Makes a package name into a directory.
     *
     * @param packageName the dot-separated package name
     * @return the package directory
     */
    private fun toPackageDirectory(packageName: String): String {
        // FIXME: This assumes that the package name contains only characters valid in a directory
        return packageName.split('.').joinToString("/") + "/"
    }

}
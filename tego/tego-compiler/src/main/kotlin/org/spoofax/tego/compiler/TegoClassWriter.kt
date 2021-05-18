package org.spoofax.tego.compiler

import com.virtlink.kasm.JvmClass
import java.nio.file.Files
import java.nio.file.Path

/**
 * Writes compiled classes to disk.
 */
interface TegoClassWriter {

    companion object {
        fun none(): TegoClassWriter = NoopClassWriter
        fun toPath(outputClasspath: Path): TegoClassWriter = ClasspathClassWriter(outputClasspath)
    }

    fun accept(cls: JvmClass)

}

private object NoopClassWriter : TegoClassWriter {

    override fun accept(cls: JvmClass) {
        // Do nothing
    }

}

private class ClasspathClassWriter(
    val outputClasspath: Path
) : TegoClassWriter {

    override fun accept(cls: JvmClass) {
        Files.createDirectories(outputClasspath)
        cls.writeInPackage(outputClasspath)
    }

}
package org.spoofax.tego.compiler

import org.junit.jupiter.api.Test
import org.spoofax.tego.aterm.TermFactoryImpl
import org.spoofax.tego.aterm.io.ATermReader
import org.spoofax.tego.ir.IrBuilder

/**
 * Tests the [TegoCompiler] class.
 */
class TegoCompilerTests {

    @Test
    fun `compiles project successfully`() {
        // Arrange
        val reader = ATermReader(TermFactoryImpl())
        val moduleTerm = checkNotNull(reader.readFromResource(TegoCompilerTests::class, "/org/spoofax/tego/compiler/example2.anf.tego.aterm"))
        val classWriter = TegoClassWriter.none()
        val compiler = TegoCompiler(
            IrBuilder(),
            StrategyAssembler.Factory(ExpAssembler.Factory()),
            classWriter
        )

        // Act
        compiler.compile(moduleTerm)

        // Assert
    }

}
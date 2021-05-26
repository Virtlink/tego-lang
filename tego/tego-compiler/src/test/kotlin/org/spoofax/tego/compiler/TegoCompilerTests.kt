package org.spoofax.tego.compiler

import mu.KotlinLogging
import org.junit.jupiter.api.Test
import org.spoofax.tego.aterm.TermFactoryImpl
import org.spoofax.tego.aterm.io.ATermReader
import org.spoofax.tego.ir.IrBuilder
import kotlin.io.path.createTempDirectory

/**
 * Tests the [TegoCompiler] class.
 */
class TegoCompilerTests {

    private val log = KotlinLogging.logger {}

    @Test
    fun `compiles completion-min successfully`() {
        // Arrange
        val reader = ATermReader(TermFactoryImpl())
        val moduleTerm = checkNotNull(reader.readFromResource(TegoCompilerTests::class, "/org/spoofax/tego/compiler/completion-min.anf.tego.aterm"))
        val classWriter = TegoClassWriter.none()
        val compiler = TegoCompiler(
            IrBuilder(),
            StrategyAssembler.Factory(ExpAssembler.Factory()),
            classWriter
        )
        val tmpDir = createTempDirectory("tego")

        // Act
        val clss = compiler.compile(moduleTerm)
        val clsPaths = clss.map { it.writeInPackage(tmpDir) }

        // Assert
        log.info("Wrote generated Java classes to: $tmpDir:${clsPaths.joinToString { "\n- $it" }}")
    }

}
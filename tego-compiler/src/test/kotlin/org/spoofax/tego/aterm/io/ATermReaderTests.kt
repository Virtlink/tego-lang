package org.spoofax.tego.aterm.io

import org.spoofax.tego.aterm.TermFactoryImpl
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.DynamicTest
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestFactory
import org.spoofax.tego.aterm.Annotations
import org.spoofax.tego.aterm.Term

/**
 * Tests the [ATermReader] class.
 */
class ATermReaderTests {

    private val factory = TermFactoryImpl()

    private val termTests: List<Pair<String, Term>> = factory.run {
        listOf(
            "()" to newAppl(""),
            "(10)" to newAppl("", newInt(10)),
            "(\"a\", 10)" to newAppl("", newString("a"), newInt(10)),

            "MyCons()" to newAppl("MyCons"),
            "MyCons(10)" to newAppl("MyCons", newInt(10)),
            "MyCons(\"a\", 10)" to newAppl("MyCons", newString("a"), newInt(10)),

            "[]" to newList(),
            "[10]" to newList(newInt(10)),
            "[\"a\", 10]" to newList(newString("a"), newInt(10)),

            "\"\"" to newString(""),
            "\"abc\"" to newString("abc"),

            "0" to newInt(0),
            "42" to newInt(42),

            ".0" to newReal(.0),
            "0.0" to newReal(0.0),
            "42.1" to newReal(42.1),
            "42e2" to newReal(42e2),
            ".1e2" to newReal(.1e2),
            "42.1e2" to newReal(42.1e2),
        )
    }

    @TestFactory
    fun simpleParseTests(): List<DynamicTest> {
        // Arrange
        val reader = ATermReader(factory)
        return termTests.map { (input, expected) -> DynamicTest.dynamicTest("should return '$expected' when parsing '$input'") {
            // Act
            val actual = reader.readFromString(input)

            // Assert
            assertEquals(expected, actual)
        } }
    }

    private val emptyAnnotationTests: List<Pair<String, Term>> = factory.run { termTests.map { (input, expected) ->
        "$input{}" to withAnnotations(expected, Annotations.of())
    } }

    @TestFactory
    fun emptyAnnotationParseTests(): List<DynamicTest> {
        // Arrange
        val factory = TermFactoryImpl()
        val reader = ATermReader(factory)
        return emptyAnnotationTests.map { (input, expected) -> DynamicTest.dynamicTest("should return '$expected' when parsing '$input'") {
            // Act
            val actual = reader.readFromString(input)

            // Assert
            assertEquals(expected, actual)
        } }
    }

    private val oneAnnotationTests: List<Pair<String, Term>> = factory.run { termTests.map { (input, expected) ->
        "$input{MyAnno(42)}" to withAnnotations(expected, Annotations.of(newAppl("MyAnno", newInt(42))))
    } }

    @TestFactory
    fun oneAnnotationParseTests(): List<DynamicTest> {
        // Arrange
        val factory = TermFactoryImpl()
        val reader = ATermReader(factory)
        return oneAnnotationTests.map { (input, expected) -> DynamicTest.dynamicTest("should return '$expected' when parsing '$input'") {
            // Act
            val actual = reader.readFromString(input)

            // Assert
            assertEquals(expected, actual)
        } }
    }

    private val multiAnnotationTests: List<Pair<String, Term>> = factory.run { termTests.map { (input, expected) ->
        "$input{.4,MyAnno(42),\"xyz\"}" to withAnnotations(expected, Annotations.of(newReal(.4), newAppl("MyAnno", newInt(42)), newString("xyz")))
    } }

    @TestFactory
    fun multiAnnotationParseTests(): List<DynamicTest> {
        // Arrange
        val factory = TermFactoryImpl()
        val reader = ATermReader(factory)
        return multiAnnotationTests.map { (input, expected) -> DynamicTest.dynamicTest("should return '$expected' when parsing '$input'") {
            // Act
            val actual = reader.readFromString(input)

            // Assert
            assertEquals(expected, actual)
        } }
    }

    @Test
    fun `should parse example1 correctly`() {
        // Arrange
        val factory = TermFactoryImpl()
        val reader = ATermReader(factory)

        // Act
        val term = reader.readFromResource(ATermReaderTests::class, "/example1.tego.aterm")

        // Assert
        assertEquals(factory.run {
            newAppl(
                "Module", newAppl("ModuleDecl", newString("example1")), newList(
                    newAppl(
                        "StrategyDecl", newString("repeat"), newList(
                            newAppl("TypeParamDef", newString("a"))
                        ), newAppl(
                            "Strategy", newList(
                                newAppl(
                                    "StrategyNoArgs",
                                    newAppl("TypeName", newString("a")),
                                    newAppl("TypeName", newString("a"))
                                )
                            ), newAppl("TypeName", newString("a")), newAppl("TypeName", newString("a"))
                        )
                    ),
                    newAppl(
                        "StrategyDef", newString("repeat"), newList(newAppl("ParamDef", newString("s"))),
                        newAppl(
                            "Call", newString("try"), newList(
                                newAppl(
                                    "Seq",
                                    newAppl("Var", newString("s")),
                                    newAppl("Call", newString("repeat"), newList(newAppl("Var", newString("s"))))
                                )
                            )
                        )
                    ), newAppl(
                        "StrategyDecl", newString("try"), newList(newAppl("TypeParamDef", newString("a"))),
                        newAppl(
                            "Strategy", newList(
                                newAppl(
                                    "StrategyNoArgs", newAppl("TypeName", newString("a")),
                                    newAppl("TypeName", newString("a"))
                                )
                            ), newAppl("TypeName", newString("a")),
                            newAppl("TypeName", newString("a"))
                        )
                    ), newAppl(
                        "StrategyDef", newString("try"), newList(
                            newAppl("ParamDef", newString("s"))
                        ), newAppl(
                            "Call", newString("glc"), newList(
                                newAppl("Var", newString("s")), newAppl("Id"), newAppl("Id")
                            )
                        )
                    )
                )
            )
        }, term)
    }

}
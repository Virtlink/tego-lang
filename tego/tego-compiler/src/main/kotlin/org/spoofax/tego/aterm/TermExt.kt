package org.spoofax.tego.aterm

import org.spoofax.tego.InvalidFormatException
import kotlin.reflect.KClass

/**
 * Gets the Java string in the term.
 *
 * @return the Java string
 */
fun Term.toJavaString(): String {
    require(this is StringTerm) { "Expected ${describeTermType(StringTerm::class)}, got ${describeTermType(this)}: $this"}
    return this.value
}

/**
 * Gets the Java integer in the term.
 *
 * @return the Java integer
 */
fun Term.toJavaInt(): Int {
    val str = this.toJavaString()
    return str.toIntOrNull() ?: throw InvalidFormatException("Expected integer value, got: $str")
}

/**
 * Gets the list in the term.
 *
 * @return the elements
 */
fun Term.toList(): List<Term> {
    require(this is ListTerm) { "Expected ${describeTermType(ListTerm::class)}, got ${describeTermType(this)}: $this"}
    return this.elements
}

/**
 * Describes the type of term.
 *
 * @param cls the term type
 * @return the term type description
 */
private fun describeTermType(cls: Class<*>): String = when {
    ApplTerm::class.java.isAssignableFrom(cls) -> "constructor application"
    ListTerm::class.java.isAssignableFrom(cls) -> "list term"
    StringTerm::class.java.isAssignableFrom(cls) -> "string term"
    RealTerm::class.java.isAssignableFrom(cls) -> "real term"
    PlaceholderTerm::class.java.isAssignableFrom(cls) -> "placeholder term"
    BlobTerm::class.java.isAssignableFrom(cls) -> "blob term"
    TermVar::class.java.isAssignableFrom(cls) -> "term variable"
    Term::class.java.isAssignableFrom(cls) -> "unknown kind of term"
    else -> "not a term"
}

/**
 * Describes the type of term.
 *
 * @param cls the term type
 * @return the term type description
 */
private fun describeTermType(cls: KClass<*>): String = describeTermType(cls.java)

/**
 * Describes the type of term.
 *
 * @param term the term
 * @return the term type description
 */
private fun describeTermType(term: Term): String = describeTermType(term::class.java)
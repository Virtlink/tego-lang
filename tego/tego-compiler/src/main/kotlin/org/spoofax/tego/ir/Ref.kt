package org.spoofax.tego.ir

/**
 * A reference.
 */
interface Ref {

}

/**
 * A term index.
 */
data class TermIndex(
    val resource: String,
    val index: Int
)

/**
 * Map mapping term indices to declarations.
 */
class DeclMap(
    private val map: MutableMap<TermIndex, Decl>
) {
    /**
     * Gets the declaration that has the given term index.
     *
     * @param termIndex the term index to look for
     * @return the declaration; or `null` if not found
     */
    operator fun get(termIndex: TermIndex): Decl? = map[termIndex]

    /**
     * Sets the declaration that has the given term index.
     *
     * @param termIndex the term index
     * @param decl the declaration
     */
    operator fun set(termIndex: TermIndex, decl: Decl) {
        map[termIndex] = decl
    }
}
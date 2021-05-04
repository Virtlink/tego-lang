package org.spoofax.tego.aterm

/**
 * A term.
 */
interface Term {
    /** The subterms of the term. */
    val subterms: List<Term>
    /** The annotations of the term. */
    val annotations: List<Term> get() = Annotations.get(this).annotations
    /** The attachments of the term. */
    val attachments: Attachments
}

/**
 * A constructor application term.
 */
interface ApplTerm : Term {
    /** The name of the constructor. */
    val constructor: String
    /** The arity of the constructor. */
    val arity: Int get() = args.size
    /** The arguments of the constructor application. */
    val args: List<Term> get()  = this.subterms
    /** Whether the constructor application is a tuple. */
    val isTuple: Boolean get() = constructor.isEmpty()
    /** Gets the argument with the specified index. */
    operator fun get(index: Int): Term = args[index]
}

/**
 * A list term.
 */
interface ListTerm : Term {
//    /**
//     * The head of the list.
//     * @throws IllegalStateException if the list is empty
//     */
//    val head: Term get() = if (!isEmpty) elements[0] else throw IllegalStateException("Cannot return head; list is empty.")
//
//    /**
//     * The tail of the list.
//     * @throws IllegalStateException if the list is empty
//     */
//    val tail: ListTerm get() = if (!isEmpty) sublist(1) else throw IllegalStateException("Cannot return tail; list is empty.")

    /** Whether the list is empty. */
    val isEmpty: Boolean get() = elements.isEmpty()

    override val subterms: List<Term> get() = elements

    /** The elements in the list. */
    val elements: List<Term> get()  = this.subterms
    /** Gets the element with the specified index. */
    operator fun get(index: Int): Term = elements[index]

//    /**
//     * Returns a tail of this list.
//     *
//     * @param offset the zero-based offset of the first element to include in the returned list
//     * @return the tail of the list
//     * @throws IndexOutOfBoundsException if the offset is negative or greater than the length of the list
//     */
//    fun sublist(offset: Int): ListTerm
}

/**
 * A floating-point value.
 */
interface StringTerm : Term {
    /** The value. */
    val value: String

    override val subterms: List<Term> get() = emptyList()
}

/**
 * An integer value.
 */
interface IntTerm : Term {
    /** The value. */
    val value: Int

    override val subterms: List<Term> get() = emptyList()
}

/**
 * A real value.
 */
interface RealTerm : Term {
    /** The value. */
    val value: Double

    override val subterms: List<Term> get() = emptyList()
}

/**
 * A placeholder term.
 */
interface PlaceholderTerm : Term {
    /** The template. */
    val template: Term

    override val subterms: List<Term> get() = emptyList()
}

/**
 * A blob, which is unspecified data.
 */
interface BlobTerm : Term {
    /** The value. */
    val value: Any

    override val subterms: List<Term> get() = emptyList()
}

/**
 * A term variable.
 */
interface TermVar : Term {
    /** The name of the variable. */
    val name: String

    override val subterms: List<Term> get() = emptyList()
}
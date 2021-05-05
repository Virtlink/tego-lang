package org.spoofax.tego.aterm

/**
 * An annotations attachment.
 */
class Annotations(
    private val annotations: List<Term>
) : Attachment, AbstractList<Term>() {

    companion object {
        /**
         * An empty annotations attachment.
         */
        fun empty(): Annotations = Annotations(emptyList())

        /**
         * Creates a new annotations attachment with the specified annotation terms.
         *
         * @param annotations the annotations to include
         * @return the built annotations
         */
        fun of(vararg annotations: Term): Annotations {
            return Annotations(annotations.toList())
        }

        /**
         * Gets the annotations attachment for the specified term.
         *
         * @return the annotations attachment; or an empty annotations attachment if it's not found
         */
        fun get(term: Term): Annotations {
            return term.attachments.get(Annotations::class.java) ?: empty()
        }
    }

    override val size: Int get() = annotations.size

    override fun get(index: Int): Term = annotations[index]

    /**
     * Gets the first constructor application term with the specified constructor and arity.
     *
     * @param constructor the constructor name; or `null`
     * @param arity the arity; or -1
     * @return the constructor application term, if found; otherwise, `null`
     */
    operator fun get(constructor: String?, arity: Int = -1): ApplTerm? {
        return annotations.filterIsInstance<ApplTerm>()
            .firstOrNull { (constructor == null || it.constructor == constructor) && (arity < 0 || it.arity == arity) }
    }

    override fun toString(): String {
        return annotations.joinToString(", ", "{", "}")
    }

}
data class Annotations2(
    val annotations: List<Term>
) : Attachment {

    companion object {

        /**
         * An empty annotations attachment.
         */
        fun empty(): Annotations = Annotations(emptyList())

        /**
         * Creates a new annotations attachment with the specified annotation terms.
         *
         * @param annotations the annotations to include
         * @return the built annotations
         */
        fun of(vararg annotations: Term): Annotations {
            return Annotations(annotations.toList())
        }

        /**
         * Gets the annotations attachment for the specified term.
         *
         * @return the annotations attachment; or an empty annotations attachment if it's not found
         */
        fun get(term: Term): Annotations {
            return term.attachments.get(Annotations::class.java) ?: empty()
        }

    }

    val isEmpty: Boolean get() = annotations.isEmpty()

    override fun toString(): String {
        return annotations.joinToString(", ", "{", "}")
    }

}
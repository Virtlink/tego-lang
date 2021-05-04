package org.spoofax.tego.aterm

/**
 * An annotations attachment.
 */
data class Annotations(
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

    override fun toString(): String {
        return annotations.joinToString(", ", "{", "}")
    }

}
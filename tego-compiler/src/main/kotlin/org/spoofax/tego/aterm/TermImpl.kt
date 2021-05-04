package org.spoofax.tego.aterm

/**
 * Default term factory implementation.
 */
class TermFactoryImpl : TermFactory {
    override fun newAppl(constructor: String, args: List<Term>, attachments: Attachments, replacedTerm: Term?): ApplTerm
        = ApplTermImpl(constructor, args, attachments)

    override fun newList(elements: List<Term>, attachments: Attachments, replacedTerm: Term?): ListTerm
        = ListTermImpl(elements, attachments)

    override fun newString(value: String, attachments: Attachments, replacedTerm: Term?): StringTerm
        = StringTermImpl(value, attachments)

    override fun newInt(value: Int, attachments: Attachments, replacedTerm: Term?): IntTerm
        = IntTermImpl(value, attachments)

    override fun newReal(value: Double, attachments: Attachments, replacedTerm: Term?): RealTerm
        = RealTermImpl(value, attachments)

    override fun newPlaceholder(template: Term, attachments: Attachments, replacedTerm: Term?): PlaceholderTerm
        = PlaceholderTermImpl(template, attachments)

    override fun newBlob(value: Any, attachments: Attachments, replacedTerm: Term?): BlobTerm
        = BlobTermImpl(value, attachments)

    override fun newVar(name: String, attachments: Attachments, replacedTerm: Term?): TermVar
        = TermVarImpl(name, attachments)

    override fun <T : Term> withSubterms(term: T, subterms: List<Term>): T {
        @Suppress("UNCHECKED_CAST")
        return when (term) {
            is ApplTerm -> newAppl(term.constructor, subterms, term.attachments, term) as T
            is ListTerm -> newList(subterms, term.attachments, term) as T
            else -> { require(subterms.isEmpty()) { "The term doesn't accept subterms." }; term }
        }
    }

    override fun <T : Term> withAttachments(term: T, attachments: Attachments): T {
        require(term is TermImpl) { "The term was not created by this term factory." }

        @Suppress("UNCHECKED_CAST")
        return term.withAttachments(attachments) as T
    }
}

private abstract class TermImpl : Term {
    /**
     * Returns a copy of this term with the specified attachments.
     *
     * @param attachments the new attachments
     * @return the new term
     */
    abstract fun withAttachments(attachments: Attachments): TermImpl

    abstract override fun toString(): String
}

private data class ApplTermImpl(
    override val constructor: String,
    override val subterms: List<Term>,
    override val attachments: Attachments,
) : TermImpl(), ApplTerm {
    override fun withAttachments(attachments: Attachments): ApplTermImpl
        = ApplTermImpl(this.constructor, this.subterms, attachments)

    override fun toString(): String
        = "$constructor(${subterms.joinToString(", ")})$attachments"
}

private data class ListTermImpl(
    override val subterms: List<Term>,
    override val attachments: Attachments,
) : TermImpl(), ListTerm {
    override fun withAttachments(attachments: Attachments): ListTermImpl
            = ListTermImpl(this.subterms, attachments)

    override fun toString(): String
        = "[${subterms.joinToString()}]$attachments"
}

private data class StringTermImpl(
    override val value: String,
    override val attachments: Attachments,
) : TermImpl(), StringTerm {
    companion object {
        private fun encode(s: String): String = s
            .replace("\\", "\\\\")
            .replace("\"", "\\\"")
    }

    override fun withAttachments(attachments: Attachments): StringTermImpl
            = StringTermImpl(this.value, attachments)

    override fun toString(): String
        = "\"${encode(value)}\"$attachments"
}

private data class IntTermImpl(
    override val value: Int,
    override val attachments: Attachments,
) : TermImpl(), IntTerm {
    override fun withAttachments(attachments: Attachments): IntTermImpl
            = IntTermImpl(this.value, attachments)

    override fun toString(): String
            = "$value$attachments"
}

private data class RealTermImpl(
    override val value: Double,
    override val attachments: Attachments,
) : TermImpl(), RealTerm {
    override fun withAttachments(attachments: Attachments): RealTermImpl
            = RealTermImpl(this.value, attachments)

    override fun toString(): String
            = "$value$attachments"
}

private data class PlaceholderTermImpl(
    override val template: Term,
    override val attachments: Attachments,
) : TermImpl(), PlaceholderTerm {
    override fun withAttachments(attachments: Attachments): PlaceholderTermImpl
            = PlaceholderTermImpl(this.template, attachments)

    override fun toString(): String
            = "<$template>$attachments"
}

private data class BlobTermImpl(
    override val value: Any,
    override val attachments: Attachments,
) : TermImpl(), BlobTerm {
    override fun withAttachments(attachments: Attachments): BlobTermImpl
            = BlobTermImpl(this.value, attachments)

    override fun toString(): String
            = "$value$attachments"
}

private data class TermVarImpl(
    override val name: String,
    override val attachments: Attachments,
) : TermImpl(), TermVar {
    override fun withAttachments(attachments: Attachments): TermVarImpl
            = TermVarImpl(this.name, attachments)

    override fun toString(): String
            = "$name$attachments"
}

package org.spoofax.tego.aterm

/**
 * A term factory.
 */
@Suppress("unused")
interface TermFactory {

    /**
     * Builds a constructor application term.
     *
     * @param constructor the constructor application name
     * @param args the constructor arguments
     * @param attachments the term attachments; or [Attachments.empty]
     * @param replacedTerm the term replaced by the built term; or `null`
     * @return the built constructor application term
     */
    fun newAppl(
        constructor: String,
        args: List<Term>,
        attachments: Attachments = Attachments.empty(),
        replacedTerm: Term? = null
    ): ApplTerm

    /**
     * Builds a list with the specified elements.
     *
     * @param elements the elements in the list
     * @param attachments the term attachments; or [Attachments.empty]
     * @param replacedTerm the term replaced by the built term; or `null`
     * @return the built term
     */
    fun newList(
        elements: List<Term>,
        attachments: Attachments = Attachments.empty(),
        replacedTerm: Term? = null)
    : ListTerm

    /**
     * Builds a string term.
     *
     * @param value the value
     * @param attachments the term attachments; or [Attachments.empty]
     * @param replacedTerm the term replaced by the built term; or `null`
     * @return the built string term
     */
    fun newString(
        value: String,
        attachments: Attachments = Attachments.empty(),
        replacedTerm: Term? = null
    ): StringTerm

    /**
     * Builds an integer term.
     *
     * @param value the value
     * @param attachments the term attachments; or [Attachments.empty]
     * @param replacedTerm the term replaced by the built term; or `null`
     * @return the built integer term
     */
    fun newInt(
        value: Int,
        attachments: Attachments = Attachments.empty(),
        replacedTerm: Term? = null
    ): IntTerm

    /**
     * Builds a real term.
     *
     * @param value the value
     * @param attachments the term attachments; or [Attachments.empty]
     * @param replacedTerm the term replaced by the built term; or `null`
     * @return the built real term
     */
    fun newReal(
        value: Double,
        attachments: Attachments = Attachments.empty(),
        replacedTerm: Term? = null
    ): RealTerm

    /**
     * Builds a placeholder term.
     *
     * @param template the template
     * @param attachments the term attachments; or [Attachments.empty]
     * @param replacedTerm the term replaced by the built term; or `null`
     * @return the built placeholder term
     */
    fun newPlaceholder(
        template: Term,
        attachments: Attachments = Attachments.empty(),
        replacedTerm: Term? = null
    ): PlaceholderTerm

    /**
     * Builds a blob term.
     *
     * @param value the value
     * @param attachments the term attachments; or [Attachments.empty]
     * @param replacedTerm the term replaced by the built term; or `null`
     * @return the built blob term
     */
    fun newBlob(
        value: Any,
        attachments: Attachments = Attachments.empty(),
        replacedTerm: Term? = null
    ): BlobTerm

    /**
     * Builds a term variable.
     *
     * @param name the name
     * @param attachments the term attachments; or [Attachments.empty]
     * @param replacedTerm the term replaced by the built term; or `null`
     * @return the built blob term
     */
    fun newVar(
        name: String,
        attachments: Attachments = Attachments.empty(),
        replacedTerm: Term? = null
    ): TermVar

    /**
     * Builds a tuple term.
     *
     * Convenience method.
     *
     * @param args the constructor arguments
     * @param attachments the term attachments; or [Attachments.empty]
     * @param replacedTerm the term replaced by the built term; or `null`
     * @return the built tuple term
     */
    fun newTuple(
        args: List<Term>,
        attachments: Attachments = Attachments.empty(),
        replacedTerm: Term? = null
    ): ApplTerm = newAppl("", args, attachments, replacedTerm)

    /**
     * Builds a tuple term.
     *
     * Convenience method.
     *
     * @param args the constructor arguments
     * @return the built tuple term
     */
    fun newTuple(vararg args: Term): ApplTerm = newAppl("", *args)

    /**
     * Builds a constructor application term.
     *
     * Convenience method.
     *
     * @param constructor the constructor application name
     * @param args the constructor arguments
     * @return the built constructor application term
     */
    fun newAppl(constructor: String, vararg args: Term): ApplTerm
        = newAppl(constructor, args.asList(), Attachments.empty(), null)

    /**
     * Builds a list with the specified elements.
     *
     * Convenience method.
     *
     * @param elements the elements in the list
     * @return the built term
     */
    fun newList(vararg elements: Term): ListTerm
        = newList(elements.asList(), Attachments.empty(), null)

    /**
     * Builds a constructor application term.
     *
     * @param constructor the constructor application name
     * @param args the constructor arguments
     * @param annotations the term annotations
     * @param replacedTerm the term replaced by the built term; or `null`
     * @return the built constructor application term
     */
    fun newAppl(
        constructor: String,
        args: List<Term>,
        annotations: List<Term>,
        replacedTerm: Term? = null
    ): ApplTerm = newAppl(constructor, args, Attachments.of(Annotations(annotations)), replacedTerm)

    /**
     * Builds a list with the specified elements.
     *
     * @param elements the elements in the list
     * @param annotations the term annotations
     * @param replacedTerm the term replaced by the built term; or `null`
     * @return the built term
     */
    fun newList(
        elements: List<Term>,
        annotations: List<Term>,
        replacedTerm: Term? = null
    ) : ListTerm = newList(elements, Attachments.of(Annotations(annotations)), replacedTerm)

    /**
     * Builds a string term.
     *
     * @param value the value
     * @param annotations the term annotations
     * @param replacedTerm the term replaced by the built term; or `null`
     * @return the built string term
     */
    fun newString(
        value: String,
        annotations: List<Term>,
        replacedTerm: Term? = null
    ): StringTerm = newString(value, Attachments.of(Annotations(annotations)), replacedTerm)

    /**
     * Builds an integer term.
     *
     * @param value the value
     * @param annotations the term annotations
     * @param replacedTerm the term replaced by the built term; or `null`
     * @return the built integer term
     */
    fun newInt(
        value: Int,
        annotations: List<Term>,
        replacedTerm: Term? = null
    ): IntTerm = newInt(value, Attachments.of(Annotations(annotations)), replacedTerm)

    /**
     * Builds a real term.
     *
     * @param value the value
     * @param annotations the term annotations
     * @param replacedTerm the term replaced by the built term; or `null`
     * @return the built real term
     */
    fun newReal(
        value: Double,
        annotations: List<Term>,
        replacedTerm: Term? = null
    ): RealTerm = newReal(value, Attachments.of(Annotations(annotations)), replacedTerm)

    /**
     * Builds a placeholder term.
     *
     * @param template the template
     * @param annotations the term annotations
     * @param replacedTerm the term replaced by the built term; or `null`
     * @return the built placeholder term
     */
    fun newPlaceholder(
        template: Term,
        annotations: List<Term>,
        replacedTerm: Term? = null
    ): PlaceholderTerm = newPlaceholder(template, Attachments.of(Annotations(annotations)), replacedTerm)

    /**
     * Builds a blob term.
     *
     * @param value the value
     * @param annotations the term annotations
     * @param replacedTerm the term replaced by the built term; or `null`
     * @return the built blob term
     */
    fun newBlob(
        value: Any,
        annotations: List<Term>,
        replacedTerm: Term? = null
    ): BlobTerm = newBlob(value, Attachments.of(Annotations(annotations)), replacedTerm)

    /**
     * Builds a term variable.
     *
     * @param name the name
     * @param annotations the term annotations
     * @param replacedTerm the term replaced by the built term; or `null`
     * @return the built blob term
     */
    fun newVar(
        name: String,
        annotations: List<Term>,
        replacedTerm: Term? = null
    ): TermVar = newVar(name, Attachments.of(Annotations(annotations)), replacedTerm)

    /**
     * Builds a tuple term.
     *
     * Convenience method.
     *
     * @param args the constructor arguments
     * @param annotations the term annotations
     * @param replacedTerm the term replaced by the built term; or `null`
     * @return the built tuple term
     */
    fun newTuple(
        args: List<Term>,
        annotations: List<Term>,
        replacedTerm: Term? = null
    ): ApplTerm = newTuple(args, Attachments.of(Annotations(annotations)), replacedTerm)

    /**
     * Builds an integer term.
     *
     * Convenience method.
     *
     * @return the built integer term
     */
    operator fun Int.not(): IntTerm = newInt(this)

    /**
     * Builds a string term.
     *
     * Convenience method.
     *
     * @return the built string term
     */
    operator fun String.not(): StringTerm = newString(this)

    /**
     * Builds a real term.
     *
     * Convenience method.
     *
     * @return the built real term
     */
    operator fun Double.not(): RealTerm = newReal(this)

    /**
     * Builds a real term.
     *
     * Convenience method.
     *
     * @return the built real term
     */
    operator fun Float.not(): RealTerm = newReal(this.toDouble())

    /**
     * Constructs a copy of the specified term with the specified subterms.
     *
     * An exception is thrown when the number or types of subterms
     * doesn't match the expected number and types of subterms.
     *
     * @param term the term
     * @param subterms the subterms
     * @return the new term
     */
    fun <T: Term> withSubterms(term: T, subterms: List<Term>): T

    /**
     * Constructs a copy of the specified term with the specified attachments.
     *
     * @param term the term
     * @param attachments the attachments
     * @return the new term
     */
    fun <T: Term> withAttachments(term: T, attachments: Attachments): T

    /**
     * Constructs a copy of the specified term with the specified annotations.
     *
     * Convenience method.
     *
     * @param term the term
     * @param annotations the annotations
     * @return the new term
     */
    fun <T: Term> withAnnotations(term: T, annotations: Annotations): T {
        return withAttachments(term, term.attachments.set(annotations))
    }
}
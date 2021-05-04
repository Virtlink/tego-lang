package org.spoofax.tego.aterm

import kotlin.reflect.KClass

/**
 * A term attachment.
 */
interface Attachment

/**
 * Contains the term's attachments.
 */
interface Attachments {

    companion object {
        /**
         * Gets an empty attachments object.
         */
        fun empty(): Attachments = EmptyAttachments

        /**
         * Gets an attachments object.
         *
         * @param attachments the attachments to include
         */
        fun of(vararg attachments: Attachment): Attachments {
            return when {
                attachments.isEmpty() -> empty()
                attachments.size == 1 -> SingletonAttachments(attachments[0])
                else -> MultiAttachments(attachments.associateBy { it::class.java })
            }
        }
    }

    /**
     * Gets whether there are no attachments.
     *
     * @return `true` when there are no attachments; otherwise, `false`
     */
    fun isEmpty(): Boolean

    /**
     * Determines whether there is an attachment of the specified type.
     *
     * @param cls the type of attachment
     * @return `true` when there is an attachment of the specified type;
     * otherwise, `false`
     */
    fun <A : Attachment> has(cls: KClass<A>): Boolean = has(cls.java)

    /**
     * Determines whether there is an attachment of the specified type.
     *
     * @param cls the type of attachment
     * @return `true` when there is an attachment of the specified type;
     * otherwise, `false`
     */
    fun <A : Attachment> has(cls: Class<A>): Boolean

    /**
     * Gets the attachment of the specified type.
     *
     * @param cls the type of attachment
     * @return the attachment with the specified type;
     * or `null` when not present
     */
    fun <A : Attachment> get(cls: KClass<A>): A? = get(cls.java)

    /**
     * Gets the attachment of the specified type.
     *
     * @param cls the type of attachment
     * @return the attachment with the specified type;
     * or `null` when not present
     */
    fun <A : Attachment> get(cls: Class<A>): A?

    /**
     * Sets the specified attachment,
     * replacing any attachment of the same type.
     *
     * @param newAttachment the attachment to set
     * @return the resulting attachments
     */
    fun set(newAttachment: Attachment): Attachments

    /**
     * Removes any attachment of the specified type.
     *
     * @param cls the type of attachment to remove
     * @return the resulting attachments
     */
    fun <A : Attachment> remove(cls: Class<A>): Attachments

    /**
     * Removes all attachments.
     *
     * @return the resulting attachments
     */
    fun removeAll(): Attachments

}

/**
 * Manages term attachments.
 */
data class MultiAttachments(
    private val map: Map<Class<out Attachment>, Attachment>
) : Attachments {

    override fun isEmpty(): Boolean {
        return map.isEmpty()
    }

    override fun <A : Attachment> has(cls: Class<A>): Boolean {
        return map.containsKey(cls)
    }

    override fun <A : Attachment> get(cls: Class<A>): A? {
        @Suppress("UNCHECKED_CAST")
        return map[cls] as A?
    }

    override fun set(newAttachment: Attachment): Attachments {
        return MultiAttachments(map + (newAttachment::class.java to newAttachment))
    }

    override fun <A : Attachment> remove(cls: Class<A>): Attachments {
        return MultiAttachments(map - cls)
    }

    override fun removeAll(): Attachments {
        return Attachments.empty()
    }

    override fun toString(): String = StringBuilder().apply{
        val annotations = get(Annotations::class.java)
        if (annotations != null) {
            append(annotations.toString())
        }
        val remainingAttachments = map.values.filterNot { it is Annotations }
        if (remainingAttachments.isNotEmpty()) {
            remainingAttachments.joinTo(this, ", ", "«", "»")
        }
    }.toString()

}


/**
 * A singleton attachments object.
 */
private data class SingletonAttachments(
    val attachment: Attachment
) : Attachments {

    override fun isEmpty(): Boolean {
        return false
    }

    override fun <A : Attachment> has(cls: Class<A>): Boolean {
        return attachment::class.java == cls
    }

    override fun <A : Attachment> get(cls: Class<A>): A? {
        @Suppress("UNCHECKED_CAST")
        return if (has(cls)) attachment as A else null
    }

    override fun set(newAttachment: Attachment): Attachments {
        return if (has(newAttachment::class.java)) {
            SingletonAttachments(newAttachment)
        } else {
            MultiAttachments(mapOf(
                this.attachment::class.java to this.attachment,
                newAttachment::class.java to newAttachment
            ))
        }
    }

    override fun <A : Attachment> remove(cls: Class<A>): Attachments {
        return if (has(cls)) {
            Attachments.empty()
        } else {
            this
        }
    }

    override fun removeAll(): Attachments {
        return Attachments.empty()
    }

    override fun toString(): String {
        return if (attachment is Annotation) {
            attachment.toString()
        } else {
            "«$attachment»"
        }
    }

}

/**
 * An empty attachments object.
 */
private object EmptyAttachments : Attachments {

    override fun isEmpty(): Boolean {
        return true
    }

    override fun <A : Attachment> has(cls: Class<A>): Boolean {
        return false
    }

    override fun <A : Attachment> get(cls: Class<A>): A? {
        return null
    }

    override fun set(newAttachment: Attachment): Attachments {
        return SingletonAttachments(newAttachment)
    }

    override fun <A : Attachment> remove(cls: Class<A>): Attachments {
        return this
    }

    override fun removeAll(): Attachments {
        return this
    }

    override fun toString(): String = ""

}
package org.spoofax.tego.aterm.io

/**
 * A parse exception.
 */
class FormatException(
    message: String? = null,
    cause: Throwable? = null,
) : IllegalStateException(
    message ?: "A parse exception occurred.",
    cause,
)
package org.spoofax.tego

/**
 * Thrown when some data has an unexpected or invalid format.
 */
class InvalidFormatException(
    message: String? = null,
    cause: Throwable? = null,
) : Exception(
    message ?: "Invalid format.",
    cause
)
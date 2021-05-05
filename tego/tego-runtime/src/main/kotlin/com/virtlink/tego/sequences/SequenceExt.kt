package com.virtlink.tego.sequences

import java.util.concurrent.atomic.AtomicBoolean

// TODO: Use Flow maybe?
// https://medium.com/mobile-app-development-publication/kotlin-flow-a-much-better-version-of-sequence-d2555ba9eb94

/**
 * Buffers the sequence.
 */
// TODO: This buffers too much!
fun <T> Sequence<T>.buffer() = this.toList().asSequence()

/**
 * Evaluates the first element of a sequence to see if there are more.
 *
 * Returns a new sequence that reproduces the first element upon iteration,
 * after which it behaves like a normal sequence.
 */
fun <T> Sequence<T>.anySafe(): Pair<Boolean, Sequence<T>> {
    val iterator = this.iterator()
    val hasNext: Boolean = iterator.hasNext()

    val seq = object: Sequence<T>{
        // For thread-safety.
        val iterated: AtomicBoolean = AtomicBoolean(false)

        // First time we return the iterator we already have.
        // Other times we just get a new iterator.
        // This should preserve behavior and not lose values when used with e.g. a random sequence,
        // or a sequence that is randomly empty or not empty.
        override fun iterator(): Iterator<T> {
            val iterated = iterated.getAndSet(true)
            if (iterated) return this@anySafe.iterator()
            return iterator
        }
    }
    return hasNext to seq
}
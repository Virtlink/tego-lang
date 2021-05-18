package org.spoofax.tego.utils

/**
 * Puts a key-value pair into a map,
 * or throws when a value is already associated with the given key.
 *
 * @param key the key
 * @param value the value
 */
fun <K, V> MutableMap<K, V>.putIfAbsentOrThrow(key: K, value: V) {
    this.compute(key) { _, v ->
        if (v != null) throw IllegalStateException("The key is already present in the map: $key")
        value
    }
}
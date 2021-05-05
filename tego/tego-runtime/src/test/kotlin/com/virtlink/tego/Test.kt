package com.virtlink.tego

import kotlinx.coroutines.delay

suspend fun a() {
    println("A")
    println("B")
}

suspend fun d() {
    println("A")
    delay(10)
    println("B")
}

suspend fun aa(q: Int, r: String) {
    println("A" + q)
    println("B" + r)
}

suspend fun da(q: Int, r: String) {
    println("A" + q)
    delay(10)
    println("B" + r)
}


suspend fun ar(): Int {
    println("A")
    println("B")
    return 10
}

suspend fun dr(): Int {
    println("A")
    delay(10)
    println("B")
    return ar()
}

suspend fun aar(q: Int, r: String): Int {
    println("A" + q)
    println("B" + r)
    return ar()
}

suspend fun dar(q: Int, r: String): Int {
    println("A" + q)
    delay(10)
    println("B" + r)
    return ar()
}


suspend fun arl(): Int {
    println("A")
    val l1 = ar()
    println("B")
    val l2 = ar()
    println("C")
    return l1
}

suspend fun drl(): Int {
    println("A")
    val l1 = ar()
    println("B")
    val l2 = ar()
    println("C")
    return l1
}

suspend fun aarl(q: Int, r: String): Int {
    println("A" + q)
    val l1 = ar()
    println("B" + r)
    val l2 = ar()
    println("C" + l2)
    return l1
}

suspend fun darl(q: Int, r: String): Int {
    println("A" + q)
    val l1 = ar()
    println("B" + r)
    val l2 = ar()
    println("C" + l2)
    return l1
}
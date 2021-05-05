package com.virtlink.kasm

import org.objectweb.asm.Label

interface Scope {
    val start: Label
    val end: Label
}
package com.virtlink.tego.runtime

import com.virtlink.tego.strategies.Strategy
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class `Main-Build_0Strategy`<CTX> private constructor() : Strategy<CTX, Any, Foo> {
    companion object {
        @JvmStatic
        private val instance: `Main-Build_0Strategy`<*> = `Main-Build_0Strategy`<Any>()
        @Suppress("UNCHECKED_CAST")
        @JvmStatic
        fun <CTX> getInstance(): `Main-Build_0Strategy`<CTX> = instance as `Main-Build_0Strategy`<CTX>
    }

    override val name: String get() = "main-build_0"

    override fun eval(ctx: CTX, input: Any): Sequence<Foo> = sequence {
        yield(Foo(1, 2))
    }

    override fun toString(): String = "$name()"
}


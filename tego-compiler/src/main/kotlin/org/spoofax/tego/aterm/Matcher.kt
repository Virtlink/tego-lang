package org.spoofax.tego.aterm

@FunctionalInterface
fun interface Matcher<T> {
    fun match(term: Term): T?

    /**
     * Combines two matchers, first trying to apply the first and if this fails, the second.
     */
    infix fun or(other: Matcher<T>): Matcher<T>
        = Matcher { term ->
            this@Matcher.match(term) ?: other.match(term)
        }

    fun <R> map(transform: (T) -> R?): Matcher<R> = Matcher { term ->
        val t = match(term)
        if (t != null) transform(t)
        else null
    }

    fun filter(predicate: (T) -> Boolean): Matcher<T> = Matcher { term ->
        val t = match(term)
        if (t != null && predicate(t)) t
        else null
    }

    //    fun <R> flatMap(transform: (T) -> R?): Matcher<R>
//    = Matcher { term ->
//        val t = match(term)
//        if (t != null) transform(t)
//        else null
//    }
    companion object {

        // TERM
//        inline fun <reified T> id() = Matcher { term -> term as? T }

        fun any() = Matcher { term -> term }

        fun <T> any(
            m: Matcher<T>
        ) = any(m) { term, _ -> term }

        fun <T, R> any(
            m: Matcher<T>,
            transform: (Term, T) -> R
        ) = Matcher { term ->
            val a = m.match(term) ?: return@Matcher null
            transform(term, a)
        }

        // APPL
        fun appl(
            constructor: String,
        ) = appl(constructor) { appl -> appl }

        fun <T0> appl(
            constructor: String,
            m0: Matcher<T0>,
        ) = appl(constructor, m0) { appl, _ -> appl }

        fun <T0, T1> appl(
            constructor: String,
            m0: Matcher<T0>,
            m1: Matcher<T1>,
        ) = appl(constructor, m0, m1) { appl, _, _ -> appl }

        fun <T0, T1, T2> appl(
            constructor: String,
            m0: Matcher<T0>,
            m1: Matcher<T1>,
            m2: Matcher<T2>,
        ) = appl(constructor, m0, m1, m2) { appl, _, _, _ -> appl }

        fun <T0, T1, T2, T3> appl(
            constructor: String,
            m0: Matcher<T0>,
            m1: Matcher<T1>,
            m2: Matcher<T2>,
            m3: Matcher<T3>,
        ) = appl(constructor, m0, m1, m2, m3) { appl, _, _, _, _ -> appl }

        fun <T0, T1, T2, T3, T4> appl(
            constructor: String,
            m0: Matcher<T0>,
            m1: Matcher<T1>,
            m2: Matcher<T2>,
            m3: Matcher<T3>,
            m4: Matcher<T4>,
        ) = appl(constructor, m0, m1, m2, m3, m4) { appl, _, _, _, _, _ -> appl }

        // APPL
        fun <R> appl(
            constructor: String,
            transform: (ApplTerm) -> R
        ): Matcher<R> = Matcher { term ->
            if (term !is ApplTerm || term.arity != 2 || term.constructor != constructor) return@Matcher null
            transform(term)
        }

        fun <T0, R> appl(
            constructor: String,
            m0: Matcher<T0>,
            transform: (ApplTerm, T0) -> R
        ): Matcher<R> = Matcher { term ->
            if (term !is ApplTerm || term.arity != 2 || term.constructor != constructor) return@Matcher null
            val a0 = m0.match(term[0]) ?: return@Matcher null
            transform(term, a0)
        }

        fun <T0, T1, R> appl(
            constructor: String,
            m0: Matcher<T0>,
            m1: Matcher<T1>,
            transform: (ApplTerm, T0, T1) -> R
        ): Matcher<R> = Matcher { term ->
            if (term !is ApplTerm || term.arity != 2 || term.constructor != constructor) return@Matcher null
            val a0 = m0.match(term[0]) ?: return@Matcher null
            val a1 = m1.match(term[1]) ?: return@Matcher null
            transform(term, a0, a1)
        }

        fun <T0, T1, T2, R> appl(
            constructor: String,
            m0: Matcher<T0>,
            m1: Matcher<T1>,
            m2: Matcher<T2>,
            transform: (ApplTerm, T0, T1, T2) -> R
        ): Matcher<R> = Matcher { term ->
            if (term !is ApplTerm || term.arity != 3 || term.constructor != constructor) return@Matcher null
            val a0 = m0.match(term[0]) ?: return@Matcher null
            val a1 = m1.match(term[1]) ?: return@Matcher null
            val a2 = m2.match(term[2]) ?: return@Matcher null
            transform(term, a0, a1, a2)
        }

        fun <T0, T1, T2, T3, R> appl(
            constructor: String,
            m0: Matcher<T0>,
            m1: Matcher<T1>,
            m2: Matcher<T2>,
            m3: Matcher<T3>,
            transform: (ApplTerm, T0, T1, T2, T3) -> R
        ): Matcher<R> = Matcher { term ->
            if (term !is ApplTerm || term.arity != 4 || term.constructor != constructor) return@Matcher null
            val a0 = m0.match(term[0]) ?: return@Matcher null
            val a1 = m1.match(term[1]) ?: return@Matcher null
            val a2 = m2.match(term[2]) ?: return@Matcher null
            val a3 = m3.match(term[3]) ?: return@Matcher null
            transform(term, a0, a1, a2, a3)
        }

        fun <T0, T1, T2, T3, T4, R> appl(
            constructor: String,
            m0: Matcher<T0>,
            m1: Matcher<T1>,
            m2: Matcher<T2>,
            m3: Matcher<T3>,
            m4: Matcher<T4>,
            transform: (ApplTerm, T0, T1, T2, T3, T4) -> R
        ): Matcher<R> = Matcher { term ->
            if (term !is ApplTerm || term.arity != 5 || term.constructor != constructor) return@Matcher null
            val a0 = m0.match(term[0]) ?: return@Matcher null
            val a1 = m1.match(term[1]) ?: return@Matcher null
            val a2 = m2.match(term[2]) ?: return@Matcher null
            val a3 = m3.match(term[3]) ?: return@Matcher null
            val a4 = m4.match(term[3]) ?: return@Matcher null
            transform(term, a0, a1, a2, a3, a4)
        }

        // TUPLE
        fun appl() = tuple() { appl -> appl }

        fun <T0> tuple(
            m0: Matcher<T0>,
        ) = tuple(m0) { appl, _ -> appl }

        fun <T0, T1> tuple(
            m0: Matcher<T0>,
            m1: Matcher<T1>,
        ) = tuple(m0, m1) { appl, _, _ -> appl }

        fun <T0, T1, T2> tuple(
            m0: Matcher<T0>,
            m1: Matcher<T1>,
            m2: Matcher<T2>,
        ) = tuple(m0, m1, m2) { appl, _, _, _ -> appl }

        fun <T0, T1, T2, T3> tuple(
            m0: Matcher<T0>,
            m1: Matcher<T1>,
            m2: Matcher<T2>,
            m3: Matcher<T3>,
        ) = tuple(m0, m1, m2, m3) { appl, _, _, _, _ -> appl }

        fun <T0, T1, T2, T3, T4> tuple(
            m0: Matcher<T0>,
            m1: Matcher<T1>,
            m2: Matcher<T2>,
            m3: Matcher<T3>,
            m4: Matcher<T4>,
        ) = tuple(m0, m1, m2, m3, m4) { appl, _, _, _, _, _ -> appl }

        // TUPLE
        fun <R> tuple(
            transform: (ApplTerm) -> R
        ): Matcher<R> = appl("", transform)

        fun <T0, R> tuple(
            m0: Matcher<T0>,
            transform: (ApplTerm, T0) -> R
        ): Matcher<R> = appl("", m0, transform)

        fun <T0, T1, R> tuple(
            m0: Matcher<T0>,
            m1: Matcher<T1>,
            transform: (ApplTerm, T0, T1) -> R
        ): Matcher<R> = appl("", m0, m1, transform)

        fun <T0, T1, T2, R> tuple(
            m0: Matcher<T0>,
            m1: Matcher<T1>,
            m2: Matcher<T2>,
            transform: (ApplTerm, T0, T1, T2) -> R
        ): Matcher<R> = appl("", m0, m1, m2, transform)

        fun <T0, T1, T2, T3, R> tuple(
            m0: Matcher<T0>,
            m1: Matcher<T1>,
            m2: Matcher<T2>,
            m3: Matcher<T3>,
            transform: (ApplTerm, T0, T1, T2, T3) -> R
        ): Matcher<R> = appl("", m0, m1, m2, m3, transform)

        fun <T0, T1, T2, T3, T4, R> tuple(
            m0: Matcher<T0>,
            m1: Matcher<T1>,
            m2: Matcher<T2>,
            m3: Matcher<T3>,
            m4: Matcher<T4>,
            transform: (ApplTerm, T0, T1, T2, T3, T4) -> R
        ): Matcher<R> = appl("", m0, m1, m2, m3, m4, transform)

        // LIST
        fun list() = Matcher { term ->
            if (term !is ListTerm) return@Matcher null
            term
        }

        fun <T> list(
            m: Matcher<T>,
        ) = list(m) { list, _ -> list }

        fun <T, R> list(
            m: Matcher<T>,
            transform: (ListTerm, T) -> R
        ) = Matcher { term ->
            if (term !is ListTerm) return@Matcher null
            val a = m.match(term) ?: return@Matcher null
            transform(term, a)
        }

        fun listElems(): Matcher<List<Term>> = listElems(any())

        fun <T> listElems(
            m: Matcher<T>
        ): Matcher<List<T>> = listElems(m) { _, elems -> elems }

        fun <R> listElems(
            transform: (ListTerm, List<Term>) -> R
        ) = listElems(any(), transform)

        fun <T, R> listElems(
            m: Matcher<T>,
            transform: (ListTerm, List<T>) -> R
        ) = Matcher { term ->
            if (term !is ListTerm) return@Matcher null
            val ass = term.elements.mapNotNull { m.match(it) }
//            if (ass.size != term.elements.size) return@Matcher null
            transform(term, ass)
        }

        // STRING
        fun string() = string { str -> str }

        fun <T> string(
            m: Matcher<T>
        ) = string(m) { str, _ -> str }

        fun <T, R> string(
            m: Matcher<T>,
            transform: (StringTerm, T) -> R
        ) = Matcher { term ->
            if (term !is StringTerm) return@Matcher null
            val a = m.match(term) ?: return@Matcher null
            transform(term, a)
        }

        fun stringValue() = stringValue { _, s -> s }

        fun <R> stringValue(
            transform: (StringTerm, String) -> R
        ) = Matcher { term ->
            if (term !is StringTerm) return@Matcher null
            transform(term, term.value)
        }

        // INT
        fun int() = int { int -> int }

        fun <T> int(
            m: Matcher<T>
        ) = int(m) { int, _ -> int }

        fun <T, R> int(
            m: Matcher<T>,
            transform: (IntTerm, T) -> R
        ) = Matcher { term ->
            if (term !is IntTerm) return@Matcher null
            val a = m.match(term) ?: return@Matcher null
            transform(term, a)
        }

        fun intValue() = intValue { _, s -> s }

        fun <R> intValue(
            transform: (IntTerm, Int) -> R
        ) = Matcher { term ->
            if (term !is IntTerm) return@Matcher null
            transform(term, term.value)
        }

        // BLOB
        fun blob() = blob { blob -> blob }

        fun <T> blob(
            m: Matcher<T>
        ) = blob(m) { blob, _ -> blob }

        fun <T, R> blob(
            m: Matcher<T>,
            transform: (BlobTerm, T) -> R
        ) = Matcher { term ->
            if (term !is BlobTerm) return@Matcher null
            val a = m.match(term) ?: return@Matcher null
            transform(term, a)
        }

        fun blobValue() = blobValue { _, s -> s }

        fun <R> blobValue(
            transform: (BlobTerm, Any) -> R
        ) = Matcher { term ->
            if (term !is BlobTerm) return@Matcher null
            transform(term, term.value)
        }

        // VAR
        fun `var`() = `var` { v -> v }

        fun <T> `var`(
            m: Matcher<T>
        ) = `var`(m) { v, _ -> v }

        fun <T, R> `var`(
            m: Matcher<T>,
            transform: (TermVar, T) -> R
        ) = Matcher { term ->
            if (term !is TermVar) return@Matcher null
            val a = m.match(term) ?: return@Matcher null
            transform(term, a)
        }

        fun varName() = varName { _, s -> s }

        fun <R> varName(
            transform: (TermVar, String) -> R
        ) = Matcher { term ->
            if (term !is TermVar) return@Matcher null
            transform(term, term.name)
        }
    }
}
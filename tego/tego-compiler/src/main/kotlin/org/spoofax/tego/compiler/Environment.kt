package org.spoofax.tego.compiler

import com.virtlink.kasm.JvmType
import com.virtlink.kasm.LocalVar
import org.spoofax.tego.ir.SymbolTable
import kotlin.jvm.Throws

class Environment private constructor(
    private val env: Map<String, LocalVar>,
) {

    companion object {
        /**
         * Returns an empty environment.
         */
        fun empty(): Environment {
            return Environment(emptyMap())
        }

        fun of(vararg lvs: LocalVar) = empty().withAll(*lvs)
        fun of(lvs: List<LocalVar>) = empty().withAll(lvs)
    }

    /**
     * Gets the local variable with the specified name.
     *
     * @param name the name of the local variable
     * @return the local variable
     */
    operator fun get(name: String): LocalVar? {
        return env[name]
    }

//    /**
//     * Gets the local variable with the specified name.
//     *
//     * @param name the name of the local variable
//     * @return the local variable
//     * @throws NoSuchElementException if no local variable with the specified name is in this environment
//     */
//    @Throws(NoSuchElementException::class)
//    operator fun get(name: String): LocalVar {
//        return env[name] ?: throw NoSuchElementException("Environment contains no variable with name '$name'.")
//    }

    /**
     * Returns a copy of this environment with the specified local variable added to it.
     *
     * If a local variable with the same name is already present,
     * the new one shadows the original one.
     *
     * @param lv the local variable to add
     * @return the new environment
     */
    fun with(lv: LocalVar): Environment {
        val name = lv.name
        require(name != null) { "Cannot add unnamed local variable to environment." }
        return Environment(env + (name to lv))
    }

    /**
     * Returns a copy of this environment with the specified local variables added to it.
     *
     * If a local variable with the same name is already present,
     * the new one shadows the original one.
     *
     * @param lvs the local variables to add
     * @return the new environment
     */
    fun withAll(vararg lvs: LocalVar): Environment {
        return withAll(lvs.toList())
    }

    /**
     * Returns a copy of this environment with the specified local variables added to it.
     *
     * If a local variable with the same name is already present,
     * the new one shadows the original one.
     *
     * @param lvs the local variables to add
     * @return the new environment
     */
    fun withAll(lvs: Iterable<LocalVar>): Environment {
        val pairs = lvs.map { lv ->
            val name = lv.name
            require(name != null) { "Cannot add unnamed local variable to environment." }
            (name to lv)
        }
        return Environment(env + pairs)
    }

    operator fun plus(lv: LocalVar): Environment
            = with(lv)
    operator fun plus(lv: Iterable<LocalVar>): Environment
            = withAll(lv)
}
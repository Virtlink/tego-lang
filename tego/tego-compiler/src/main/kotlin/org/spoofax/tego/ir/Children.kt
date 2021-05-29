package org.spoofax.tego.ir

/**
 * A list of child objects.
 */
class Children<T, O>(
    val owner: O,
    val getter: (T) -> O?,
    val setter: (T, O?) -> Unit,
) : AbstractMutableList<T>() {

    private val innerList = mutableListOf<T>()

    override val size: Int
        get() = innerList.size

    override fun get(index: Int): T {
        return innerList.get(index)
    }

    override fun set(index: Int, element: T): T {
        setOwner(element)
        return unsetOwner(innerList.set(index, element))
    }

    override fun add(index: Int, element: T) {
        setOwner(element)
        innerList.add(index, element)
    }

    override fun removeAt(index: Int): T {
        return unsetOwner(innerList.removeAt(index))
    }

    private fun setOwner(element: T): T {
        val currentOwner = getter(element)
        check(currentOwner == this.owner || currentOwner == null) { "New element belongs to a different collection." }
        setter(element, this.owner)
        return element
    }

    private fun unsetOwner(element: T): T {
        val currentOwner = getter(element)
        check(currentOwner == this.owner || currentOwner == null) { "Existing element already belongs to a different collection." }
        setter(element, this.owner)
        return element
    }
}

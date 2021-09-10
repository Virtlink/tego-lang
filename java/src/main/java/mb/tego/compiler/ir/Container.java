package mb.tego.compiler.ir;

import org.checkerframework.checker.nullness.qual.Nullable;

import java.util.AbstractList;
import java.util.ArrayList;
import java.util.function.BiConsumer;
import java.util.function.Function;

/**
 * A container for owned objects.
 *
 * @param <E> the type of objects in the container
 * @param <O> the type of owner
 */
public final class Container<E, O> extends AbstractList<E> {

    private final O owner;
    private final Function<E, @Nullable O> getter;
    private final BiConsumer<E, @Nullable O> setter;
    private final ArrayList<E> innerList = new ArrayList<>();

    /**
     * Initializes a new instance of the {@link Container} class.
     *
     * @param owner the owner of this container
     * @param getter the getter of the owner of the specified element
     * @param setter the setter of the owner of the specified element
     */
    public Container(O owner, Function<E, @Nullable O> getter, BiConsumer<E, @Nullable O> setter) {
        if (owner == null) throw new NullPointerException();
        if (getter == null) throw new NullPointerException();
        if (setter == null) throw new NullPointerException();

        this.owner = owner;
        this.getter = getter;
        this.setter = setter;
    }

    @Override
    public int size() {
        return this.innerList.size();
    }

    @Override
    public E get(int index) {
        if (index < 0 || index >= size())
            throw new IndexOutOfBoundsException();

        return this.innerList.get(index);
    }

    @Override
    public E set(int index, E element) {
        if (index < 0 || index >= size())
            throw new IndexOutOfBoundsException();
        if (element == null)
            throw new NullPointerException();

        setOwner(element);
        return clearOwner(innerList.set(index, element));
    }

    @Override
    public void add(int index, E element) {
        if (index < 0 || index > size())
            throw new IndexOutOfBoundsException();
        if (element == null)
            throw new NullPointerException();

        setOwner(element);
        innerList.add(index, element);
    }

    @Override
    public E remove(int index) {
        if (index < 0 || index >= size())
            throw new IndexOutOfBoundsException();

        return clearOwner(innerList.remove(index));
    }

    /**
     * Sets the owner of the specified element to this container's owner.
     *
     * @param element the element whose owner to set
     * @return the element
     */
    private E setOwner(E element) {
        final O currentOwner = getter.apply(element);
        if (currentOwner != this.owner && currentOwner != null)
            throw new IllegalArgumentException("New element belongs to a different collection.");
        setter.accept(element, this.owner);
        return element;
    }

    /**
     * Clears the owner of the specified element.
     *
     * @param element the element whose owner to clear
     * @return the elementr
     */
    private E clearOwner(E element) {
        final O currentOwner = getter.apply(element);
        if (currentOwner != this.owner && currentOwner != null)
            throw new IllegalArgumentException("Existing element already belongs to a different collection.");
        setter.accept(element, null);
        return element;
    }

}

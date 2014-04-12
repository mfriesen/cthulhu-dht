package ca.gobits.cthulhu;

import java.util.ArrayList;
import java.util.Collection;

/**
 * ArrayList back class that guarantee the order of elements.
 *
 * @param <E> - Type of Class
 */
public final class SortedList<E extends Comparable<E>> extends ArrayList<E> {

    /** serialVersionUID. */
    private static final long serialVersionUID = -3438432973777683931L;

    /** flag to indicate whether duplicate values are supported. */
    private final boolean allowDuplicates;

    /**
     * Constructs an empty list with an initial capacity of ten.
     * @param duplicates  whether to allow duplicates or not
     */
    public SortedList(final boolean duplicates) {
        super();
        this.allowDuplicates = duplicates;
    }

    /**
     * Constructs an list with initial capacity.
     * @param initialCapacity  inital capacity of list
     * @param duplicates  whether to allow duplicates or not
     */
    public SortedList(final int initialCapacity, final boolean duplicates) {
        super(initialCapacity);
        this.allowDuplicates = duplicates;
    }

    /**
     * Constructs a list containing the elements of the specified
     * collection, in the order they are returned by the collection's
     * iterator.
     *
     * @param c the collection whose elements are to be placed into this list
     * @param duplicates  whether to allow duplicates or not
     */
    public SortedList(final Collection<? extends E> c,
            final boolean duplicates) {
        this(c.size(), duplicates);
        addAll(c);
    }

    @Override
    public boolean add(final E e) {

        int position = indexOf(e, allowDuplicates);
        boolean added = position > -1;

        if (added) {
            super.add(position, e);
        }

        return added;
    }

    @Override
    public void add(final int index, final E element) {
        throw new UnsupportedOperationException(
                "element position cannot be specified");
    }

    @Override
    public boolean addAll(final int index,
            final Collection<? extends E> c) {
        throw new UnsupportedOperationException(
                "element position cannot be specified");
    }

    @Override
    public E set(final int index, final E element) {
        throw new UnsupportedOperationException(
                "element position cannot be specified");
    }

    @Override
    public boolean addAll(final Collection<? extends E> c) {

        boolean added = false;
        for (E e : c) {
            if (add(e)) {
                added = true;
            }
        }

        return added;
    }

    /**
     * Finds sorted position to insert object.
     * @param e  object to find position for
     * @param duplicates  whether to allow duplicates or not
     * @return int  position to insert object
     */
    private int indexOf(final E e, final boolean duplicates) {

        int imin = 0;
        int imax = size();
        // continue searching while [imin,imax] is not empty
        while (size() > 0 && imax > imin) {
            // calculate the midpoint for roughly equal partition
            int imid = imin + ((imax - imin) / 2);

            int c = get(imid).compareTo(e);

            if (c == 0) {
                // key found at index imid
                return duplicates ? imid : -1;
                // determine which subarray to search
            } else if (c < 0) {
                // change min index to search upper subarray
                imin = imid + 1;
            } else {
                // change max index to search lower subarray
                imax = imid - 1;
            }
        }

        return imin;
    }

    @SuppressWarnings("unchecked")
    @Override
    public int indexOf(final Object o) {

        if (o instanceof Comparable) {
            return indexOf((E) o, true);
        }

        throw new UnsupportedOperationException(
                "object must be instanceof Comparable");
    }
}

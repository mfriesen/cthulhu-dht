//
// Copyright 2013 Mike Friesen
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
// http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.
//

package ca.gobits.cthulhu;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

/**
 * ArrayList back class that guarantee the order of elements.
 *
 * @param <E> - Type of Class
 */
public final class SortedList<E> implements SortedCollection<E>, Serializable {

    /** serialVersionUID. */
    private static final long serialVersionUID = -3438432973777683931L;

    /** flag to indicate whether duplicate values are supported. */
    private final boolean allowDuplicates;

    /** Comparator instance. */
    private final Comparator<E> comparable;

    /** Backing list implementation. */
    private final ArrayList<E> list;

    /**
     * Constructs an empty list with an initial capacity of ten.
     * @param compare Comparable
     * @param duplicates  whether to allow duplicates or not
     */
    public SortedList(final Comparator<E> compare, final boolean duplicates) {
        list = new ArrayList<E>();
        this.allowDuplicates = duplicates;
        this.comparable = compare;
    }

    /**
     * Constructs an list with initial capacity.
     * @param initialCapacity  inital capacity of list
     * @param compare Comparable
     * @param duplicates  whether to allow duplicates or not
     */
    public SortedList(final int initialCapacity, final Comparator<E> compare,
            final boolean duplicates) {
        list = new ArrayList<E>(initialCapacity);
        this.allowDuplicates = duplicates;
        this.comparable = compare;
    }

    /**
     * Constructs a list containing the elements of the specified
     * collection, in the order they are returned by the collection's
     * iterator.
     *
     * @param c the collection whose elements are to be placed into this list
     * @param compare Comparable
     * @param duplicates  whether to allow duplicates or not
     */
    public SortedList(final Collection<? extends E> c,
        final Comparator<E> compare, final boolean duplicates) {
        this(c.size(), compare, duplicates);
        addAll(c);
    }

    @Override
    public boolean add(final E e) {

        int position = indexOf(e, allowDuplicates);
        boolean added = position > -1;

        if (added) {
            list.add(position, e);
        }

        return added;
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
        int imax = list.size();
        // continue searching while [imin,imax] is not empty
        while (list.size() > 0 && imax > imin) {
            // calculate the midpoint for roughly equal partition
            int imid = imin + ((imax - imin) / 2);

            int c = comparable.compare(e, list.get(imid));

            if (c == 0) {
                // key found at index imid
                return duplicates ? imid : -1;
                // determine which subarray to search
            } else if (c < 0) {
                // change max index to search lower subarray
                imax = imid - 1;
            } else {
                // change min index to search upper subarray
                imin = imid + 1;
            }
        }

        if (list.size() > imin) {
            int c = comparable.compare(e, list.get(imin));
            if (c > 0) {
                imin++;
            }
        }

        return imin;
    }

    @Override
    public int indexOf(final E o) {
        return indexOf(o, true);
    }

    @Override
    public E get(final E e) {

        E nodeMatch = null;
        int index = this.indexOf(e);

        if (index >= 0 && index < list.size()) {
            E foundNode = list.get(index);
            if (foundNode.equals(e)) {
                nodeMatch = foundNode;
            }
        }

        return nodeMatch;
    }

    @Override
    public int size() {
        return list.size();
    }

    @Override
    public Object[] toArray() {
        return list.toArray();
    }

    @Override
    public E get(final int index) {
        return list.get(index);
    }

    @Override
    public List<E> subList(final int fromIndex, final int toIndex) {
        return list.subList(fromIndex, toIndex);
    }

    @Override
    public Iterator<E> iterator() {
        return list.iterator();
    }

    @Override
    public void clear() {
        list.clear();
    }

    @Override
    public boolean isEmpty() {
        return list.isEmpty();
    }

    @Override
    public boolean remove(final E o) {
        return list.remove(o);
    }

    @Override
    public boolean removeAll(final Collection<E> c) {
        return list.removeAll(c);
    }
}

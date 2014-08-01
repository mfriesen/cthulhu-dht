//
// Copyright 2014 Mike Friesen
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

import java.util.Collection;
import java.util.Iterator;
import java.util.List;

/**
 * The root interface in the Sorted Collection hierarchy.
 * @param <E>
 */
public interface SortedCollection<E> extends Iterable<E> {

    /**
     * Adds object to list.
     * @param e  object to add
     * @return boolean whether add was successful or not
     */
    boolean add(final E e);

    /**
     * Adds collection of objects to list.
     * @param c  collection of objects
     * @return boolean whether add was successful or not
     */
    boolean addAll(final Collection<? extends E> c);

    /**
     * Removes all of the elements from this collection.
     */
    void clear();

    /**
     * Finds equals object from list.
     * @param e  object to find
     * @return E
     */
    E get(final E e);

    /**
     * Gets object at specific index.
     * @param index  position to get object from
     * @return E
     */
    E get(final int index);

    /**
     * Find the position of object in list.
     * @param o object to find position of
     * @return int
     */
    int indexOf(final E o);

    /**
     * @return Returns true if this collection contains no elements.
     */
    boolean isEmpty();

    /**
     * @return Iterator<E>
     */
    @Override
    Iterator<E> iterator();

    /**
     * Removes a single instance of the specified element from this collection,
     * if it is present (optional operation).
     *
     * @param o  object
     * @return boolean
     */
    boolean remove(final E o);

    /**
     * Removes all of this collection's elements that are also contained in the
     * specified collection (optional operation).
     *
     * @param c  Collection
     * @return boolean
     */
    boolean removeAll(final Collection<E> c);

    /**
     * size of list.
     * @return int
     */
    int size();

    /**
     * Returns sublist.
     * @param fromIndex  start index
     * @param toIndex  end index
     * @return List<E>
     */
    List<E> subList(final int fromIndex, final int toIndex);

    /**
     * @return Object[]
     */
    Object[] toArray();

}

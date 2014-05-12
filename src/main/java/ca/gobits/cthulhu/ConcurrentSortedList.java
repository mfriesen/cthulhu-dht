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
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * A Sorted List implementation that is thread safe.
 * @param <E>
 *
 */
public final class ConcurrentSortedList<E> implements SortedCollection<E>,
        Serializable {

    /** serialVersionUID. */
    private static final long serialVersionUID = 1013436370878496184L;

    /** Read/Write Locking. */
    private final ReentrantReadWriteLock locking = new ReentrantReadWriteLock();

    /** Read Lock. */
    private final Lock readLock = locking.readLock();

    /** Write Lock. */
    private final Lock writeLock = locking.writeLock();

    /** SortedList reference. */
    private final SortedList<E> list;

    /**
     * Constructs an empty list with an initial capacity of ten.
     * @param compare Comparable
     * @param duplicates  whether to allow duplicates or not
     */
    public ConcurrentSortedList(final Comparator<E> compare,
            final boolean duplicates) {
        list = new SortedList<E>(compare, duplicates);
    }

    /**
     * Constructs an list with initial capacity.
     * @param initialCapacity  inital capacity of list
     * @param compare Comparable
     * @param duplicates  whether to allow duplicates or not
     */
    public ConcurrentSortedList(final int initialCapacity,
            final Comparator<E> compare, final boolean duplicates) {
        list = new SortedList<E>(initialCapacity, compare, duplicates);
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
    public ConcurrentSortedList(final Collection<? extends E> c,
        final Comparator<E> compare, final boolean duplicates) {
        list = new SortedList<>(c,  compare, duplicates);
    }

    @Override
    public boolean add(final E e) {

        boolean result = false;
        writeLock.lock();

        try {
            result = this.list.add(e);
        } finally {
            writeLock.unlock();
        }

        return result;
    }

    @Override
    public boolean addAll(final Collection<? extends E> c) {

        boolean result = false;
        readLock.lock();

        try {
            result = this.list.addAll(c);
        } finally {
            readLock.unlock();
        }

        return result;
    }

    @Override
    public int indexOf(final E o) {

        readLock.lock();

        try {
            return list.indexOf(o);
        } finally {
            readLock.unlock();
        }
    }

    @Override
    public E get(final E e) {
        readLock.lock();

        try {
            return list.get(e);
        } finally {
            readLock.unlock();
        }
    }

    @Override
    public int size() {
        readLock.lock();

        try {
            return list.size();
        } finally {
            readLock.unlock();
        }

    }

    @Override
    public Object[] toArray() {
        readLock.lock();

        try {
            return list.toArray();
        } finally {
            readLock.unlock();
        }

    }

    @Override
    public E get(final int index) {
        readLock.lock();

        try {
            return list.get(index);
        } finally {
            readLock.unlock();
        }

    }

    @Override
    public List<E> subList(final int fromIndex, final int toIndex) {
        readLock.lock();

        try {
            return list.subList(fromIndex, toIndex);
        } finally {
            readLock.unlock();
        }

    }

    @Override
    public Iterator<E> iterator() {
        readLock.lock();

        try {
            return list.iterator();
        } finally {
            readLock.unlock();
        }
    }

    @Override
    public void clear() {
        writeLock.lock();

        try {
            this.list.clear();
        } finally {
            writeLock.unlock();
        }
    }

    @Override
    public boolean isEmpty() {
        readLock.lock();

        try {
            return list.isEmpty();
        } finally {
            readLock.unlock();
        }
    }

    @Override
    public boolean remove(final E o) {
        writeLock.lock();

        try {
            return list.remove(o);
        } finally {
            writeLock.unlock();
        }
    }

    @Override
    public boolean removeAll(final Collection<E> c) {
        writeLock.lock();

        try {
            return list.removeAll(c);
        } finally {
            writeLock.unlock();
        }
    }
}

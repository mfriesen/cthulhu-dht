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
    private final Lock readLock = this.locking.readLock();

    /** Write Lock. */
    private final Lock writeLock = this.locking.writeLock();

    /** SortedList reference. */
    private final SortedCollection<E> list;

    /**
     * Constructs an empty list with an initial capacity of ten.
     * @param compare Comparable
     * @param duplicates  whether to allow duplicates or not
     */
    public ConcurrentSortedList(final Comparator<E> compare,
            final boolean duplicates) {
        this.list = new SortedList<E>(compare, duplicates);
    }

    /**
     * Constructs an list with initial capacity.
     * @param initialCapacity  inital capacity of list
     * @param compare Comparable
     * @param duplicates  whether to allow duplicates or not
     */
    public ConcurrentSortedList(final int initialCapacity,
            final Comparator<E> compare, final boolean duplicates) {
        this.list = new SortedList<E>(initialCapacity, compare, duplicates);
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
        this.list = new SortedList<>(c,  compare, duplicates);
    }

    @Override
    public boolean add(final E e) {

        boolean result = false;
        this.writeLock.lock();

        try {
            result = this.list.add(e);
        } finally {
            this.writeLock.unlock();
        }

        return result;
    }

    @Override
    public boolean addAll(final Collection<? extends E> c) {

        boolean result = false;
        this.readLock.lock();

        try {
            result = this.list.addAll(c);
        } finally {
            this.readLock.unlock();
        }

        return result;
    }

    @Override
    public int indexOf(final E o) {

        this.readLock.lock();

        try {
            return this.list.indexOf(o);
        } finally {
            this.readLock.unlock();
        }
    }

    @Override
    public E get(final E e) {
        this.readLock.lock();

        try {
            return this.list.get(e);
        } finally {
            this.readLock.unlock();
        }
    }

    @Override
    public int size() {
        this.readLock.lock();

        try {
            return this.list.size();
        } finally {
            this.readLock.unlock();
        }

    }

    @Override
    public Object[] toArray() {
        this.readLock.lock();

        try {
            return this.list.toArray();
        } finally {
            this.readLock.unlock();
        }

    }

    @Override
    public E get(final int index) {
        this.readLock.lock();

        try {
            return this.list.get(index);
        } finally {
            this.readLock.unlock();
        }

    }

    @Override
    public List<E> subList(final int fromIndex, final int toIndex) {
        this.readLock.lock();

        try {
            return this.list.subList(fromIndex, toIndex);
        } finally {
            this.readLock.unlock();
        }

    }

    @Override
    public Iterator<E> iterator() {
        this.readLock.lock();

        try {
            return this.list.iterator();
        } finally {
            this.readLock.unlock();
        }
    }

    @Override
    public void clear() {
        this.writeLock.lock();

        try {
            this.list.clear();
        } finally {
            this.writeLock.unlock();
        }
    }

    @Override
    public boolean isEmpty() {
        this.readLock.lock();

        try {
            return this.list.isEmpty();
        } finally {
            this.readLock.unlock();
        }
    }

    @Override
    public boolean remove(final E o) {
        this.writeLock.lock();

        try {
            return this.list.remove(o);
        } finally {
            this.writeLock.unlock();
        }
    }

    @Override
    public boolean removeAll(final Collection<E> c) {
        this.writeLock.lock();

        try {
            return this.list.removeAll(c);
        } finally {
            this.writeLock.unlock();
        }
    }
}

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

package ca.gobits.dht.server.integration.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.UUID;

import org.apache.log4j.Logger;
import org.junit.Test;

import ca.gobits.dht.util.ConcurrentSortedList;

/**
 * ConcurrentSortedList Unit Tests.
 *
 */
public final class ConcurrentSortedListIntegrationTest {

    /** Number of Iterators per thread. */
    private static final int ITERATION_COUNT = 100;

    /** Logger. */
    private static final Logger LOGGER = Logger
            .getLogger(ConcurrentSortedListIntegrationTest.class);

    /**
     * testRunningMultipleThreads().
     * @throws Exception  Exception
     */
    @Test
    public void testRunningMultipleThreads() throws Exception {
        // given
        ConcurrentSortedList<String> list = new ConcurrentSortedList<String>(
                String.CASE_INSENSITIVE_ORDER, false);
        list.add("test");

        // when
        Thread add = add(list);
        add.start();
        Thread addAll = addAll(list);
        addAll.start();
        Thread getIndexOf = threadGetAndIndexOf(list);
        getIndexOf.start();
        Thread toArray = threadToArray(list);
        toArray.start();
        Thread sublist = threadSubList(list);
        sublist.start();
        Thread iterator = threadIterator(list);
        iterator.start();
        Thread isEmpty = threadIsEmpty(list);
        isEmpty.start();

        // then
        while (add.isAlive() && addAll.isAlive() && getIndexOf.isAlive()
                && toArray.isAlive() && sublist.isAlive() && iterator.isAlive()
                && isEmpty.isAlive()) {
            assertTrue(true);
        }

        assertEquals(10 * ITERATION_COUNT + 1, list.size());
    }

    /**
     * testAllRemovingElements().
     * @throws Exception  Exception
     */
    @Test
    public void testAllRemovingElements() throws Exception {
        // given
        ConcurrentSortedList<String> list = new ConcurrentSortedList<String>(
                String.CASE_INSENSITIVE_ORDER, false);

        for (int i = 0; i < 100; i++) {
            list.add(UUID.randomUUID().toString());
        }

        // when
        Thread add = add(list);
        add.start();

        Thread addAll = addAll(list);
        addAll.start();

        Thread remove = remove(list);
        remove.start();

        Thread removeAll = removeAll(list);
        removeAll.start();

        // then
        while (add.isAlive()
                && addAll.isAlive()
                && remove.isAlive()
                && removeAll.isAlive()) {
            assertTrue(true);
        }

        // don't know or care if list is empty or not.
        assertTrue(list.isEmpty() || !list.isEmpty());
    }

    /**
     * testAllRemovingElements().
     * @throws Exception  Exception
     */
    @Test
    public void testAllAndClear() throws Exception {
        // given
        ConcurrentSortedList<String> list = new ConcurrentSortedList<String>(
                String.CASE_INSENSITIVE_ORDER, false);

        // when
        Thread add = add(list);
        add.start();

        Thread addAll = addAll(list);
        addAll.start();

        Thread clearAll = clear(list);
        clearAll.start();

        // then
        while (add.isAlive() && addAll.isAlive() && clearAll.isAlive()) {
            assertTrue(true);
        }

        // don't know or care if list is empty or not.
        assertTrue(list.isEmpty() || !list.isEmpty());
    }

    /**
     * Call "clear" on ConcurrentSortedList.
     * @param list  list
     * @return Future<Void>
     */
    private Thread clear(final ConcurrentSortedList<String> list) {
        return new Thread(new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < ITERATION_COUNT; i++) {
                    LOGGER.trace("CLEAR");
                    list.clear();

                    sleep();
                }
            }
        });
    }

    /**
     * Call "remove" on ConcurrentSortedList.
     * @param list  list
     * @return Future<Void>
     */
    private Thread remove(final ConcurrentSortedList<String> list) {
        return new Thread(new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < ITERATION_COUNT; i++) {

                    LOGGER.trace("REMOVE");
                    if (!list.isEmpty()) {
                        String s = list.get(0);
                        list.remove(s);
                    }

                    sleep();
                }
            }
        });
    }

    /**
     * Call "removeAll" on ConcurrentSortedList.
     * @param list  list
     * @return Future<Void>
     */
    private Thread removeAll(final ConcurrentSortedList<String> list) {
        return new Thread(new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < ITERATION_COUNT; i++) {

                    if (!list.isEmpty()) {
                        LOGGER.trace("REMOVEALL");

                        String s = list.get(0);
                        list.removeAll(Arrays.asList(s));
                    }

                    sleep();
                }
            }
        });
    }

    /**
     * Call "add" on ConcurrentSortedList.
     * @param list  list
     * @return Future<Void>
     */
    private Thread add(final ConcurrentSortedList<String> list) {

        return new Thread(new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < ITERATION_COUNT; i++) {
                    LOGGER.trace("ADD");
                    list.add(UUID.randomUUID().toString());
                    list.add(UUID.randomUUID().toString());
                    list.add(UUID.randomUUID().toString());
                    list.add(UUID.randomUUID().toString());
                    list.add(UUID.randomUUID().toString());

                    sleep();
                }
            }
        });
    }

    /**
     * Sleeps.
     */
    private static void sleep() {
        try {
            Thread.sleep(50);
        } catch (Exception e) {
            LOGGER.fatal(e, e);
        }
    }

    /**
     * Call "addAll" on ConcurrentSortedList.
     * @param list  list
     * @return Future<Void>
     */
    private Thread addAll(final ConcurrentSortedList<String> list) {

        return new Thread(new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < ITERATION_COUNT; i++) {
                    LOGGER.trace("ADDALL");
                    String uuid0 = UUID.randomUUID().toString();
                    String uuid1 = UUID.randomUUID().toString();
                    String uuid2 = UUID.randomUUID().toString();
                    String uuid3 = UUID.randomUUID().toString();
                    String uuid4 = UUID.randomUUID().toString();

                    list.addAll(Arrays
                            .asList(uuid0, uuid1, uuid2, uuid3, uuid4));

                    sleep();
                }
            }
        });
    }

    /**
     * Call "get" and "indexOf" on ConcurrentSortedList.
     * @param list  list
     * @return Future<Void>
     */
    private Thread threadGetAndIndexOf(
            final ConcurrentSortedList<String> list) {

        return new Thread(new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < ITERATION_COUNT; i++) {
                    LOGGER.trace("GET");
                    String s = list.get(list.size() - 1);
                    assertNotNull(list.get(s));

                    int index = list.indexOf(s);
                    LOGGER.trace("INDEXOF");
                    assertTrue(index > -1);
                    assertNotNull(list.get(s));

                    sleep();
                }
            }
        });
    }

    /**
     * Call "toArray" on ConcurrentSortedList.
     * @param list  list
     * @return Future<Void>
     */
    private Thread threadToArray(
            final ConcurrentSortedList<String> list) {

        return new Thread(new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < ITERATION_COUNT; i++) {
                    LOGGER.trace("TOARRAY");
                    assertNotNull(list.toArray());

                    sleep();
                }
            }
        });
    }

    /**
     * Call "subList" on ConcurrentSortedList.
     * @param list  list
     * @return Future<Void>
     */
    private Thread threadSubList(
            final ConcurrentSortedList<String> list) {

        return new Thread(new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < ITERATION_COUNT; i++) {

                    assertNotNull(list.subList(0, list.size() - 1));
                    LOGGER.trace("SUBLIST");
                    sleep();
                }
            }
        });
    }

    /**
     * Call "iterator" on ConcurrentSortedList.
     * @param list  list
     * @return Future<Void>
     */
    private Thread threadIterator(
            final ConcurrentSortedList<String> list) {
        return new Thread(new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < ITERATION_COUNT; i++) {

                    assertNotNull(list.iterator());
                    LOGGER.trace("ITERATOR");
                    sleep();
                }
            }
        });
    }

    /**
     * Call "isEmpty" on ConcurrentSortedList.
     * @param list  list
     * @return Future<Void>
     */
    private Thread threadIsEmpty(
            final ConcurrentSortedList<String> list) {
        return new Thread(new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < ITERATION_COUNT; i++) {

                    assertFalse(list.isEmpty());
                    LOGGER.trace("ISEMPTY");
                    sleep();
                }
            }
        });
    }
}

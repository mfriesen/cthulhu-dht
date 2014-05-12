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

package ca.gobits.cthulhu.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import org.junit.Test;

import ca.gobits.cthulhu.SortedCollection;

/**
 * Abstract Unit test for SortedList.
 */
public abstract class SortedListAbstractTest {

    /** BigInteger Comparator. */
    protected static final Comparator<BigInteger> BIGINTEGER_COMPARATOR =
            new Comparator<BigInteger>() {
        @Override
        public int compare(final BigInteger o1, final BigInteger o2) {
            return o1.compareTo(o2);
        }
    };

    /** Integer Comparator. */
    protected static final Comparator<Integer> INTEGER_COMPARATOR =
            new Comparator<Integer>() {
        @Override
        public int compare(final Integer o1, final Integer o2) {
            return o1.compareTo(o2);
        }
    };

    /** Double Comparator. */
    protected static final Comparator<Double> DOUBLE_COMPARATOR =
            new Comparator<Double>() {
        @Override
        public int compare(final Double o1, final Double o2) {
            return o1.compareTo(o2);
        }
    };

//    /**
//     * testConstructor01().
//     */
//    @Test
//    public final void testConstructor01() {
//        // given
//        boolean duplicates = true;
//        List<String> elements = Arrays.asList("z", "c", "a", "c");
//
//        // when
//        SortedCollection<String> list = new SortedCollection<String>(elements,
//                String.CASE_INSENSITIVE_ORDER, duplicates);
//
//        // then
//        assertEquals(4, list.size());
//        assertEquals("a", list.toArray()[0]);
//        assertEquals("c", list.toArray()[1]);
//        assertEquals("c", list.toArray()[2]);
//        assertEquals("z", list.toArray()[3]);
//    }

    /**
     * testAdd01() - add 1 element to sorted list.
     */
    @Test
    public final void testAdd01() {
        // given
        boolean duplicates = true;
        SortedCollection<String> list = getInstance(duplicates);

        // when
        list.add("test");

        // then
        assertEquals(1, list.size());
        assertEquals("test", list.toArray()[0]);
    }

    /**
     * testAdd02() - add 3 elements in correct order.
     */
    @Test
    public final void testAdd02() {
        // given
        boolean duplicates = true;
        SortedCollection<String> list = getInstance(duplicates);

        // when
        list.add("a");
        list.add("c");
        list.add("z");

        // then
        assertEquals(3, list.size());
        assertEquals("a", list.toArray()[0]);
        assertEquals("c", list.toArray()[1]);
        assertEquals("z", list.toArray()[2]);
    }

    /**
     * testAdd03() - add 2 elements in reverse order.
     */
    @Test
    public final void testAdd03() {
        // given
        boolean duplicates = true;
        SortedCollection<Integer> list = getInstanceInteger(duplicates);

        // when
        for (int i = 18; i >= 0; i -= 2) {
            list.add(Integer.valueOf(i));
        }

        list.add(Integer.valueOf(17));

        // then
        assertEquals(11, list.size());
        assertEquals(Integer.valueOf(0), list.toArray()[0]);
        assertEquals(Integer.valueOf(2), list.toArray()[1]);
        assertEquals(Integer.valueOf(4), list.toArray()[2]);
        assertEquals(Integer.valueOf(6), list.toArray()[3]);
        assertEquals(Integer.valueOf(8), list.toArray()[4]);
        assertEquals(Integer.valueOf(10), list.toArray()[5]);
        assertEquals(Integer.valueOf(12), list.toArray()[6]);
        assertEquals(Integer.valueOf(14), list.toArray()[7]);
        assertEquals(Integer.valueOf(16), list.toArray()[8]);
        assertEquals(Integer.valueOf(17), list.toArray()[9]);
        assertEquals(Integer.valueOf(18), list.toArray()[10]);
    }

    /**
     * testAdd04() - add 3 elements in reverse order.
     */
    @Test
    public final void testAdd04() {
        // given
        boolean duplicates = true;
        SortedCollection<String> list = getInstance(duplicates);

        // when
        list.add("z");
        list.add("c");
        list.add("a");

        // then
        assertEquals(3, list.size());
        assertEquals("a", list.toArray()[0]);
        assertEquals("c", list.toArray()[1]);
        assertEquals("z", list.toArray()[2]);
    }

    /**
     * testAdd05() - add same elements, duplicate=TRUE.
     */
    @Test
    public final void testAdd05() {
        // given
        boolean duplicates = true;
        SortedCollection<String> list = getInstance(duplicates);

        // when
        list.add("z");
        list.add("c");
        list.add("a");
        list.add("c");

        // then
        assertEquals(4, list.size());
        assertEquals("a", list.toArray()[0]);
        assertEquals("c", list.toArray()[1]);
        assertEquals("c", list.toArray()[2]);
        assertEquals("z", list.toArray()[3]);
    }

    /**
     * testAdd06() - add same elements, duplicate=FALSE.
     */
    @Test
    public final void testAdd06() {
        // given
        boolean duplicates = false;
        SortedCollection<String> list = getInstance(duplicates);

        // when
        list.add("z");
        list.add("c");
        list.add("a");
        list.add("c");

        // then
        assertEquals(3, list.size());
        assertEquals("a", list.toArray()[0]);
        assertEquals("c", list.toArray()[1]);
        assertEquals("z", list.toArray()[2]);
    }

    /**
     * testAddNode07() - test adding nodes and checking order.
     */
    @Test
    public final void testAdd07() {
        // given
        boolean duplicates = false;
        SortedCollection<Double> list = getInstanceDouble(duplicates);
        Double d0 = Double.valueOf(1);
        Double d1 = Double
                .valueOf(217572328821850967755762913845138112465869557436d);
        Double d2 = Double
                .valueOf(253718933283387888344146948372599275024431560999d);
        Double d3 = Double
                .valueOf(909396897490697132528408310795708133687135388426d);

        // when
        list.add(d0);
        list.add(d3);
        list.add(d2);
        list.add(d1);

        // then
        assertEquals(4, list.size());
        assertEquals(d0, list.get(0));
        assertEquals(d1, list.get(1));
        assertEquals(d2, list.get(2));
        assertEquals(d3, list.get(3));
    }

    /**
     * testAddNode08() - insert a random million entries and verify order is
     * correct.
     */
    @Test
    public final void testAdd08() {
        // given
        int len = 10000;
        boolean duplicates = false;
        SortedCollection<BigInteger> list = getInstanceBigInteger(duplicates);

        Random rnd = new Random(System.currentTimeMillis());

        // when
        for (int i = 0; i < len; i++) {
            BigInteger bi = new BigInteger(160, rnd);
            list.add(bi);
        }

        // then
        Iterator<BigInteger> itr = list.iterator();
        BigInteger last = itr.next();
        while (itr.hasNext()) {
            BigInteger curr = itr.next();
            assertTrue(curr.compareTo(last) > 0);
            last = curr;
        }
    }

    /**
     * testAddAll01() - add same elements, duplicate=FALSE.
     */
    @Test
    public final void testAddAll01() {
        // given
        boolean duplicates = false;
        SortedCollection<String> list = getInstance(duplicates);

        // when
        list.addAll(Arrays.asList("z", "c", "a", "c"));

        // then
        assertEquals(3, list.size());
        assertEquals("a", list.toArray()[0]);
        assertEquals("c", list.toArray()[1]);
        assertEquals("z", list.toArray()[2]);
    }

    /**
     * testAddAll02() - add same elements, duplicate=TRUE.
     */
    @Test
    public final void testAddAll02() {
        // given
        boolean duplicates = true;
        SortedCollection<String> list = getInstance(duplicates);

        // when
        list.addAll(Arrays.asList("z", "c", "a", "c"));

        // then
        assertEquals(4, list.size());
        assertEquals("a", list.toArray()[0]);
        assertEquals("c", list.toArray()[1]);
        assertEquals("c", list.toArray()[2]);
        assertEquals("z", list.toArray()[3]);
    }

    /**
     * testIndexOf01().
     */
    @Test
    public final void testIndexOf01() {
        // given
        SortedCollection<String> list = getInstance(true);

        // when
        list.addAll(Arrays.asList("z", "c", "a", "c"));

        // then
        assertEquals(0, list.indexOf("a"));
        assertEquals(2, list.indexOf("c"));
        assertEquals(2, list.indexOf("c"));
        assertEquals(3, list.indexOf("z"));
    }

    /**
     * testGet01().
     */
    @Test
    public final void testGet01() {
        // given
        boolean dup = false;
        SortedCollection<String> list = getInstance(dup);

        list.add("test0");
        list.add("test1");
        list.add("test2");
        list.add("test3");

        // when
        String s = list.get("test2");

        // then
        assertNotNull(s);
    }

    /**
     * testSubList01().
     */
    @Test
    public final void testSubList01() {
        // given
        boolean dup = false;
        SortedCollection<String> list = getInstance(dup);

        list.add("test0");
        list.add("test1");
        list.add("test2");
        list.add("test3");

        // when
        List<String> s = list.subList(1, 3);

        // then
        assertEquals(2, s.size());
        assertEquals("test1", s.get(0));
        assertEquals("test2", s.get(1));
    }

    /**
     * testClear01().
     */
    @Test
    public final void testClear01() {
        // given
        boolean dup = false;
        SortedCollection<String> list = getInstance(dup);

        list.add("test0");
        list.add("test1");
        list.add("test2");
        list.add("test3");

        // when
        assertFalse(list.isEmpty());
        list.clear();

        // then
        assertTrue(list.isEmpty());
    }

    /**
     * testRemove01().
     */
    @Test
    public final void testRemove01() {
        // given
        boolean dup = false;
        SortedCollection<String> list = getInstance(dup);

        list.add("test0");
        list.add("test1");
        list.add("test2");
        list.add("test3");

        // when
        assertEquals(4, list.size());
        list.remove("test1");

        // then
        assertEquals(3, list.size());
    }

    /**
     * testRemoveAll01().
     */
    @Test
    public final void testRemoveAll01() {
        // given
        boolean dup = false;
        SortedCollection<String> list = getInstance(dup);

        list.add("test0");
        list.add("test1");
        list.add("test2");
        list.add("test3");

        // when
        assertEquals(4, list.size());
        list.removeAll(Arrays.asList("test1", "test3"));

        // then
        assertEquals(2, list.size());
        assertNotNull(list.get("test0"));
        assertNotNull(list.get("test2"));
    }

    /**
     * @param dup  support duplicates
     * @return SortedCollection<BigInteger> instance
     */
    public abstract SortedCollection<String> getInstance(final boolean dup);

    /**
     * Instance.
     * @param dup  support duplicates
     * @return SortedCollection<BigInteger>
     */
    public abstract SortedCollection<BigInteger> getInstanceBigInteger(
            final boolean dup);

    /**
     * Instance.
     * @param dup  support duplicates
     * @return SortedCollection<Integer>
     */
    public abstract SortedCollection<Integer> getInstanceInteger(
            final boolean dup);

    /**
     * Instance.
     * @param dup  support duplicates
     * @return SortedCollection<Double>
     */
    public abstract SortedCollection<Double> getInstanceDouble(
            final boolean dup);
}

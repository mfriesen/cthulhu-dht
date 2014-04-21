package ca.gobits.cthulhu.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import org.junit.Test;

import ca.gobits.cthulhu.SortedList;

/**
 * Unit test for SortedList.class.
 */
public final class SortedListUnitTest {

    /**
     * testConstructor01().
     */
    @Test
    public void testConstructor01() {
        // given
        boolean duplicates = true;
        List<String> elements = Arrays.asList("z", "c", "a", "c");

        // when
        SortedList<String> list = new SortedList<String>(elements, duplicates);

        // then
        assertEquals(4, list.size());
        assertEquals("a", list.toArray()[0]);
        assertEquals("c", list.toArray()[1]);
        assertEquals("c", list.toArray()[2]);
        assertEquals("z", list.toArray()[3]);
    }

    /**
     * testAdd01() - add 1 element to sorted list.
     */
    @Test
    public void testAdd01() {
        // given
        boolean duplicates = true;
        SortedList<String> list = new SortedList<String>(duplicates);

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
    public void testAdd02() {
        // given
        boolean duplicates = true;
        SortedList<String> list = new SortedList<String>(duplicates);

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
    public void testAdd03() {
        // given
        boolean duplicates = true;
        SortedList<Integer> list = new SortedList<Integer>(duplicates);

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
    public void testAdd04() {
        // given
        boolean duplicates = true;
        SortedList<String> list = new SortedList<String>(duplicates);

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
    public void testAdd05() {
        // given
        boolean duplicates = true;
        SortedList<String> list = new SortedList<String>(duplicates);

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
    public void testAdd06() {
        // given
        boolean duplicates = false;
        SortedList<String> list = new SortedList<String>(duplicates);

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
    public void testAdd07() {
        // given
        boolean duplicates = false;
        SortedList<Double> list = new SortedList<Double>(duplicates);
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
    public void testAdd08() {
        // given
        int len = 10000;
        boolean duplicates = false;
        SortedList<BigInteger> list = new SortedList<BigInteger>(duplicates);
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
    public void testAddAll01() {
        // given
        boolean duplicates = false;
        SortedList<String> list = new SortedList<String>(duplicates);

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
    public void testAddAll02() {
        // given
        boolean duplicates = true;
        SortedList<String> list = new SortedList<String>(duplicates);

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
    public void testIndexOf01() {
        // given
        SortedList<String> list = new SortedList<String>(true);

        // when
        list.addAll(Arrays.asList("z", "c", "a", "c"));

        // then
        assertEquals(0, list.indexOf("a"));
        assertEquals(2, list.indexOf("c"));
        assertEquals(2, list.indexOf("c"));
        assertEquals(3, list.indexOf("z"));
    }

    /**
     * testIndexOf02() - not instance of Comparable.
     */
    @Test(expected = UnsupportedOperationException.class)
    public void testIndexOf02() {
        // given
        SortedList<String> list = new SortedList<String>(true);

        // when
        list.indexOf(new ArrayList<String>());

        // then
    }

    /**
     * testAddAtIndex01().
     */
    @Test(expected = UnsupportedOperationException.class)
    public void testAddAtIndex01() {
        // given
        boolean duplicates = false;
        SortedList<String> list = new SortedList<String>(duplicates);

        // when
        list.add(0, "AD");

        // then
    }

    /**
     * testAddAllAtIndex01().
     */
    @Test(expected = UnsupportedOperationException.class)
    public void testAddAllAtIndex01() {
        // given
        boolean duplicates = false;
        SortedList<String> list = new SortedList<String>(duplicates);

        // when
        list.addAll(0, Arrays.asList("AD"));

        // then
    }

    /**
     * testSet01().
     */
    @Test(expected = UnsupportedOperationException.class)
    public void testSet01() {
        // given
        boolean duplicates = false;
        SortedList<String> list = new SortedList<String>(duplicates);

        // when
        list.set(0, "AD");

        // then
    }
}

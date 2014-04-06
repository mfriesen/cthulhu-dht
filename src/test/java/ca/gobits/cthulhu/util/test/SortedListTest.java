package ca.gobits.cthulhu.util.test;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Test;

import ca.gobits.cthulhu.util.SortedList;

/**
 * Unit test for SortedList.class.
 */
public final class SortedListTest {

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
     * testAdd02() - add 2 elements in correct order.
     */
    @Test
    public void testAdd02() {
        // given
        boolean duplicates = true;
        SortedList<String> list = new SortedList<String>(duplicates);

        // when
        list.add("a");
        list.add("z");

        // then
        assertEquals(2, list.size());
        assertEquals("a", list.toArray()[0]);
        assertEquals("z", list.toArray()[1]);
    }

    /**
     * testAdd03() - add 2 elements in reverse order.
     */
    @Test
    public void testAdd03() {
        // given
        boolean duplicates = true;
        SortedList<String> list = new SortedList<String>(duplicates);

        // when
        list.add("z");
        list.add("a");

        // then
        assertEquals(2, list.size());
        assertEquals("a", list.toArray()[0]);
        assertEquals("z", list.toArray()[1]);
    }

    /**
     * testAdd04() - add same elements, duplicate=TRUE.
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
        list.add("c");

        // then
        assertEquals(4, list.size());
        assertEquals("a", list.toArray()[0]);
        assertEquals("c", list.toArray()[1]);
        assertEquals("c", list.toArray()[2]);
        assertEquals("z", list.toArray()[3]);
    }

    /**
     * testAdd04() - add same elements, duplicate=FALSE.
     */
    @Test
    public void testAdd05() {
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

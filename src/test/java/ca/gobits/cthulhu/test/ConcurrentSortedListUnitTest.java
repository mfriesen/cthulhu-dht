package ca.gobits.cthulhu.test;

import static org.junit.Assert.assertEquals;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.List;

import org.junit.Test;

import ca.gobits.cthulhu.ConcurrentSortedList;
import ca.gobits.cthulhu.SortedCollection;

/**
  ConcurrentSortedList Unit Tests.
 *
 */
public final class ConcurrentSortedListUnitTest extends SortedListAbstractTest {

    /**
     * testConstructor01().
     */
    @Test
    public void testConstructor01() {
        // given
        boolean duplicates = true;
        List<String> elements = Arrays.asList("z", "c", "a", "c");

        // when
        SortedCollection<String> list = new ConcurrentSortedList<String>(
                elements, String.CASE_INSENSITIVE_ORDER, duplicates);

        // then
        assertEquals(4, list.size());
        assertEquals("a", list.toArray()[0]);
        assertEquals("c", list.toArray()[1]);
        assertEquals("c", list.toArray()[2]);
        assertEquals("z", list.toArray()[3]);
    }

    /**
     * testConstructor01().
     */
    @Test
    public void testConstructor02() {
        // given
        boolean duplicates = true;
        List<String> elements = Arrays.asList("z", "c", "a", "c");

        // when
        SortedCollection<String> list = new ConcurrentSortedList<String>(
                10, String.CASE_INSENSITIVE_ORDER, duplicates);
        list.addAll(elements);

        // then
        assertEquals(4, list.size());
        assertEquals("a", list.toArray()[0]);
        assertEquals("c", list.toArray()[1]);
        assertEquals("c", list.toArray()[2]);
        assertEquals("z", list.toArray()[3]);
    }

    @Override
    public SortedCollection<String> getInstance(final boolean duplicate) {
        return new ConcurrentSortedList<String>(String.CASE_INSENSITIVE_ORDER,
                duplicate);
    }

    @Override
    public SortedCollection<BigInteger> getInstanceBigInteger(
            final boolean dup) {
        return new ConcurrentSortedList<BigInteger>(BIGINTEGER_COMPARATOR,
                dup);
    }

    @Override
    public SortedCollection<Integer> getInstanceInteger(final boolean dup) {
        return new ConcurrentSortedList<Integer>(INTEGER_COMPARATOR,
                dup);
    }

    @Override
    public SortedCollection<Double> getInstanceDouble(final boolean dup) {
        return new ConcurrentSortedList<Double>(DOUBLE_COMPARATOR,
                dup);
    }

}

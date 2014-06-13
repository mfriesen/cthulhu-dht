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

package ca.gobits.cthulhu.test;

import static org.junit.Assert.assertEquals;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.List;

import org.junit.Test;

import ca.gobits.cthulhu.SortedCollection;
import ca.gobits.cthulhu.SortedList;

/**
 * Unit test for SortedList.class.
 */
public final class SortedListUnitTest extends SortedListAbstractTest {

    /**
     * testConstructor01().
     */
    @Test
    public void testConstructor01() {
        // given
        boolean duplicates = true;
        List<String> elements = Arrays.asList("z", "c", "a", "c");

        // when
        SortedList<String> list = new SortedList<String>(elements,
                String.CASE_INSENSITIVE_ORDER, duplicates);

        // then
        assertEquals(4, list.size());
        assertEquals("a", list.toArray()[0]);
        assertEquals("c", list.toArray()[1]);
        assertEquals("c", list.toArray()[2]);
        assertEquals("z", list.toArray()[3]);
    }

    @Override
    public SortedCollection<String> getInstance(final boolean duplicate) {
        return new SortedList<String>(String.CASE_INSENSITIVE_ORDER,
                duplicate);
    }

    @Override
    public SortedCollection<BigInteger> getInstanceBigInteger(
            final boolean dup) {
        return new SortedList<BigInteger>(BIGINTEGER_COMPARATOR,
                dup);
    }

    @Override
    public SortedCollection<Integer> getInstanceInteger(final boolean dup) {
        return new SortedList<Integer>(INTEGER_COMPARATOR,
                dup);
    }

    @Override
    public SortedCollection<Double> getInstanceDouble(final boolean dup) {
        return new SortedList<Double>(DOUBLE_COMPARATOR,
                dup);
    }
}

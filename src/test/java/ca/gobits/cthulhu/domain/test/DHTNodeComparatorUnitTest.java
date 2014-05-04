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

package ca.gobits.cthulhu.domain.test;

import static org.junit.Assert.assertEquals;

import java.math.BigInteger;

import org.junit.Test;

import ca.gobits.cthulhu.domain.DHTNode;
import ca.gobits.cthulhu.domain.DHTNodeComparator;

/**
 * DHTNodeComparator Unit Tests.
 */
public final class DHTNodeComparatorUnitTest {

    /**
     * testCompare01() IDs are equal.
     */
    @Test
    public void testCompare01() {
        // given
        DHTNode node0 = new DHTNode(new BigInteger("2"), (byte[]) null, 0);
        DHTNode node1 = new DHTNode(new BigInteger("2"), (byte[]) null, 0);

        // when
        int result = DHTNodeComparator.getInstance().compare(node0, node1);

        // then
        assertEquals(0, result);
    }

    /**
     * testCompare02() ID less than.
     */
    @Test
    public void testCompare02() {
        // given
        DHTNode node0 = new DHTNode(new BigInteger("2"), (byte[]) null, 0);
        DHTNode node1 = new DHTNode(new BigInteger("5"), (byte[]) null, 0);

        // when
        int result = DHTNodeComparator.getInstance().compare(node0, node1);

        // then
        assertEquals(-1, result);
    }

    /**
     * testCompare03() ID greater than.
     */
    @Test
    public void testCompare03() {
        // given
        DHTNode node0 = new DHTNode(new BigInteger("5"), (byte[]) null, 0);
        DHTNode node1 = new DHTNode(new BigInteger("2"), (byte[]) null, 0);

        // when
        int result = DHTNodeComparator.getInstance().compare(node0, node1);

        // then
        assertEquals(1, result);
    }
}

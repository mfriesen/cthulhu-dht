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

package ca.gobits.test.dht.comparator;

import static ca.gobits.dht.factory.DHTNodeFactory.create;
import static org.junit.Assert.assertEquals;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.Test;

import ca.gobits.dht.DHTNode;
import ca.gobits.dht.comparator.DHTNodeComparator;

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
        DHTNode node0 = create(new BigInteger("2").toByteArray(),
                DHTNode.State.UNKNOWN);
        DHTNode node1 = create(new BigInteger("2").toByteArray(),
                DHTNode.State.UNKNOWN);

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
        DHTNode node0 = create(new BigInteger("2").toByteArray(),
                DHTNode.State.UNKNOWN);
        DHTNode node1 = create(new BigInteger("5").toByteArray(),
                DHTNode.State.UNKNOWN);

        // when
        int result = DHTNodeComparator.getInstance().compare(node0, node1);

        // then
        assertEquals(-3, result);
    }

    /**
     * testCompare03() ID greater than.
     */
    @Test
    public void testCompare03() {
        // given
        DHTNode node0 = create(new BigInteger("5").toByteArray(),
                DHTNode.State.UNKNOWN);
        DHTNode node1 = create(new BigInteger("2").toByteArray(),
                DHTNode.State.UNKNOWN);

        // when
        int result = DHTNodeComparator.getInstance().compare(node0, node1);

        // then
        assertEquals(3, result);
    }

    /**
     * testCompare04().
     */
    @Test
    public void testCompare04() {
        // given
        byte[] id0 = new byte[] {64, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                0, 0, 0, 0, 0 };
        byte[] id1 = new byte[] {-128, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                0, 0, 0, 0, 0 };
        DHTNode node0 = create(id0,
                DHTNode.State.UNKNOWN);
        DHTNode node1 = create(id1,
                DHTNode.State.UNKNOWN);

        List<DHTNode> list = Arrays.asList(node1, node0);

        // when
        Collections.sort(list, DHTNodeComparator.getInstance());

        // then
        assertEquals(node0, list.get(0));
        assertEquals(node1, list.get(1));
    }

    /**
     * testCompare05().
     */
    @Test
    public void testCompare05() {
        // given
        byte[] id0 = new byte[] {0, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
                -1, -1, -1, -1, -1, -1, -1, -1, -1 };
        byte[] id1 = new byte[] {-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
                -1, -1, -1, -1, -1, -1, -1, -1, -1 };
        DHTNode node0 = create(id0,
                DHTNode.State.UNKNOWN);
        DHTNode node1 = create(id1,
                DHTNode.State.UNKNOWN);

        List<DHTNode> list = Arrays.asList(node1, node0);

        // when
        Collections.sort(list, DHTNodeComparator.getInstance());

        // then
        assertEquals(node0, list.get(0));
        assertEquals(node1, list.get(1));
    }
}

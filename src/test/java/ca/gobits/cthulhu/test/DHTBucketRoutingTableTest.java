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
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.math.BigDecimal;
import java.math.BigInteger;

import org.junit.Test;

import ca.gobits.cthulhu.DHTBucket;
import ca.gobits.cthulhu.DHTBucketRoutingTable;
import ca.gobits.cthulhu.DHTNode;
import ca.gobits.cthulhu.util.DHTUtil;

/**
 * DHTBucketRoutingTableTest.
 */
public class DHTBucketRoutingTableTest {

    /**
     * testConstructor01().
     */
    @Test
    public final void testConstructor01() {
        // given
        DHTBucketRoutingTable routingTable = new DHTBucketRoutingTable();

        // when
        DHTBucket result = routingTable.getRoot();

        // then
        assertEquals(0, result.getNodes().size());
        assertEquals(BigInteger.ZERO, result.getMin());
        assertEquals(new BigInteger(
                "1461501637330902918203684832716283019655932542976"),
                result.getMax());
        assertNull(result.getLeft());
        assertNull(result.getRight());
    }

    /**
     * testAddNode01() - test add nodes to bucket.
     */
    @Test
    public final void testAddNode01() {
        // given
        DHTBucketRoutingTable routingTable = new DHTBucketRoutingTable();
        DHTNode node = new DHTNode(new BigInteger("1"), null, 0);

        // when
        routingTable.addNode(node);

        // then
        assertEquals(1, routingTable.getNodeCount());
        DHTBucket root = routingTable.getRoot();
        assertEquals(1, root.getNodes().size());
        assertEquals(node, root.getNodes().iterator().next());
        assertNull(root.getLeft());
        assertNull(root.getRight());
    }

//    /**
//     * testAddNode02() - test Max Number of nodes.
//     */
//    @Test
//    public final void testAddNode02() {
//        // given
//        int nodeCount = DHTBucketRoutingTable.MAX_NUMBER_OF_NODES + 1;
//        DHTBucketRoutingTable routingTable = new DHTBucketRoutingTable();
//
//        // when
//        for (int i = 0; i < nodeCount; i++) {
//            DHTNode node = new DHTNode(new BigInteger("" + i), null, 0);
//            routingTable.addNode(node);
//            if (i % 1000 == 0) {
//                System.out.println("I : " + i);
//            }
//        }
//
//        // then
//        assertEquals(DHTBucketRoutingTable.MAX_NUMBER_OF_NODES,
//                routingTable.getNodeCount());
//    }

    /**
     * testAddNode03() - test adding duplicate nodes.
     */
    @Test
    public final void testAddNode03() {
        // given
        DHTBucketRoutingTable routingTable = new DHTBucketRoutingTable();
        DHTNode node = new DHTNode(new BigInteger("1"), null, 0);

        // when
        routingTable.addNode(node);
        routingTable.addNode(node);

        // then
        assertEquals(1, routingTable.getNodeCount());
        DHTBucket root = routingTable.getRoot();
        assertEquals(1, root.getNodes().size());
        assertEquals(node, root.getNodes().iterator().next());
        assertNull(root.getLeft());
        assertNull(root.getRight());
    }

    /**
     * testAddNode04() - test spliting buckets.
     */
    @Test
    public final void testAddNode04() {
        // given
        int nodeStartId = 50;
        DHTBucketRoutingTable routingTable = new DHTBucketRoutingTable();
        DHTNode node = new DHTNode(DHTUtil.sha1("10"), null, 0);

        // when
        for (int i = 0; i <= DHTBucket.BUCKET_MAX; i++) {
            node = new DHTNode(DHTUtil.sha1("" + (nodeStartId + i)), null, 0);
            routingTable.addNode(node);
        }

        // then
        assertEquals(9, routingTable.getNodeCount());
        DHTBucket root = routingTable.getRoot();
        assertNotNull(root.getLeft());
        assertNotNull(root.getRight());

        assertNull(root.getNodes());
        assertEquals("0", root.getMin().toString());
        assertEquals("1461501637330902918203684832716283019655932542976",
                root.getMax().toString());

        DHTBucket left = root.getLeft();
        assertEquals(2, left.getNodes().size());
        assertEquals("0", left.getMin().toString());
        assertEquals("730750818665451459101842416358141509827966271488",
                left.getMax().toString());

        DHTBucket right = root.getRight();
        assertEquals(7, right.getNodes().size());
        assertEquals("730750818665451459101842416358141509827966271489",
                right.getMin().toString());
        assertEquals("1461501637330902918203684832716283019655932542976",
                right.getMax().toString());
    }

    /**
     * testAddNode04() - test if split is all on one side, don't split.
     */
    @Test
    public final void testAddNode05() {
        // given
        BigInteger max = new BigDecimal(Math.pow(2, DHTUtil.NODE_ID_LENGTH))
            .toBigInteger();

        DHTBucketRoutingTable routingTable = new DHTBucketRoutingTable();

        // when
        for (int i = 0; i <= DHTBucket.BUCKET_MAX; i++) {
            BigInteger id = max.subtract(new BigInteger("" + i));
            DHTNode node = new DHTNode(id, null, 0);
            routingTable.addNode(node);
        }

        // then
        assertEquals(DHTBucket.BUCKET_MAX, routingTable.getNodeCount());
        DHTBucket root = routingTable.getRoot();
        assertNull(root.getLeft());
        assertNull(root.getRight());
    }
}

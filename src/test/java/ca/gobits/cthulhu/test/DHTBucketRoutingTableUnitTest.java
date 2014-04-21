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

import java.math.BigInteger;
import java.util.List;

import org.junit.Test;

import ca.gobits.cthulhu.DHTBucketRoutingTable;
import ca.gobits.cthulhu.DHTNode;
import ca.gobits.cthulhu.SortedList;

/**
 * DHTBucketRoutingTableTest.
 */
public final class DHTBucketRoutingTableUnitTest {

    /**
     * testConstructor01().
     */
    @Test
    public void testConstructor01() {
        // given
        DHTBucketRoutingTable routingTable = new DHTBucketRoutingTable();

        // when
        SortedList<DHTNode> result = routingTable.getNodes();

        // then
        assertEquals(0, result.size());
    }

    /**
     * testAddNode01() - test add nodes to bucket.
     */
    @Test
    public void testAddNode01() {
        // given
        DHTBucketRoutingTable routingTable = new DHTBucketRoutingTable();
        DHTNode node = new DHTNode(new BigInteger("1"), null, 0);

        // when
        routingTable.addNode(node);

        // then
        assertEquals(1, routingTable.getTotalNodeCount());
        SortedList<DHTNode> root = routingTable.getNodes();
        assertEquals(1, root.size());
        assertEquals(node, root.get(0));
    }

    /**
     * testAddNode02() - test Max Number of nodes.
     */
    @Test
    public void testAddNode02() {
        // given
        int nodeCount = DHTBucketRoutingTable.MAX_NUMBER_OF_NODES + 1;
        DHTBucketRoutingTable routingTable = new DHTBucketRoutingTable();

        // when
        for (int i = 0; i < nodeCount; i++) {
            DHTNode node = new DHTNode(new BigInteger("" + i), null, 0);
            routingTable.addNode(node);
        }

        // then
        assertEquals(DHTBucketRoutingTable.MAX_NUMBER_OF_NODES,
                routingTable.getTotalNodeCount());
    }

    /**
     * testAddNode03() - test adding duplicate nodes.
     */
    @Test
    public void testAddNode03() {
        // given
        DHTBucketRoutingTable routingTable = new DHTBucketRoutingTable();
        DHTNode node = new DHTNode(new BigInteger("1"), null, 0);

        // when
        routingTable.addNode(node);
        routingTable.addNode(node);

        // then
        assertEquals(1, routingTable.getTotalNodeCount());
        SortedList<DHTNode> root = routingTable.getNodes();
        assertEquals(1, root.size());
        assertEquals(node, root.get(0));
    }

    /**
     * testAddNode04() - test adding nodes and checking order.
     */
    @Test
    public void testAddNode04() {
        // given
        DHTBucketRoutingTable routingTable = new DHTBucketRoutingTable();
        DHTNode node0 = new DHTNode(new BigInteger("1"), null, 0);
        DHTNode node1 = new DHTNode(new BigInteger(
                "217572328821850967755762913845138112465869557436"), null, 0);
        DHTNode node2 = new DHTNode(new BigInteger(
                "253718933283387888344146948372599275024431560999"), null, 0);
        DHTNode node3 = new DHTNode(new BigInteger(
                "909396897490697132528408310795708133687135388426"), null, 0);

        // when
        routingTable.addNode(node0);
        routingTable.addNode(node3);
        routingTable.addNode(node2);
        routingTable.addNode(node1);

        // then
        assertEquals(4, routingTable.getTotalNodeCount());
        SortedList<DHTNode> nodes = routingTable.getNodes();
        assertEquals(node0, nodes.get(0));
        assertEquals(node1, nodes.get(1));
        assertEquals(node2, nodes.get(2));
        assertEquals(node3, nodes.get(3));
    }

    /**
     * testFindClosestNodes01() - find the closests 8 nodes.
     */
    @Test
    public void testFindClosestNodes01() {
        // given
        DHTNode n = new DHTNode(new BigInteger("11"), null, 0);
        DHTBucketRoutingTable rt = new DHTBucketRoutingTable();

        for (int i = 0; i < 40; i = i + 2) {
            DHTNode node = new DHTNode(new BigInteger("" + i), null, 0);
            rt.addNode(node);
        }

        // when
        List<DHTNode> results = rt.findClosestNodes(n.getId(), 8);

        // then
        assertEquals(8, results.size());
        assertEquals("4", results.get(0).getId().toString());
        assertEquals("6", results.get(1).getId().toString());
        assertEquals("8", results.get(2).getId().toString());
        assertEquals("10", results.get(3).getId().toString());
        assertEquals("12", results.get(4).getId().toString());
        assertEquals("14", results.get(5).getId().toString());
        assertEquals("16", results.get(6).getId().toString());
        assertEquals("18", results.get(7).getId().toString());
    }

    /**
     * testFindClosestNodes02() - find the closests 8 nodes at beginning
     * of list.
     */
    @Test
    public void testFindClosestNodes02() {
        // given
        DHTNode n = new DHTNode(new BigInteger("1"), null, 0);
        DHTBucketRoutingTable rt = new DHTBucketRoutingTable();

        for (int i = 0; i < 40; i = i + 2) {
            DHTNode node = new DHTNode(new BigInteger("" + i), null, 0);
            rt.addNode(node);
        }

        // when
        List<DHTNode> results = rt.findClosestNodes(n.getId(), 8);

        // then
        assertEquals(8, results.size());
        assertEquals("0", results.get(0).getId().toString());
        assertEquals("2", results.get(1).getId().toString());
        assertEquals("4", results.get(2).getId().toString());
        assertEquals("6", results.get(3).getId().toString());
        assertEquals("8", results.get(4).getId().toString());
        assertEquals("10", results.get(5).getId().toString());
        assertEquals("12", results.get(6).getId().toString());
        assertEquals("14", results.get(7).getId().toString());
    }

    /**
     * testFindClosestNodes03() - find the closests 8 nodes at end
     * of list.
     */
    @Test
    public void testFindClosestNodes03() {
        // given
        DHTNode n = new DHTNode(new BigInteger("41"), null, 0);
        DHTBucketRoutingTable rt = new DHTBucketRoutingTable();

        for (int i = 0; i < 40; i = i + 2) {
            DHTNode node = new DHTNode(new BigInteger("" + i), null, 0);
            rt.addNode(node);
        }

        // when
        List<DHTNode> results = rt.findClosestNodes(n.getId(), 8);

        // then
        assertEquals(8, results.size());
        assertEquals("24", results.get(0).getId().toString());
        assertEquals("26", results.get(1).getId().toString());
        assertEquals("28", results.get(2).getId().toString());
        assertEquals("30", results.get(3).getId().toString());
        assertEquals("32", results.get(4).getId().toString());
        assertEquals("34", results.get(5).getId().toString());
        assertEquals("36", results.get(6).getId().toString());
        assertEquals("38", results.get(7).getId().toString());
    }

    /**
     * testFindClosestNodes04() - find the exact match node.
     */
    @Test
    public void testFindClosestNodes04() {
        // given
        DHTNode n = new DHTNode(new BigInteger("22"), null, 0);
        DHTBucketRoutingTable rt = new DHTBucketRoutingTable();

        for (int i = 0; i < 40; i = i + 2) {
            DHTNode node = new DHTNode(new BigInteger("" + i), null, 0);
            rt.addNode(node);
        }

        // when
        List<DHTNode> results = rt.findClosestNodes(n.getId(), 8);

        // then
        assertEquals(8, results.size());
        assertEquals("14", results.get(0).getId().toString());
        assertEquals("16", results.get(1).getId().toString());
        assertEquals("18", results.get(2).getId().toString());
        assertEquals("20", results.get(3).getId().toString());
        assertEquals("22", results.get(4).getId().toString());
        assertEquals("24", results.get(5).getId().toString());
        assertEquals("26", results.get(6).getId().toString());
        assertEquals("28", results.get(7).getId().toString());
    }
}
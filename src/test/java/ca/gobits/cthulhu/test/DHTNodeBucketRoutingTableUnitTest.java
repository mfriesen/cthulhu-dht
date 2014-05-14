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

import static ca.gobits.cthulhu.domain.DHTNodeFactory.create;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.math.BigInteger;
import java.util.List;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.junit.Test;

import ca.gobits.cthulhu.DHTNodeBucketRoutingTable;
import ca.gobits.cthulhu.SortedCollection;
import ca.gobits.cthulhu.domain.DHTNode;

/**
 * DHTBucketRoutingTableTest.
 */
public final class DHTNodeBucketRoutingTableUnitTest {

    /** address. */
    private static byte[] addr = new byte[] {50, 71, 50, 12 };

    /** port. */
    private static int port = 64568;

    /**
     * testConstructor01().
     */
    @Test
    public void testConstructor01() {
        // given
        DHTNodeBucketRoutingTable rt = new DHTNodeBucketRoutingTable();

        // when
        SortedCollection<DHTNode> result = rt.getNodes();

        // then
        assertEquals(0, result.size());
    }

    /**
     * testAddNode01() - test add nodes to bucket.
     */
    @Test
    public void testAddNode01() {
        // given
        Logger.getLogger(DHTNodeBucketRoutingTable.class).setLevel(Level.DEBUG);
        DHTNodeBucketRoutingTable rt = new DHTNodeBucketRoutingTable();
        DHTNode node = create(new BigInteger("1"), addr, port,
                DHTNode.State.UNKNOWN);

        // when
        rt.addNode(node);

        // then
        assertEquals(1, rt.getTotalNodeCount());
        SortedCollection<DHTNode> root = rt.getNodes();
        assertEquals(1, root.size());
        assertEquals(node, root.get(0));

        Logger.getLogger(DHTNodeBucketRoutingTable.class).setLevel(Level.INFO);
    }

    /**
     * testAddNode02() - test Max Number of nodes.
     */
    @Test
    public void testAddNode02() {
        // given
        int nodeCount = DHTNodeBucketRoutingTable.MAX_NUMBER_OF_NODES + 1;
        DHTNodeBucketRoutingTable rt = new DHTNodeBucketRoutingTable();

        // when
        for (int i = 0; i < nodeCount; i++) {
            DHTNode node = create(new BigInteger("" + i),
                    addr, port, DHTNode.State.UNKNOWN);
            rt.addNode(node);
        }

        // then
        assertEquals(DHTNodeBucketRoutingTable.MAX_NUMBER_OF_NODES,
                rt.getTotalNodeCount());
    }

    /**
     * testAddNode03() - test adding duplicate nodes.
     */
    @Test
    public void testAddNode03() {
        // given
        DHTNodeBucketRoutingTable rt = new DHTNodeBucketRoutingTable();
        DHTNode node = create(new BigInteger("1"),
            addr, port, DHTNode.State.UNKNOWN);

        // when
        rt.addNode(node);
        rt.addNode(node);

        // then
        assertEquals(1, rt.getTotalNodeCount());
        SortedCollection<DHTNode> root = rt.getNodes();
        assertEquals(1, root.size());
        assertEquals(node, root.get(0));
    }

    /**
     * testAddNode04() - test adding nodes and checking order.
     */
    @Test
    public void testAddNode04() {
        // given
        DHTNodeBucketRoutingTable rt = new DHTNodeBucketRoutingTable();
        DHTNode node0 = create(new BigInteger("1"), addr, port,
                DHTNode.State.UNKNOWN);
        DHTNode node1 = create(new BigInteger(
            "217572328821850967755762913845138112465869557436"),
            addr, port, DHTNode.State.UNKNOWN);
        DHTNode node2 = create(new BigInteger(
            "253718933283387888344146948372599275024431560999"),
            addr, port, DHTNode.State.UNKNOWN);
        DHTNode node3 = create(new BigInteger(
            "909396897490697132528408310795708133687135388426"),
            addr, port, DHTNode.State.UNKNOWN);

        // when
        rt.addNode(node0);
        rt.addNode(node3);
        rt.addNode(node2);
        rt.addNode(node1);

        // then
        assertEquals(4, rt.getTotalNodeCount());
        SortedCollection<DHTNode> nodes = rt.getNodes();
        assertEquals(node0, nodes.get(0));
        assertEquals(node1, nodes.get(1));
        assertEquals(node2, nodes.get(2));
        assertEquals(node3, nodes.get(3));
    }

    /**
     * testAddNode05() - test add nodes to bucket, UnknownHostException.
     */
    @Test
    public void testAddNode05() {
        // given
        Logger.getLogger(DHTNodeBucketRoutingTable.class).setLevel(Level.DEBUG);
        DHTNodeBucketRoutingTable rt = new DHTNodeBucketRoutingTable();
        DHTNode node = create(new BigInteger("1"), DHTNode.State.UNKNOWN);

        // when
        rt.addNode(node);

        // then
        assertEquals(1, rt.getTotalNodeCount());
        SortedCollection<DHTNode> root = rt.getNodes();
        assertEquals(1, root.size());
        assertEquals(node, root.get(0));

        Logger.getLogger(DHTNodeBucketRoutingTable.class).setLevel(Level.INFO);
    }

    /**
     * testFindClosestNodes01() - find the closests 8 nodes.
     */
    @Test
    public void testFindClosestNodes01() {
        // given
        DHTNode n = create(new BigInteger("11"), addr, port,
                DHTNode.State.UNKNOWN);
        DHTNodeBucketRoutingTable rt = new DHTNodeBucketRoutingTable();

        addNodes(rt);

        // when
        List<DHTNode> results = rt.findClosestNodes(n.getInfoHash(), 8);

        // then
        assertEquals(8, results.size());
        assertEquals("4", results.get(0).getInfoHash().toString());
        assertEquals("6", results.get(1).getInfoHash().toString());
        assertEquals("8", results.get(2).getInfoHash().toString());
        assertEquals("10", results.get(3).getInfoHash().toString());
        assertEquals("12", results.get(4).getInfoHash().toString());
        assertEquals("14", results.get(5).getInfoHash().toString());
        assertEquals("16", results.get(6).getInfoHash().toString());
        assertEquals("18", results.get(7).getInfoHash().toString());
    }

    /**
     * testFindClosestNodes02() - find the closests 8 nodes at beginning
     * of list.
     */
    @Test
    public void testFindClosestNodes02() {
        // given
        DHTNode n = create(new BigInteger("1"), addr, port,
                DHTNode.State.UNKNOWN);
        DHTNodeBucketRoutingTable rt = new DHTNodeBucketRoutingTable();

        addNodes(rt);

        // when
        List<DHTNode> results = rt.findClosestNodes(n.getInfoHash(), 8);

        // then
        assertEquals(8, results.size());
        assertEquals("0", results.get(0).getInfoHash().toString());
        assertEquals("2", results.get(1).getInfoHash().toString());
        assertEquals("4", results.get(2).getInfoHash().toString());
        assertEquals("6", results.get(3).getInfoHash().toString());
        assertEquals("8", results.get(4).getInfoHash().toString());
        assertEquals("10", results.get(5).getInfoHash().toString());
        assertEquals("12", results.get(6).getInfoHash().toString());
        assertEquals("14", results.get(7).getInfoHash().toString());
    }

    /**
     * testFindClosestNodes03() - find the closests 8 nodes at end
     * of list.
     */
    @Test
    public void testFindClosestNodes03() {
        // given
        DHTNode n = create(new BigInteger("41"), addr, port,
                DHTNode.State.UNKNOWN);
        DHTNodeBucketRoutingTable rt = new DHTNodeBucketRoutingTable();

        addNodes(rt);

        // when
        List<DHTNode> results = rt.findClosestNodes(n.getInfoHash(), 8);

        // then
        assertEquals(8, results.size());
        assertEquals("24", results.get(0).getInfoHash().toString());
        assertEquals("26", results.get(1).getInfoHash().toString());
        assertEquals("28", results.get(2).getInfoHash().toString());
        assertEquals("30", results.get(3).getInfoHash().toString());
        assertEquals("32", results.get(4).getInfoHash().toString());
        assertEquals("34", results.get(5).getInfoHash().toString());
        assertEquals("36", results.get(6).getInfoHash().toString());
        assertEquals("38", results.get(7).getInfoHash().toString());
    }

    /**
     * testFindClosestNodes04() - find the exact match node.
     */
    @Test
    public void testFindClosestNodes04() {
        // given
        DHTNode n = create(new BigInteger("22"), addr, port,
                DHTNode.State.UNKNOWN);
        DHTNodeBucketRoutingTable rt = new DHTNodeBucketRoutingTable();

        addNodes(rt);

        // when
        List<DHTNode> results = rt.findClosestNodes(n.getInfoHash(), 8);

        // then
        assertEquals(8, results.size());
        assertEquals("14", results.get(0).getInfoHash().toString());
        assertEquals("16", results.get(1).getInfoHash().toString());
        assertEquals("18", results.get(2).getInfoHash().toString());
        assertEquals("20", results.get(3).getInfoHash().toString());
        assertEquals("22", results.get(4).getInfoHash().toString());
        assertEquals("24", results.get(5).getInfoHash().toString());
        assertEquals("26", results.get(6).getInfoHash().toString());
        assertEquals("28", results.get(7).getInfoHash().toString());
    }

    /**
     * testFindClosestNodes05() - default Node Count first Node.
     */
    @Test
    public void testFindClosestNodes05() {
        // given
        DHTNode n = create(new BigInteger("0"), addr, port,
                DHTNode.State.UNKNOWN);
        DHTNodeBucketRoutingTable rt = new DHTNodeBucketRoutingTable();

        addNodes(rt);

        // when
        List<DHTNode> results = rt.findClosestNodes(n.getInfoHash());

        // then
        assertEquals(16, results.size());
    }

    /**
     * testFindExactNode01() - index < size().
     */
    @Test
    public void testFindExactNode01() {
        // given
        DHTNodeBucketRoutingTable rt = new DHTNodeBucketRoutingTable();
        BigInteger nodeId = new BigInteger("5");

        // when
        DHTNode result = rt.findExactNode(nodeId);

        // then
        assertNull(result);
    }

    /**
     * testFindExactNode02() - node found ID does not match.
     */
    @Test
    public void testFindExactNode02() {
        // given
        DHTNodeBucketRoutingTable rt = new DHTNodeBucketRoutingTable();
        BigInteger nodeId = new BigInteger("5");
        addNodes(rt);

        // when
        DHTNode result = rt.findExactNode(nodeId);

        // then
        assertNull(result);
    }

    /**
     * testFindExactNode03() - found match.
     */
    @Test
    public void testFindExactNode03() {
        // given
        DHTNodeBucketRoutingTable rt = new DHTNodeBucketRoutingTable();
        BigInteger nodeId = new BigInteger("4");
        addNodes(rt);

        // when
        DHTNode result = rt.findExactNode(nodeId);

        // then
        assertNotNull(result);
        assertEquals(nodeId, result.getInfoHash());
    }

    /**
     * Add Nodes to routing table.
     * @param rt routing table.
     */
    private void addNodes(final DHTNodeBucketRoutingTable rt) {
        for (int i = 0; i < 40; i = i + 2) {
            DHTNode node = create(new BigInteger("" + i),
                addr, port, DHTNode.State.UNKNOWN);
            rt.addNode(node);
        }
    }
}

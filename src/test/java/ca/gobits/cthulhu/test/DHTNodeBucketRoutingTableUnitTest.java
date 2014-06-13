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

import static ca.gobits.cthulhu.domain.DHTNodeFactory.create;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.math.BigInteger;
import java.net.InetAddress;
import java.util.Arrays;
import java.util.List;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.junit.Test;

import ca.gobits.cthulhu.DHTNodeBucketRoutingTable;
import ca.gobits.cthulhu.SortedCollection;
import ca.gobits.cthulhu.domain.DHTNode;
import ca.gobits.cthulhu.domain.DHTNode.State;

/**
 * DHTBucketRoutingTableTest.
 */
public final class DHTNodeBucketRoutingTableUnitTest {

    /** address. */
    private static byte[] addr = new byte[] {50, 71, 50, 12 };

    /** port. */
    private static int port = 64568;

    /** inet socket address. */
    private static InetAddress iaddr;

    /**
     * constuctor.
     * @throws Exception  Exception
     */
    public DHTNodeBucketRoutingTableUnitTest() throws Exception {
        iaddr = InetAddress.getByAddress(addr);
    }

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
     * @throws Exception  Exception
     */
    @Test
    public void testAddNode01() throws Exception {
        // given
        Logger.getLogger(DHTNodeBucketRoutingTable.class).setLevel(Level.DEBUG);
        DHTNodeBucketRoutingTable rt = new DHTNodeBucketRoutingTable();
        DHTNode node = create(new BigInteger("1").toByteArray(), addr, port,
                State.GOOD);

        // when
        rt.addNode(new BigInteger("1").toByteArray(), iaddr, port, State.GOOD);

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
        DHTNodeBucketRoutingTable rt = new DHTNodeBucketRoutingTable();
        int nodeCount = rt.getMaxNodeCount() + 1;

        // when
        for (int i = 0; i < nodeCount; i++) {
            rt.addNode(new BigInteger("" + i).toByteArray(), iaddr, port,
                    State.GOOD);
        }

        // then
        assertEquals(rt.getMaxNodeCount(), rt.getTotalNodeCount());
    }

    /**
     * testAddNode03() - test adding duplicate nodes.
     */
    @Test
    public void testAddNode03() {
        // given
        DHTNodeBucketRoutingTable rt = new DHTNodeBucketRoutingTable();
        byte[] id = new BigInteger("1").toByteArray();
        DHTNode node = create(new BigInteger("1").toByteArray(),
            addr, port, State.GOOD);

        // when
        rt.addNode(id, iaddr, port, State.GOOD);
        rt.addNode(id, iaddr, port, State.GOOD);

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
        byte[] bytes0 = new byte[] {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                0, 0, 0, 0, 0, 1 };
        byte[] bytes1 = new byte[] {38, 28, 71, -40, -112, 54, -31, -10, 100,
                78, 13, -100, 26, -96, -94, -118, 2, -23, 78, -68 };
        byte[] bytes2 = new byte[] {44, 113, 38, -125, -19, -98, 103, 105, 60,
                -95, 53, -68, -98, -9, -48, 122, -31, -47, -71, 39 };
        byte[] bytes3 = new byte[] {-97, 74, -60, 105, 35, -102, 80, -77, 40,
                -2, -19, 64, 127, 15, -105, -44, -84, 125, 67, 10 };

        DHTNode node0 = create(bytes0, addr, port, State.GOOD);
        DHTNode node1 = create(bytes1, addr, port, State.GOOD);
        DHTNode node2 = create(bytes2, addr, port, State.GOOD);
        DHTNode node3 = create(bytes3, addr, port, DHTNode.State.GOOD);

        // when
        rt.addNode(node0.getInfoHash(), iaddr, port, State.GOOD);
        rt.addNode(node3.getInfoHash(), iaddr, port, State.GOOD);
        rt.addNode(node2.getInfoHash(), iaddr, port, State.GOOD);
        rt.addNode(node1.getInfoHash(), iaddr, port, State.GOOD);

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
        DHTNode node = create(new BigInteger("1").toByteArray(), State.GOOD);

        // when
        rt.addNode(node.getInfoHash(), iaddr, port, State.GOOD);

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
        DHTNode n = create(new BigInteger("11").toByteArray(), addr, port,
                State.UNKNOWN);
        DHTNodeBucketRoutingTable rt = new DHTNodeBucketRoutingTable();

        addNodes(rt);

        // when
        List<DHTNode> results = rt.findClosestNodes(n.getInfoHash(), 8);

        // then
        assertEquals(8, results.size());
        assertEquals("[4]", Arrays.toString(results.get(0).getInfoHash()));
        assertEquals("[6]", Arrays.toString(results.get(1).getInfoHash()));
        assertEquals("[8]", Arrays.toString(results.get(2).getInfoHash()));
        assertEquals("[10]", Arrays.toString(results.get(3).getInfoHash()));
        assertEquals("[12]", Arrays.toString(results.get(4).getInfoHash()));
        assertEquals("[14]", Arrays.toString(results.get(5).getInfoHash()));
        assertEquals("[16]", Arrays.toString(results.get(6).getInfoHash()));
        assertEquals("[18]", Arrays.toString(results.get(7).getInfoHash()));
    }

    /**
     * testFindClosestNodes02() - find the closests 8 nodes at beginning
     * of list.
     */
    @Test
    public void testFindClosestNodes02() {
        // given
        DHTNode n = create(new BigInteger("1").toByteArray(), addr, port,
                DHTNode.State.UNKNOWN);
        DHTNodeBucketRoutingTable rt = new DHTNodeBucketRoutingTable();

        addNodes(rt);

        // when
        List<DHTNode> results = rt.findClosestNodes(n.getInfoHash(), 8);

        // then
        assertEquals(8, results.size());
        assertEquals("[0]", Arrays.toString(results.get(0).getInfoHash()));
        assertEquals("[2]", Arrays.toString(results.get(1).getInfoHash()));
        assertEquals("[4]", Arrays.toString(results.get(2).getInfoHash()));
        assertEquals("[6]", Arrays.toString(results.get(3).getInfoHash()));
        assertEquals("[8]", Arrays.toString(results.get(4).getInfoHash()));
        assertEquals("[10]", Arrays.toString(results.get(5).getInfoHash()));
        assertEquals("[12]", Arrays.toString(results.get(6).getInfoHash()));
        assertEquals("[14]", Arrays.toString(results.get(7).getInfoHash()));
    }

    /**
     * testFindClosestNodes03() - find the closests 8 nodes at end
     * of list.
     */
    @Test
    public void testFindClosestNodes03() {
        // given
        DHTNode n = create(new BigInteger("41").toByteArray(), addr, port,
                DHTNode.State.UNKNOWN);
        DHTNodeBucketRoutingTable rt = new DHTNodeBucketRoutingTable();

        addNodes(rt);

        // when
        List<DHTNode> results = rt.findClosestNodes(n.getInfoHash(), 8);

        // then
        assertEquals(8, results.size());
        assertEquals("[24]", Arrays.toString(results.get(0).getInfoHash()));
        assertEquals("[26]", Arrays.toString(results.get(1).getInfoHash()));
        assertEquals("[28]", Arrays.toString(results.get(2).getInfoHash()));
        assertEquals("[30]", Arrays.toString(results.get(3).getInfoHash()));
        assertEquals("[32]", Arrays.toString(results.get(4).getInfoHash()));
        assertEquals("[34]", Arrays.toString(results.get(5).getInfoHash()));
        assertEquals("[36]", Arrays.toString(results.get(6).getInfoHash()));
        assertEquals("[38]", Arrays.toString(results.get(7).getInfoHash()));
    }

    /**
     * testFindClosestNodes04() - find the exact match node.
     */
    @Test
    public void testFindClosestNodes04() {
        // given
        DHTNode n = create(new BigInteger("22").toByteArray(), addr, port,
                DHTNode.State.UNKNOWN);
        DHTNodeBucketRoutingTable rt = new DHTNodeBucketRoutingTable();

        addNodes(rt);

        // when
        List<DHTNode> results = rt.findClosestNodes(n.getInfoHash(), 8);

        // then
        assertEquals(8, results.size());
        assertEquals("[14]", Arrays.toString(results.get(0).getInfoHash()));
        assertEquals("[16]", Arrays.toString(results.get(1).getInfoHash()));
        assertEquals("[18]", Arrays.toString(results.get(2).getInfoHash()));
        assertEquals("[20]", Arrays.toString(results.get(3).getInfoHash()));
        assertEquals("[22]", Arrays.toString(results.get(4).getInfoHash()));
        assertEquals("[24]", Arrays.toString(results.get(5).getInfoHash()));
        assertEquals("[26]", Arrays.toString(results.get(6).getInfoHash()));
        assertEquals("[28]", Arrays.toString(results.get(7).getInfoHash()));
    }

    /**
     * testFindClosestNodes05() - default Node Count first Node.
     */
    @Test
    public void testFindClosestNodes05() {
        // given
        DHTNode n = create(new BigInteger("0").toByteArray(), addr, port,
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
        DHTNode result = rt.findExactNode(nodeId.toByteArray());

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
        DHTNode result = rt.findExactNode(nodeId.toByteArray());

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
        byte[] bytes0 = new byte[] {4};
        addNodes(rt);

        // when
        DHTNode result = rt.findExactNode(bytes0);

        // then
        assertNotNull(result);
        assertTrue(Arrays.equals(bytes0, result.getInfoHash()));
    }

    /**
     * Add Nodes to routing table.
     * @param rt routing table.
     */
    private void addNodes(final DHTNodeBucketRoutingTable rt) {
        for (int i = 0; i < 40; i = i + 2) {
            byte[] id = new BigInteger("" + i).toByteArray();
            rt.addNode(id, iaddr, port, State.GOOD);
        }
    }
}

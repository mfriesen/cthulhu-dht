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

import static ca.gobits.cthulhu.domain.DHTNodeFactory.NODE_ID_LENGTH;
import static ca.gobits.cthulhu.domain.DHTNodeFactory.create;
import static ca.gobits.dht.DHTConversion.fitToSize;
import static ca.gobits.dht.DHTConversion.toBigInteger;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.easymock.EasyMockRunner;
import org.easymock.EasyMockSupport;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;

import ca.gobits.cthulhu.DHTNodeBucketRoutingTable;
import ca.gobits.cthulhu.SortedCollection;
import ca.gobits.cthulhu.domain.DHTBucket;
import ca.gobits.cthulhu.domain.DHTNode;
import ca.gobits.cthulhu.domain.DHTNode.State;

/**
 * DHTBucketRoutingTableTest.
 */
@RunWith(EasyMockRunner.class)
public final class DHTNodeBucketRoutingTableUnitTest extends EasyMockSupport {

    /** LOGGER. */
    private static final Logger LOGGER = Logger
            .getLogger(DHTNodeBucketRoutingTableUnitTest.class);

    /** Dummy Node Id. */
    private final byte[] nodeId = fitToSize(new BigInteger("11").toByteArray(),
            NODE_ID_LENGTH);

    /** Test Subject instance. */
    private final DHTNodeBucketRoutingTable rt =
        new DHTNodeBucketRoutingTable(this.nodeId);

    /** port. */
    private final int port = 64568;

    /** inet socket address. */
    private final InetAddress iaddr;

    /** inet v6 address. */
    private final InetAddress iaddr6;

    /**
     * constuctor.
     * @throws Exception  Exception
     */
    public DHTNodeBucketRoutingTableUnitTest() throws Exception {
        this.iaddr = InetAddress.getByName("50.71.50.12");
        this.iaddr6 = InetAddress
                .getByName("805b:2d9d:dc28:0000:0000:fc57:d4c8:1fff");
    }

    /**
     * testConstructor01().
     */
    @Test
    public void testConstructor01() {
        // given

        // when
        SortedCollection<DHTNode> result = this.rt.getNodes();

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
        boolean ipv6 = false;
        Logger.getLogger(DHTNodeBucketRoutingTable.class).setLevel(Level.DEBUG);
        DHTNode node = create(new BigInteger("1").toByteArray(),
                this.iaddr, this.port, State.GOOD);

        // when
        replayAll();
        this.rt.addNode(new BigInteger("1").toByteArray(), this.iaddr,
                this.port, State.GOOD);

        // then
        verifyAll();
        assertEquals(1, this.rt.getTotalNodeCount(ipv6));
        SortedCollection<DHTNode> root = this.rt.getNodes();
        assertEquals(1, root.size());
        assertEquals(node, root.get(0));

        Logger.getLogger(DHTNodeBucketRoutingTable.class).setLevel(Level.INFO);
    }

    /**
     * testAddNode02() - test Max Number of nodes.
     */
    // TODO rename once server mode works..
    @Test
    @Ignore
    public void testAddNode02() {
        // given
        boolean ipv6 = false;
        int nodeCount = this.rt.getMaxNodeCount() + 1;
        List<byte[]> infohashes = new ArrayList<byte[]>(nodeCount);

        for (int i = 0; i < nodeCount; i++) {
            byte[] bb = fitToSize(new BigInteger("" + i).toByteArray(),
                    NODE_ID_LENGTH);
            infohashes.add(bb);
        }

        // when
        replayAll();

        int i = 0;
        for (byte[] bb : infohashes) {
            this.rt.addNode(bb, this.iaddr,
                    this.port, State.GOOD);
            i++;

            if (i % 100 == 0) {
                LOGGER.info("COUNT " + i);
            }
        }

        // then
        verifyAll();
        assertEquals(this.rt.getMaxNodeCount(),
                this.rt.getTotalNodeCount(ipv6));
    }

    /**
     * testAddNode03() - test adding duplicate nodes.
     */
    @Test
    public void testAddNode03() {
        // given
        boolean ipv6 = false;
        byte[] id = new BigInteger("1").toByteArray();
        DHTNode node = create(new BigInteger("1").toByteArray(),
            this.iaddr, this.port, State.GOOD);

        // when
        this.rt.addNode(id, this.iaddr, this.port, State.GOOD);
        this.rt.addNode(id, this.iaddr, this.port, State.GOOD);

        // then
        assertEquals(1, this.rt.getTotalNodeCount(ipv6));
        SortedCollection<DHTNode> root = this.rt.getNodes();
        assertEquals(1, root.size());
        assertEquals(node, root.get(0));
    }

    /**
     * testAddNode04() - test adding nodes and checking order.
     */
    @Test
    public void testAddNode04() {
        // given
        boolean ipv6 = false;
        byte[] bytes0 = new byte[] {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                0, 0, 0, 0, 0, 1 };
        byte[] bytes1 = new byte[] {38, 28, 71, -40, -112, 54, -31, -10, 100,
                78, 13, -100, 26, -96, -94, -118, 2, -23, 78, -68 };
        byte[] bytes2 = new byte[] {44, 113, 38, -125, -19, -98, 103, 105, 60,
                -95, 53, -68, -98, -9, -48, 122, -31, -47, -71, 39 };
        byte[] bytes3 = new byte[] {-97, 74, -60, 105, 35, -102, 80, -77, 40,
                -2, -19, 64, 127, 15, -105, -44, -84, 125, 67, 10 };

        DHTNode node0 = create(bytes0, this.iaddr, this.port, State.GOOD);
        DHTNode node1 = create(bytes1, this.iaddr, this.port, State.GOOD);
        DHTNode node2 = create(bytes2, this.iaddr, this.port, State.GOOD);
        DHTNode node3 = create(bytes3, this.iaddr, this.port, State.GOOD);

        // when
        this.rt.addNode(node0.getInfoHash(), this.iaddr, this.port, State.GOOD);
        this.rt.addNode(node3.getInfoHash(), this.iaddr, this.port, State.GOOD);
        this.rt.addNode(node2.getInfoHash(), this.iaddr, this.port, State.GOOD);
        this.rt.addNode(node1.getInfoHash(), this.iaddr, this.port, State.GOOD);

        // then
        assertEquals(4, this.rt.getTotalNodeCount(ipv6));
        SortedCollection<DHTNode> nodes = this.rt.getNodes();

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
        boolean ipv6 = false;
        Logger.getLogger(DHTNodeBucketRoutingTable.class).setLevel(Level.DEBUG);
        DHTNode node = create(new BigInteger("1").toByteArray(), State.GOOD);

        // when
        this.rt.addNode(node.getInfoHash(), this.iaddr, this.port, State.GOOD);

        // then
        assertEquals(1, this.rt.getTotalNodeCount(ipv6));
        SortedCollection<DHTNode> root = this.rt.getNodes();
        assertEquals(1, root.size());
        assertEquals(node, root.get(0));

        Logger.getLogger(DHTNodeBucketRoutingTable.class).setLevel(Level.INFO);
    }

    /**
     * testAddNode06() - test add nodes IPv6 node.
     */
    @Test
    public void testAddNode06() {
        // given
        Logger.getLogger(DHTNodeBucketRoutingTable.class).setLevel(Level.DEBUG);
        DHTNode node = create(new BigInteger("1").toByteArray(), State.GOOD);

        // when
        this.rt.addNode(node.getInfoHash(), this.iaddr6, this.port, State.GOOD);

        // then
        assertEquals(1, this.rt.getTotalNodeCount(true));
        SortedCollection<DHTNode> root = this.rt.getNodes6();
        assertEquals(1, root.size());
        assertEquals(node, root.get(0));

        assertEquals(1, this.rt.getTotalNodeCount(true));
        assertEquals(0, this.rt.getTotalNodeCount(false));
        Logger.getLogger(DHTNodeBucketRoutingTable.class).setLevel(Level.INFO);
    }

    /**
     * Test Basic adding 8 nodes... only have 1 bucket.
     */
    @Test
    public void testAddNode07() {
        // given

        // when
        replayAll();

        for (int i = 0; i < 8; i++) {
            addNode(new BigInteger("" + i));
        }

        SortedCollection<DHTBucket> result = this.rt.getBuckets();

        // then
        verifyAll();

        assertEquals(1, result.size());
        DHTBucket b0 = result.get(0);
        assertEquals(8, b0.getNodeCount());
        assertEquals("0", toBigInteger(b0.getMin()).toString());
        assertEquals("1461501637330902918203684832716283019655932542975",
                toBigInteger(b0.getMax()).toString());
    }

    /**
     * Test Basic adding 12 nodes. 2 bucket.
     */
    @Test
    public void testAddNode08() {
        // given
        double topDouble = Math.pow(2, 159) + 1;
        BigInteger top = new BigDecimal(topDouble).toBigInteger();

        // when
        replayAll();

        for (int i = 0; i < 4; i++) {
            addNode(top.add(new BigInteger("" + i)));
        }

        for (int i = 0; i < 8; i++) {
            addNode(new BigInteger("" + i));
        }

        SortedCollection<DHTBucket> result = this.rt.getBuckets();

        // then
        verifyAll();

        assertEquals(2, result.size());
        DHTBucket b0 = result.get(0);
        assertEquals(8, b0.getNodeCount());
        assertEquals("0", toBigInteger(b0.getMin()).toString());
        assertEquals("730750818665451459101842416358141509827966271487",
                toBigInteger(b0.getMax()).toString());

        DHTBucket b1 = result.get(1);
        assertEquals(4, b1.getNodeCount());
        assertEquals("730750818665451459101842416358141509827966271488",
                toBigInteger(b1.getMin()).toString());
        assertEquals("1461501637330902918203684832716283019655932542975",
                toBigInteger(b1.getMax()).toString());
    }

    /**
     * Test Basic adding 24 nodes. 3 bucket.
     */
    @Test
    public void testAddNode09() {
        // given
        double topDouble = Math.pow(2, 159) + 1;
        BigInteger top = new BigDecimal(topDouble).toBigInteger();

        // when
        replayAll();

        // TOP Bucket
        for (int i = 0; i < 8; i++) {
            addNode(top.add(new BigInteger("" + i)));
        }

        // MID Bucket
        for (int i = 0; i < 8; i++) {
            BigInteger bi = new BigInteger("" + (i - 100));
            addNode(top.add(bi));
        }

        // Bottom Bucket
        for (int i = 0; i < 8; i++) {
            addNode(new BigInteger("" + i));
        }

        SortedCollection<DHTBucket> result = this.rt.getBuckets();

        // then
        verifyAll();

        assertEquals(3, result.size());
        DHTBucket b0 = result.get(0);
        assertEquals(8, b0.getNodeCount());
        assertEquals("0", toBigInteger(b0.getMin()).toString());
        assertEquals("365375409332725729550921208179070754913983135743",
                toBigInteger(b0.getMax()).toString());

        DHTBucket b1 = result.get(1);
        assertEquals(8, b1.getNodeCount());
        assertEquals("365375409332725729550921208179070754913983135744",
                toBigInteger(b1.getMin()).toString());
        assertEquals("730750818665451459101842416358141509827966271487",
                toBigInteger(b1.getMax()).toString());

        DHTBucket b2 = result.get(2);
        assertEquals(8, b2.getNodeCount());
        assertEquals("730750818665451459101842416358141509827966271488",
                toBigInteger(b2.getMin()).toString());
        assertEquals("1461501637330902918203684832716283019655932542975",
                toBigInteger(b2.getMax()).toString());
    }

    /**
     * testFindClosestNodes01() - find the closests 8 nodes.
     */
    @Test
    public void testFindClosestNodes01() {
        // given
        boolean ipv6 = false;
        DHTNode n = create(new BigInteger("11").toByteArray(), this.iaddr,
                this.port, State.UNKNOWN);

        // when
        replayAll();
        addNodes();

        List<DHTNode> results = this.rt.findClosestNodes(n.getInfoHash(), 8,
                ipv6);

        // then
        verifyAll();
        assertEquals(8, results.size());
        assertEquals(
                "[0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0]",
                Arrays.toString(results.get(0).getInfoHash()));
        assertEquals(
                "[0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 2]",
                Arrays.toString(results.get(1).getInfoHash()));
        assertEquals(
                "[0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 4]",
                Arrays.toString(results.get(2).getInfoHash()));
        assertEquals(
                "[0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 6]",
                Arrays.toString(results.get(3).getInfoHash()));
        assertEquals(
                "[0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 8]",
                Arrays.toString(results.get(4).getInfoHash()));
        assertEquals(
                "[0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 10]",
                Arrays.toString(results.get(5).getInfoHash()));
        assertEquals(
                "[0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 12]",
                Arrays.toString(results.get(6).getInfoHash()));
        assertEquals(
                "[0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 14]",
                Arrays.toString(results.get(7).getInfoHash()));
    }

    /**
     * testFindClosestNodes02() - find the closests 8 nodes at beginning
     * of list.
     */
    @Test
    public void testFindClosestNodes02() {
        // given
        boolean ipv6 = false;
        DHTNode n = create(new BigInteger("1").toByteArray(), this.iaddr,
                this.port, State.UNKNOWN);

        // when
        replayAll();

        addNodes();
        List<DHTNode> results = this.rt.findClosestNodes(n.getInfoHash(), 8,
                ipv6);

        // then
        verifyAll();
        assertEquals(8, results.size());
        assertEquals(
                "[0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0]",
                Arrays.toString(results.get(0).getInfoHash()));
        assertEquals(
                "[0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 2]",
                Arrays.toString(results.get(1).getInfoHash()));
        assertEquals(
                "[0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 4]",
                Arrays.toString(results.get(2).getInfoHash()));
        assertEquals(
                "[0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 6]",
                Arrays.toString(results.get(3).getInfoHash()));
        assertEquals(
                "[0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 8]",
                Arrays.toString(results.get(4).getInfoHash()));
        assertEquals(
                "[0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 10]",
                Arrays.toString(results.get(5).getInfoHash()));
        assertEquals(
                "[0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 12]",
                Arrays.toString(results.get(6).getInfoHash()));
        assertEquals(
                "[0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 14]",
                Arrays.toString(results.get(7).getInfoHash()));
    }

    /**
     * testFindClosestNodes03() - find the closests 8 nodes at end
     * of list.
     */
    @Test
    public void testFindClosestNodes03() {
        // given
        boolean ipv6 = false;
        DHTNode n = create(new BigInteger("41").toByteArray(), this.iaddr,
            this.port, State.UNKNOWN);

        // when
        replayAll();

        addNodes();
        assertEquals(20, this.rt.getTotalNodeCount(false));

        List<DHTNode> results = this.rt.findClosestNodes(n.getInfoHash(), 8,
                ipv6);

        // then
        verifyAll();
        assertEquals(8, results.size());
        assertEquals(
                "[0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 24]",
                Arrays.toString(results.get(0).getInfoHash()));
        assertEquals(
                "[0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 26]",
                Arrays.toString(results.get(1).getInfoHash()));
        assertEquals(
                "[0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 28]",
                Arrays.toString(results.get(2).getInfoHash()));
        assertEquals(
                "[0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 30]",
                Arrays.toString(results.get(3).getInfoHash()));
        assertEquals(
                "[0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 32]",
                Arrays.toString(results.get(4).getInfoHash()));
        assertEquals(
                "[0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 34]",
                Arrays.toString(results.get(5).getInfoHash()));
        assertEquals(
                "[0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 36]",
                Arrays.toString(results.get(6).getInfoHash()));
        assertEquals(
                "[0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 38]",
                Arrays.toString(results.get(7).getInfoHash()));
    }

    /**
     * testFindClosestNodes04() - find the exact match node.
     */
    @Test
    public void testFindClosestNodes04() {
        // given
        boolean ipv6 = false;
        DHTNode n = create(new BigInteger("22").toByteArray(), this.iaddr,
                this.port, State.UNKNOWN);

        // when
        replayAll();

        addNodes();
        List<DHTNode> results = this.rt.findClosestNodes(n.getInfoHash(), 8,
                ipv6);

        // then
        verifyAll();
        assertEquals(8, results.size());
        assertEquals(
                "[0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 14]",
                Arrays.toString(results.get(0).getInfoHash()));
        assertEquals(
                "[0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 16]",
                Arrays.toString(results.get(1).getInfoHash()));
        assertEquals(
                "[0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 18]",
                Arrays.toString(results.get(2).getInfoHash()));
        assertEquals(
                "[0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 20]",
                Arrays.toString(results.get(3).getInfoHash()));
        assertEquals(
                "[0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 22]",
                Arrays.toString(results.get(4).getInfoHash()));
        assertEquals(
                "[0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 24]",
                Arrays.toString(results.get(5).getInfoHash()));
        assertEquals(
                "[0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 26]",
                Arrays.toString(results.get(6).getInfoHash()));
        assertEquals(
                "[0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 28]",
                Arrays.toString(results.get(7).getInfoHash()));
    }

    /**
     * testFindClosestNodes05() - default Node Count first Node.
     */
    @Test
    public void testFindClosestNodes05() {
        // given
        boolean ipv6 = false;
        DHTNode n = create(new BigInteger("0").toByteArray(), this.iaddr,
                this.port, State.UNKNOWN);

        // when
        replayAll();

        addNodes();
        List<DHTNode> results = this.rt.findClosestNodes(n.getInfoHash(), ipv6);

        // then
        verifyAll();
        assertEquals(16, results.size());

        assertEquals(
                "[0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0]",
                Arrays.toString(results.get(0).getInfoHash()));
        assertEquals(
                "[0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 2]",
                Arrays.toString(results.get(1).getInfoHash()));
        assertEquals(
                "[0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 4]",
                Arrays.toString(results.get(2).getInfoHash()));
        assertEquals(
                "[0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 6]",
                Arrays.toString(results.get(3).getInfoHash()));
        assertEquals(
                "[0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 8]",
                Arrays.toString(results.get(4).getInfoHash()));
        assertEquals(
                "[0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 10]",
                Arrays.toString(results.get(5).getInfoHash()));
        assertEquals(
                "[0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 12]",
                Arrays.toString(results.get(6).getInfoHash()));
        assertEquals(
                "[0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 14]",
                Arrays.toString(results.get(7).getInfoHash()));
        assertEquals(
                "[0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 16]",
                Arrays.toString(results.get(8).getInfoHash()));
        assertEquals(
                "[0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 18]",
                Arrays.toString(results.get(9).getInfoHash()));
        assertEquals(
                "[0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 20]",
                Arrays.toString(results.get(10).getInfoHash()));
        assertEquals(
                "[0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 22]",
                Arrays.toString(results.get(11).getInfoHash()));
        assertEquals(
                "[0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 24]",
                Arrays.toString(results.get(12).getInfoHash()));
        assertEquals(
                "[0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 26]",
                Arrays.toString(results.get(13).getInfoHash()));
        assertEquals(
                "[0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 28]",
                Arrays.toString(results.get(14).getInfoHash()));
        assertEquals(
                "[0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 30]",
                Arrays.toString(results.get(15).getInfoHash()));

    }

    /**
     * testFindClosestNodes06() - find mixure of nodes depending on distance.
     * Routing Table has Nodes: 10 - 26, 32-35, 100 - 150
     * Looking for Node 40.
     */
    @Test
    public void testFindClosestNodes06() {
        // given
        boolean ipv6 = false;
        byte[] id2 = fitToSize(new BigInteger("39").toByteArray(),
                NODE_ID_LENGTH);
        DHTNodeBucketRoutingTable rt2 =
                new DHTNodeBucketRoutingTable(id2);

        DHTNode n = create(new BigInteger("40").toByteArray(), this.iaddr,
                this.port, State.UNKNOWN);

        // when
        replayAll();

        for (int i = 10; i < 27; i++) {
            byte[] id = new BigInteger("" + i).toByteArray();
            rt2.addNode(id, this.iaddr, this.port, State.GOOD);
        }

        for (int i = 32; i < 36; i++) {
            byte[] id = new BigInteger("" + i).toByteArray();
            rt2.addNode(id, this.iaddr, this.port, State.GOOD);
        }

        for (int i = 100; i < 151; i++) {
            byte[] id = new BigInteger("" + i).toByteArray();
            rt2.addNode(id, this.iaddr, this.port, State.GOOD);
        }

        List<DHTNode> results = rt2.findClosestNodes(n.getInfoHash(), ipv6);

        // then
        verifyAll();
        assertEquals(16, results.size());

        assertEquals(
            "[0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 10]",
            Arrays.toString(results.get(0).getInfoHash()));
        assertEquals(
            "[0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 11]",
            Arrays.toString(results.get(1).getInfoHash()));
        assertEquals(
            "[0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 12]",
            Arrays.toString(results.get(2).getInfoHash()));
        assertEquals(
            "[0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 13]",
            Arrays.toString(results.get(3).getInfoHash()));
        assertEquals(
            "[0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 14]",
            Arrays.toString(results.get(4).getInfoHash()));
        assertEquals(
            "[0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 15]",
            Arrays.toString(results.get(5).getInfoHash()));
        assertEquals(
            "[0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 16]",
            Arrays.toString(results.get(6).getInfoHash()));
        assertEquals(
            "[0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 17]",
            Arrays.toString(results.get(7).getInfoHash()));
        assertEquals(
            "[0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 32]",
            Arrays.toString(results.get(8).getInfoHash()));
        assertEquals(
            "[0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 33]",
            Arrays.toString(results.get(9).getInfoHash()));
        assertEquals(
            "[0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 34]",
            Arrays.toString(results.get(10).getInfoHash()));
        assertEquals(
            "[0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 35]",
            Arrays.toString(results.get(11).getInfoHash()));
        assertEquals(
            "[0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 100]",
            Arrays.toString(results.get(12).getInfoHash()));
        assertEquals(
            "[0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 101]",
            Arrays.toString(results.get(13).getInfoHash()));
        assertEquals(
            "[0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 102]",
            Arrays.toString(results.get(14).getInfoHash()));
        assertEquals(
            "[0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 103]",
            Arrays.toString(results.get(15).getInfoHash()));
    }

    /**
     * testFindClosestNodes07() - find start nodes of Routing Table.
     * Routing Table has Nodes: 20 - 26 & 100 - 150
     * Looking for Node 40.
     */
    @Test
    public void testFindClosestNodes07() {
        // given
        boolean ipv6 = false;
        DHTNode n = create(new BigInteger("40").toByteArray(), this.iaddr,
                this.port, State.UNKNOWN);

        // when
        replayAll();

        for (int i = 20; i < 27; i++) {
            byte[] id = new BigInteger("" + i).toByteArray();
            this.rt.addNode(id, this.iaddr, this.port, State.GOOD);
        }

        for (int i = 100; i < 151; i++) {
            byte[] id = new BigInteger("" + i).toByteArray();
            this.rt.addNode(id, this.iaddr, this.port, State.GOOD);
        }

        List<DHTNode> results = this.rt.findClosestNodes(n.getInfoHash(), ipv6);

        // then
        verifyAll();
        assertEquals(16, results.size());

        assertEquals(
                "[0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 20]",
                Arrays.toString(results.get(0).getInfoHash()));
        assertEquals(
                "[0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 21]",
                Arrays.toString(results.get(1).getInfoHash()));
        assertEquals(
                "[0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 22]",
                Arrays.toString(results.get(2).getInfoHash()));
        assertEquals(
                "[0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 23]",
                Arrays.toString(results.get(3).getInfoHash()));
        assertEquals(
                "[0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 24]",
                Arrays.toString(results.get(4).getInfoHash()));
        assertEquals(
                "[0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 25]",
                Arrays.toString(results.get(5).getInfoHash()));
        assertEquals(
                "[0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 26]",
                Arrays.toString(results.get(6).getInfoHash()));
        assertEquals(
            "[0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 100]",
            Arrays.toString(results.get(7).getInfoHash()));
        assertEquals(
            "[0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 101]",
            Arrays.toString(results.get(8).getInfoHash()));
        assertEquals(
            "[0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 102]",
            Arrays.toString(results.get(9).getInfoHash()));
        assertEquals(
            "[0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 103]",
            Arrays.toString(results.get(10).getInfoHash()));
        assertEquals(
            "[0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 104]",
            Arrays.toString(results.get(11).getInfoHash()));
        assertEquals(
            "[0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 105]",
            Arrays.toString(results.get(12).getInfoHash()));
        assertEquals(
            "[0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 106]",
            Arrays.toString(results.get(13).getInfoHash()));
        assertEquals(
            "[0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 107]",
            Arrays.toString(results.get(14).getInfoHash()));
        assertEquals(
            "[0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, -128]",
            Arrays.toString(results.get(15).getInfoHash()));
    }

    /**
     * testFindClosestNodes08() - find END nodes of the Routing Table.
     * Routing Table has Nodes: 20 - 26 & 100 - 150
     * Looking for Node 200.
     */
    @Test
    public void testFindClosestNodes08() {
        // given
        boolean ipv6 = false;
        DHTNode n = create(new BigInteger("200").toByteArray(), this.iaddr,
                this.port, State.UNKNOWN);

        // when
        replayAll();

        for (int i = 20; i < 27; i++) {
            byte[] id = new BigInteger("" + i).toByteArray();
            this.rt.addNode(id, this.iaddr, this.port, State.GOOD);
        }

        for (int i = 100; i < 151; i++) {
            byte[] id = new BigInteger("" + i).toByteArray();
            this.rt.addNode(id, this.iaddr, this.port, State.GOOD);
        }

        List<DHTNode> results = this.rt.findClosestNodes(n.getInfoHash(), ipv6);

        // then
        verifyAll();

        assertEquals(16, results.size());

        assertEquals(
            "[0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 100]",
            Arrays.toString(results.get(0).getInfoHash()));
        assertEquals(
            "[0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 101]",
            Arrays.toString(results.get(1).getInfoHash()));
        assertEquals(
            "[0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 102]",
            Arrays.toString(results.get(2).getInfoHash()));
        assertEquals(
            "[0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 103]",
            Arrays.toString(results.get(3).getInfoHash()));
        assertEquals(
            "[0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 104]",
            Arrays.toString(results.get(4).getInfoHash()));
        assertEquals(
            "[0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 105]",
            Arrays.toString(results.get(5).getInfoHash()));
        assertEquals(
            "[0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 106]",
            Arrays.toString(results.get(6).getInfoHash()));
        assertEquals(
            "[0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 107]",
            Arrays.toString(results.get(7).getInfoHash()));
        assertEquals(
            "[0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, -128]",
            Arrays.toString(results.get(8).getInfoHash()));
        assertEquals(
            "[0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, -127]",
            Arrays.toString(results.get(9).getInfoHash()));
        assertEquals(
            "[0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, -126]",
            Arrays.toString(results.get(10).getInfoHash()));
        assertEquals(
            "[0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, -125]",
            Arrays.toString(results.get(11).getInfoHash()));
        assertEquals(
            "[0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, -124]",
            Arrays.toString(results.get(12).getInfoHash()));
        assertEquals(
            "[0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, -123]",
            Arrays.toString(results.get(13).getInfoHash()));
        assertEquals(
            "[0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, -122]",
            Arrays.toString(results.get(14).getInfoHash()));
        assertEquals(
            "[0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, -121]",
            Arrays.toString(results.get(15).getInfoHash()));
    }

    /**
     * testFindClosestNodes09() - routing table has 5 nodes.
     * Routing Table has Nodes: 20 - 25
     * Looking for Node 40.
     */
    @Test
    public void testFindClosestNodes09() {
        // given
        boolean ipv6 = false;
        DHTNode n = create(new BigInteger("40").toByteArray(), this.iaddr,
                this.port, State.UNKNOWN);

        // when
        replayAll();

        for (int i = 20; i < 26; i++) {
            byte[] id = new BigInteger("" + i).toByteArray();
            this.rt.addNode(id, this.iaddr, this.port, State.GOOD);
        }

        List<DHTNode> results = this.rt.findClosestNodes(n.getInfoHash(), ipv6);

        // then
        verifyAll();
        assertEquals(6, results.size());

        assertEquals(
                "[0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 20]",
                Arrays.toString(results.get(0).getInfoHash()));
        assertEquals(
                "[0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 21]",
                Arrays.toString(results.get(1).getInfoHash()));
        assertEquals(
                "[0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 22]",
                Arrays.toString(results.get(2).getInfoHash()));
        assertEquals(
                "[0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 23]",
                Arrays.toString(results.get(3).getInfoHash()));
        assertEquals(
                "[0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 24]",
                Arrays.toString(results.get(4).getInfoHash()));
        assertEquals(
                "[0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 25]",
                Arrays.toString(results.get(5).getInfoHash()));
    }

    /**
     * testFindExactNode01() - index < size().
     */
    @Test
    public void testFindExactNode01() {
        // given
        boolean ipv6 = false;
        BigInteger nodeId2 = new BigInteger("5");

        // when
        replayAll();
        DHTNode result = this.rt.findExactNode(nodeId2.toByteArray(), ipv6);

        // then
        verifyAll();
        assertNull(result);
    }

    /**
     * testFindExactNode02() - node found ID does not match.
     */
    @Test
    public void testFindExactNode02() {
        // given
        boolean ipv6 = false;
        BigInteger nodeId2 = new BigInteger("5");

        // when
        replayAll();

        addNodes();
        DHTNode result = this.rt.findExactNode(nodeId2.toByteArray(), ipv6);

        // then
        verifyAll();
        assertNull(result);
    }

    /**
     * testFindExactNode03() - found match.
     */
    @Test
    public void testFindExactNode03() {
        // given
        boolean ipv6 = false;
        byte[] bytes0 = new byte[] {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                0, 0, 0, 0, 0, 4 };

        // when
        replayAll();

        addNodes();
        DHTNode result = this.rt.findExactNode(bytes0, ipv6);

        // then
        verifyAll();
        assertNotNull(result);
        assertTrue(Arrays.equals(bytes0, result.getInfoHash()));
    }

    /**
     * testClear01().
     */
    @Test
    public void testClear01() {
        // given
        this.rt.addNode(new BigInteger("1").toByteArray(),
                this.iaddr, this.port, State.GOOD);
        this.rt.addNode(new BigInteger("1").toByteArray(),
                this.iaddr6, this.port, State.GOOD);

        assertEquals(1, this.rt.getTotalNodeCount(true));
        assertEquals(1, this.rt.getTotalNodeCount(false));

        // when
        replayAll();

        this.rt.clear();

        // then
        verifyAll();
        assertEquals(0, this.rt.getTotalNodeCount(true));
        assertEquals(0, this.rt.getTotalNodeCount(false));
    }

    /**
     * Add Nodes to routing table.
     */
    private void addNodes() {
        for (int i = 0; i < 40; i = i + 2) {
            addNode(new BigInteger("" + i));
        }
    }

    /**
     * Add Node to routing table.
     * @param i  BigInteger
     */
    private void addNode(final BigInteger i) {
        byte[] id = fitToSize(new BigInteger("" + i).toByteArray(),
                NODE_ID_LENGTH);
        this.rt.addNode(id, this.iaddr, this.port, State.GOOD);
    }
}

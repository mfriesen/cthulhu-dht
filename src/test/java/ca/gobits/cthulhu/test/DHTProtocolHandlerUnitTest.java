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

import static ca.gobits.cthulhu.test.DHTTestHelper.assertNodesEquals;
import static org.easymock.EasyMock.aryEq;
import static org.easymock.EasyMock.eq;
import static org.easymock.EasyMock.expect;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.math.BigInteger;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.codec.binary.Base64;
import org.easymock.EasyMockRunner;
import org.easymock.EasyMockSupport;
import org.easymock.Mock;
import org.easymock.TestSubject;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.util.ReflectionTestUtils;

import ca.gobits.cthulhu.DHTInfoHashRoutingTable;
import ca.gobits.cthulhu.DHTNodeRoutingTable;
import ca.gobits.cthulhu.DHTProtocolHandler;
import ca.gobits.cthulhu.DHTQueryProtocol;
import ca.gobits.cthulhu.DHTServerConfig;
import ca.gobits.cthulhu.DHTTokenTable;
import ca.gobits.cthulhu.discovery.DHTNodeDiscovery;
import ca.gobits.cthulhu.domain.DHTNode;
import ca.gobits.cthulhu.domain.DHTNode.State;
import ca.gobits.cthulhu.domain.DHTNodeBasic;
import ca.gobits.cthulhu.domain.DHTNodeFactory;
import ca.gobits.cthulhu.domain.DHTPeer;
import ca.gobits.cthulhu.domain.DHTPeerBasic;
import ca.gobits.dht.BDecoder;
import ca.gobits.dht.BEncoder;
import ca.gobits.dht.DHTConversion;
import ca.gobits.dht.DHTIdentifier;

/**
 * DHTProtocolHandler Unit Tests.
 */
@SuppressWarnings({ "unchecked", "boxing" })
@RunWith(EasyMockRunner.class)
public final class DHTProtocolHandlerUnitTest extends EasyMockSupport {

    /** DHTProtcolHandler. */
    @TestSubject
    private final DHTProtocolHandler handler = new DHTProtocolHandler();

    /** Mock routing table. */
    @Mock
    private DHTNodeRoutingTable routingTable;

    /** Mock Peer Routing Table. */
    @Mock
    private DHTInfoHashRoutingTable peerRoutingTable;

    /** Mock DHT Token Table. */
    @Mock
    private DHTTokenTable tokenTable;

    /** Mock DHTServerConfig. */
    @Mock
    private DHTServerConfig config;

    /** Mock DHTNodeDiscovery. */
    @Mock
    private DHTNodeDiscovery discovery;

    /** InetSocketAddress. */
    private InetAddress iaddr;

    /** InetSocketAddress. */
    private InetAddress iaddr6;

    /** Port. */
    private final int port = 64568;

    /** Node Id: mnopqrstuvwxyz123456. */
    private final byte[] nodeId12345 = new byte[] {109, 110, 111, 112, 113, 114,
            115, 116, 117, 118, 119, 120, 121, 122, 49, 50, 51, 52, 53, 54 };

    /**
     * before().
     * @throws Exception Exception
     */
    @Before
    public void before() throws Exception {
        ReflectionTestUtils.setField(this.handler, "routingTable",
                this.routingTable);

        this.iaddr = InetAddress.getByName("50.71.214.139");
        this.iaddr6 = InetAddress
                .getByName("805b:2d9d:dc28:0000:0000:fc57:d4c8:1fff");

    }

    /**
     * testHandle01() - "ping" request and ping node back.
     * @throws Exception  Exception
     */
    @Test
    public void testHandle01() throws Exception {
        // given
        boolean isIPv6 = false;
        String dat = "d1:ad2:id20:abcdefghij0123456789e1:q4:ping1:t2:aa1:y1:qe";
        byte[] bb = dat.getBytes();
        byte[] id = "abcdefghij0123456789".getBytes();

        DatagramPacket packet = new DatagramPacket(bb, bb.length, this.iaddr,
                this.port);

        // when
        this.discovery.addNode(this.iaddr, this.port);
        expect(this.config.getNodeId()).andReturn(
                "ABCDEFGHIJKLMNOPQRST".getBytes());
        expect(this.routingTable.findExactNode(aryEq(id), eq(isIPv6)))
                .andReturn(null);

        replayAll();
        byte[] resultBytes = this.handler.handle(packet);

        // then
        verifyAll();

        String result = new String(resultBytes);

        assertTrue(result.contains("d2:ip6:2G"));
        assertTrue(result
                .endsWith("81:rd2:id20:ABCDEFGHIJKLMNOPQRSTe1:t2:aa1:y1:re"));
    }

    /**
     * testHandle02() - "ping" request and node already in RoutingTable.
     * @throws Exception  Exception
     */
    @Test
    public void testHandle02() throws Exception {
        // given
        boolean isIPv6 = false;
        String dat = "d1:ad2:id20:abcdefghij0123456789e1:q4:ping1:t2:aa1:y1:qe";
        byte[] id = "abcdefghij0123456789".getBytes();
        byte[] bb = dat.getBytes();

        DHTNode node = new DHTNodeBasic();
        node.setState(State.UNKNOWN);
        DatagramPacket packet = new DatagramPacket(bb, bb.length, this.iaddr,
                this.port);

        // when
        expect(this.config.getNodeId()).andReturn(
                "ABCDEFGHIJKLMNOPQRST".getBytes());
        expect(this.routingTable.findExactNode(aryEq(id), eq(isIPv6)))
                .andReturn(node);

        replayAll();
        byte[] resultBytes = this.handler.handle(packet);

        // then
        verifyAll();

        assertEquals(State.GOOD, node.getState());
        String result = new String(resultBytes);

        assertTrue(result.contains("d2:ip6:2G"));
        assertTrue(result
                .endsWith("81:rd2:id20:ABCDEFGHIJKLMNOPQRSTe1:t2:aa1:y1:re"));
    }

    /**
     * testHandle03() - "find_node" ipv4 compare our result from
     * router.bittorrent.com response.
     * @throws Exception  Exception
     */
    @Test
    public void testHandle03() throws Exception {
        // given
        boolean isIPv6 = false;
        // 1019541382561204384426858321430530264477101611793")
        byte[] nodeId = new byte[] {-78, -107, -47, 23, 19, 90, -105, 99, -38,
                40, 46, 125, -82, 115, -91, -54, 125, 62, 91, 17 };
        // 1461501637330902918203684832716283019655932542975
        byte[] target = new byte[] {-1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
                -1, -1, -1, -1, -1, -1, -1, -1, -1, -1 };

        byte[] id = DHTIdentifier.sha1("salt".getBytes());
        byte[] bb = DHTQueryProtocol.findNodeQuery("aa", id , target, null);

        DatagramPacket packet = new DatagramPacket(bb, bb.length, this.iaddr,
                this.port);

        assertEquals(20, nodeId.length);
        assertEquals(20, target.length);

        // when
        expect(this.routingTable.findExactNode(aryEq(nodeId), eq(isIPv6)))
            .andReturn(new DHTNodeBasic());

        expect(this.routingTable.findClosestNodes(aryEq(target), eq(isIPv6)))
            .andReturn(getFindNodes());

        replayAll();
        byte[] bytes = this.handler.handle(packet);

        // then
        verifyAll();

        byte[] bytesA = Base64.decodeBase64(getBase64FindNodeResponse());

        Map<String, Object> mapA = (Map<String, Object>)
                new BDecoder().decode(bytesA);
        assertEquals(bytesA.length, bytes.length);

        Map<String, Object> mapB = (Map<String, Object>)
                new BDecoder().decode(bytes);

        assertArrayEquals(mapA.keySet().toArray(new String[0]),
                mapB.keySet().toArray(new String[0]));

        assertEquals(new String((byte[]) mapA.get("t")),
                new String((byte[]) mapB.get("t")));

        assertEquals(new String((byte[]) mapA.get("y")),
                new String((byte[]) mapB.get("y")));

        assertEquals(new String((byte[]) mapA.get("ip")),
                new String((byte[]) mapB.get("ip")));

        Map<String, Object> mapA1 = (Map<String, Object>) mapA.get("r");
        Map<String, Object> mapB1 = (Map<String, Object>) mapB.get("r");

        assertArrayEquals(mapA1.keySet().toArray(new String[0]),
                mapB1.keySet().toArray(new String[0]));

        byte[] idA = (byte[]) mapA1.get("id");
        byte[] idB = (byte[]) mapB1.get("id");
        assertEquals(idA.length, idB.length);

        assertNodesEquals((byte[]) mapA1.get("nodes"),
                (byte[]) mapB1.get("nodes"));
    }

    /**
     * testHandle04() - "find_node" ipv6.
     * @throws Exception  Exception
     */
    @Test
    public void testHandle04() throws Exception {
        // given
        boolean isIPv6 = true;
        // 1019541382561204384426858321430530264477101611793")
        byte[] nodeId = new byte[] {-78, -107, -47, 23, 19, 90, -105, 99, -38,
                40, 46, 125, -82, 115, -91, -54, 125, 62, 91, 17 };
        // 1461501637330902918203684832716283019655932542975
        byte[] target = new byte[] {-1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
                -1, -1, -1, -1, -1, -1, -1, -1, -1, -1 };

        assertEquals(20, nodeId.length);
        assertEquals(20, target.length);

        byte[] id = DHTIdentifier.sha1("salt".getBytes());
        byte[] bb = DHTQueryProtocol.findNodeQuery("aa", id, target,
                Arrays.asList("n6".getBytes()));

        DatagramPacket packet = new DatagramPacket(bb, bb.length, this.iaddr6,
                this.port);

        // when
        expect(this.routingTable.findExactNode(aryEq(nodeId), eq(isIPv6)))
            .andReturn(new DHTNodeBasic());

        expect(this.routingTable.findClosestNodes(aryEq(target), eq(isIPv6)))
            .andReturn(getFindNodes6());

        replayAll();
        byte[] bytes = this.handler.handle(packet);

        // then
        verifyAll();

        Map<Object, Object> map = (Map<Object, Object>)
                new BDecoder().decode(bytes);

        assertTrue(map.containsKey("ip"));
        assertEquals("aa", new String((byte[]) map.get("t")));
        assertEquals("r", new String((byte[]) map.get("y")));

        Map<Object, Object> r = (Map<Object, Object>) map.get("r");
        assertNotNull(r.get("id"));

        byte[] nodes6 = (byte[]) r.get("nodes6");
        assertEquals(76, nodes6.length);

        byte[] ip0 = new byte[18];
        System.arraycopy(nodes6, 20, ip0, 0, 18);
        assertEquals("805b:2d9d:dc28:0:0:fc57:d4c8:1fff", DHTConversion
                .compactAddress(ip0).getHostAddress());
        assertEquals(37518, DHTConversion.compactAddressPort(ip0));

        byte[] ip1 = new byte[18];
        System.arraycopy(nodes6, 58, ip1, 0, 18);
        assertEquals("ffff:ffff:ffff:ffff:ffff:ffff:ffff:ffff", DHTConversion
                .compactAddress(ip1).getHostAddress());
        assertEquals(11503, DHTConversion.compactAddressPort(ip1));
    }

    /**
     * testHandle05() - test "unknown method" request.
     * @throws Exception  Exception
     */
    @Test
    public void testHandle05() throws Exception {
        // given
        String dat = "d1:ad2:id20:abcdefghij0123456789e1:q4:pinA1:t2:aa1:y1:qe";
        byte[] bb = dat.getBytes();

        DatagramPacket packet = new DatagramPacket(bb, bb.length, this.iaddr,
                this.port);
        expectUpdateNodeStatus(false);

        // when
        replayAll();
        byte[] bytes = this.handler.handle(packet);

        // then
        verifyAll();

        String result = new String(bytes);
        assertTrue(result.endsWith(":rd3:20414:Method Unknowne1:t2:aa1:y1:ee"));

        assertEquals("ZDI6aXA2OjJH1ov8ODE6cmQzOjIwNDE0Ok1ldGh"
                + "vZCBVbmtub3duZTE6dDI6YWExOnkxOmVl",
                Base64.encodeBase64String(bytes));
    }

    /**
     * testHandle06() - test exception thrown in queryRequestHandler.
     * @throws Exception  Exception
     */
    @Test
    public void testHandle06() throws Exception {
        // given
        String dat = "d1:ad2:id20:abcdefghij0123456789e1:q4:ping1:t2:aa1:y1:qe";
        byte[] bb = dat.getBytes();

        DatagramPacket pkt = new DatagramPacket(bb, bb.length, null, this.port);


        // when
        replayAll();
        byte[] bytes = this.handler.handle(pkt);

        // then
        verifyAll();

        String result = new String(bytes);
        assertEquals("d1:rd3:20212:Server Errore1:t2:aa1:y1:ee", result);
    }

    /**
     * testHandle07() - test "garbage" request.
     * @throws Exception  Exception
     */
    @Test
    public void testHandle07() throws Exception {
        // given
        String dat = "adsadadsa";
        byte[] bb = dat.getBytes();

        DatagramPacket packet = new DatagramPacket(bb, bb.length, this.iaddr,
                this.port);

        // when
        replayAll();
        byte[] bytes = this.handler.handle(packet);

        // then
        verifyAll();

        String result = new String(bytes);
        assertTrue(result.startsWith("d1:rd3:20212:Server Errore1:y1:ee"));
    }

    /**
     * testHandle08() - test "get_peers" request and peers exists.
     * @throws Exception  Exception
     */
    @Test
    public void testHandle08() throws Exception {
        // given
        String dat = "d1:ad2:id20:abcdefghij01234567899:info_hash20:"
                + "mnopqrstuvwxyz123456e1:q9:get_peers1:t2:aa1:y1:qe";
        byte[] bb = dat.getBytes();

        DatagramPacket packet = new DatagramPacket(bb, bb.length, this.iaddr,
                this.port);

        DHTPeer peer = new DHTPeerBasic(InetAddress.getByName("240.120.222.12")
                .getAddress(), 23);
        Collection<DHTPeer> peers = Arrays.asList(peer);

        // when
        expect(this.peerRoutingTable.findPeers(aryEq(this.nodeId12345)))
                .andReturn(peers);
        expectUpdateNodeStatus(false);

        replayAll();
        byte[] bytes = this.handler.handle(packet);

        // then
        verifyAll();

        Map<String, Object> map = (Map<String, Object>) new BDecoder()
            .decode(bytes);

        assertEquals("aa", new String((byte[]) map.get("t")));
        assertEquals("r", new String((byte[]) map.get("y")));

        Map<String, Object> rmap = (Map<String, Object>) map.get("r");

        assertEquals(20, ((byte[]) rmap.get("id")).length);
        assertEquals(10, ((byte[]) rmap.get("token")).length);

        List<byte[]> list = (List<byte[]>) rmap.get("values");
        assertEquals(1, list.size());
        byte[] addr = list.get(0);
        assertEquals(6, addr.length);
        assertEquals(-16, addr[0]);
        assertEquals(120, addr[1]);
        assertEquals(-34, addr[2]);
        assertEquals(12, addr[3]);
        assertEquals(0, addr[4]);
        assertEquals(23, addr[5]);
    }

    /**
     * testHandle09() - test IPv4 "get_peers" request and InfoHash node is
     * null.
     *
     * @throws Exception Exception
     */
    @Test
    public void testHandle09() throws Exception {
        // given
        boolean isIPv6 = false;
        String dat = "d1:ad2:id20:abcdefghij01234567899:info_hash20:"
                + "mnopqrstuvwxyz123456e1:q9:get_peers1:t2:aa1:y1:qe";
        byte[] bb = dat.getBytes();

        DatagramPacket packet = new DatagramPacket(bb, bb.length, this.iaddr,
                this.port);

        // when
        expectUpdateNodeStatus(false);
        expect(this.peerRoutingTable.findPeers(aryEq(this.nodeId12345)))
                .andReturn(null);
        expect(
                this.routingTable.findClosestNodes(aryEq(this.nodeId12345),
                        eq(isIPv6))).andReturn(getFindNodes());

        replayAll();
        byte[] bytes = this.handler.handle(packet);

        // then
        verifyAll();

        Map<String, Object> map = (Map<String, Object>) new BDecoder()
            .decode(bytes);

        assertEquals("aa", new String((byte[]) map.get("t")));
        assertEquals("r", new String((byte[]) map.get("y")));

        Map<String, Object> rmap = (Map<String, Object>) map.get("r");

        assertEquals(20, ((byte[]) rmap.get("id")).length);
        assertEquals(10, ((byte[]) rmap.get("token")).length);

        assertFalse(rmap.containsKey("nodes6"));
        assertEquals(416, ((byte[]) rmap.get("nodes")).length);
        assertEquals(
                "s6s1sj0aMsjo0fAFMAYObQwHAZUlTKAcko6fSsRpI5pQsyj+7UB/D5"
                + "fUrH1DCrY7sMcs71v5bQCnbfiHZdCYH+bzcSJBMroBsnzNMUIPc0T"
                + "rbacU+emI/f3ENJ80FvNRHtQFDdrW2zSAW+gVIoWVAVfYp36yPfzL"
                + "wyChrE+jbUxxbdwPD/tJnDRHnKk+LL0E2lNsZDNMAr7eT+L6hgEV0"
                + "dmFDd76VjKCOU0lzsq2xcpc7V1FQ3c1TskWAgINWmFP8ZwVCfqL+C2"
                + "gGgWB5RBVXXIJ0QSc7jPmoezhUWU4FxDLlSqWQ6YyH8+qJhxH2JA24"
                + "fZkTg2cGqCiigLpTryy3qIXR2LYcjkD0zuwq7mXTdW4xHM0jckJkh/"
                + "YovBPn1VwC+JW0CQiRviQspuEOdqnVNgDH7U4wuofhLVzzTAzsibfP6"
                + "HZ5PrsGT6YuQxQ6bXWL8YscSaD7Z5naTyhNbye99B64dG5J08WQ0yWd"
                + "pwHVCMYWbhgQ2OE3X4RTyLF7WXyXGNXe2YIxN47mXqYnZtH9vXi/dzC"
                + "giaNTUqwDDsy8HE=",
                Base64.encodeBase64String((byte[]) rmap.get("nodes")));
    }

    /**
     * testHandle10() - test IPv6 "get_peers" request and InfoHash node is
     * null.
     *
     * @throws Exception Exception
     */
    @Test
    public void testHandle10() throws Exception {
        // given
        boolean isIPv6 = true;

        byte[] bb = DHTQueryProtocol.getPeers("aa",
                "abcdefghij0123456789".getBytes(),
                "mnopqrstuvwxyz123456".getBytes(),
                Arrays.asList("n6".getBytes()));

        DatagramPacket packet = new DatagramPacket(bb, bb.length, this.iaddr6,
                this.port);

        // when
        expectUpdateNodeStatus(true);
        expect(this.peerRoutingTable.findPeers(aryEq(this.nodeId12345)))
                .andReturn(null);
        expect(
                this.routingTable.findClosestNodes(aryEq(this.nodeId12345),
                        eq(isIPv6))).andReturn(getFindNodes6());

        replayAll();
        byte[] bytes = this.handler.handle(packet);

        // then
        verifyAll();

        Map<String, Object> map = (Map<String, Object>) new BDecoder()
            .decode(bytes);

        assertEquals("aa", new String((byte[]) map.get("t")));
        assertEquals("r", new String((byte[]) map.get("y")));

        Map<String, Object> rmap = (Map<String, Object>) map.get("r");

        assertEquals(20, ((byte[]) rmap.get("id")).length);
        assertEquals(10, ((byte[]) rmap.get("token")).length);

        assertFalse(rmap.containsKey("nodes"));
        assertEquals(76, ((byte[]) rmap.get("nodes6")).length);
        assertEquals(
                "s6s1sj0aMsjo0fAFMAYObQwHAZWAWy2d3CgAAAAA/FfUyB//ko6fSsRpI5pQsy"
                + "j+7UB/D5fUrH1DCv////////////////////8s7w==",
                Base64.encodeBase64String((byte[]) rmap.get("nodes6")));
    }

    /**
     * testHandle11() - "announce_peer" request and InfoHash is found.
     *
     * @throws Exception Exception
     */
    @Test
    public void testHandle11() throws Exception {
        // given
        int p = 6881;
        byte[] secret = "aoeusnth".getBytes();
        String dat = "d1:ad2:id20:abcdefghij01234567899:info_hash20:"
                + "mnopqrstuvwxyz1234564:porti6881e5:token8:aoeusnthe1:q13:"
                + "announce_peer1:t2:aa1:y1:qe";
        byte[] bb = dat.getBytes();

        DatagramPacket packet = new DatagramPacket(bb, bb.length, this.iaddr,
                this.port);

        // when
        expectUpdateNodeStatus(false);
        expect(this.tokenTable.valid(eq(this.iaddr), eq(p), aryEq(secret)))
                .andReturn(true);
        this.peerRoutingTable.addPeer(aryEq(this.nodeId12345),
                aryEq(this.iaddr.getAddress()), eq(p));

        replayAll();
        byte[] bytes = this.handler.handle(packet);

        // then
        verifyAll();

        verifyAnnouncePeer(bytes);
    }

    /**
     * testHandle12() - "announce_peer" request, with implied_port of 0.
     *
     * @throws Exception Exception
     */
    @Test
    public void testHandle12() throws Exception {
        // given
        int p = 6881;
        byte[] secret = "aoeusnth".getBytes();
        String dat = "d1:ad2:id20:abcdefghij01234567899:info_hash20:"
                + "mnopqrstuvwxyz12345612:implied_porti0e4:porti6881e5:"
                + "token8:aoeusnthe1:q13:announce_peer1:t2:aa1:y1:qe";
        byte[] bb = dat.getBytes();

        DatagramPacket packet = new DatagramPacket(bb, bb.length, this.iaddr,
                this.port);

        // when
        expectUpdateNodeStatus(false);
        expect(this.tokenTable.valid(eq(this.iaddr), eq(6881), aryEq(secret)))
                .andReturn(true);
        this.peerRoutingTable.addPeer(aryEq(this.nodeId12345),
                aryEq(this.iaddr.getAddress()), eq(p));

        replayAll();
        byte[] bytes = this.handler.handle(packet);

        // then
        verifyAll();

        verifyAnnouncePeer(bytes);
    }

    /**
     * testHandle13() - "announce_peer" request, with implied_port of 1.
     *
     * @throws Exception Exception
     */
    @Test
    public void testHandle13() throws Exception {
        // given
        byte[] secret = "aoeusnth".getBytes();
        String dat = "d1:ad2:id20:abcdefghij01234567899:info_hash20:"
                + "mnopqrstuvwxyz12345612:implied_porti1e4:porti6881e5:"
                + "token8:aoeusnthe1:q13:announce_peer1:t2:aa1:y1:qe";
        byte[] bb = dat.getBytes();

        DatagramPacket packet = new DatagramPacket(bb, bb.length, this.iaddr,
                this.port);

        // when
        expectUpdateNodeStatus(false);
        expect(
                this.tokenTable.valid(eq(this.iaddr), eq(this.port),
                        aryEq(secret))).andReturn(true);

        this.peerRoutingTable.addPeer(aryEq(this.nodeId12345),
                aryEq(this.iaddr.getAddress()), eq(this.port));

        replayAll();
        byte[] bytes = this.handler.handle(packet);

        // then
        verifyAll();

        verifyAnnouncePeer(bytes);
    }

    /**
     * testHandle14() - "announce_peer" request, with invalid token.
     *
     * @throws Exception Exception
     */
    @Test
    public void testHandle14() throws Exception {
        // given
        byte[] secret = "aoeusnth".getBytes();

        String dat = "d1:ad2:id20:abcdefghij01234567899:info_hash20:"
                + "mnopqrstuvwxyz12345612:implied_porti1e4:porti6881e5:"
                + "token8:aoeusnthe1:q13:announce_peer1:t2:aa1:y1:qe";
        byte[] bb = dat.getBytes();

        DatagramPacket packet = new DatagramPacket(bb, bb.length, this.iaddr,
                this.port);

        // when
        expectUpdateNodeStatus(false);
        expect(
                this.tokenTable.valid(eq(this.iaddr), eq(this.port),
                        aryEq(secret))).andReturn(false);

        replayAll();
        byte[] bytes = this.handler.handle(packet);

        // then
        verifyAll();

        String result = new String(bytes);
        assertTrue(result.endsWith(":rd3:2039:Bad Tokene1:t2:aa1:y1:ee"));
    }

    /**
     * testHandle15() - test request is NOT "r".
     * @throws Exception  Exception
     */
    @Test
    public void testHandle15() throws Exception {
        // given
        String s = "d1:t2:aa1:y1:re";
        byte[] bb = s.getBytes();
        DatagramPacket packet = new DatagramPacket(bb, bb.length, this.iaddr,
                this.port);

        // when
        replayAll();

        byte[] bytes = this.handler.handle(packet);

        // then
        verifyAll();

        assertNull(bytes);
    }

    /**
     * testHandle16() - test request is HAS "r", not does not have "id".
     * @throws Exception  Exception
     */
    @Test
    public void testHandle16() throws Exception {
        // given
        String s = "d1:rd2:ia20:mnopqrstuvwxyz123456e1:t2:aa1:y1:re";
        byte[] bb = s.getBytes();
        DatagramPacket packet = new DatagramPacket(bb, bb.length, this.iaddr,
                this.port);

        // when
        replayAll();

        byte[] bytes = this.handler.handle(packet);

        // then
        verifyAll();

        assertNull(bytes);
    }

    /**
     * testHandle17() - test request HAS "id" and NODE IS found.
     * @throws Exception  Exception
     */
    @Test
    public void testHandle17() throws Exception {
        // given
        boolean isIPv6 = false;
        byte[] id = new byte[] {109, 110, 111, 112, 113, 114, 115, 116, 117,
                118, 119, 120, 121, 122, 49, 50, 51, 52, 53, 54 };
        String s = "d1:rd2:id20:mnopqrstuvwxyz123456e1:t2:aa1:y1:re";
        byte[] bb = s.getBytes();
        DatagramPacket packet = new DatagramPacket(bb, bb.length, this.iaddr,
                this.port);

        DHTNode node = new DHTNodeBasic();
        assertNull(node.getState());

        // when
        expect(this.tokenTable.isValidTransactionId("aa")).andReturn(true);
        expect(this.routingTable.findExactNode(aryEq(id), eq(isIPv6)))
                .andReturn(node);
        replayAll();

        byte[] bytes = this.handler.handle(packet);

        // then
        verifyAll();

        assertNull(bytes);
        assertEquals(State.GOOD, node.getState());
    }

    /**
     * testHandle18() - test request HAS "id" and NODE IS NOT found.
     * Transaction ID is valid.
     * @throws Exception  Exception
     */
    @Test
    public void testHandle18() throws Exception {

        // given
        DHTNode node = null;
        boolean isIPv6 = false;
        byte[] id = new byte[] {109, 110, 111, 112, 113, 114, 115, 116, 117,
                118, 119, 120, 121, 122, 49, 50, 51, 52, 53, 54 };

        String s = "d1:rd2:id20:mnopqrstuvwxyz123456e1:t2:aa1:y1:re";
        byte[] bb = s.getBytes();
        DatagramPacket packet = new DatagramPacket(bb, bb.length, this.iaddr,
                this.port);

        // when
        expect(this.routingTable.findExactNode(aryEq(id), eq(isIPv6)))
                .andReturn(node);
        expect(this.tokenTable.isValidTransactionId("aa")).andReturn(true);
        this.routingTable.addNode(aryEq(id), eq(this.iaddr), eq(this.port),
                eq(State.GOOD));

        replayAll();

        byte[] bytes = this.handler.handle(packet);

        // then
        verifyAll();

        assertNull(bytes);
    }

    /**
     * testHandle19() - test request HAS "id" and NODE IS NOT found.
     * Transaction ID is NOT valid.
     * @throws Exception  Exception
     */
    @Test
    public void testHandle19() throws Exception {

        // given
        String s = "d1:rd2:id20:mnopqrstuvwxyz123456e1:t2:aa1:y1:re";
        byte[] bb = s.getBytes();
        DatagramPacket packet = new DatagramPacket(bb, bb.length, this.iaddr,
                this.port);

        // when
        expect(this.tokenTable.isValidTransactionId("aa")).andReturn(false);

        replayAll();

        byte[] bytes = this.handler.handle(packet);

        // then
        verifyAll();

        assertNull(bytes);
    }

    /**
     * testHandle20() - test invalid packet missing "T" param.
     * @throws Exception  Exception
     */
    @Test
    public void testHandle20() throws Exception {
        // given
        String dat = "d1:ad2:id20:abcdefghij0123456789e1:q4:ping1:z2:aa1:y1:qe";
        byte[] bb = dat.getBytes();

        DatagramPacket packet = new DatagramPacket(bb, bb.length, this.iaddr,
                this.port);

        // when
        replayAll();
        byte[] result = this.handler.handle(packet);

        // then
        verifyAll();

        assertNull(result);
    }

    /**
     * testHandle21() - test invalid packet missing "Y" param.
     * @throws Exception  Exception
     */
    @Test
    public void testHandle21() throws Exception {
        // given
        String dat = "d1:ad2:id20:abcdefghij0123456789e1:q4:ping1:t2:aa1:z1:qe";
        byte[] bb = dat.getBytes();

        DatagramPacket packet = new DatagramPacket(bb, bb.length, this.iaddr,
                this.port);

        // when
        replayAll();
        byte[] result = this.handler.handle(packet);

        // then
        verifyAll();

        assertNull(result);
    }

    /**
     * Handles DHT Query "find_nodes" ipv4 Response.
     * @throws Exception  Exception
     */
    @Test
    public void testHandle22() throws Exception {
        // given
        byte[] bb = Base64.decodeBase64(getBase64FindNodeResponse());
        DatagramPacket packet = new DatagramPacket(bb, bb.length, this.iaddr,
                this.port);

        expect(this.tokenTable.isValidTransactionId("aa")).andReturn(
                Boolean.FALSE);

        // when
        this.discovery.addNode(InetAddress.getByName("37.76.160.28"), 37518);
        this.discovery.addNode(InetAddress.getByName("182.59.176.199"), 11503);
        this.discovery.addNode(InetAddress.getByName("178.124.205.49"), 16911);
        this.discovery.addNode(InetAddress.getByName("5.13.218.214"), 56116);
        this.discovery.addNode(InetAddress.getByName("79.163.109.76"), 29037);
        this.discovery.addNode(InetAddress.getByName("2.190.222.79"), 58106);
        this.discovery.addNode(InetAddress.getByName("92.237.93.69"), 17271);
        this.discovery.addNode(InetAddress.getByName("5.129.229.16"), 21853);
        this.discovery.addNode(InetAddress.getByName("67.166.50.31"), 53162);
        this.discovery.addNode(InetAddress.getByName("178.222.162.23"), 18274);
        this.discovery.addNode(InetAddress.getByName("31.216.162.240"), 20383);
        this.discovery.addNode(InetAddress.getByName("31.181.56.194"), 59935);
        this.discovery.addNode(InetAddress.getByName("80.233.181.214"), 12230);
        this.discovery.addNode(InetAddress.getByName("79.22.67.76"), 38518);
        this.discovery.addNode(InetAddress.getByName("92.99.87.123"), 26120);
        this.discovery.addNode(InetAddress.getByName("176.12.59.50"), 61553);

        replayAll();
        byte[] result = this.handler.handle(packet);

        // then
        verifyAll();

        assertNull(result);
    }

    /**
     * Handles DHT Query "find_nodes" ipv6 Response.
     * @throws Exception  Exception
     */
    @Test
    public void testHandle23() throws Exception {
        // given
        Map<Object, Object> map = new HashMap<Object, Object>();
        map.put("t", "aa");
        map.put("y", "r");
        Map<Object, Object> r = new HashMap<Object, Object>();
        r.put("id", DHTIdentifier.sha1("salt".getBytes()));
        r.put("nodes6", new byte[] {80, 13, -127, -86, -2, 99, 119, 23, -91,
                47, -122, 80, -27, 66, 6, -26, 77, -93, 61, 39, -1, -1, -1, -1,
                -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 0, 123, -7, 55,
                -61, 126, -108, -99, -98, -6, 32, -46, -107, -118, -13, 9, 35,
                92, 115, -20, 3, -102, -128, 91, 45, -99, -36, 40, 0, 0, 0, 0,
                -4, 87, -44, -56, 31, -1, 0, 124 });
        map.put("r", r);

        byte[] bb = BEncoder.bencoding(map);
        DatagramPacket packet = new DatagramPacket(bb, bb.length, this.iaddr,
                this.port);

        expect(this.tokenTable.isValidTransactionId("aa")).andReturn(
                Boolean.FALSE);

        // when
        this.discovery.addNode(InetAddress
                .getByName("ffff:ffff:ffff:ffff:ffff:ffff:ffff:ffff"), 123);
        this.discovery.addNode(InetAddress
                .getByName("805b:2d9d:dc28:0000:0000:fc57:d4c8:1fff"), 124);

        replayAll();
        byte[] result = this.handler.handle(packet);

        // then
        verifyAll();

        assertNull(result);
    }

    /**
     * Verify AnnouncePeer response.
     * @param bytes  bytes
     * @throws IOException  IOException
     */
    private void verifyAnnouncePeer(final byte[] bytes) throws IOException {

        Map<String, Object> map = (Map<String, Object>) new BDecoder()
            .decode(bytes);

        assertEquals("aa", new String((byte[]) map.get("t")));
        assertEquals("r", new String((byte[]) map.get("y")));

        Map<String, Object> rmap = (Map<String, Object>) map.get("r");

        byte[] id = (byte[]) rmap.get("id");
        assertEquals(20, id.length);
        assertEquals("mnopqrstuvwxyz123456", new String(id));
    }

    /**
     * Adds expected nodes to the routing table.
     * @throws UnknownHostException  UnknownHostException
     * @return List<DHTNode>
     */
    private List<DHTNode> getFindNodes() throws UnknownHostException {

        return Arrays.asList(
                createDHTNode(
                "1025727453009050644114422909938179475956677673365",
                "37.76.160.28", 37518),
        createDHTNode(
                "909396897490697132528408310795708133687135388426",
                "182.59.176.199", 11503),
        createDHTNode(
                "525080541161122160152898021711579691652547262977",
                "178.124.205.49", 16911),
        createDHTNode(
                "658070898018303575756492289276695009391046368980",
                "5.13.218.214", 56116),
        createDHTNode(
                "732800403720670969048970409366815229228420735404",
                "79.163.109.76", 29037),
        createDHTNode(
                "1256313872952230430598882201394466767467396215628",
                "2.190.222.79", 58106),
        createDHTNode(
                "765028964801745612216665519019856689419949360586",
                "92.237.93.69", 17271),
        createDHTNode(
                "304333486037502350876881646365121976203989590042",
                "5.129.229.16", 21853),
        createDHTNode(
                "651043862618190073616414008555095633000553327254",
                "67.166.50.31", 53162),
        createDHTNode(
                "217572328821850967755762913845138112465869557436",
                "178.222.162.23", 18274),
        createDHTNode(
                "1235689258152504075304182876266224318368488950162",
                "31.216.162.240", 20383),
        createDHTNode(
                "487762934236616301113020799412763967579181340675",
                "31.181.56.194", 59935),
        createDHTNode(
                "757633304364519595494275276101980823332425611532",
                "80.233.181.214", 12230),
        createDHTNode(
                "253718933283387888344146948372599275024431560999",
                "79.22.67.76", 38518),
        createDHTNode(
                "890765994839177116145299793227790251293353534962",
                "92.99.87.123", 26120),
        createDHTNode(
                "1123918148366576699094456176144333565208604527946",
                "176.12.59.50", 61553));
    }

    /**
     * Adds expected nodes to the routing table.
     * @throws UnknownHostException  UnknownHostException
     * @return List<DHTNode>
     */
    private List<DHTNode> getFindNodes6() throws UnknownHostException {

        return Arrays.asList(
                createDHTNode(
                "1025727453009050644114422909938179475956677673365",
                "805b:2d9d:dc28:0000:0000:fc57:d4c8:1fff", 37518),
        createDHTNode(
                "909396897490697132528408310795708133687135388426",
                "ffff:ffff:ffff:ffff:ffff:ffff:ffff:ffff", 11503));
    }

    /**
     * Create DHTNode.
     * @param id  identifier
     * @param address  hostname
     * @param portNo  port
     * @return DHTNode
     * @throws UnknownHostException  UnknownHostException
     */
    private DHTNode createDHTNode(final String id, final String address,
            final int portNo) throws UnknownHostException {
        InetAddress addr = InetAddress.getByName(address);
        return DHTNodeFactory.create(new BigInteger(id).toByteArray(), addr,
                portNo, DHTNode.State.UNKNOWN);
    }

    /**
     * real find_node response from router.bittorrent.com.
     * @return String
     */
    private String getBase64FindNodeResponse() {
        return "ZDI6aXA2OjJH1ov8ODE6cmQyOmlkMjA6HbzsI8Zpc1H/Suwpz"
                + "bqr8vvjRmc1Om5vZGVzNDE2OrOrNbI9GjLI6NHwBTAGDm0MBwGV"
                + "JUygHJKOn0rEaSOaULMo/u1Afw+X1Kx9Qwq2O7DHLO9b+W0Ap234"
                + "h2XQmB/m83EiQTK6AbJ8zTFCD3NE622nFPnpiP39xDSfNBbzUR7UB"
                + "Q3a1ts0gFvoFSKFlQFX2Kd+sj38y8MgoaxPo21McW3cDw/7SZw0R5y"
                + "pPiy9BNpTbGQzTAK+3k/i+oYBFdHZhQ3e+lYygjlNJc7KtsXKXO1dRU"
                + "N3NU7JFgICDVphT/GcFQn6i/gtoBoFgeUQVV1yCdEEnO4z5qHs4VFlOB"
                + "cQy5UqlkOmMh/PqiYcR9iQNuH2ZE4NnBqgoooC6U68st6iF0di2HI5A9M"
                + "7sKu5l03VuMRzNI3JCZIf2KLwT59VcAviVtAkIkb4kLKbhDnap1TYAx+"
                + "1OMLqH4S1c80wM7Im3z+h2eT67Bk+mLkMUOm11i/GLHEmg+2eZ2k8oTW8"
                + "nvfQeuHRuSdPFkNMlnacB1QjGFm4YENjhN1+EU8ixe1l8lxjV3tmCMTeO5"
                + "l6mJ2bR/b14v3cwoImjU1KsAw7MvBxZTE6dDI6YWExOnkxOnJl";
    }

    /**
     * expect Update Node Status to Good.
     * @param isIPv6  Is IPv6
     */
    private void expectUpdateNodeStatus(final boolean isIPv6) {
        byte[] bytes = new BigInteger(
                "555966236078696110491139251576793858856027895865")
                .toByteArray();
        expect(this.routingTable.findExactNode(aryEq(bytes), eq(isIPv6)))
                .andReturn(new DHTNodeBasic());
    }
}

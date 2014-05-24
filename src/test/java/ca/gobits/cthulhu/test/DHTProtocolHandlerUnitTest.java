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

import static ca.gobits.cthulhu.test.DHTTestHelper.assertNodesEquals;
import static org.easymock.EasyMock.aryEq;
import static org.easymock.EasyMock.capture;
import static org.easymock.EasyMock.eq;
import static org.easymock.EasyMock.expect;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.socket.DatagramPacket;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.codec.binary.Base64;
import org.easymock.Capture;
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
import ca.gobits.cthulhu.DHTServer;
import ca.gobits.cthulhu.DHTTokenTable;
import ca.gobits.cthulhu.domain.DHTNode;
import ca.gobits.cthulhu.domain.DHTNodeFactory;
import ca.gobits.cthulhu.domain.DHTPeer;
import ca.gobits.cthulhu.domain.DHTPeerBasic;
import ca.gobits.dht.BDecoder;
import ca.gobits.dht.BEncoder;

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

    /** Mock ChannelHandlerContext. */
    @Mock
    private ChannelHandlerContext ctx;

    /** Capture DatagramPacket. */
    private final Capture<DatagramPacket> capturedPacket =
            new Capture<DatagramPacket>();

    /** InetSocketAddress. */
    private InetSocketAddress iaddr;

    /**
     * before().
     * @throws Exception Exception
     */
    @Before
    public void before() throws Exception {
        ReflectionTestUtils.setField(handler, "routingTable", routingTable);

        iaddr = new InetSocketAddress(
                InetAddress.getByName("50.71.214.139"), 64568);
    }

    /**
     * testChannelRead001() - "ping" request.
     * @throws Exception  Exception
     */
    @Test
    public void testChannelRead001() throws Exception {
        // given
        String dat = "d1:ad2:id20:abcdefghij0123456789e1:q4:ping1:t2:aa1:y1:qe";

        DatagramPacket packet = new DatagramPacket(
                Unpooled.copiedBuffer(dat.getBytes()), iaddr,
                iaddr);

        // when
        expect(ctx.write(capture(capturedPacket))).andReturn(null);

        replayAll();
        handler.channelRead0(ctx , packet);

        // then
        verifyAll();

        ByteArrayOutputStream os = handler.extractBytes(capturedPacket
                .getValue().content());

        String result = new String(os.toByteArray());

        assertTrue(result.contains("d2:ip6:2G"));
        assertTrue(result.contains(":rd2:id20:6h"));
        assertTrue(result.contains("e1:t2:aa1:y1:re"));

        assertEquals("ZDI6aXA2OjJH1ov8ODE6cmQyOmlkMjA6NmjAFP"
                + "wuQVw4idejCIEqBeXEPQFlMTp0MjphYTE6eTE6cmU=",
                Base64.encodeBase64String(os.toByteArray()));

        os.close();
    }

    /**
     * testChannelRead003() - "find_node" compare our result from
     * router.bittorrent.com response.
     * @throws Exception  Exception
     */
    @Test
    public void testChannelRead003() throws Exception {
        // given
        BigInteger nodeId = new BigInteger(
                "1461501637330902918203684832716283019655932542975");
        DatagramPacket packet = createFindNodeRequest();

        // when
        expect(routingTable.findClosestNodes(nodeId)).andReturn(getFindNodes());
        expect(ctx.write(capture(capturedPacket))).andReturn(null);

        replayAll();
        handler.channelRead0(ctx , packet);

        // then
        verifyAll();

        ByteArrayOutputStream os = handler.extractBytes(capturedPacket
                .getValue().content());

        byte[] bytesA = Base64.decodeBase64(getBase64FindNodeResponse());

        Map<String, Object> mapA = (Map<String, Object>)
                new BDecoder().decode(bytesA);
        assertEquals(bytesA.length, os.toByteArray().length);

        Map<String, Object> mapB = (Map<String, Object>)
                new BDecoder().decode(os.toByteArray());

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

        os.close();
    }

    /**
     * testChannelRead004() - test "unknown method" request.
     * @throws Exception  Exception
     */
    @Test
    public void testChannelRead004() throws Exception {
        // given
        String dat = "d1:ad2:id20:abcdefghij0123456789e1:q4:pinA1:t2:aa1:y1:qe";

        DatagramPacket packet = new DatagramPacket(
                Unpooled.copiedBuffer(dat.getBytes()), iaddr,
                iaddr);

        // when
        expect(ctx.write(capture(capturedPacket))).andReturn(null);

        replayAll();
        handler.channelRead0(ctx , packet);

        // then
        verifyAll();

        ByteArrayOutputStream os = handler.extractBytes(capturedPacket
                .getValue().content());

        String result = new String(os.toByteArray());
        assertTrue(result.endsWith(":rd3:20414:Method Unknowne1:t2:aa1:y1:ee"));

        assertEquals("ZDI6aXA2OjJH1ov8ODE6cmQzOjIwNDE0Ok1ldGh"
                + "vZCBVbmtub3duZTE6dDI6YWExOnkxOmVl",
                Base64.encodeBase64String(os.toByteArray()));

        os.close();
    }

    /**
     * testChannelRead005() - test "garbage" request.
     * @throws Exception  Exception
     */
    @Test
    public void testChannelRead005() throws Exception {
        // given
        String dat = "adsadadsa";

        DatagramPacket packet = new DatagramPacket(
                Unpooled.copiedBuffer(dat.getBytes()), iaddr,
                iaddr);

        // when
        expect(ctx.write(capture(capturedPacket))).andReturn(null);

        replayAll();
        handler.channelRead0(ctx , packet);

        // then
        verifyAll();

        ByteArrayOutputStream os = handler.extractBytes(capturedPacket
                .getValue().content());

        String result = new String(os.toByteArray());
        assertTrue(result.startsWith("d1:rd3:20212:Server Errore1:y1:ee"));

        os.close();
    }

    /**
     * testChannelRead006() - test "get_peers" request and peers exists.
     * @throws Exception  Exception
     */
    @Test
    public void testChannelRead006() throws Exception {
        // given
        BigInteger nodeId = new BigInteger("mnopqrstuvwxyz123456".getBytes());
        String dat = "d1:ad2:id20:abcdefghij01234567899:info_hash20:"
                + "mnopqrstuvwxyz123456e1:q9:get_peers1:t2:aa1:y1:qe";

        DatagramPacket packet = new DatagramPacket(
                Unpooled.copiedBuffer(dat.getBytes()), iaddr,
                iaddr);

        DHTPeer peer = new DHTPeerBasic(InetAddress.getByName("240.120.222.12")
                .getAddress(), 23);
        Collection<DHTPeer> peers = Arrays.asList(peer);

        // when
        expect(peerRoutingTable.findPeers(nodeId)).andReturn(peers);
        expect(ctx.write(capture(capturedPacket))).andReturn(null);

        replayAll();
        handler.channelRead0(ctx , packet);

        // then
        verifyAll();

        ByteArrayOutputStream os = handler.extractBytes(capturedPacket
                .getValue().content());

        Map<String, Object> map = (Map<String, Object>) new BDecoder()
            .decode(os.toByteArray());

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

        os.close();
    }

    /**
     * testChannelRead007() - test "get_peers" request and InfoHash node is
     * null.
     *
     * @throws Exception Exception
     */
    @Test
    public void testChannelRead007() throws Exception {
        // given
        BigInteger nodeId = new BigInteger("mnopqrstuvwxyz123456".getBytes());
        String dat = "d1:ad2:id20:abcdefghij01234567899:info_hash20:"
                + "mnopqrstuvwxyz123456e1:q9:get_peers1:t2:aa1:y1:qe";

        DatagramPacket packet = new DatagramPacket(
                Unpooled.copiedBuffer(dat.getBytes()), iaddr,
                iaddr);

        // when
        expect(peerRoutingTable.findPeers(nodeId)).andReturn(null);
        expect(routingTable.findClosestNodes(nodeId)).andReturn(getFindNodes());
        expect(ctx.write(capture(capturedPacket))).andReturn(null);

        replayAll();
        handler.channelRead0(ctx , packet);

        // then
        verifyAll();

        ByteArrayOutputStream os = handler.extractBytes(capturedPacket
                .getValue().content());

        Map<String, Object> map = (Map<String, Object>) new BDecoder()
            .decode(os.toByteArray());

        assertEquals("aa", new String((byte[]) map.get("t")));
        assertEquals("r", new String((byte[]) map.get("y")));

        Map<String, Object> rmap = (Map<String, Object>) map.get("r");

        assertEquals(20, ((byte[]) rmap.get("id")).length);
        assertEquals(10, ((byte[]) rmap.get("token")).length);

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

        os.close();
    }

    /**
     * testChannelRead008() - "announce_peer" request and InfoHash is found.
     *
     * @throws Exception Exception
     */
    @Test
    public void testChannelRead008() throws Exception {
        // given
        byte[] secret = "aoeusnth".getBytes();
        BigInteger nodeId = new BigInteger("mnopqrstuvwxyz123456".getBytes());
        String dat = "d1:ad2:id20:abcdefghij01234567899:info_hash20:"
                + "mnopqrstuvwxyz1234564:porti6881e5:token8:aoeusnthe1:q13:"
                + "announce_peer1:t2:aa1:y1:qe";

        DatagramPacket packet = new DatagramPacket(
                Unpooled.copiedBuffer(dat.getBytes()), iaddr,
                iaddr);

        // when
        expect(tokenTable.valid(eq(iaddr), aryEq(secret))).andReturn(true);
        peerRoutingTable.addPeer(eq(nodeId), aryEq(iaddr.getAddress()
                .getAddress()), eq(6881));
        expect(ctx.write(capture(capturedPacket))).andReturn(null);

        replayAll();
        handler.channelRead0(ctx , packet);

        // then
        verifyAll();

        verifyAnnouncePeer();
    }

    /**
     * testChannelRead009() - "announce_peer" request, with implied_port of 0.
     *
     * @throws Exception Exception
     */
    @Test
    public void testChannelRead009() throws Exception {
        // given
        byte[] secret = "aoeusnth".getBytes();
        BigInteger nodeId = new BigInteger("mnopqrstuvwxyz123456".getBytes());
        String dat = "d1:ad2:id20:abcdefghij01234567899:info_hash20:"
                + "mnopqrstuvwxyz12345612:implied_porti0e4:porti6881e5:"
                + "token8:aoeusnthe1:q13:announce_peer1:t2:aa1:y1:qe";

        DatagramPacket packet = new DatagramPacket(
                Unpooled.copiedBuffer(dat.getBytes()), iaddr,
                iaddr);

        // when
        expect(tokenTable.valid(eq(iaddr), aryEq(secret))).andReturn(true);
        peerRoutingTable.addPeer(eq(nodeId), aryEq(iaddr.getAddress()
                .getAddress()), eq(6881));
        expect(ctx.write(capture(capturedPacket))).andReturn(null);

        replayAll();
        handler.channelRead0(ctx , packet);

        // then
        verifyAll();

        verifyAnnouncePeer();
    }

    /**
     * testChannelRead010() - "announce_peer" request, with implied_port of 1.
     *
     * @throws Exception Exception
     */
    @Test
    public void testChannelRead010() throws Exception {
        // given
        byte[] secret = "aoeusnth".getBytes();
        BigInteger nodeId = new BigInteger("mnopqrstuvwxyz123456".getBytes());
        String dat = "d1:ad2:id20:abcdefghij01234567899:info_hash20:"
                + "mnopqrstuvwxyz12345612:implied_porti1e4:porti6881e5:"
                + "token8:aoeusnthe1:q13:announce_peer1:t2:aa1:y1:qe";

        DatagramPacket packet = new DatagramPacket(
                Unpooled.copiedBuffer(dat.getBytes()), iaddr,
                iaddr);

        // when
        expect(tokenTable.valid(eq(iaddr), aryEq(secret))).andReturn(true);
        peerRoutingTable.addPeer(eq(nodeId), aryEq(iaddr.getAddress()
                .getAddress()), eq(iaddr.getPort()));
        expect(ctx.write(capture(capturedPacket))).andReturn(null);

        replayAll();
        handler.channelRead0(ctx , packet);

        // then
        verifyAll();

        verifyAnnouncePeer();
    }

    /**
     * testChannelRead011() - "announce_peer" request, with invalid token.
     *
     * @throws Exception Exception
     */
    @Test
    public void testChannelRead011() throws Exception {
        // given
        byte[] secret = "aoeusnth".getBytes();

        String dat = "d1:ad2:id20:abcdefghij01234567899:info_hash20:"
                + "mnopqrstuvwxyz12345612:implied_porti1e4:porti6881e5:"
                + "token8:aoeusnthe1:q13:announce_peer1:t2:aa1:y1:qe";

        DatagramPacket packet = new DatagramPacket(
                Unpooled.copiedBuffer(dat.getBytes()), iaddr,
                iaddr);

        // when
        expect(tokenTable.valid(eq(iaddr), aryEq(secret))).andReturn(false);
        expect(ctx.write(capture(capturedPacket))).andReturn(null);

        replayAll();
        handler.channelRead0(ctx , packet);

        // then
        verifyAll();

        ByteArrayOutputStream os = handler.extractBytes(capturedPacket
                .getValue().content());

        String result = new String(os.toByteArray());
        assertTrue(result.endsWith(":rd3:2039:Bad Tokene1:t2:aa1:y1:ee"));

        os.close();
    }

    /**
     * Verify AnnouncePeer response.
     * @throws IOException  IOException
     */
    private void verifyAnnouncePeer() throws IOException {
        ByteArrayOutputStream os = handler.extractBytes(capturedPacket
                .getValue().content());

        Map<String, Object> map = (Map<String, Object>) new BDecoder()
            .decode(os.toByteArray());

        assertEquals("aa", new String((byte[]) map.get("t")));
        assertEquals("r", new String((byte[]) map.get("y")));

        Map<String, Object> rmap = (Map<String, Object>) map.get("r");

        byte[] id = (byte[]) rmap.get("id");
        assertEquals(20, id.length);
        assertEquals("mnopqrstuvwxyz123456", new String(id));

        os.close();
    }

    /**
     * testChannelReadComplete01().
     * @throws Exception Exception
     */
    @Test
    public void testChannelReadComplete01() throws Exception {
        // given

        // when
        expect(ctx.flush()).andReturn(null);
        replayAll();

        handler.channelReadComplete(ctx);

        // then
        verifyAll();
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
     * Create DHTNode.
     * @param id  identifier
     * @param address  hostname
     * @param port  port
     * @return DHTNode
     * @throws UnknownHostException  UnknownHostException
     */
    private DHTNode createDHTNode(final String id, final String address,
            final int port) throws UnknownHostException {
        byte[] addr = InetAddress.getByName(address).getAddress();
        return DHTNodeFactory.create(new BigInteger(id), addr, port,
                DHTNode.State.UNKNOWN);
    }

    /**
     * Creates a find request.
     * @return Map<String, Object>
     */
    private Map<String, Object> createFindNodeRequestMap() {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("t", "aa");
        map.put("y", "q");
        map.put("q", "find_node");

        Map<String, Object> map2 = new HashMap<String, Object>();
        map2.put("id", DHTServer.NODE_ID);
        map2.put("target", new int[] {255, 255, 255, 255, 255, 255, 255, 255,
                255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255 });
        map.put("a", map2);

        return map;
    }

    /**
     * Create Find Node Request packet.
     * @return DatagramPacket
     * @throws IOException IOException
     */
    private DatagramPacket createFindNodeRequest() throws IOException {
        Map<String, Object> map = createFindNodeRequestMap();
        ByteArrayOutputStream bytes = BEncoder.bencoding(map);

        DatagramPacket packet = new DatagramPacket(
                Unpooled.copiedBuffer(bytes.toByteArray()), iaddr,
                iaddr);

        bytes.close();

        return packet;
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
}

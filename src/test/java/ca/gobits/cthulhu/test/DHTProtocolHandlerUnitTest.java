package ca.gobits.cthulhu.test;

import static org.easymock.EasyMock.capture;
import static org.easymock.EasyMock.expect;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.socket.DatagramPacket;

import java.io.ByteArrayOutputStream;
import java.math.BigInteger;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.Arrays;
import java.util.HashMap;
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

import ca.gobits.cthulhu.DHTNode;
import ca.gobits.cthulhu.DHTProtocolHandler;
import ca.gobits.cthulhu.DHTRoutingTable;
import ca.gobits.cthulhu.DHTServer;
import ca.gobits.dht.BDecoder;
import ca.gobits.dht.BEncoder;

/**
 * DHTProtocolHandler Unit Tests.
 */
@RunWith(EasyMockRunner.class)
public final class DHTProtocolHandlerUnitTest extends EasyMockSupport {

    /** DHTProtcolHandler. */
    @TestSubject
    private final DHTProtocolHandler handler = new DHTProtocolHandler();

    /** Reference to routing table. */
    private final DHTRoutingTable routingTable = (DHTRoutingTable)
            ReflectionTestUtils.getField(handler, "routingTable");

    /** Mock ChannelHandlerContext. */
    @Mock
    private ChannelHandlerContext ctx;

    /** before(). */
    @Before
    public void before() {
        addRandomNodesToRoutingTable();
        addExpectedNodesToRoutingTable();
    }

    /**
     * testChannelRead001() - compare our result from
     * router.bittorrent.com response.
     * @throws Exception  Exception
     */
    @SuppressWarnings("unchecked")
    @Test
    public void testChannelRead001() throws Exception {
        // given
        Capture<DatagramPacket> capturedPacket = new Capture<DatagramPacket>();
        Map<String, Object> map = createFindRequest();
        ByteArrayOutputStream bytes = BEncoder.bencoding(map);

        InetSocketAddress recipient = new InetSocketAddress(
                InetAddress.getByName("50.71.214.139"), 64568);
        DatagramPacket packet = new DatagramPacket(
                Unpooled.copiedBuffer(bytes.toByteArray()), recipient,
                recipient);

        // when
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

        bytes.close();
        os.close();
    }

    /**
     * Assert two "nodes" results are equal.
     * @param a  byte[]
     * @param b  byte[]
     */
    private void assertNodesEquals(final byte[] a, final byte[] b) {
        assertEquals(a.length, b.length);

        Map<Double, String> mapA = buildNodeMap(a);
        Map<Double, String> mapB = buildNodeMap(b);

        for (Map.Entry<Double, String> e : mapA.entrySet()) {
            assertTrue(mapB.containsKey(e.getKey()));
            assertTrue(mapB.containsValue(e.getValue()));
        }

    }

    /**
     * Build a Map from a node list.
     * @param a  byte[]
     * @return Map<byte[], String>
     */
    private Map<Double, String> buildNodeMap(final byte[] a) {
        Map<Double, String> map = new HashMap<Double, String>();

        int i = 0;
        while (i < a.length) {
            byte[] key = Arrays.copyOfRange(a, i, i + 20);
            i += 20;
            byte[] ipBytes = Arrays.copyOfRange(a, i, i + 6);
            String addr = BDecoder.decodeCompactIP(ipBytes);
            i += 6;
            double d = ca.gobits.dht.Arrays.toDouble(key);
            map.put(Double.valueOf(d), addr);
        }

        return map;
    }

    /**
     * Adds random nodes to the routing table.
     */
    private void addRandomNodesToRoutingTable() {
        routingTable.addNode(new DHTNode(new BigInteger("1"), "12.12.12.12",
                432));
    }

    /**
     * Adds expected nodes to the routing table.
     */
    private void addExpectedNodesToRoutingTable() {

        routingTable.addNode(new DHTNode(new BigInteger(
                "1025727453009050644114422909938179475956677673365"),
                "37.76.160.28", 37518));
        routingTable.addNode(new DHTNode(new BigInteger(
                "909396897490697132528408310795708133687135388426"),
                "182.59.176.199", 11503));
        routingTable.addNode(new DHTNode(new BigInteger(
                "525080541161122160152898021711579691652547262977"),
                "178.124.205.49", 16911));
        routingTable.addNode(new DHTNode(new BigInteger(
                "658070898018303575756492289276695009391046368980"),
                "5.13.218.214", 56116));
        routingTable.addNode(new DHTNode(new BigInteger(
                "732800403720670969048970409366815229228420735404"),
                "79.163.109.76", 29037));
        routingTable.addNode(new DHTNode(new BigInteger(
                "1256313872952230430598882201394466767467396215628"),
                "2.190.222.79", 58106));
        routingTable.addNode(new DHTNode(new BigInteger(
                "765028964801745612216665519019856689419949360586"),
                "92.237.93.69", 17271));
        routingTable.addNode(new DHTNode(new BigInteger(
                "304333486037502350876881646365121976203989590042"),
                "5.129.229.16", 21853));
        routingTable.addNode(new DHTNode(new BigInteger(
                "651043862618190073616414008555095633000553327254"),
                "67.166.50.31", 53162));
        routingTable.addNode(new DHTNode(new BigInteger(
                "217572328821850967755762913845138112465869557436"),
                "178.222.162.23", 18274));
        routingTable.addNode(new DHTNode(new BigInteger(
                "1235689258152504075304182876266224318368488950162"),
                "31.216.162.240", 20383));
        routingTable.addNode(new DHTNode(new BigInteger(
                "487762934236616301113020799412763967579181340675"),
                "31.181.56.194", 59935));
        routingTable.addNode(new DHTNode(new BigInteger(
                "757633304364519595494275276101980823332425611532"),
                "80.233.181.214", 12230));
        routingTable.addNode(new DHTNode(new BigInteger(
                "253718933283387888344146948372599275024431560999"),
                "79.22.67.76", 38518));
        routingTable.addNode(new DHTNode(new BigInteger(
                "890765994839177116145299793227790251293353534962"),
                "92.99.87.123", 26120));
        routingTable.addNode(new DHTNode(new BigInteger(
                "1123918148366576699094456176144333565208604527946"),
                "176.12.59.50", 61553));
    }

    /**
     * Creates a find request.
     * @return Map<String, Object>
     */
    private Map<String, Object> createFindRequest() {
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

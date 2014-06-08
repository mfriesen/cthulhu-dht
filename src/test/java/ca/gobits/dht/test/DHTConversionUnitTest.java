package ca.gobits.dht.test;

import static ca.gobits.dht.DHTConversion.COMPACT_ADDR_LENGTH;
import static ca.gobits.dht.DHTConversion.NODE_ID_LENGTH;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;
import java.math.BigInteger;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.junit.Test;

import ca.gobits.cthulhu.domain.DHTNode;
import ca.gobits.cthulhu.domain.DHTNodeFactory;
import ca.gobits.cthulhu.domain.DHTPeer;
import ca.gobits.cthulhu.domain.DHTPeerBasic;
import ca.gobits.dht.DHTConversion;

/**
 * DHT DataConversion Unit Tests.
 *
 */
public final class DHTConversionUnitTest {

    /**
     * testConstructorIsPrivate().
     *
     * @throws Exception  Exception
     */
    @Test
    public void testConstructorIsPrivate() throws Exception {
        Constructor<DHTConversion> constructor = DHTConversion.class
                .getDeclaredConstructor();
        assertTrue(Modifier.isPrivate(constructor.getModifiers()));
        constructor.setAccessible(true);
        constructor.newInstance();
    }

    /**
     * Test converting a IPv4 address to long.
     * @throws Exception  Exception
     */
    @Test
    public void testToLongArray01() throws Exception {

        // given
        InetAddress addr = InetAddress.getByName("255.255.255.255");

        // when
        long[] result = DHTConversion.toLongArray(addr.getAddress());

        // then
        assertEquals(1, result.length);
        assertEquals(4294967295L, result[0]);
    }

    /**
     * Test converting a IPv6 address to long.
     * @throws Exception  Exception
     */
    @Test
    public void testToLongArray02() throws Exception {

        // given
        InetAddress addr = Inet6Address
                .getByName("805b:2d9d:dc28:0000:0000:fc57:d4c8:1fff");

        // when
        long[] result = DHTConversion.toLongArray(addr.getAddress());

        // then
        assertEquals(4, result.length);
        assertEquals(2153459101L, result[0]);
        assertEquals(3693608960L, result[1]);
        assertEquals(64599L, result[2]);
        assertEquals(3569885183L, result[3]);
    }

    /**
     * Test converting MAX IPv6 address to long.
     * @throws Exception  Exception
     */
    @Test
    public void testToLongArray03() throws Exception {

        // given
        InetAddress addr = Inet6Address
                .getByName("ffff:ffff:ffff:ffff:ffff:ffff:ffff:ffff");

        // when
        long[] result = DHTConversion.toLongArray(addr.getAddress());

        // then
        assertEquals(4, result.length);
        assertEquals(4294967295L, result[0]);
        assertEquals(4294967295L, result[1]);
        assertEquals(4294967295L, result[2]);
        assertEquals(4294967295L, result[3]);
    }

    /**
     * Test converting long back to byte array.
     */
    @Test
    public void testToByteArray01() {
        // given
        long l = 4294967295L;

        // when
        byte[] result = DHTConversion.toByteArray(l);

        // then
        assertEquals(4, result.length);
        assertEquals(-1, result[0]);
        assertEquals(-1, result[1]);
        assertEquals(-1, result[2]);
        assertEquals(-1, result[3]);
    }

    /**
     * Test converting long back to byte array.
     */
    @Test
    public void testToByteArray02() {
        // given
        long[] l = new long[] {4294967295L, 4294967295L,
                4294967295L, 4294967295L };

        // when
        byte[] result = DHTConversion.toByteArray(l);

        // then
        assertEquals(16, result.length);
        assertEquals(-1, result[0]);
        assertEquals(-1, result[1]);
        assertEquals(-1, result[2]);
        assertEquals(-1, result[3]);
        assertEquals(-1, result[4]);
        assertEquals(-1, result[5]);
        assertEquals(-1, result[6]);
        assertEquals(-1, result[7]);
        assertEquals(-1, result[8]);
        assertEquals(-1, result[9]);
        assertEquals(-1, result[10]);
        assertEquals(-1, result[11]);
        assertEquals(-1, result[12]);
        assertEquals(-1, result[13]);
        assertEquals(-1, result[14]);
        assertEquals(-1, result[15]);
    }

    /**
     * Test IPv6 address.
     * @throws Exception  Exception
     */
    @Test
    public void testToInetAddress01() throws Exception {
        // given
        long[] l = new long[] {4294967295L, 4294967295L,
                4294967295L, 4294967295L };

        // when
        InetAddress result = DHTConversion.toInetAddress(l);

        // then
        assertEquals("ffff:ffff:ffff:ffff:ffff:ffff:ffff:ffff",
                result.getHostAddress());
    }

    /**
     * Test IPv6 address.
     * @throws Exception  Exception
     */
    @Test
    public void testToInetAddress02() throws Exception {
        // given
        long[] l = new long[] {2153459101L, 3693608960L,
                64599L, 3569885183L };

        // when
        InetAddress result = DHTConversion.toInetAddress(l);

        // then
        assertEquals("805b:2d9d:dc28:0:0:fc57:d4c8:1fff",
                result.getHostAddress());
    }

    /**
     * Test IPv4 address.
     * @throws Exception  Exception
     */
    @Test
    public void testToInetAddress03() throws Exception {
        // given
        long[] l = new long[] {4294967295L };

        // when
        InetAddress result = DHTConversion.toInetAddress(l);

        // then
        assertEquals("255.255.255.255",
                result.getHostAddress());
    }

    /**
     * Test IPv4 address.
     * @throws Exception  Exception
     */
    @Test
    public void testToInetAddress04() throws Exception {
        // given
        long[] l = new long[] {2294367295L };

        // when
        InetAddress result = DHTConversion.toInetAddress(l);

        // then
        assertEquals("136.193.68.63",
                result.getHostAddress());
    }

    /**
     * Test IPv4 address.
     * @throws Exception  Exception
     */
    @Test
    public void testToInetAddressString01() throws Exception {
        // given
        long[] l = new long[] {2294367295L };

        // when
        String result = DHTConversion.toInetAddressString(l);

        // then
        assertEquals("136.193.68.63", result);
    }

    /**
     * Test IPv6 address.
     * @throws Exception  Exception
     */
    @Test
    public void testToInetAddressString02() throws Exception {
        // given
        long[] l = new long[] {2153459101L, 3693608960L,
                64599L, 3569885183L };

        // when
        String result = DHTConversion.toInetAddressString(l);

        // then
        assertEquals("805b:2d9d:dc28:0:0:fc57:d4c8:1fff", result);
    }

    /**
     * Test UnknownHostException.
     * @throws Exception  Exception
     */
    @Test
    public void testToInetAddressString03() throws Exception {
        // given
        long[] l = new long[] {};

        // when
        String result = DHTConversion.toInetAddressString(l);

        // then
        assertEquals("unknown", result);
    }

    /**
     * testToByteArrayFromDHTNode01() - IPV4.
     *
     * @throws Exception  Exception
     */
    @Test
    public void testToByteArrayFromDHTNode01() throws Exception {
        // given
        byte[] addr0 = new byte[] {73, 54, 93, 12 };
        BigInteger bi0 = new BigInteger(
                "FFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFF", 16);
        DHTNode n0 = DHTNodeFactory.create(bi0, addr0, 123,
                DHTNode.State.UNKNOWN);

        byte[] addr1 = new byte[] {34, 64, 43, 51 };
        DHTNode n1 = DHTNodeFactory.create(new BigInteger("13242"), addr1, 8080,
                DHTNode.State.UNKNOWN);

        List<DHTNode> nodes = java.util.Arrays.asList(n0, n1);

        // when
        byte[] result = DHTConversion.toByteArrayFromDHTNode(nodes);

        // then
        assertEquals(52, result.length);
        byte[] id = new byte[NODE_ID_LENGTH];
        byte[] addr = new byte[COMPACT_ADDR_LENGTH];
        System.arraycopy(result, 0, id, 0, 20);
        System.arraycopy(result, 20, addr, 0, 6);

        assertEquals("1461501637330902918203684832716283019655932542975",
                DHTConversion.toBigInteger(id).toString());
        assertEquals("73.54.93.12:123",
                DHTConversion.decodeCompactAddressToString(addr));

        System.arraycopy(result, 26, id, 0, 20);
        System.arraycopy(result, 46, addr, 0, 6);

        assertEquals("13242", new BigInteger(id).toString());
        assertEquals("34.64.43.51:8080",
                DHTConversion.decodeCompactAddressToString(addr));
    }

    /**
     * toByteArrayFromDHTPeer01() - IPV4.
     *
     * @throws Exception  Exception
     */
    @Test
    public void testToByteArrayFromDHTPeer01() throws Exception {
        // given
        byte[] addr0 = new byte[] {73, 54, 93, 12 };
        byte[] addr1 = new byte[] {34, 64, 43, 51 };

        DHTPeer n0 = new DHTPeerBasic(addr0, 123);
        DHTPeer n1 = new DHTPeerBasic(addr1, 8080);

        List<DHTPeer> peers = java.util.Arrays.asList(n0, n1);

        // when
        List<byte[]> result = DHTConversion.toByteArrayFromDHTPeer(peers);

        // then
        assertEquals(2, result.size());
        byte[] addr = result.get(0);

        assertEquals("73.54.93.12:123",
                DHTConversion.decodeCompactAddressToString(addr));

        addr = result.get(1);

        assertEquals("34.64.43.51:8080",
                DHTConversion.decodeCompactAddressToString(addr));
    }

    /**
     * testToBigInteger01() - convert unsigned bytes[] to BigInteger.
     */
    @Test
    public void testToBigInteger01() {
        // given
        byte[] bytes = new byte[] {-24, -121 };

        // when
        BigInteger result = DHTConversion.toBigInteger(bytes);

        // then
        assertEquals(59527, result.intValue());
    }

    /**
     * testToBigInteger02() - convert byte[] to BigInteger.
     */
    @Test
    public void testToBigInteger02() {
        // given
        byte[] bytes = new byte[] {-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
                -1, -1, -1, -1, -1, -1, -1, -1, -1 };

        // when
        BigInteger result = DHTConversion.toBigInteger(bytes);

        // then
        assertEquals(Math.pow(2, 160), result.doubleValue(), 0);
    }

    /**
     * testToBigInteger03() - convert byte[] to BigInteger.
     */
    @Test
    public void testToBigInteger03() {
        // given
        byte[] bytes = new byte[] {87, 30, -96, -117, 67, 17, -88, -32, -84,
                79, 84, 64, 4, 84, 37, 77, 107, 123, 28, -3 };

        // when
        BigInteger result = DHTConversion.toBigInteger(bytes);

        // then
        assertEquals("497365204771778010961873067068386536698253352189",
                result.toString());
    }

    /**
     * testToDHTNode01() - transform bytes to DHTNode.
     */
    @Test
    public void testToDHTNode01() {
        // given
        byte[] bytes = new byte[] {-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
                -1, -1, -1, -1, -1, -1, -1, -1, -1, 73, 54, 93, 12, 0, 123, 0,
                0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 51, -70, 34,
                64, 43, 51, 31, -112 };

        // when
        Collection<DHTNode> results = DHTConversion.toDHTNode(bytes);

        // then
        assertEquals(2, results.size());
        Iterator<DHTNode> itr = results.iterator();

        DHTNode node0 = itr.next();
        assertEquals("1461501637330902918203684832716283019655932542975",
                node0.getInfoHash().toString());

        assertEquals("73.54.93.12",
                DHTConversion.toInetAddressString(node0.getAddress()));
        assertEquals(123, node0.getPort());

        DHTNode node1 = itr.next();
        assertEquals("13242",
                node1.getInfoHash().toString());

        assertEquals("34.64.43.51",
                DHTConversion.toInetAddressString(node1.getAddress()));
        assertEquals(8080, node1.getPort());
    }

    /**
     * testToDHTNode02() - incorrect length of byte array.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testToDHTNode02() {
        // given
        byte[] bytes = new byte[] {12, 3, 2, 1 };

        // when
        DHTConversion.toDHTNode(bytes);

        // then
    }

    /**
     * testTransformToUnsignedBytes01() - test 2^160.
     */
    @Test
    public void testTransformToUnsignedBytes01() {
        // given
        byte[] bytes = new BigInteger(
            "1461501637330902918203684832716283019655932542975")
            .toByteArray();

        // when
        int[] results = DHTConversion.transformToUnsignedBytes(bytes);

        // then
        assertEquals(20, results.length);
        assertArrayEquals(new int[] {255, 255, 255, 255, 255, 255, 255, 255,
                255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255 },
                results);

        byte[] resultBytes = DHTConversion.transformFromUnsignedBytes(results);
        assertArrayEquals(resultBytes, bytes);
    }

    /**
     * testTransformToUnsignedBytes02() - test 2^160 / 2.
     */
    @Test
    public void testTransformToUnsignedBytes02() {
        // given
        byte[] bytes = new BigInteger(
            "730750818665451459101842416358141509827966271487")
            .toByteArray();

        // when
        int[] results = DHTConversion.transformToUnsignedBytes(bytes);

        // then
        assertEquals(20, results.length);
        assertArrayEquals(new int[] {127, 255, 255, 255, 255, 255, 255, 255,
                255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255 },
                results);

        byte[] resultBytes = DHTConversion.transformFromUnsignedBytes(results);
        assertArrayEquals(resultBytes, bytes);
    }

    /**
     * testTransformToUnsignedBytes02() - test 2^160 / 2 - 1.
     */
    @Test
    public void testTransformToUnsignedBytes03() {
        // given
        byte[] bytes = new BigInteger(
            "730750818665451459101842416358141509827966271488")
            .toByteArray();

        // when
        int[] results = DHTConversion.transformToUnsignedBytes(bytes);

        // then
        assertEquals(20, results.length);
        assertArrayEquals(new int[] {128, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                0, 0, 0, 0, 0, 0, 0 }, results);

        byte[] resultBytes = DHTConversion.transformFromUnsignedBytes(results);
        assertArrayEquals(resultBytes, bytes);
    }
}

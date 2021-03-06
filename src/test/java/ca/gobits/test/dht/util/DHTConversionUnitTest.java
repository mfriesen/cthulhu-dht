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

package ca.gobits.test.dht.util;

import static ca.gobits.dht.DHTIdentifier.NODE_ID_LENGTH;
import static ca.gobits.dht.util.DHTConversion.toByteArrayFromDHTNode;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;
import java.math.BigInteger;
import java.net.InetAddress;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.junit.Test;

import ca.gobits.dht.DHTIdentifier;
import ca.gobits.dht.DHTNode;
import ca.gobits.dht.DHTNode.State;
import ca.gobits.dht.DHTPeer;
import ca.gobits.dht.factory.DHTNodeFactory;
import ca.gobits.dht.util.DHTConversion;

import com.google.common.primitives.UnsignedLong;

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
    public void testToUnsignedLong01() throws Exception {

        // given
        InetAddress addr = InetAddress.getByName("255.255.255.255");

        // when
        UnsignedLong[] result = DHTConversion.toUnsignedLong(addr.getAddress());

        // then
        assertEquals(1, result.length);
        assertEquals("4294967295", result[0].toString());
        assertArrayEquals(addr.getAddress(),
                DHTConversion.toInetAddress(result[0], null).getAddress());
    }

    /**
     * Test converting a IPv6 address to long.
     * @throws Exception  Exception
     */
    @Test
    public void testToUnsignedLong02() throws Exception {

        // given
        InetAddress addr = InetAddress
                .getByName("805b:2d9d:dc28:0000:0000:fc57:d4c8:1fff");

        // when
        UnsignedLong[] result = DHTConversion.toUnsignedLong(addr.getAddress());

        // then
        assertEquals(2, result.length);
        assertEquals("9249036415762169856", result[0].toString());
        assertEquals("277454162239487", result[1].toString());
        assertArrayEquals(addr.getAddress(),
                DHTConversion.toInetAddress(result[0], result[1]).getAddress());
    }

    /**
     * Test converting MAX IPv6 address to long.
     * @throws Exception  Exception
     */
    @Test
    public void testToLongArray03() throws Exception {

        // given
        InetAddress addr = InetAddress
                .getByName("ffff:ffff:ffff:ffff:ffff:ffff:ffff:ffff");

        // when
        UnsignedLong[] result = DHTConversion.toUnsignedLong(addr.getAddress());

        // then
        assertEquals(2, result.length);
        assertEquals("18446744073709551615", result[0].toString());
        assertEquals("18446744073709551615", result[1].toString());
        assertArrayEquals(addr.getAddress(),
                DHTConversion.toInetAddress(result[0], result[1]).getAddress());
    }

    /**
     * testToByteArrayFromDHTNode01() - IPV4.
     *
     * @throws Exception  Exception
     */
    @Test
    public void testToByteArrayFromDHTNode01() throws Exception {
        // given
        boolean isIPv6 = false;
        InetAddress addr0 = InetAddress.getByAddress(new byte[] {73, 54, 93,
                12 });

        BigInteger bi0 = new BigInteger(
                "FFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFF", 16);
        DHTNode n0 = DHTNodeFactory.create(bi0.toByteArray(), addr0, 123,
                DHTNode.State.UNKNOWN);

        InetAddress addr1 = InetAddress.getByAddress(new byte[] {34, 64, 43,
                51 });

        DHTNode n1 = DHTNodeFactory.create(
                new BigInteger("13242").toByteArray(), addr1, 8080,
                DHTNode.State.UNKNOWN);

        List<DHTNode> nodes = java.util.Arrays.asList(n0, n1);

        // when
        byte[] result = DHTConversion.toByteArrayFromDHTNode(nodes, isIPv6);

        // then
        assertEquals(52, result.length);

        assertArrayEquals(new byte[] {-1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
                -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 73, 54, 93, 12, 0, 123,
                0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 51, -70,
                34, 64, 43, 51, 31, -112}, result);

        byte[] id = new byte[NODE_ID_LENGTH];
        byte[] addr = new byte[6];
        System.arraycopy(result, 0, id, 0, 20);
        System.arraycopy(result, 20, addr, 0, 6);

        assertEquals("1461501637330902918203684832716283019655932542975",
                DHTConversion.toBigInteger(id).toString());
        assertEquals("73.54.93.12",
                DHTConversion.compactAddress(addr).getHostAddress());
        assertEquals(123,
                DHTConversion.compactAddressPort(addr));

        System.arraycopy(result, 26, id, 0, 20);
        System.arraycopy(result, 46, addr, 0, 6);

        assertEquals("13242", new BigInteger(id).toString());
        assertEquals("34.64.43.51",
                DHTConversion.compactAddress(addr).getHostAddress());
        assertEquals(8080,
                DHTConversion.compactAddressPort(addr));
    }

    /**
     * testToByteArrayFromDHTNode02() - IPV6.
     *
     * @throws Exception  Exception
     */
    @Test
    public void testToByteArrayFromDHTNode02() throws Exception {
        // given
        boolean isIPv6 = true;

        byte[] id0 = DHTIdentifier.sha1("node0".getBytes());
        int port0 = 123;
        State state0 = State.UNKNOWN;
        InetAddress addr0 = InetAddress
                .getByName("ffff:ffff:ffff:ffff:ffff:ffff:ffff:ffff");

        byte[] id1 = DHTIdentifier.sha1("node1".getBytes());
        int port1 = 124;
        State state1 = State.UNKNOWN;
        InetAddress addr1 = InetAddress
                .getByName("805b:2d9d:dc28:0000:0000:fc57:d4c8:1fff");

        DHTNode n0 = DHTNodeFactory.create(id0, addr0, port0, state0);
        DHTNode n1 = DHTNodeFactory.create(id1, addr1, port1, state1);

        // when
        byte[] result = toByteArrayFromDHTNode(Arrays.asList(n0, n1), isIPv6);

        // then
        assertEquals(76, result.length);
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

        DHTPeer n0 = new DHTPeer(addr0, 123);
        DHTPeer n1 = new DHTPeer(addr1, 8080);

        List<DHTPeer> peers = java.util.Arrays.asList(n0, n1);

        // when
        List<byte[]> result = DHTConversion.toByteArrayFromDHTPeer(peers);

        // then
        assertEquals(2, result.size());
        byte[] addr = result.get(0);

        assertEquals("73.54.93.12",
                DHTConversion.compactAddress(addr).getHostAddress());
        assertEquals(123,
                DHTConversion.compactAddressPort(addr));

        addr = result.get(1);

        assertEquals("34.64.43.51",
                DHTConversion.compactAddress(addr).getHostAddress());
        assertEquals(8080,
                DHTConversion.compactAddressPort(addr));
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
     * @throws Exception   Exception
     */
    @Test
    public void testToDHTNode01() throws Exception {
        // given
        boolean isIPV6 = false;
        byte[] bytes = new byte[] {-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
                -1, -1, -1, -1, -1, -1, -1, -1, -1, 73, 54, 93, 12, 0, 123, 0,
                0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 51, -70, 34,
                64, 43, 51, 31, -112 };

        // when
        Collection<DHTNode> results = DHTConversion.toDHTNode(bytes, isIPV6);

        // then
        assertEquals(2, results.size());
        Iterator<DHTNode> itr = results.iterator();

        DHTNode node0 = itr.next();
        BigInteger b0 = DHTConversion.toBigInteger(node0.getInfoHash());
        assertEquals("1461501637330902918203684832716283019655932542975",
                b0.toString());

        assertEquals("73.54.93.12", node0.getAddress().getHostAddress());
        assertEquals(123, node0.getPort());

        DHTNode node1 = itr.next();
        BigInteger b1 = DHTConversion.toBigInteger(node1.getInfoHash());
        assertEquals("13242", b1.toString());

        assertEquals("34.64.43.51", node1.getAddress().getHostAddress());
        assertEquals(8080, node1.getPort());
    }

    /**
     * testToDHTNode02() - IPv6 transform bytes to DHTNode.
     * @throws Exception   Exception
     */
    @Test
    public void testToDHTNode02() throws Exception {
        // given
        boolean isIPV6 = true;

        byte[] bytes = new byte[] {-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
                -1, -1, -1, -1, -1, -1, -1, -1, -1, -128, 91, 45, -99, -36, 40,
                0, 0, 0, 0, -4, 87, -44, -56, 31, -1, 0, 123, 0, 0, 0, 0, 0, 0,
                0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 51, -70, -1, -1, -1, -1,
                -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 31, -112 };

        // when
        Collection<DHTNode> results = DHTConversion.toDHTNode(bytes, isIPV6);

        // then
        assertEquals(2, results.size());

        Iterator<DHTNode> itr = results.iterator();

        DHTNode node0 = itr.next();
        BigInteger b0 = DHTConversion.toBigInteger(node0.getInfoHash());
        assertEquals("1461501637330902918203684832716283019655932542975",
                b0.toString());

        assertEquals("805b:2d9d:dc28:0:0:fc57:d4c8:1fff",
                node0.getAddress().getHostAddress());
        assertEquals(123, node0.getPort());

        DHTNode node1 = itr.next();
        BigInteger b1 = DHTConversion.toBigInteger(node1.getInfoHash());
        assertEquals("13242", b1.toString());

        assertEquals("ffff:ffff:ffff:ffff:ffff:ffff:ffff:ffff",
                node1.getAddress().getHostAddress());
        assertEquals(8080, node1.getPort());

    }

    /**
     * testToDHTNode03() - incorrect length of byte array.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testToDHTNode03() {
        // given
        byte[] bytes = new byte[] {12, 3, 2, 1 };

        // when
        DHTConversion.toDHTNode(bytes, false);

        // then
    }

    /**
     * testDecodeCompactIP01() - decodes IPv4 compact IP.
     * @throws Exception   Exception
     */
    @Test
    public void testDecodeCompactIP01() throws Exception {
        // given
        byte[] bytes = new byte[]{37, 76, -96, 28, -110, -114};

        // when
        String result = DHTConversion.compactAddress(bytes).getHostAddress();
        int port = DHTConversion.compactAddressPort(bytes);

        // then
        assertEquals("37.76.160.28", result);
        assertEquals(37518, port);
    }

    /**
     * testDecodeCompactIP02() - decodes IPv6 compact IP.
     * @throws Exception   Exception
     */
    @Test
    public void testDecodeCompactIP02() throws Exception {
        // given
        InetAddress addr = InetAddress
                .getByName("805b:2d9d:dc28:0000:0000:fc57:d4c8:1fff");
        byte[] bytes = DHTConversion.compactAddress(addr.getAddress(), 37518);

        // when
        String result = DHTConversion.compactAddress(bytes).getHostAddress();
        int port = DHTConversion.compactAddressPort(bytes);

        // then
        assertEquals("805b:2d9d:dc28:0:0:fc57:d4c8:1fff", result);
        assertEquals(37518, port);
    }

    /**
     * testCompactAddress01() convert IPv4 to compact mode and
     * back again.
     * @throws Exception   Exception
     */
    @Test
    public void testCompactAddress01() throws Exception {
        // given
        int port = 4832;
        InetAddress addr = InetAddress.getByName("54.242.12.2");

        // when
        byte[] compact = DHTConversion.compactAddress(addr.getAddress(), port);
        InetAddress result = DHTConversion.compactAddress(compact);

        // then
        assertEquals(6, compact.length);
        assertEquals(addr.getHostAddress(), result.getHostAddress());
        assertEquals(port, DHTConversion.compactAddressPort(compact));
    }

    /**
     * testCompactAddress02() convert IPv6 to compact mode and
     * back again.
     * @throws Exception   Exception
     */
    @Test
    public void testCompactAddress02() throws Exception {
        // given
        int port = 4832;
        InetAddress addr = InetAddress
                .getByName("805b:2d9d:dc28:0000:0000:fc57:d4c8:1fff");

        // when
        byte[] compact = DHTConversion.compactAddress(addr.getAddress(), port);
        InetAddress result = DHTConversion.compactAddress(compact);

        // then
        assertEquals(18, compact.length);
        assertEquals(addr.getHostAddress(), result.getHostAddress());
        assertEquals(port, DHTConversion.compactAddressPort(compact));
    }

    /**
     * testCompactAddress03()  UnknownHost
     * back again.
     * @throws Exception   Exception
     */
    @Test
    public void testCompactAddress03() throws Exception {
        // given
        byte[] bytes = new byte[] {12, 3, 4};

        // when
        InetAddress result = DHTConversion.compactAddress(bytes);

        // then
        assertNull(result);
    }

    /**
     * testFitToSize01() - bytes.length == length.
     */
    @Test
    public void testFitToSize01() {
        // given
        byte[] bytes = new byte[] {34, 54, 24, 12, 43, 65};
        int len = bytes.length;

        // when
        byte[] results = DHTConversion.fitToSize(bytes, len);

        // then
        assertArrayEquals(bytes, results);
    }

    /**
     * testFitToSize02() - bytes.length > length.
     */
    @Test
    public void testFitToSize02() {
        // given
        byte[] bytes = new byte[] {34, 54, 24, 12, 43, 65};
        int len = 3;

        // when
        byte[] results = DHTConversion.fitToSize(bytes, len);

        // then
        assertArrayEquals(new byte[] {12, 43, 65}, results);
    }

    /**
     * testFitToSize03() - bytes.length < length.
     */
    @Test
    public void testFitToSize03() {
        // given
        byte[] bytes = new byte[] {34, 54, 24, 12, 43, 65};
        int len = 10;

        // when
        byte[] results = DHTConversion.fitToSize(bytes, len);

        // then
        assertArrayEquals(new byte[] {0, 0, 0, 0, 34, 54, 24, 12, 43, 65},
                results);
    }

    /**
     * testToInetAddressAsString01().
     * @throws Exception   Exception
     */
    @Test
    public void testToInetAddressAsString01() throws Exception {
        // given
        InetAddress addr = InetAddress.getByName("23.54.14.2");
        UnsignedLong[] uls = DHTConversion.toUnsignedLong(addr.getAddress());

        // when
        String result = DHTConversion.toInetAddressAsString(uls[0], null);

        // then
        assertEquals("23.54.14.2", result);
    }
}

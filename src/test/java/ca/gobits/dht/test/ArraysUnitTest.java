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

package ca.gobits.dht.test;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;
import java.math.BigInteger;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.junit.Test;

import ca.gobits.cthulhu.domain.DHTNode;
import ca.gobits.dht.Arrays;
import ca.gobits.dht.BDecoder;

/**
 * Arrays Unit Tests.
 */
public final class ArraysUnitTest {

    /**
     * testConstructorIsPrivate().
     *
     * @throws Exception
     *             Exception
     */
    @Test
    public void testConstructorIsPrivate() throws Exception {
        Constructor<Arrays> constructor = Arrays.class.getDeclaredConstructor();
        assertTrue(Modifier.isPrivate(constructor.getModifiers()));
        constructor.setAccessible(true);
        constructor.newInstance();
    }

    /**
     * testToBigInteger01() - convert unsigned bytes[] to BigInteger.
     */
    @Test
    public void testToBigInteger01() {
        // given
        byte[] bytes = new byte[] {-24, -121 };

        // when
        BigInteger result = Arrays.toBigInteger(bytes);

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
        BigInteger result = Arrays.toBigInteger(bytes);

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
        BigInteger result = Arrays.toBigInteger(bytes);

        // then
        assertEquals("497365204771778010961873067068386536698253352189",
                result.toString());
    }

    /**
     * testToLong01().
     */
    @Test
    public void testToLong01() {
        // given
        byte[] bytes = new byte[] {127, 0, 123, 43, 12, 32 };

        // when
        long result = Arrays.toLong(bytes);

        // then
        assertEquals(139640043146272L, result);
    }

    /**
     * testToLong02().
     */
    @Test
    public void testToLong02() {
        // given
        byte[] bytes = new byte[] {-1, -1, -1, -1, -1, -1 };

        // when
        long result = Arrays.toLong(bytes);

        // then
        assertEquals(281474976710655L, result);
    }

    /**
     * testToByteArray01().
     */
    @Test
    public void testToByteArray01() {
        // given
        long l = 139640043146272L;

        // when
        byte[] results = Arrays.toByteArray(l);

        // then
        assertEquals("[127, 0, 123, 43, 12, 32]",
                java.util.Arrays.toString(results));
    }

    /**
     * testToByteArray02().
     */
    @Test
    public void testToByteArray02() {
        // given
        long l = 281474976710655L;

        // when
        byte[] results = Arrays.toByteArray(l);

        // then
        assertEquals("[-1, -1, -1, -1, -1, -1]",
                java.util.Arrays.toString(results));
    }

    /**
     * testToByteArray03().
     */
    @Test
    public void testToByteArray03() {
        // given
        long l =  9223372036854775807L;

        // when
        byte[] results = Arrays.toByteArray(l);

        // then
        assertEquals("[127, -1, -1, -1, -1, -1, -1, -1]",
                java.util.Arrays.toString(results));
    }

    /**
     * testToByteArray04().
     */
    @Test
    public void testToByteArray04() {
        // given
        long l =  0;

        // when
        byte[] results = Arrays.toByteArray(l);

        // then
        assertEquals("[]", java.util.Arrays.toString(results));
    }

    /**
     * testToByteArrayFromBigInteger01() - convert BigInteger to byte[].
     */
    @Test
    public void testToByteArrayFromBigInteger01() {
        // given
        BigInteger bi = new BigInteger(
                "497365204771778010961873067068386536698253352189");

        // when
        byte[] results = Arrays.toByteArray(bi);

        // then
        byte[] bytes = new byte[] {87, 30, -96, -117, 67, 17, -88, -32, -84,
                79, 84, 64, 4, 84, 37, 77, 107, 123, 28, -3 };

        assertArrayEquals(bytes, results);
    }

    /**
     * testToByteArrayFromDHTNode01().
     *
     * @throws Exception
     *             Exception
     */
    @Test
    public void testToByteArrayFromDHTNode01() throws Exception {
        // given
        byte[] addr0 = new byte[] {73, 54, 93, 12 };
        BigInteger bi0 = new BigInteger(
                "FFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFF", 16);
        DHTNode n0 = new DHTNode(bi0, addr0, 123);
        byte[] addr1 = new byte[] {34, 64, 43, 51 };
        DHTNode n1 = new DHTNode(new BigInteger("13242"), addr1, 8080);

        List<DHTNode> nodes = java.util.Arrays.asList(n0, n1);

        // when
        byte[] result = Arrays.toByteArray(nodes);

        // then
        assertEquals(52, result.length);
        byte[] id = new byte[Arrays.NODE_ID_LENGTH];
        byte[] addr = new byte[Arrays.COMPACT_ADDR_LENGTH];
        System.arraycopy(result, 0, id, 0, 20);
        System.arraycopy(result, 20, addr, 0, 6);

        assertEquals("1461501637330902918203684832716283019655932542975",
                Arrays.toBigInteger(id).toString());
        assertEquals("73.54.93.12:123",
                BDecoder.decodeCompactAddressToString(addr));

        System.arraycopy(result, 26, id, 0, 20);
        System.arraycopy(result, 46, addr, 0, 6);

        assertEquals("13242", new BigInteger(id).toString());
        assertEquals("34.64.43.51:8080",
                BDecoder.decodeCompactAddressToString(addr));
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
        Collection<DHTNode> results = Arrays.toDHTNode(bytes);

        // then
        assertEquals(2, results.size());
        Iterator<DHTNode> itr = results.iterator();

        DHTNode node0 = itr.next();
        assertEquals("1461501637330902918203684832716283019655932542975",
                node0.getId().toString());

        byte[] addr0 = Arrays.toByteArray(node0.getAddress());

        assertEquals("73.54.93.12:123",
                BDecoder.decodeCompactAddressToString(addr0));

        DHTNode node1 = itr.next();
        assertEquals("13242",
                node1.getId().toString());

        byte[] addr1 = Arrays.toByteArray(node1.getAddress());

        assertEquals("34.64.43.51:8080",
                BDecoder.decodeCompactAddressToString(addr1));
    }

    /**
     * testToDHTNode02() - incorrect length of byte array.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testToDHTNode02() {
        // given
        byte[] bytes = new byte[] {12, 3, 2, 1 };

        // when
        Arrays.toDHTNode(bytes);

        // then
    }
}

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

package ca.gobits.test.dht.bencoding;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import org.junit.Test;

import ca.gobits.dht.bencoding.BEncoder;
import ca.gobits.dht.util.DHTConversion;

/**
 * Unit Test cases for BEncoder.
 */
public final class BEncoderUnitTest {

    /**
     * testBencoding01() test encoding a map.
     * @throws Exception   Exception
     */
    @Test
    public void testBencoding01() throws Exception {
        // given
        Map<Object, Object> map = new HashMap<Object, Object>();
        map.put("t", "aa");
        map.put("y", "q");
        map.put("q", "ping");
        Map<Object, Object> map2 = new HashMap<Object, Object>();
        map2.put("id", "abcdefghij0123456789");
        map.put("a", map2);

        // when
        byte[] result = BEncoder.bencoding(map);

        // then
        assertEquals(
                "d1:ad2:id20:abcdefghij0123456789e1:q4:ping1:t2:aa1:y1:qe",
                new String(result));
    }

    /**
     * testBencoding02() test encoding a map / numbers.
     * @throws Exception   Exception
     */
    @Test
    public void testBencoding02() throws Exception {
        // given
        Map<Object, Object> map = new HashMap<Object, Object>();
        map.put("t", "aa");
        map.put("y", "q");
        map.put("q", "announce_peer");
        Map<Object, Object> map2 = new HashMap<Object, Object>();
        map2.put("id", "abcdefghij0123456789");
        map.put("a", map2);
        map2.put("info_hash", "mnopqrstuvwxyz123456");
        map2.put("port", Integer.valueOf(6881));
        map2.put("token", "aoeusnth");

        // when
        byte[] result = BEncoder.bencoding(map);

        // then
        assertEquals(
          "d1:ad2:id20:abcdefghij01234567899:info_hash20:mnopqrstuvwxyz1234564:"
          + "porti6881e5:token8:aoeusnthe1:q13:announce_peer1:t2:aa1:y1:qe",
          new String(result));
    }

    /**
     * testBencoding03() test encoding a Collection.
     * @throws Exception   Exception
     */
    @Test
    public void testBencoding03() throws Exception {
        // given
        List<Object> list = new ArrayList<>();
        list.add("s");
        list.add(Long.valueOf(12));

        // when
        byte[] result = BEncoder.bencoding(list);

        // then
        assertEquals("l1:si12ee", new String(result));
    }

    /**
     * testBencoding04() test encoding a Collection inside a map.
     * @throws Exception   Exception
     */
    @Test
    public void testBencoding04() throws Exception {
        // given
        Map<Object, Object> map = new HashMap<Object, Object>();
        List<Object> list = new ArrayList<>();
        list.add("s");
        list.add(Long.valueOf(12));
        map.put("t", list);

        // when
        byte[] result = BEncoder.bencoding(map);

        // then
        assertEquals("d1:tl1:si12eee", new String(result));
    }

    /**
     * testBencoding05() test encoding unknown object.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testBencoding05() {
        // given
        StringTokenizer st = new StringTokenizer("AD");

        // when
        BEncoder.bencoding(st);

        // then
    }

    /**
     * Test encoding byte array.
     * @throws Exception  Exception
     */
    @Test
    public void testBencoding06() throws Exception {
        // given
        byte[] bytes = new byte[] {-15, 14, 40, 33, -69,
              -66, -91, 39, -22, 2, 32, 3, 82, 49, 59, -64, 89, 68, 81, -112 };

        // when
        byte[] result = BEncoder.bencoding(bytes);
//        byte[] resultBytes = result.toByteArray();

        // then
        assertEquals(3 + bytes.length, result.length);
        assertEquals('2', result[0]);
        assertEquals('0', result[1]);
        assertEquals(':', result[2]);
        assertEquals(bytes[0], result[3]);
        assertEquals(bytes[1], result[4]);
        assertEquals(bytes[2], result[5]);
    }

    /**
     * testConstructorIsPrivate().
     * @throws Exception  Exception
     */
    @Test
    public void testConstructorIsPrivate() throws Exception {
        // given
        Constructor<BEncoder> constructor = BEncoder.class
                .getDeclaredConstructor();

        // when
        int result = constructor.getModifiers();

        // then
        assertTrue(Modifier.isPrivate(result));
        constructor.setAccessible(true);
        constructor.newInstance();
    }

    /**
     * Tests changing an IP / Port into
     * 6 byte compact version.
     * @throws Exception  Exception
     */
    @Test
    public void testCompactAddress01() throws Exception {
        // given
        InetAddress addr = InetAddress.getByName("37.76.160.28");
        int port = 37518;

        // when
        byte[] result = DHTConversion.compactAddress(addr.getAddress(), port);

        // then
        assertEquals(6, result.length);
        assertEquals(37, result[0]);
        assertEquals(76, result[1]);
        assertEquals(-96, result[2]);
        assertEquals(28, result[3]);
        assertEquals(-110, result[4]);
        assertEquals(-114, result[5]);
    }

    /**
     * Tests changing an IPv6 / Port into
     * 18 byte compact version.
     * @throws Exception  Exception
     */
    @Test
    public void testCompactAddress02() throws Exception {
        // given
        InetAddress addr = InetAddress
                .getByName("805b:2d9d:dc28:0000:0000:fc57:d4c8:1fff");
        int port = 37518;

        // when
        byte[] result = DHTConversion.compactAddress(addr.getAddress(), port);

        // then
        assertEquals(18, result.length);
        assertEquals(-128, result[0]);
        assertEquals(91, result[1]);
        assertEquals(45, result[2]);
        assertEquals(-99, result[3]);
        assertEquals(-36, result[4]);
        assertEquals(40, result[5]);
        assertEquals(0, result[6]);
        assertEquals(0, result[7]);
        assertEquals(0, result[8]);
        assertEquals(0, result[9]);
        assertEquals(-4, result[10]);
        assertEquals(87, result[11]);
        assertEquals(-44, result[12]);
        assertEquals(-56, result[13]);
        assertEquals(31, result[14]);
        assertEquals(-1, result[15]);
        assertEquals(-110, result[16]);
        assertEquals(-114, result[17]);
    }

}

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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.ByteArrayOutputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import org.junit.Test;

import ca.gobits.dht.BEncoder;

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
        ByteArrayOutputStream result = BEncoder.bencoding(map);

        // then
        assertEquals(
                "d1:ad2:id20:abcdefghij0123456789e1:q4:ping1:t2:aa1:y1:qe",
                result.toString());
        result.close();
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
        ByteArrayOutputStream result = BEncoder.bencoding(map);

        // then
        assertEquals(
          "d1:ad2:id20:abcdefghij01234567899:info_hash20:mnopqrstuvwxyz1234564:"
          + "porti6881e5:token8:aoeusnthe1:q13:announce_peer1:t2:aa1:y1:qe",
          result.toString());

        result.close();
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
        ByteArrayOutputStream result = BEncoder.bencoding(list);

        // then
        assertEquals("l1:si12ee", result.toString());

        result.close();
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
        ByteArrayOutputStream result = BEncoder.bencoding(map);

        // then
        assertEquals("d1:tl1:si12eee", new String(result.toByteArray()));
        result.close();
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
        ByteArrayOutputStream result = BEncoder.bencoding(bytes);
        byte[] resultBytes = result.toByteArray();

        // then
        assertEquals(3 + bytes.length, resultBytes.length);
        assertEquals('2', resultBytes[0]);
        assertEquals('0', resultBytes[1]);
        assertEquals(':', resultBytes[2]);
        assertEquals(bytes[0], resultBytes[3]);
        assertEquals(bytes[1], resultBytes[4]);
        assertEquals(bytes[2], resultBytes[5]);

        result.close();
    }

    /**
     * testConstructorIsPrivate().
     * @throws Exception  Exception
     */
    @Test
    public void testConstructorIsPrivate() throws Exception {
        Constructor<BEncoder> constructor = BEncoder.class
                .getDeclaredConstructor();
        assertTrue(Modifier.isPrivate(constructor.getModifiers()));
        constructor.setAccessible(true);
        constructor.newInstance();
    }
}

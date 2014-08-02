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

import java.util.List;
import java.util.Map;

import org.junit.Test;

import ca.gobits.dht.bencoding.BDecoder;


/**
 * Unit Test cases for BEncoder.
 */
public final class BDecoderUnitTest {

    /**
     * testDecoder01() test decoding a map.
     */
    @SuppressWarnings("unchecked")
    @Test
    public void testDecode01() {
        // given
        String s = "d1:q13:announce_peer1:t2:aa1:y1:qe";

        // when
        Object result = new BDecoder().decode(s.getBytes());

        // then
        assertTrue(result instanceof Map);
        Map<Object, Object> map = (Map<Object, Object>) result;
        assertEquals("announce_peer", new String((byte[]) map.get("q")));
        assertEquals("aa", new String((byte[]) map.get("t")));
        assertEquals("q", new String((byte[]) map.get("y")));
    }

    /**
     * testDecoder02() test decoding a nested map.
     */
    @SuppressWarnings("unchecked")
    @Test
    public void testDecode02() {
        // given
        String s = "d1:ad2:id20:abcdefghij0123456789e1:q4:ping1:t2:aa1:y1:qe";

        // when
        Object result = new BDecoder().decode(s.getBytes());

        // then
        assertTrue(result instanceof Map);
        Map<Object, Object> map = (Map<Object, Object>) result;

        assertEquals("aa", new String((byte[]) map.get("t")));
        assertEquals("q", new String((byte[]) map.get("y")));
        assertEquals("ping", new String((byte[]) map.get("q")));

        Map<Object, Object> map2 = (Map<Object, Object>) map.get("a");
        assertEquals("abcdefghij0123456789",
                new String((byte[]) map2.get("id")));
    }

    /**
     * testBencoding03() test encoding a map / numbers.
     */
    @SuppressWarnings("unchecked")
    @Test
    public void testDecode03() {
        // given
        String s = "d1:ad2:id20:abcdefghij01234567899:info_hash20"
        + ":mnopqrstuvwxyz1234564:"
        + "porti6881e5:token8:aoeusnthe1:q13:announce_peer1:t2:aa1:y1:qe";

        // when
        Object result = new BDecoder().decode(s.getBytes());

        // then
        assertTrue(result instanceof Map);
        Map<Object, Object> map = (Map<Object, Object>) result;

        assertEquals("aa", new String((byte[]) map.get("t")));
        assertEquals("q", new String((byte[]) map.get("y")));
        assertEquals("announce_peer", new String((byte[]) map.get("q")));

        Map<Object, Object> map2 = (Map<Object, Object>) map.get("a");
        assertEquals("abcdefghij0123456789",
                new String((byte[]) map2.get("id")));
        assertEquals("mnopqrstuvwxyz123456",
                new String((byte[]) map2.get("info_hash")));
        assertEquals(Long.valueOf(6881), map2.get("port"));
        assertEquals("aoeusnth", new String((byte[]) map2.get("token")));
    }

    /**
     * testBencoding04() test collections.
     */
    @SuppressWarnings("unchecked")
    @Test
    public void testDecode04() {
        // given
        String s = "l1:si12ee";

        // when
        Object result = new BDecoder().decode(s.getBytes());

        // then
        assertTrue(result instanceof List);
        List<Object> list = (List<Object>) result;

        assertEquals(2, list.size());
        assertEquals("s", new String((byte[]) list.get(0)));
        assertEquals(Long.valueOf(12), list.get(1));
    }
}

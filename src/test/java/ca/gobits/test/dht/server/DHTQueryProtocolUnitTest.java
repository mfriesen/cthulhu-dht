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

package ca.gobits.test.dht.server;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.apache.commons.codec.binary.Base64;
import org.junit.Test;

import ca.gobits.dht.DHTIdentifier;
import ca.gobits.dht.bencoding.BDecoder;
import ca.gobits.dht.server.DHTQueryProtocol;

/**
 * DHTQueryProtocol Unit Tests.
 *
 */
public final class DHTQueryProtocolUnitTest {

    /** Dummy NodeId. */
    private final byte[] nodeId = DHTIdentifier.sha1("test".getBytes());

    /** Transaction Id. */
    private final String transId = "aa";

    /** Target. */
    private final byte[] target = new byte[] {-1, -1, -1, -1, -1, -1, -1, -1,
            -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1 };

    /**
     * testConstructorIsPrivate().
     * @throws Exception  Exception
     */
    @Test
    public void testConstructorIsPrivate() throws Exception {
        Constructor<DHTQueryProtocol> constructor = DHTQueryProtocol.class
                .getDeclaredConstructor();
        assertTrue(Modifier.isPrivate(constructor.getModifiers()));
        constructor.setAccessible(true);
        constructor.newInstance();
    }

    /**
     * testPingQuery01() - generate ping request.
     */
    @SuppressWarnings("unchecked")
    @Test
    public void testPingQuery01() {

        // given

        // when
        byte[] result = DHTQueryProtocol.pingQuery(this.transId, this.nodeId);

        // then
        assertEquals(56, result.length);

        Map<Object, Object> map = (Map<Object, Object>) new BDecoder()
                .decode(result);
        assertEquals("aa", new String((byte[]) map.get("t")));
        assertEquals("q", new String((byte[]) map.get("y")));
        assertEquals("ping", new String((byte[]) map.get("q")));

        Map<Object, Object> a = (Map<Object, Object>) map.get("a");
        assertEquals(20, ((byte[]) a.get("id")).length);
        assertEquals(Base64.encodeBase64String(this.nodeId),
                Base64.encodeBase64String((byte[]) a.get("id")));
    }

    /**
     * testFindNodeQuery01() - generate find_node request.
     */
    @SuppressWarnings("unchecked")
    @Test
    public void testFindNodeQuery01() {

        // given

        // when
        byte[] result = DHTQueryProtocol.findNodeQuery(this.transId,
                this.nodeId, this.target, null);

        // then
        assertEquals(92, result.length);

        Map<Object, Object> map = (Map<Object, Object>) new BDecoder()
                .decode(result);
        assertEquals(this.transId, new String((byte[]) map.get("t")));
        assertEquals("q", new String((byte[]) map.get("y")));
        assertEquals("find_node", new String((byte[]) map.get("q")));

        Map<Object, Object> a = (Map<Object, Object>) map.get("a");
        assertEquals(20, ((byte[]) a.get("id")).length);
        assertEquals(20, ((byte[]) a.get("target")).length);
        assertEquals(Base64.encodeBase64String(this.nodeId),
                Base64.encodeBase64String((byte[]) a.get("id")));
    }

    /**
     * testFindNodeQuery02() - generate find_node request with want.
     */
    @SuppressWarnings("unchecked")
    @Test
    public void testFindNodeQuery02() {

        // given

        // when
        byte[] result = DHTQueryProtocol.findNodeQuery(this.transId,
                this.nodeId, this.target, Arrays.asList("n6".getBytes()));

        // then
        assertEquals(104, result.length);

        Map<Object, Object> map = (Map<Object, Object>) new BDecoder()
                .decode(result);
        assertEquals(this.transId, new String((byte[]) map.get("t")));
        assertEquals("q", new String((byte[]) map.get("y")));
        assertEquals("find_node", new String((byte[]) map.get("q")));

        Map<Object, Object> a = (Map<Object, Object>) map.get("a");
        assertEquals(20, ((byte[]) a.get("id")).length);
        assertEquals(20, ((byte[]) a.get("target")).length);

        List<byte[]> want = (ArrayList<byte[]>) a.get("want");
        assertEquals(1, want.size());
        assertEquals("n6", new String(want.get(0)));

        assertEquals(Base64.encodeBase64String(this.nodeId),
                Base64.encodeBase64String((byte[]) a.get("id")));
    }

    /**
     * testGetPeersQuery01() - generate get_peers request.
     */
    @SuppressWarnings("unchecked")
    @Test
    public void testGetPeersQuery01() {

        // given

        // when
        byte[] result = DHTQueryProtocol.getPeersQuery(this.transId,
                this.nodeId, this.target, null);

        // then
        assertEquals(95, result.length);

        Map<Object, Object> map = (Map<Object, Object>) new BDecoder()
                .decode(result);
        assertEquals(this.transId, new String((byte[]) map.get("t")));
        assertEquals("q", new String((byte[]) map.get("y")));
        assertEquals("get_peers", new String((byte[]) map.get("q")));

        Map<Object, Object> a = (Map<Object, Object>) map.get("a");
        assertEquals(20, ((byte[]) a.get("id")).length);
        assertEquals(20, ((byte[]) a.get("info_hash")).length);
        assertEquals(Base64.encodeBase64String(this.nodeId),
                Base64.encodeBase64String((byte[]) a.get("id")));
    }

    /**
     * testGetPeersQuery02() - generate get_peers with want request.
     */
    @SuppressWarnings("unchecked")
    @Test
    public void testGetPeersQuery02() {

        // given

        // when
        byte[] result = DHTQueryProtocol.getPeersQuery(this.transId,
                this.nodeId, this.target, Arrays.asList("n6".getBytes()));

        // then
        assertEquals(107, result.length);

        Map<Object, Object> map = (Map<Object, Object>) new BDecoder()
                .decode(result);
        assertEquals(this.transId, new String((byte[]) map.get("t")));
        assertEquals("q", new String((byte[]) map.get("y")));
        assertEquals("get_peers", new String((byte[]) map.get("q")));

        Map<Object, Object> a = (Map<Object, Object>) map.get("a");
        assertEquals(20, ((byte[]) a.get("id")).length);
        assertEquals(20, ((byte[]) a.get("info_hash")).length);

        List<byte[]> want = (ArrayList<byte[]>) a.get("want");
        assertEquals(1, want.size());
        assertEquals("n6", new String(want.get(0)));

        assertEquals(Base64.encodeBase64String(this.nodeId),
                Base64.encodeBase64String((byte[]) a.get("id")));
    }
}

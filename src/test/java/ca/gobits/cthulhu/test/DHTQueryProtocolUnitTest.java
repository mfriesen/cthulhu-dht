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

import static org.junit.Assert.assertEquals;

import java.util.Map;

import org.apache.commons.codec.binary.Base64;
import org.junit.Test;

import ca.gobits.cthulhu.DHTQueryProtocol;
import ca.gobits.dht.BDecoder;
import ca.gobits.dht.DHTIdentifier;

/**
 * DHTQueryProtocol Unit Tests.
 *
 */
public final class DHTQueryProtocolUnitTest {

    /** Dummy NodeId. */
    private final byte[] nodeId = DHTIdentifier.sha1("test".getBytes());

    /**
     * testPingQuery01() - generate ping request.
     */
    @SuppressWarnings("unchecked")
    @Test
    public void testPingQuery01() {

        // given
        String transactionId = "aa";

        // when
        byte[] result = DHTQueryProtocol.pingQuery(transactionId, this.nodeId);

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
        String transactionId = "aa";
        byte[] target = new byte[] {-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
                -1, -1, -1, -1, -1, -1, -1, -1, -1 };

        // when
        byte[] result = DHTQueryProtocol.findNodeQuery(transactionId,
                this.nodeId, target, null);

        // then
        assertEquals(92, result.length);

        Map<Object, Object> map = (Map<Object, Object>) new BDecoder()
                .decode(result);
        assertEquals(transactionId, new String((byte[]) map.get("t")));
        assertEquals("q", new String((byte[]) map.get("y")));
        assertEquals("find_node", new String((byte[]) map.get("q")));

        Map<Object, Object> a = (Map<Object, Object>) map.get("a");
        assertEquals(20, ((byte[]) a.get("id")).length);
        assertEquals(20, ((byte[]) a.get("target")).length);
        assertEquals(Base64.encodeBase64String(this.nodeId),
                Base64.encodeBase64String((byte[]) a.get("id")));
    }
}

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

package ca.gobits.cthulhu.domain.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.math.BigInteger;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.junit.Test;

import ca.gobits.cthulhu.domain.DHTInfoHash;
import ca.gobits.cthulhu.domain.DHTPeer;

/**
 * Unit Test for DHTInfoHash.
 */
public final class DHTInfoHashUnitTest {

    /**
     * testConstructor01().
     */
    @Test
    public void testConstructor01() {
        // given
        BigInteger nodeId = new BigInteger("123");

        // when
        DHTInfoHash result = new DHTInfoHash(nodeId);

        // then
        assertEquals(nodeId, result.getInfoHash());
    }

    /**
     * testConstructor02().
     */
    @Test
    public void testConstructor02() {
        // given
        BigInteger nodeId = new BigInteger("123");
        byte[] addr = new byte[] {12, 43, 65, 4 };
        int port = 8000;

        // when
        DHTInfoHash result = new DHTInfoHash(nodeId, addr, port);

        // then
        assertEquals(nodeId, result.getInfoHash());
        assertEquals(13379913916224L, result.getAddress());
    }

    /**
     * testConstructor03().
     */
    @Test
    public void testConstructor03() {
        // given
        Long id = Long.valueOf(234);
        BigInteger nodeId = new BigInteger("123");
        DHTInfoHash result = new DHTInfoHash();

        // when
        result.setId(id);
        result.setInfoHash(nodeId);

        // then
        assertEquals(id, result.getId());
        assertEquals(nodeId, result.getInfoHash());
    }

    /**
     * testToString01().
     */
    @Test
    public void testToString01() {
        // given
        BigInteger nodeId = new BigInteger("123");
        DHTInfoHash infohash = new DHTInfoHash(nodeId);

        // when
        String result = infohash.toString();

        // then
        assertTrue(result
                .startsWith("ca.gobits.cthulhu.domain.DHTInfoHash"));
        assertTrue(result.toString().endsWith(
                "[id=<null>,infoHash=123]"));
    }

    /**
     * testToString02().
     */
    @Test
    public void testToString02() {
        // given
        BigInteger nodeId = new BigInteger("123");
        DHTInfoHash infoHash = new DHTInfoHash(nodeId);
        infoHash.setAddress(13379913916224L);

        // when
        String result = infoHash.toString();

        // then
        assertTrue(result
                .startsWith("ca.gobits.cthulhu.domain.DHTInfoHash"));
        assertTrue(result.toString().endsWith(
                "[id=<null>,infoHash=123,address=12.43.65.4:8000]"));
    }

    /**
     * testEquals01()  null object.
     */
    @Test
    public void testEquals01() {
        // given
        BigInteger nodeId = new BigInteger("123");
        DHTInfoHash node = new DHTInfoHash(nodeId);

        // when
        boolean result = node.equals(null);

        // then
        assertFalse(result);
    }

    /**
     * testEquals02()  same object.
     */
    @Test
    public void testEquals02() {
        // given
        BigInteger nodeId = new BigInteger("123");
        DHTInfoHash node = new DHTInfoHash(nodeId);

        // when
        boolean result = node.equals(node);

        // then
        assertTrue(result);
    }

    /**
     * testEquals03()  non DHTInfoHash object.
     */
    @Test
    public void testEquals03() {
        // given
        BigInteger nodeId = new BigInteger("123");
        DHTInfoHash node = new DHTInfoHash(nodeId);

        // when
        boolean result = node.equals("");

        // then
        assertFalse(result);
    }

    /**
     * testEquals04()  equal DHTInfoHash object.
     */
    @Test
    public void testEquals04() {
        // given
        BigInteger nodeId = new BigInteger("123");
        DHTInfoHash node = new DHTInfoHash(nodeId);
        DHTInfoHash node1 = new DHTInfoHash(nodeId);

        // when
        boolean result = node.equals(node1);

        // then
        assertTrue(result);
    }

    /**
     * testAddPeer01() - first time through.
     */
    @Test
    public void testAddPeer01() {
        // given
        BigInteger nodeId = new BigInteger("123");
        DHTInfoHash node = new DHTInfoHash(nodeId);
        byte[] address = new byte[] {127, 0, 0, 1 };
        int port = 103;

        assertNull(node.getPeers());

        // when
        node.addPeer(address, port);
        Collection<DHTPeer> result = node.getPeers();

        // then
        assertEquals(1, result.size());
        assertEquals(139637976793191L, result.iterator().next().getAddress());
    }

    /**
     * testAddPeer02() - duplicate peer.
     */
    @Test
    public void testAddPeer02() {
        // given
        BigInteger nodeId = new BigInteger("123");
        DHTInfoHash node = new DHTInfoHash(nodeId);
        byte[] address = new byte[] {127, 0, 0, 1 };
        int port = 103;

        assertNull(node.getPeers());

        // when
        node.addPeer(address, port);
        node.addPeer(address, port);
        Collection<DHTPeer> result = node.getPeers();

        // then
        assertEquals(1, result.size());
    }

    /**
     * testAddPeer03().
     */
    @Test
    public void testAddPeer03() {
        // given
        BigInteger nodeId = new BigInteger("123");
        DHTInfoHash node = new DHTInfoHash(nodeId);
        byte[] address = new byte[] {127, 0, 0, 1 };
        int port = 103;
        DHTPeer peer = new DHTPeer(address, port);
        Set<DHTPeer> peers = new HashSet<DHTPeer>();
        peers.add(peer);

        assertNull(node.getPeers());

        // when
        node.setPeers(peers);

        // then
        assertEquals(1, node.getPeers().size());
        assertEquals(139637976793191L, node.getPeers().iterator().next()
                .getAddress());
    }
}

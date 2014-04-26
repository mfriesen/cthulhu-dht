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

package ca.gobits.cthulhu.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.math.BigInteger;
import java.util.Collection;

import org.junit.Test;

import ca.gobits.cthulhu.DHTInfoHash;

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
        assertEquals(nodeId, result.getId());
        assertEquals(752, result.hashCode());
    }

    /**
     * testToString01().
     */
    @Test
    public void testToString01() {
        // given
        BigInteger nodeId = new BigInteger("123");

        // when
        DHTInfoHash result = new DHTInfoHash(nodeId);

        // then
        assertTrue(result.toString()
                .startsWith("ca.gobits.cthulhu.DHTInfoHash"));
        assertTrue(result.toString().endsWith(
                "[id=123]"));
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
        Collection<Long> result = node.getPeers();

        // then
        assertEquals(1, result.size());
        assertEquals(139637976793191L, result.iterator().next().longValue());
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
        Collection<Long> result = node.getPeers();

        // then
        assertEquals(1, result.size());
    }
}

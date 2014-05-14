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

import static ca.gobits.cthulhu.domain.DHTNodeFactory.create;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.math.BigInteger;

import org.junit.Test;

import ca.gobits.cthulhu.domain.DHTNode;
import ca.gobits.cthulhu.domain.DHTNodeBasic;

/**
 * Unit Test for DHTNode.
 */
public final class DHTNodeBasicUnitTest {

    /**
     * testConstructor01().
     */
    @Test
    public void testConstructor01() {
        // given
        BigInteger nodeId = new BigInteger("123");
        byte[] address = new byte[] {127, 0, 0, 1 };
        int nodePort = 103;

        // when
        DHTNode result = create(nodeId, address, nodePort,
                DHTNode.State.UNKNOWN);

        // then
        assertEquals(nodeId, result.getInfoHash());
        assertEquals(2130706433L, result.getAddress()[0]);
        assertEquals(752, result.hashCode());
        assertNotNull(result.getLastUpdated());
    }

    /**
     * testConstructor02(). Null address
     */
    @Test
    public void testConstructor02() {
        // given
        BigInteger nodeId = new BigInteger("123");

        // when
        DHTNode result = create(nodeId, DHTNode.State.UNKNOWN);

        // then
        assertEquals(nodeId, result.getInfoHash());
        assertNull(result.getAddress());
        assertEquals(752, result.hashCode());
        assertNotNull(result.getLastUpdated());
    }

    /**
     * testConstructor03().
     */
    @Test
    public void testConstructor03() {
        // given
        BigInteger nodeId = new BigInteger("123");
        long[] address = new long[] {1L };
        int nodePort = 0;

        // when
        DHTNodeBasic result = new DHTNodeBasic();
        result.setInfoHash(nodeId);
        result.setAddress(address);
        result.setPort(nodePort);

        // then
        assertEquals(nodeId, result.getInfoHash());
        assertEquals(address, result.getAddress());
        assertEquals(nodePort, result.getPort());
    }

    /**
     * testToString01().
     */
    @Test
    public void testToString01() {
        // given
        BigInteger nodeId = new BigInteger("123");
        byte[] address = new byte[] {127, 0, 0, 1 };
        int nodePort = 103;
        DHTNode node = create(nodeId, address, nodePort,
                DHTNode.State.UNKNOWN);

        // when
        String result = node.toString();

        // then
        assertTrue(result.startsWith("ca.gobits.cthulhu.domain.DHTNode"));
        assertTrue(result
                .contains("[infohash=123,address=127.0.0.1,"
                        + "port=103,state=UNKNOWN,lastUpdated="));
    }

    /**
     * testEquals01() null object.
     */
    @Test
    public void testEquals01() {
        // given
        BigInteger nodeId = new BigInteger("123");
        byte[] address = new byte[] {127, 0, 0, 1 };
        int nodePort = 103;
        DHTNode node = create(nodeId, address, nodePort,
                DHTNode.State.UNKNOWN);

        // when
        boolean result = node.equals(null);

        // then
        assertFalse(result);
    }

    /**
     * testEquals02() same object.
     */
    @Test
    public void testEquals02() {
        // given
        BigInteger nodeId = new BigInteger("123");
        byte[] address = new byte[] {127, 0, 0, 1 };
        int nodePort = 103;
        DHTNode node = create(nodeId, address, nodePort,
                DHTNode.State.UNKNOWN);

        // when
        boolean result = node.equals(node);

        // then
        assertTrue(result);
    }

    /**
     * testEquals03() non DHTNode object.
     */
    @Test
    public void testEquals03() {
        // given
        BigInteger nodeId = new BigInteger("123");
        byte[] address = new byte[] {127, 0, 0, 1 };
        int nodePort = 103;
        DHTNode node = create(nodeId, address, nodePort,
                DHTNode.State.UNKNOWN);

        // when
        boolean result = node.equals("");

        // then
        assertFalse(result);
    }

    /**
     * testEquals04() equal DHTNode object.
     */
    @Test
    public void testEquals04() {
        // given
        BigInteger nodeId = new BigInteger("123");
        byte[] address = new byte[] {127, 0, 0, 1 };
        int nodePort = 103;
        DHTNode node = create(nodeId, address, nodePort,
                DHTNode.State.UNKNOWN);
        DHTNode node1 = create(nodeId, address, nodePort,
                DHTNode.State.UNKNOWN);

        // when
        boolean result = node.equals(node1);

        // then
        assertTrue(result);
    }
}

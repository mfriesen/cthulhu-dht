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
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.math.BigInteger;
import java.util.Date;

import org.junit.Test;

import ca.gobits.cthulhu.domain.DHTNode;

/**
 * Unit Test for DHTNode.
 */
public final class DHTNodeUnitTest {

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
        DHTNode result = new DHTNode(nodeId, address, nodePort);

        // then
        assertEquals(nodeId, result.getId());
        assertEquals(139637976793191L, result.getAddress());
        assertEquals(752, result.hashCode());
        assertNotNull(result.getLastUpdated());
    }

    /**
     * testConstructor02().  Null address
     */
    @Test
    public void testConstructor02() {
        // given
        BigInteger nodeId = new BigInteger("123");
        byte[] address = null;
        int nodePort = 0;

        // when
        DHTNode result = new DHTNode(nodeId, address, nodePort);

        // then
        assertEquals(nodeId, result.getId());
        assertEquals(0L, result.getAddress());
        assertEquals(752, result.hashCode());
        assertNotNull(result.getLastUpdated());
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

        // when
        DHTNode result = new DHTNode(nodeId, address, nodePort);

        // then
        assertTrue(result.toString().startsWith(
                "ca.gobits.cthulhu.domain.DHTNode"));
        assertTrue(result.toString().endsWith(
                "[id=123,address=139637976793191]"));
    }

    /**
     * testEquals01()  null object.
     */
    @Test
    public void testEquals01() {
        // given
        BigInteger nodeId = new BigInteger("123");
        byte[] address = new byte[] {127, 0, 0, 1 };
        int nodePort = 103;
        DHTNode node = new DHTNode(nodeId, address, nodePort);

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
        byte[] address = new byte[] {127, 0, 0, 1 };
        int nodePort = 103;
        DHTNode node = new DHTNode(nodeId, address, nodePort);

        // when
        boolean result = node.equals(node);

        // then
        assertTrue(result);
    }

    /**
     * testEquals03()  non DHTNode object.
     */
    @Test
    public void testEquals03() {
        // given
        BigInteger nodeId = new BigInteger("123");
        byte[] address = new byte[] {127, 0, 0, 1 };
        int nodePort = 103;
        DHTNode node = new DHTNode(nodeId, address, nodePort);

        // when
        boolean result = node.equals("");

        // then
        assertFalse(result);
    }

    /**
     * testEquals04()  equal DHTNode object.
     */
    @Test
    public void testEquals04() {
        // given
        BigInteger nodeId = new BigInteger("123");
        byte[] address = new byte[] {127, 0, 0, 1 };
        int nodePort = 103;
        DHTNode node = new DHTNode(nodeId, address, nodePort);
        DHTNode node1 = new DHTNode(nodeId, address, nodePort);

        // when
        boolean result = node.equals(node1);

        // then
        assertTrue(result);
    }

    /**
     * testSetLastUpdated01().
     */
    @Test
    public void testSetLastUpdated01() {
        // given
        Date date = new Date();
        DHTNode node = new DHTNode(new BigInteger("1"), (byte[]) null, 0);

        // when
        node.setLastUpdated(date);

        // then
        assertEquals(date, node.getLastUpdated());
    }
}

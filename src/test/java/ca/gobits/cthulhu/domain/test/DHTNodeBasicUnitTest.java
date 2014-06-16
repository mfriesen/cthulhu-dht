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

package ca.gobits.cthulhu.domain.test;

import static ca.gobits.cthulhu.domain.DHTNodeFactory.create;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.math.BigInteger;
import java.net.InetAddress;

import org.junit.Test;

import ca.gobits.cthulhu.domain.DHTNode;
import ca.gobits.cthulhu.domain.DHTNode.State;
import ca.gobits.cthulhu.domain.DHTNodeBasic;

/**
 * Unit Test for DHTNode.
 */
public final class DHTNodeBasicUnitTest {

    /**
     * testConstructor01().
     * @throws Exception   Exception
     */
    @Test
    public void testConstructor01() throws Exception {
        // given
        byte[] nodeId = new BigInteger("123").toByteArray();
        byte[] address = new byte[] {127, 0, 0, 1 };
        int nodePort = 103;

        // when
        DHTNode result = create(nodeId, address, nodePort,
                DHTNode.State.UNKNOWN);

        // then
        assertEquals(nodeId, result.getInfoHash());
        assertEquals("127.0.0.1", result.getAddress().getHostAddress());
        assertEquals(752, result.hashCode());
        assertNotNull(result.getLastUpdated());
        assertEquals(State.UNKNOWN, result.getState());
    }

    /**
     * testConstructor02() - sets IPv4 Address.
     * @throws Exception   Exception
     */
    @Test
    public void testConstructor02() throws Exception {
        // given
        byte[] nodeId = new BigInteger("123").toByteArray();
        InetAddress address = InetAddress.getByName("54.23.54.12");
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
     * testConstructor03() - sets IPv6 Address.
     * @throws Exception   Exception
     */
    @Test
    public void testConstructor03() throws Exception {
        // given
        byte[] nodeId = new BigInteger("123").toByteArray();
        InetAddress address = InetAddress
                .getByName("805b:2d9d:dc28:0000:0000:fc57:d4c8:1fff");
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
        byte[] nodeId = new BigInteger("123").toByteArray();
        byte[] address = new byte[] {127, 0, 0, 1 };
        int nodePort = 103;
        DHTNode node = create(nodeId, address, nodePort,
                DHTNode.State.UNKNOWN);

        // when
        String result = node.toString();

        // then
        assertTrue(result.startsWith("ca.gobits.cthulhu.domain.DHTNode"));
        assertTrue(result
                .contains("[infohash={123},address=127.0.0.1,"
                        + "port=103,state=UNKNOWN,lastUpdated="));
    }

    /**
     * testEquals01() null object.
     */
    @Test
    public void testEquals01() {
        // given
        byte[] nodeId = new BigInteger("123").toByteArray();
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
        byte[] nodeId = new BigInteger("123").toByteArray();
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
        byte[] nodeId = new BigInteger("123").toByteArray();
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
        byte[] nodeId = new BigInteger("123").toByteArray();
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

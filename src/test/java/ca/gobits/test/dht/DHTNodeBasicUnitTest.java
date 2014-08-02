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

package ca.gobits.test.dht;

import static ca.gobits.dht.DHTIdentifier.NODE_ID_LENGTH;
import static ca.gobits.dht.factory.DHTNodeFactory.create;
import static ca.gobits.dht.util.DHTConversion.fitToSize;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.math.BigInteger;
import java.net.InetAddress;

import org.junit.Test;

import ca.gobits.dht.DHTNode;
import ca.gobits.dht.DHTNode.State;
import ca.gobits.dht.DHTNodeBasic;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

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
        byte[] nodeId = fitToSize(new BigInteger("123").toByteArray(),
                NODE_ID_LENGTH);
        InetAddress addr = InetAddress.getByAddress(new byte[] {127, 0, 0, 1 });
        int nodePort = 103;

        // when
        DHTNode result = create(nodeId, addr, nodePort, State.UNKNOWN);

        // then
        assertArrayEquals(nodeId, result.getInfoHash());
        assertEquals("127.0.0.1", result.getAddress().getHostAddress());
        assertEquals(-1430351300, result.hashCode());
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
        assertArrayEquals(nodeId, result.getInfoHash());
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
        assertArrayEquals(nodeId, result.getInfoHash());
        assertEquals(address, result.getAddress());
        assertEquals(nodePort, result.getPort());
    }

    /**
     * testToString01().
     * @throws Exception  Eception
     */
    @Test
    public void testToString01() throws Exception {
        // given
        byte[] nodeId = new BigInteger("123").toByteArray();
        InetAddress addr = InetAddress.getByAddress(new byte[] {127, 0, 0, 1 });
        int nodePort = 103;
        DHTNode node = create(nodeId, addr, nodePort, State.UNKNOWN);

        // when
        String result = node.toString();

        // then
        assertTrue(result.startsWith("ca.gobits.dht.DHTNode"));
        assertTrue(result.contains("[infohash="
                + "{0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,123},"
                + "address=127.0.0.1,"
                + "port=103,state=UNKNOWN,lastUpdated="));
    }

    /**
     * testEquals01() null object.
     * @throws Exception   Exception
     */
    @Test
    public void testEquals01() throws Exception {
        // given
        byte[] nodeId = new BigInteger("123").toByteArray();
        InetAddress addr = InetAddress.getByAddress(new byte[] {127, 0, 0, 1 });
        int nodePort = 103;
        DHTNode node = create(nodeId, addr, nodePort, State.UNKNOWN);

        // when
        boolean result = node.equals(null);

        // then
        assertFalse(result);
    }

    /**
     * testEquals02() same object.
     * @throws Exception   Exception
     */
    @Test
    public void testEquals02() throws Exception {
        // given
        byte[] nodeId = new BigInteger("123").toByteArray();
        InetAddress addr = InetAddress.getByAddress(new byte[] {127, 0, 0, 1 });
        int nodePort = 103;
        DHTNode node = create(nodeId, addr, nodePort, State.UNKNOWN);

        // when
        boolean result = node.equals(node);

        // then
        assertTrue(result);
    }

    /**
     * testEquals03() non DHTNode object.
     * @throws Exception   Exception
     */
    @Test
    @SuppressFBWarnings(value = "EC_UNRELATED_CLASS_AND_INTERFACE")
    public void testEquals03() throws Exception {
        // given
        byte[] nodeId = new BigInteger("123").toByteArray();
        InetAddress addr = InetAddress.getByAddress(new byte[] {127, 0, 0, 1 });
        int nodePort = 103;
        DHTNode node = create(nodeId, addr, nodePort, State.UNKNOWN);

        // when
        boolean result = node.equals("");

        // then
        assertFalse(result);
    }

    /**
     * testEquals04() equal DHTNode object.
     * @throws Exception   Exception
     */
    @Test
    public void testEquals04() throws Exception {
        // given
        byte[] nodeId = new BigInteger("123").toByteArray();
        InetAddress addr = InetAddress.getByAddress(new byte[] {127, 0, 0, 1 });
        int nodePort = 103;
        DHTNode node = create(nodeId, addr, nodePort, State.UNKNOWN);
        DHTNode node1 = create(nodeId, addr, nodePort, State.UNKNOWN);

        // when
        boolean result = node.equals(node1);

        // then
        assertTrue(result);
    }
}

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

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.math.BigInteger;
import java.net.InetAddress;

import org.junit.Test;

import ca.gobits.dht.DHTToken;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

/**
 * DHTToken Unit Test.
 *
 */
public final class DHTTokenUnitTest {

    /**
     * testConstructor01.
     * @throws Exception  Exception
     */
    @Test
    public void testConstructor01() throws Exception {
        // given
        byte[] nodeId = new BigInteger("1").toByteArray();
        byte[] addr = new byte[] {12, 12, 12, 12 };
        int port = 80;

        // when
        DHTToken result = new DHTToken(nodeId, addr, port);

        // then
        assertArrayEquals(nodeId, result.getInfoHash());
        assertEquals("12.12.12.12", result.getAddress().getHostAddress());
        assertEquals(630, result.hashCode());
        assertNotNull(result.getAddedDate());
        assertTrue(result.toString().contains(
                "infohash={1},address=12.12.12.12,port=80,addedDate="));
    }

    /**
     * testConstructor02.
     * @throws Exception  Exception
     */
    @Test
    public void testConstructor02() throws Exception {
        // given
        byte[] nodeId = new BigInteger("1").toByteArray();
        InetAddress addr = InetAddress.getByName("54.23.54.12");
        int port = 80;

        // when
        DHTToken result = new DHTToken(nodeId, addr.getAddress(), port);

        // then
        assertArrayEquals(nodeId, result.getInfoHash());
        assertEquals("54.23.54.12", result.getAddress().getHostAddress());
        assertEquals(630, result.hashCode());
        assertEquals(port, result.getPort());
        assertNotNull(result.getAddedDate());
        assertTrue(result.toString().contains(
                "infohash={1},address=54.23.54.12,port=80,addedDate="));
    }

    /**
     * testEquals01()  null object.
     */
    @Test
    public void testEquals01() {
        // given
        byte[] id = new BigInteger("1").toByteArray();
        byte[] addr = new byte[] {12, 12, 12, 12 };
        int port = 80;
        DHTToken token = new DHTToken(id, addr, port);

        // when
        boolean result = token.equals(null);

        // then
        assertFalse(result);
    }

    /**
     * testEquals02()  same object.
     */
    @Test
    public void testEquals02() {
        // given
        byte[] id = new BigInteger("1").toByteArray();
        byte[] addr = new byte[] {12, 12, 12, 12 };
        int port = 80;
        DHTToken token = new DHTToken(id, addr, port);

        // when
        boolean result = token.equals(token);

        // then
        assertTrue(result);
    }

    /**
     * testEquals03()  non DHTInfoHash object.
     */
    @Test
    @SuppressFBWarnings(value = "EC_UNRELATED_TYPES")
    public void testEquals03() {
        // given
        byte[] id = new BigInteger("1").toByteArray();
        byte[] addr = new byte[] {12, 12, 12, 12 };
        int port = 80;
        DHTToken token = new DHTToken(id, addr, port);

        // when
        boolean result = token.equals("");

        // then
        assertFalse(result);
    }

    /**
     * testEquals04()  equal DHTInfoHash object.
     */
    @Test
    public void testEquals04() {
        // given
        byte[] id = new BigInteger("1").toByteArray();
        byte[] addr = new byte[] {12, 12, 12, 12 };
        int port = 80;
        DHTToken token = new DHTToken(id, addr, port);
        DHTToken token1 = new DHTToken(id, addr, port);

        // when
        boolean result = token.equals(token1);

        // then
        assertTrue(result);
    }
}

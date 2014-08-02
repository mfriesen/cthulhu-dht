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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.net.InetAddress;

import org.junit.Test;

import ca.gobits.dht.DHTPeer;
import ca.gobits.dht.DHTPeerBasic;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

/**
 * DHTPeer Unit Test.
 *
 */
public final class DHTPeerBasicUnitTest {

    /**
     * testConstructor01().
     * @throws Exception   Exception
     */
    @Test
    public void testConstructor01() throws Exception {
        // given
        byte[] addr = new byte[] {3, 4, 5, 6 };
        int port = 8000;

        // when
        DHTPeer result = new DHTPeerBasic(addr, port);

        // then
        assertEquals("3.4.5.6", result.getAddress().getHostAddress());
    }

    /**
     * testToString01().
     * @throws Exception   Exception
     */
    @Test
    public void testToString01() throws Exception {
        // given
        int port = 123;
        InetAddress addr = InetAddress.getByName("127.0.0.1");
        DHTPeer peer = new DHTPeerBasic(addr.getAddress(), port);

        // when
        String result = peer.toString();

        // then
        assertTrue(result.startsWith("ca.gobits.dht.DHTPeer"));
        assertTrue(result.endsWith("address=127.0.0.1,port=123]"));
        assertEquals(123, peer.getPort());
    }

    /**
     * testEquals01()  null object.
     */
    @Test
    public void testEquals01() {
        // given
        DHTPeer node = new DHTPeerBasic();

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
        DHTPeer node = new DHTPeerBasic();

        // when
        boolean result = node.equals(node);

        // then
        assertTrue(result);
    }

    /**
     * testEquals03()  non DHTPeer object.
     */
    @Test
    @SuppressFBWarnings(value = "EC_UNRELATED_TYPES")
    public void testEquals03() {
        // given
        DHTPeer node = new DHTPeerBasic();

        // when
        boolean result = node.equals("");

        // then
        assertFalse(result);
    }

    /**
     * testEquals04()  equal DHTPeer object.
     */
    @Test
    public void testEquals04() {
        // given
        byte[] addr = new byte[] {3, 4, 5, 6 };
        int port = 8000;

        DHTPeer node = new DHTPeerBasic(addr, port);
        DHTPeer node1 = new DHTPeerBasic(addr, port);

        // when
        boolean result = node.equals(node1);

        // then
        assertTrue(result);
    }

}

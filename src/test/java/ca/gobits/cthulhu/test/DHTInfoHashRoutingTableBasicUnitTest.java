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
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.math.BigInteger;
import java.util.Collection;
import java.util.Iterator;

import org.junit.Test;

import ca.gobits.cthulhu.DHTInfoHashRoutingTableBasic;
import ca.gobits.cthulhu.domain.DHTInfoHash;
import ca.gobits.cthulhu.domain.DHTPeer;

/**
 * DHTInfoHashRoutingTableBasic Unit Tests.
 *
 */
public final class DHTInfoHashRoutingTableBasicUnitTest {

    /** DHTInfoHashRoutingTable. */
    private final DHTInfoHashRoutingTableBasic rt =
            new DHTInfoHashRoutingTableBasic();

    /**
     * testFindPeers01() - peer cannot be found.
     */
    @Test
    public void testFindPeers01() {
        // given
        BigInteger infoHash = new BigInteger("123123");

        // when
        Collection<DHTPeer> result = this.rt.findPeers(infoHash.toByteArray());

        // then
        assertNull(result);
    }

    /**
     * testFindPeers02() - peer found.
     * @throws Exception   Exception
     */
    @Test
    public void testFindPeers02() throws Exception {
        // given
        BigInteger infoHash = new BigInteger("123123");
        byte[] address = new byte[] {127, 0, 0, 1 };
        int port = 1234;

        this.rt.addPeer(infoHash.toByteArray(), address, port);

        // when
        Collection<DHTPeer> result = this.rt.findPeers(infoHash.toByteArray());

        // then
        assertEquals(1, result.size());
        DHTPeer peer = result.iterator().next();
        assertEquals("127.0.0.1", peer.getAddress().getHostAddress());
        assertEquals(port, peer.getPort());
    }

    /**
     * testAddPeer01() - InfoHash is missing.
     * @throws Exception   Exception
     */
    @Test
    public void testAddPeer01() throws Exception {
        // given
        BigInteger infoHash = new BigInteger("12341");
        byte[] address = new byte[] {127, 0, 0, 1 };
        int port = 1234;

        // when
        assertNull(this.rt.findInfoHash(infoHash.toByteArray()));
        this.rt.addPeer(infoHash.toByteArray(), address, port);
        DHTInfoHash result = this.rt.findInfoHash(infoHash.toByteArray());

        // then
        assertNotNull(result);
        assertEquals(1, result.getPeers().size());
        DHTPeer l = result.getPeers().iterator().next();
        assertEquals("127.0.0.1", l.getAddress().getHostAddress());
    }

    /**
     * testAddPeer02() - InfoHash exists.
     * @throws Exception   Exception
     */
    @Test
    public void testAddPeer02() throws Exception {
        // given
        byte[] infoHash = new BigInteger("12341").toByteArray();
        byte[] address = new byte[] {127, 0, 0, 1 };
        int port = 1234;

        // when
        this.rt.addPeer(infoHash, address, port);
        assertNotNull(this.rt.findInfoHash(infoHash));
        this.rt.addPeer(infoHash, address, port);
        DHTInfoHash result = this.rt.findInfoHash(infoHash);

        // then
        assertNotNull(result);
        assertEquals(1, result.getPeers().size());
        Iterator<DHTPeer> itr = result.getPeers().iterator();
        assertEquals("127.0.0.1", itr.next().getAddress().getHostAddress());
    }

    /**
     * testAddPeer03() - Test Maximum number of peers.
     * @throws Exception   Exception
     */
    @Test
    public void testAddPeer03() throws Exception {
        // given
        byte[] infoHash = new BigInteger("12341").toByteArray();
        byte[] address = new byte[] {127, 0, 0, 1 };
        int port = 1234;
        this.rt.setPeerMax(0);

        // when
        this.rt.addPeer(infoHash, address, port);
        assertNotNull(this.rt.findInfoHash(infoHash));
        this.rt.addPeer(infoHash, address, port);
        DHTInfoHash result = this.rt.findInfoHash(infoHash);

        // then
        assertNotNull(result);
        assertEquals(0, result.getPeerCount());
        assertEquals(0, this.rt.getPeerMax());
    }
}

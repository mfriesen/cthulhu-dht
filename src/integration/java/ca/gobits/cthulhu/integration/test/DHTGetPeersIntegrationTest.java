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

package ca.gobits.cthulhu.integration.test;

import static ca.gobits.cthulhu.integration.test.DHTServerIntegrationHelper.sendUDPPacket;
import static ca.gobits.cthulhu.test.DHTTestHelper.assertNodesEquals;
import static ca.gobits.cthulhu.test.DHTTestHelper.runDHTServerInNewThread;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.codec.binary.Base64;
import org.apache.log4j.Logger;
import org.junit.Before;
import org.junit.Test;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import ca.gobits.cthulhu.DHTConfiguration;
import ca.gobits.cthulhu.DHTNode;
import ca.gobits.cthulhu.DHTNodeRoutingTable;
import ca.gobits.cthulhu.DHTServerConfig;
import ca.gobits.dht.Arrays;
import ca.gobits.dht.BDecoder;
import ca.gobits.dht.BEncoder;


/**
 * Integration Test for "get_peers" requests.
 *
 */
public final class DHTGetPeersIntegrationTest {

    /** LOGGER. */
    private static final Logger LOGGER = Logger
            .getLogger(DHTGetPeersIntegrationTest.class);

    /** Flag to indicate whether Server has been started. */
    private static boolean started = false;

    /** Application Context. */
    private final ConfigurableApplicationContext ac =
            new AnnotationConfigApplicationContext(
                    DHTConfiguration.class);

    /** Reference to DHTNodeRoutingTable. */
    private final DHTNodeRoutingTable nodeRoutingTable = ac
            .getBean(DHTNodeRoutingTable.class);

    /**
     *before().
     * @throws Exception  Exception
     */
    @Before
    public void before() throws Exception {

        if (!started) {
            runDHTServerInNewThread(ac, DHTServerConfig.DEFAULT_PORT);
            started = true;
        }
    }

    /**
     * testGetPeers01().
     * @throws Exception  Exception
     */
    @SuppressWarnings("unchecked")
    @Test
    public void testGetPeers01() throws Exception {
        // given
        LOGGER.debug("testGetPeers01\n-------------------------");

        String id = "abcdefghij0123456789";
        String infohash = "mnopqrstuvwxyz123456";

        Map<String, Object> realResponse = (Map<String, Object>) new BDecoder()
            .decode(getRealGetPeersResponse());

        byte[] realnodes = (byte[]) ((Map<String, Object>) realResponse
                .get("r")).get("nodes");
        addNodesToRoutingTable(realnodes);

        Map<String, Object> request = createRequest("aa", id.getBytes(),
                infohash.getBytes());

        // when
        byte[] results = sendUDPPacket(BEncoder.bencoding(request)
                .toByteArray());

        // then
        Map<String, Object> response = (Map<String, Object>) new BDecoder()
                .decode(results);
        assertEquals(4, realResponse.size());
        assertEquals(4, response.size());
        assertEquals(new String((byte[]) realResponse.get("t")),
                new String((byte[]) response.get("t")));
        assertEquals(new String((byte[]) realResponse.get("y")),
                new String((byte[]) response.get("y")));
        assertTrue(BDecoder.decodeCompactAddressToString(
                (byte[]) response.get("ip")).startsWith("127.0.0.1"));

        Map<String, Object> r = (Map<String, Object>) response.get("r");
        assertEquals(3, r.size());
        assertEquals(id, new String((byte[]) r.get("id")));
        assertEquals(10, ((byte[]) r.get("token")).length);

        byte[] nodes = (byte[]) r.get("nodes");
        assertEquals(416, nodes.length);

        assertNodesEquals(nodes, realnodes);
    }

    /**
     * Add Node to Routing table.
     * @param bytes  in "compact node" format
     * @throws IOException  IOException
     */
    private void addNodesToRoutingTable(final byte[] bytes) throws IOException {
        Collection<DHTNode> nodes = Arrays.toDHTNode(bytes);
        LOGGER.debug("add " + nodes.size() + " nodes to DHTNode Routing Table");
        assertFalse(nodes.isEmpty());
        for (DHTNode node : nodes) {
            nodeRoutingTable.addNode(node);
        }
    }

    /**
     * Create "get_peers" request.
     * @param t t
     * @param id id
     * @param infoHash infoHash
     * @return Map<String, Object>
     */
    private Map<String, Object> createRequest(final String t, final byte[] id,
            final byte[] infoHash) {
        Map<String, Object> request = new HashMap<String, Object>();
        request.put("t", t);
        request.put("y", "q");
        request.put("q", "get_peers");

        Map<String, Object> a = new HashMap<String, Object>();
        a.put("id", id);
        a.put("info_hash", infoHash);
        request.put("a", a);

        return request;
    }

    /**
     * @return byte[]
     */
    private byte[] getRealGetPeersResponse() {
        return Base64.decodeBase64("ZDI6aXA2OjJH1ovz5TE6cmQyOmlkMjA6HbzsI8Zpc1"
                + "H/Suwpzbqr8vvjRmc1Om5vZGVzNDE2OkPYR0WL68BBINLKLmQP85tqrT0nT"
                + "wn5mKj14EgIq8ZQqYD5R5YC9NQWxrx6oPe+MBWPuAaWKfPCUbFfbdX0dJdr"
                + "Gm3LKKN3Y740l3QvbW5ikCq/QBDeKdkF+xlyO5Xave1nsi0DDZxTH2mzv8W"
                + "FADLE+MuKlC2XbfX8Jb+6LaWlz6wrM+cFqxRrYYFM2CFvOSsSGADN7gJ5+A"
                + "vM2VceoItDEajgrE9UQARUJU1rexz9eqmKACpn+h/D9q0T3+fuahjtnis/Y"
                + "jctY+ZZ2MekKOBSLrxZd0L5oklIWdZknq6uLY936k9x8oJW/DP5hRXxu+nr"
                + "s6bbPIcMPpkkXg3xTvB8DwBQDTdbitLjWCSDBN/+obulzAOq1vxNXRQqnxB"
                + "J332qW9QO7jYE8oobYSaBoc9nwwJWgJLTiikdfeMyrMb6FtyZbdYUunt1Bf"
                + "3hTVWs6knqlodOa0qZg68c9zV6W51aBQ7HUncuOwWbgwM7kFISXKCZx8y8l"
                + "GsxyUo3CFWHPLGxOmNpVcUCkLTbnhTmf3GYoLy88iPDPPgsVEgHY/C8ZTE6"
                + "dDI6YWExOnkxOnJlAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA"
                + "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA"
                + "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA"
                + "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA"
                + "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA"
                + "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA"
                + "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA"
                + "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA"
                + "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA"
                + "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA"
                + "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA"
                + "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA"
                + "AAAAAAAAAAAAAAAAAAAAAAAAAA==");
    }
}
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

package ca.gobits.cthulhu;

import static ca.gobits.dht.DHTConversion.toByteArrayFromDHTPeer;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;

import ca.gobits.cthulhu.domain.DHTNode;
import ca.gobits.cthulhu.domain.DHTPeer;
import ca.gobits.dht.BDecoder;
import ca.gobits.dht.BEncoder;
import ca.gobits.dht.DHTConversion;

/**
 * DHTProtocolHandler  implementation of the BitTorrent protocol.
 * http://www.bittorrent.org/beps/bep_0005.html
 */
public final class DHTProtocolHandler {

    /** Length of Get_Peers token. */
    private static final int GET_PEERS_TOKEN_LENGTH = 10;

    /** DHTProtocolHandler Logger. */
    private static final Logger LOGGER = Logger
            .getLogger(DHTProtocolHandler.class);

    /** DHT Node Routing Table. */
    @Autowired
    private DHTNodeRoutingTable routingTable;

    /** DHT Peer Routing Table. */
    @Autowired
    private DHTInfoHashRoutingTable peerRoutingTable;

    /** DHT Token Table. */
    @Autowired
    private DHTTokenTable tokenTable;

    /**
     * Read DatagramPacket.
     * @param packet  packet
     * @return byte[]
     * @throws IOException  IOException
     */
    public byte[] handle(final DatagramPacket packet) throws IOException {

        InetAddress addr = packet.getAddress();
        int port = packet.getPort();

        Map<String, Object> response = new HashMap<String, Object>();

        try {
            Map<String, Object> request = bdecode(packet.getData());

            response.put("y", "r");
            response.put("t", request.get("t"));
            response.put("ip",
                    DHTConversion.compactAddress(addr.getAddress(), port));

            String action = new String((byte[]) request.get("q"));

            @SuppressWarnings("unchecked")
            DHTArgumentRequest arguments = new DHTArgumentRequest(
                    (Map<String, Object>) request.get("a"));

            if (action.equals("ping")) {

                addPingResponse(arguments, response, packet);

//                routingTable.addNode(arguments.getId(), packet.sender(),
//                        State.UNKNOWN);

            } else if (action.equals("find_node")) {

                addFindNodeResponse(arguments, response, packet);

            } else if (action.equals("get_peers")) {

                addGetPeersResponse(arguments, response, packet);

            } else if (action.equals("announce_peer")) {

                addAnnouncePeerResponse(arguments, response, packet);

            } else {
                addMethodUnknownResponse(response);
            }

        } catch (Exception e) {

            LOGGER.fatal(e, e);
            addServerError(response);

        }

        ByteArrayOutputStream os = BEncoder.bencoding(response);
        byte[] bytes = os.toByteArray();
        os.close();

        return bytes;
    }

    /**
     * Announces that a peer has joined an InfoHash.
     * @param arguments  DHTArgumentRequest
     * @param response  Map<String, Object>
     * @param packet  DatagramPacket
     */
    private void addAnnouncePeerResponse(final DHTArgumentRequest arguments,
            final Map<String, Object> response, final DatagramPacket packet) {

        int port = isImpliedPort(arguments) ? packet.getPort()
                : arguments.getPort().intValue();

        if (tokenTable.valid(packet.getAddress(), port, arguments.getToken())) {

            BigInteger infoHash = DHTConversion.toBigInteger(arguments
                    .getInfoHash());

            InetAddress addr = packet.getAddress();
            byte[] address = addr.getAddress();

            peerRoutingTable.addPeer(infoHash, address, port);

            Map<String, Object> rp = new HashMap<String, Object>();
            rp.put("id", arguments.getInfoHash());
            response.put("r", rp);

        } else {

            response.put("y", "e");
            response.put("r", map("203", "Bad Token"));
        }
    }

    /**
     * Is "implied_port" value is set and non-zero, the port argument
     * should be ignored and the source port of the UDP packet should
     * be used as the peer's port instead.
     * @param arguments  DHTArgumentRequest
     * @return boolean
     */
    private boolean isImpliedPort(final DHTArgumentRequest arguments) {
        Long impliedPort = arguments.getImpliedPort();
        return impliedPort != null && impliedPort.intValue() > 0;
    }

    /**
     * Get peers associated with a torrent infohash. If the queried node has no
     * peers for the infohash, a key "nodes" is returned containing the K nodes
     * in the queried nodes routing table closest to the infohash supplied in
     * the query.
     *
     * @param arguments DHTArgumentRequest
     * @param response  Map<String, Object>
     * @param packet  DatagramPacket
     * @throws IOException  IOException
     */
    private void addGetPeersResponse(final DHTArgumentRequest arguments,
        final Map<String, Object> response, final DatagramPacket packet)
            throws IOException {

        Map<String, Object> responseParameter = new HashMap<String, Object>();

        BigInteger infoHash = DHTConversion.toBigInteger(arguments
                .getInfoHash());

        Collection<DHTPeer> peers = peerRoutingTable.findPeers(infoHash);

        if (!CollectionUtils.isEmpty(peers)) {

            List<byte[]> bytes = toByteArrayFromDHTPeer(peers);
            responseParameter.put("values", bytes);

        } else {

            List<DHTNode> nodes = routingTable.findClosestNodes(infoHash);

            byte[] transformNodes = DHTConversion.toByteArrayFromDHTNode(nodes);
            responseParameter.put("nodes", transformNodes);
        }

        responseParameter.put("token", generateToken());
        responseParameter.put("id", arguments.getId());

        response.put("r", responseParameter);
    }

    /**
     * Add Method Unknown Error to response.
     * @param response Map<String, Object>
     */
    private void addMethodUnknownResponse(final Map<String, Object> response) {
        addServerError(response);
        response.put("r", map("204", "Method Unknown"));
    }

    /**
     * Add "find_node" data to response.
     * @param arguments  DHTArgumentRequest
     * @param response  Map<String, Object>
     * @param packet  DatagramPacket
     * @throws IOException  IOException
     */
    private void addFindNodeResponse(final DHTArgumentRequest arguments,
            final Map<String, Object> response,
            final DatagramPacket packet) throws IOException {

        Map<String, Object> responseParameter = new HashMap<String, Object>();
        response.put("r", responseParameter);
        responseParameter.put("id", arguments.getId());

        List<DHTNode> nodes = findClosestNodes(arguments.getTarget());

        byte[] transformNodes = DHTConversion.toByteArrayFromDHTNode(nodes);
        responseParameter.put("nodes", transformNodes);
    }

    /**
     * Add "ping" data to response.
     * @param arguments  DHTArgumentRequest
     * @param response  Map<String, Object>
     * @param packet DatagramPacket
     */
    private void addPingResponse(final DHTArgumentRequest arguments,
            final Map<String, Object> response,
            final DatagramPacket packet) {

        response.put("r", map("id", DHTServer.NODE_ID));
    }

    /**
     * Find the closest X nodes to the target.
     * @param targetBytes  ID of the target node to find
     * @return List<DHTNode>
     */
    private List<DHTNode> findClosestNodes(final byte[] targetBytes) {

        BigInteger target = DHTConversion.toBigInteger(targetBytes);
        return routingTable.findClosestNodes(target);
    }

    /**
     * Create Map.
     * @param key  map key
     * @param value map value
     * @return Map<String, Object>
     */
    private Map<String, Object> map(final String key, final Object value) {
        Map<String, Object> map = new HashMap<String, Object>(1);
        map.put(key, value);
        return map;
    }

    /**
     * Add server error to Map object.
     * @param map  Map<String, Object>
     */
    private void addServerError(final Map<String, Object> map) {
        map.put("y", "e");
        map.put("r", map("202", "Server Error"));
    }

    /**
     * Extract Byte Array using BDecoder.
     *
     * @param bytes  bytes
     * @return Map<String, Object>
     */
    @SuppressWarnings("unchecked")
    private Map<String, Object> bdecode(final byte[] bytes) {

        Map<String, Object> map = (Map<String, Object>) new BDecoder()
                .decode(bytes);
        return map;
    }

    /**
     * Token returned to a get_peers request.
     * This is to prevent malicious hosts from signing up other hosts
     * @return byte[]
     */
    private byte[] generateToken() {
        byte[] bytes = new byte[GET_PEERS_TOKEN_LENGTH];
        Random random = new Random();
        random.nextBytes(bytes);
        return bytes;
    }
}

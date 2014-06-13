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

package ca.gobits.cthulhu;

import static ca.gobits.dht.DHTConversion.toByteArrayFromDHTPeer;

import java.io.IOException;
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

import ca.gobits.cthulhu.discovery.DHTNodeDiscovery;
import ca.gobits.cthulhu.domain.DHTNode;
import ca.gobits.cthulhu.domain.DHTNode.State;
import ca.gobits.cthulhu.domain.DHTPeer;
import ca.gobits.dht.BDecoder;
import ca.gobits.dht.BEncoder;
import ca.gobits.dht.DHTConversion;

/**
 * DHTProtocolHandler  implementation of the BitTorrent protocol.
 * http://www.bittorrent.org/beps/bep_0005.html
 */
public class DHTProtocolHandler {

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

    /** DHTQuery Protocol instance. */
    @Autowired
    private DHTQueryProtocol queryProtocol;

    /** DHTServerConfig. */
    @Autowired
    private DHTServerConfig config;

    /** DHTNode Discovery Engine. */
    @Autowired
    private DHTNodeDiscovery discovery;

    /**
     * Read DatagramPacket.
     * @param packet  packet
     * @return byte[]
     * @throws IOException  IOException
     */
    public byte[] handle(final DatagramPacket packet) throws IOException {

        byte[] bytes = null;

        Map<String, Object> response = new HashMap<String, Object>();

        try {

            Map<String, Object> request = bdecode(packet.getData());

            if (isValid(request)) {

                 if (request.containsKey("q")) {
                    bytes = queryRequestHandler(packet, request);
                } else {
                    queryResponseHandler(packet, request);
                }
            }

        } catch (Exception e) {

            LOGGER.fatal(e, e);
            addServerError(response);
            bytes = bencode(response);
        }

        return bytes;
    }

    /**
     * Whether Request is valid.
     * @param request  Map<String, Object>
     * @return boolean
     */
    private boolean isValid(final Map<String, Object> request) {

        return request.containsKey("t")
                && request.containsKey("y");
    }

    /**
     * Handles DHT Query Response.
     * @param packet  DatagramPacket
     * @param request Map<String, Object>
     */
    private void queryResponseHandler(final DatagramPacket packet,
        final Map<String, Object> request) {

        if (request.containsKey("r")) {

            @SuppressWarnings("unchecked")
            Map<String, Object> request1 =
                (Map<String, Object>) request.get("r");

            if (request1.containsKey("id")) {

                byte[] id = (byte[]) request1.get("id");

                String transId = new String((byte[]) request.get("t"));
                if (this.tokenTable.isValidTransactionId(transId)) {

                    DHTNode node = this.routingTable.findExactNode(id);

                    if (node != null) {
                        node.setState(State.GOOD);
                    } else {
                        this.routingTable.addNode(id, packet.getAddress(),
                            packet.getPort(), State.GOOD);
                    }
                }
            }
        }
    }

    /**
     * Updates DHTNode's state to Good if exists in RoutingTable
     * or Sends Ping Request.
     * @param infohash  infohash
     * @param addr  InetAddress
     * @param port  port
     */
    private void updateNodeStatusOrPing(final byte[] infohash,
            final InetAddress addr, final int port) {

        DHTNode node = this.routingTable.findExactNode(infohash);

        if (node != null) {
            node.setState(State.GOOD);
        } else {
            this.discovery.addNode(addr, port);
        }
    }

    /**
     * Creates response from DHT Query request.
     * @param packet  DatagramPacket
     * @param request Map<String, Object>
     * @return byte[]
     */
    private byte[] queryRequestHandler(final DatagramPacket packet,
            final Map<String, Object> request) {

        Map<String, Object> response = new HashMap<String, Object>();

        try {

            String action = new String((byte[]) request.get("q"));

            InetAddress addr = packet.getAddress();
            int port = packet.getPort();

            response.put("y", "r");
            response.put("t", request.get("t"));
            response.put("ip",
                    DHTConversion.compactAddress(addr.getAddress(), port));

            @SuppressWarnings("unchecked")
            DHTArgumentRequest arguments = new DHTArgumentRequest(
                    (Map<String, Object>) request.get("a"));

            updateNodeStatusOrPing(arguments.getId(), addr, port);

            if (action.equals("ping")) {

                addPingResponse(arguments, response, packet);

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
            LOGGER.debug(e, e);
            addServerError(response);
        }

        return bencode(response);
    }

    /**
     * BEncodes response.
     * @param response  Map<String, Object>
     * @return byte[]
     */
    private byte[] bencode(final Map<String, Object> response) {
        byte[] bytes = BEncoder.bencoding(response);
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

        if (this.tokenTable.valid(packet.getAddress(), port,
                arguments.getToken())) {

            byte[] infoHash = arguments.getInfoHash();

            InetAddress addr = packet.getAddress();
            byte[] address = addr.getAddress();

            this.peerRoutingTable.addPeer(infoHash, address, port);

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

        byte[] infoHash = arguments.getInfoHash();

        Collection<DHTPeer> peers = this.peerRoutingTable.findPeers(infoHash);

        if (!CollectionUtils.isEmpty(peers)) {

            List<byte[]> bytes = toByteArrayFromDHTPeer(peers);
            responseParameter.put("values", bytes);

        } else {

            List<DHTNode> nodes = this.routingTable.findClosestNodes(infoHash);

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

        response.put("r", map("id", this.config.getNodeId()));
    }

    /**
     * Find the closest X nodes to the target.
     * @param targetBytes  ID of the target node to find
     * @return List<DHTNode>
     */
    private List<DHTNode> findClosestNodes(final byte[] targetBytes) {
        return this.routingTable.findClosestNodes(targetBytes);
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

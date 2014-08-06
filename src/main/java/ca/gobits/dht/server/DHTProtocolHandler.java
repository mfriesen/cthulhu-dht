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

package ca.gobits.dht.server;

import static ca.gobits.dht.util.DHTConversion.compactAddress;
import static ca.gobits.dht.util.DHTConversion.toByteArrayFromDHTNode;
import static ca.gobits.dht.util.DHTConversion.toByteArrayFromDHTPeer;
import static ca.gobits.dht.util.DHTConversion.toDHTNode;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;

import ca.gobits.dht.DHTInfoHashRoutingTable;
import ca.gobits.dht.DHTNode;
import ca.gobits.dht.DHTNodeRoutingTable;
import ca.gobits.dht.DHTPeer;
import ca.gobits.dht.bencoding.BDecoder;
import ca.gobits.dht.bencoding.BEncoder;
import ca.gobits.dht.server.DHTParameters.DHTQueryType;
import ca.gobits.dht.server.queue.DHTNodeStatusQueue;
import ca.gobits.dht.server.queue.DHTPingQueue;
import ca.gobits.dht.server.queue.DHTTokenQueue;

/**
 * DHTProtocolHandler implementation of the BitTorrent protocol.
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
    private DHTTokenQueue tokenTable;

    /** DHTServerConfig. */
    @Autowired
    private DHTServerConfig config;

    /** DHTPingQueue instance. */
    @Autowired
    private DHTPingQueue pingQueue;

    /** DHTNodeStatusQueue instance. */
    @Autowired
    private DHTNodeStatusQueue statusQueue;

    /**
     * Read DatagramPacket.
     *
     * @param packet DatagramPacket
     * @return byte[]
     */
    public byte[] handle(final DatagramPacket packet) {

        byte[] bytes = null;

        InetAddress addr = packet.getAddress();
        Map<String, Object> response = new HashMap<String, Object>();

        try {

            Map<String, Object> request = bdecode(packet.getData());
            DHTParameters params = new DHTParameters(addr, request);

            boolean valid = isValid(params);

            if (valid) {

                if (params.isQuery()) {

                    valid = isValidQuery(params);

                    if (valid) {

                        LOGGER.info("received valid query from "
                            + addr.getHostAddress() + ":" + packet.getPort());
                        bytes = queryRequestHandler(packet, params);
                    }

                } else {

                    valid = isValidResponse(params);

                    if (valid) {

                        LOGGER.info("received valid response from "
                            + addr.getHostAddress() + ":" + packet.getPort());
                        queryResponseHandler(packet, params);
                    }
                }
            }

            if (!valid) {

                bytes = handleInvalidParameters(response, params);

                LOGGER.info("received INVALID request/response from "
                        + addr.getHostAddress() + ":" + packet.getPort());
            }

        } catch (Exception e) {

            LOGGER.fatal(e, e);
            addServerError(response);
            bytes = bencode(response);
        }

        return bytes;
    }

    /**
     * Handle Invalid Parameters and determine error message.
     * @param response Map<String, Object>
     * @param params DHTParameters
     * @return byte[]
     */
    private byte[] handleInvalidParameters(final Map<String, Object> response,
            final DHTParameters params) {

        if (params.getT() != null) {
            response.put("t", params.getT());
        }

        if (params.getQueryType() == null) {
            addMethodUnknownResponse(response);
        } else {
            addInvalidArguementsErrorResponse(response);
        }

        return bencode(response);
    }

    /**
     * Whether Request is valid.
     *
     * @param params DHTParameters
     * @return boolean
     */
    private boolean isValid(final DHTParameters params) {

        return params.getT() != null && params.getY() != null;
    }

    /**
     * Is Query Request Valid.
     * @param params DHTParameters
     * @return boolean
     */
    private boolean isValidQuery(final DHTParameters params) {
        return params.getT() != null && params.getY() != null
                && isQValid(params);
    }

    /**
     * Is Q parameter valid.
     * @param params DHTParameters
     * @return boolean
     */
    private boolean isQValid(final DHTParameters params) {
        return params.getQueryType() != null;
    }

    /**
     * Is Query Response Valid.
     * @param params DHTParameters
     * @return boolean
     */
    private boolean isValidResponse(final DHTParameters params) {
        return params.getT() != null && params.getY() != null;
    }

    /**
     * Handles DHT Query Response.
     *
     * @param packet DatagramPacket
     * @param params DHTParameters
     */
    private void queryResponseHandler(final DatagramPacket packet,
            final DHTParameters params) {

        byte[] id = params.getId();
        boolean ipv6 = params.isIpv6();

        if (params.getNodes() != null || params.getNodes6() != null) {

            this.statusQueue.receivedFindNodeResponse(id,
                    packet.getAddress(), packet.getPort(), ipv6);

            // find_nodes ipv4
            if (params.getNodes() != null) {
                Collection<DHTNode> nodes = toDHTNode(params.getNodes(), false);
                LOGGER.info("adding " + nodes.size() + " to discovery");
                addToDiscovery(nodes);
            }

            // find_nodes ipv6
            if (params.getNodes6() != null) {
                Collection<DHTNode> nodes = toDHTNode(params.getNodes6(), true);
                LOGGER.info("adding " + nodes.size() + " to discovery");
                addToDiscovery(nodes);
            }

        } else {

            this.statusQueue.updateExistingNodeToGood(id, ipv6);
        }
    }

    /**
     * Adds nodes to be discovery list.
     *
     * @param nodes Collection of DHTNodes
     */
    private void addToDiscovery(final Collection<DHTNode> nodes) {

        for (DHTNode node : nodes) {

            int port = node.getPort();

            InetAddress address = node.getAddress();
            this.pingQueue.pingWithDelay(address, port);
        }
    }

    /**
     * Creates response from DHT Query request.
     *
     * @param packet DatagramPacket
     * @param params DHTParameters
     * @return byte[]
     * @throws IOException IOException
     */
    private byte[] queryRequestHandler(final DatagramPacket packet,
            final DHTParameters params) throws IOException {

        Map<String, Object> response = new HashMap<String, Object>();

        DHTQueryType qt = params.getQueryType();

        InetAddress addr = packet.getAddress();
        int port = packet.getPort();

        response.put("y", "r");
        response.put("t", params.getT());
        response.put("ip", compactAddress(addr.getAddress(), port));

        this.statusQueue.updateExistingNodeToGood(params.getId(),
                params.isIpv6());

        if (DHTQueryType.PING == qt) {

            addPingResponse(response);

        } else if (DHTQueryType.FIND_NODE == qt) {

            addFindNodeResponse(params, response, packet);

        } else if (DHTQueryType.GET_PEERS == qt) {

            addGetPeersResponse(params, response, packet);

        } else if (DHTQueryType.ANNOUNCE_PEER == qt) {

            addAnnouncePeerResponse(params, response, packet);
        }

        return bencode(response);
    }

    /**
     * BEncodes response.
     *
     * @param response
     *            Map<String, Object>
     * @return byte[]
     */
    private byte[] bencode(final Map<String, Object> response) {
        byte[] bytes = BEncoder.bencoding(response);
        return bytes;
    }

    /**
     * Announces that a peer has joined an InfoHash.
     *
     * @param params DHTParameters
     * @param response Map<String, Object>
     * @param packet DatagramPacket
     * @throws UnknownHostException
     *             UnknownHostException
     */
    private void addAnnouncePeerResponse(final DHTParameters params,
            final Map<String, Object> response, final DatagramPacket packet)
            throws UnknownHostException {

        int port = isImpliedPort(params) ? packet.getPort() : params
                .getPort().intValue();

        if (this.tokenTable.valid(packet.getAddress(), port,
                params.getToken())) {

            byte[] infoHash = params.getInfoHash();

            InetAddress addr = packet.getAddress();
            byte[] address = addr.getAddress();

            this.peerRoutingTable.addPeer(infoHash, address, port);

            Map<String, Object> rp = new HashMap<String, Object>();
            rp.put("id", params.getInfoHash());
            response.put("r", rp);

        } else {

            response.put("y", "e");
            response.put("r", map("203", "Bad Token"));
        }
    }

    /**
     * Is "implied_port" value is set and non-zero, the port argument should be
     * ignored and the source port of the UDP packet should be used as the
     * peer's port instead.
     *
     * @param params DHTParameters
     * @return boolean
     */
    private boolean isImpliedPort(final DHTParameters params) {
        Long impliedPort = params.getImpliedPort();
        return impliedPort != null && impliedPort.intValue() > 0;
    }

    /**
     * Get peers associated with a torrent infohash. If the queried node has no
     * peers for the infohash, a key "nodes" is returned containing the K nodes
     * in the queried nodes routing table closest to the infohash supplied in
     * the query.
     *
     * @param params DHTParameters
     * @param response Map<String, Object>
     * @param packet DatagramPacket
     * @throws IOException IOException
     */
    private void addGetPeersResponse(final DHTParameters params,
            final Map<String, Object> response, final DatagramPacket packet)
            throws IOException {

        boolean isIPv6 = params.isIpv6();
        Map<String, Object> responseParameter = new HashMap<String, Object>();

        byte[] infoHash = params.getInfoHash();

        Collection<DHTPeer> peers = this.peerRoutingTable.findPeers(infoHash);

        if (!CollectionUtils.isEmpty(peers)) {

            List<byte[]> bytes = toByteArrayFromDHTPeer(peers);
            responseParameter.put("values", bytes);

        } else {

            if (isIPv6) {
                List<DHTNode> nodes = this.routingTable.findClosestNodes(
                        infoHash, true);

                byte[] transformNodes = toByteArrayFromDHTNode(nodes, true);

                responseParameter.put("nodes6", transformNodes);
            }

            if (params.isIpv4()) {
                List<DHTNode> nodes = this.routingTable.findClosestNodes(
                        infoHash, false);

                byte[] transformNodes = toByteArrayFromDHTNode(nodes, false);

                responseParameter.put("nodes", transformNodes);
            }
        }

        responseParameter.put("token", generateToken());
        responseParameter.put("id", params.getId());

        response.put("r", responseParameter);
    }

    /**
     * Add Method Unknown Error to response.
     *
     * @param response Map<String, Object>
     */
    private void addMethodUnknownResponse(final Map<String, Object> response) {
        addServerError(response);
        response.put("r", map("204", "Method Unknown"));
    }

    /**
     * Add Invalid arguements Error to response.
     *
     * @param response Map<String, Object>
     */
    private void addInvalidArguementsErrorResponse(
            final Map<String, Object> response) {
        addServerError(response);
        response.put("r", map("203", "invalid arguements"));
    }

    /**
     * Add "find_node" data to response.
     *
     * @param params DHTParameters
     * @param response Map<String, Object>
     * @param packet DatagramPacket
     * @throws IOException IOException
     */
    private void addFindNodeResponse(final DHTParameters params,
            final Map<String, Object> response, final DatagramPacket packet)
            throws IOException {

        boolean isIPv6 = params.isIpv6();
        Map<String, Object> responseParameter = new HashMap<String, Object>();
        response.put("r", responseParameter);
        responseParameter.put("id", params.getId());

        List<DHTNode> nodes = findClosestNodes(params.getTarget(), isIPv6);

        byte[] transformNodes = toByteArrayFromDHTNode(nodes, isIPv6);
        responseParameter.put(isIPv6 ? "nodes6" : "nodes", transformNodes);
    }

    /**
     * Add "ping" data to response.
     *
     * @param response Map<String, Object>
     */
    private void addPingResponse(final Map<String, Object> response) {

        response.put("r", map("id", this.config.getNodeId()));
    }

    /**
     * Find the closest X nodes to the target.
     *
     * @param targetBytes
     *            ID of the target node to find
     * @param isIPv6
     *            Is request an IPv6
     * @return List<DHTNode>
     */
    private List<DHTNode> findClosestNodes(final byte[] targetBytes,
            final boolean isIPv6) {
        return this.routingTable.findClosestNodes(targetBytes, isIPv6);
    }

    /**
     * Create Map.
     *
     * @param key
     *            map key
     * @param value
     *            map value
     * @return Map<String, Object>
     */
    private Map<String, Object> map(final String key, final Object value) {
        Map<String, Object> map = new HashMap<String, Object>(1);
        map.put(key, value);
        return map;
    }

    /**
     * Add server error to Map object.
     *
     * @param map
     *            Map<String, Object>
     */
    private void addServerError(final Map<String, Object> map) {
        map.put("y", "e");
        map.put("r", map("202", "Server Error"));
    }

    /**
     * Extract Byte Array using BDecoder.
     *
     * @param bytes
     *            bytes
     * @return Map<String, Object>
     */
    @SuppressWarnings("unchecked")
    private Map<String, Object> bdecode(final byte[] bytes) {

        Map<String, Object> map = (Map<String, Object>) new BDecoder()
                .decode(bytes);
        return map;
    }

    /**
     * Token returned to a get_peers request. This is to prevent malicious hosts
     * from signing up other hosts
     *
     * @return byte[]
     */
    private byte[] generateToken() {
        byte[] bytes = new byte[GET_PEERS_TOKEN_LENGTH];
        Random random = new Random();
        random.nextBytes(bytes);
        return bytes;
    }
}

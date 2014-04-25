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

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.socket.DatagramPacket;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.net.InetSocketAddress;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import ca.gobits.dht.Arrays;
import ca.gobits.dht.BDecoder;
import ca.gobits.dht.BEncoder;
import ca.gobits.dht.DHTIdentifier;

/**
 * DHTProtocolHandler  implementation of the BitTorrent protocol.
 * http://www.bittorrent.org/beps/bep_0005.html
 */
@Service
public final class DHTProtocolHandler extends
        SimpleChannelInboundHandler<DatagramPacket> {

    /** Length of Get_Peers token. */
    private static final int GET_PEERS_TOKEN_LENGTH = 10;

    /** Contact information for nodes is encoded as a 26-byte string. */
    private static final int COMPACT_NODE_LENGTH = 26;

    /** DHTProtocolHandler Logger. */
    private static final Logger LOGGER = Logger
            .getLogger(DHTProtocolHandler.class);

    /** DHT Node Routing Table. */
    @Autowired
    private DHTNodeRoutingTable routingTable;

    /** DHT Peer Routing Table. */
    @Autowired
    private DHTInfoHashRoutingTable peerRoutingTable;

    @Override
    public void channelRead0(final ChannelHandlerContext ctx,
            final DatagramPacket packet)
            throws Exception {

        Map<String, Object> response = new HashMap<String, Object>();

        try {

            Map<String, Object> request = bdecode(packet.content());

            response.put("y", "r");
            response.put("t", request.get("t"));

            String action = new String((byte[]) request.get("q"));

            if (action.equals("ping")) {

                addPingResponse(request, response, packet);

            } else if (action.equals("find_node")) {

                addFindNodeResponse(request, response, packet);

            } else if (action.equals("get_peers")) {

                addGetPeersResponse(request, response, packet);

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

        ctx.write(new DatagramPacket(
                Unpooled.copiedBuffer(bytes), packet.sender()));
    }

    /**
     * Get peers associated with a torrent infohash. If the queried node has no
     * peers for the infohash, a key "nodes" is returned containing the K nodes
     * in the queried nodes routing table closest to the infohash supplied in
     * the query.
     *
     * @param request  Map<String, Object>
     * @param response  Map<String, Object>
     * @param packet  DatagramPacket
     * @throws IOException  IOException
     */
    private void addGetPeersResponse(final Map<String, Object> request,
        final Map<String, Object> response, final DatagramPacket packet)
            throws IOException {

        Map<String, Object> responseParameter = new HashMap<String, Object>();

        @SuppressWarnings("unchecked")
        Map<String, Object> arguments = (Map<String, Object>) request.get("a");
        BigInteger infoHash = Arrays.toBigInteger((byte[]) arguments
                .get("info_hash"));

        Collection<byte[]> peers = peerRoutingTable.findPeers(infoHash);
        if (!CollectionUtils.isEmpty(peers)) {
            responseParameter.put("values", peers);
        } else {

            List<DHTNode> nodes = routingTable.findClosestNodes(infoHash);
            byte[] transformNodes = transformNodes(nodes);
            responseParameter.put("nodes", transformNodes);
        }

        responseParameter.put("token", generateToken(packet.sender()));
        responseParameter.put("id", arguments.get("id"));

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
     * @param request  request parameters
     * @param response  Map<String, Object>
     * @param packet  DatagramPacket
     * @throws IOException  IOException
     */
    private void addFindNodeResponse(final Map<String, Object> request,
            final Map<String, Object> response,
            final DatagramPacket packet) throws IOException {

        addSenderIpResponse(response, packet);

        @SuppressWarnings("unchecked")
        Map<String, Object> arguments = (Map<String, Object>) request.get("a");

        Map<String, Object> responseParameter = new HashMap<String, Object>();
        response.put("r", responseParameter);
        responseParameter.put("id", arguments.get("id"));

        List<DHTNode> nodes = findClosestNodes(
                (byte[]) arguments.get("target"));

        byte[] transformNodes = transformNodes(nodes);
        responseParameter.put("nodes", transformNodes);
    }

    /**
     * Adds Send's IP to response.
     * @param response  Map<String, Object>
     * @param packet DatagramPacket
     */
    private void addSenderIpResponse(final Map<String, Object> response,
            final DatagramPacket packet) {
        InetSocketAddress addr = packet.sender();
        response.put(
                "ip", BEncoder.compactAddress(addr.getAddress().getAddress(),
                        addr.getPort()));
    }

    /**
     * Add "ping" data to response.
     * @param request  Map<String, Object>
     * @param response  Map<String, Object>
     * @param packet DatagramPacket
     */
    private void addPingResponse(final Map<String, Object> request,
            final Map<String, Object> response,
            final DatagramPacket packet) {

        @SuppressWarnings("unchecked")
        Map<String, Object> arguments = (Map<String, Object>) request.get("a");

        byte[] id = (byte[]) arguments.get("id");
        ping(Arrays.toBigInteger(id), packet.sender());

        response.put("r", map("id", DHTServer.NODE_ID));
    }

    /**
     * Ping a node in the routing table.
     * @param nodeId the nodeId to ping
     * @param addr sender of the ping
     */
    private void ping(final BigInteger nodeId, final InetSocketAddress addr) {
        DHTNode node = routingTable.findExactNode(nodeId);
        if (node == null) {
            node = new DHTNode(nodeId, addr.getAddress().getAddress(),
                    addr.getPort());
            routingTable.addNode(node);
        }

        node.setLastUpdated(new Date());
    }

    /**
     * Transforms Nodes to "compact node info" mode.
     * @param nodes  Collection of DHTNode objects
     * @return byte[]
     * @throws IOException  IOException
     */
    private byte[] transformNodes(final Collection<DHTNode> nodes)
            throws IOException {

        byte[] bytes = new byte[nodes.size() * COMPACT_NODE_LENGTH];

        int pos = 0;
        for (DHTNode node : nodes) {
            byte[] nodeBytes = transform(node);
            System.arraycopy(nodeBytes, 0, bytes, pos, nodeBytes.length);
            pos += nodeBytes.length;
        }

        return bytes;
    }

    /**
     * Find the closest X nodes to the target.
     * @param targetBytes  ID of the target node to find
     * @return List<DHTNode>
     */
    private List<DHTNode> findClosestNodes(final byte[] targetBytes) {

        BigInteger target = Arrays.toBigInteger(targetBytes);
        return routingTable.findClosestNodes(target);
    }

    /**
     * Transform DHTNode into 26-byte string. Known as "Compact node info" the
     * 20-byte Node ID in network byte order has the compact IP-address/port
     * info concatenated to the end.
     *
     * @param node  DHTNode
     * @return byte[]
     * @throws IOException  IOException
     */
    private byte[] transform(final DHTNode node) throws IOException {

        int max = DHTIdentifier.NODE_ID_LENGTH;
        byte[] bytes = new byte[COMPACT_NODE_LENGTH];
        byte[] id = Arrays.toByte(node.getId());

        int start = id.length > max ? id.length - max : 0;
        int len = id.length > max ? max : id.length;
        int destPos = max > id.length ? max - id.length : 0;
        System.arraycopy(id, start, bytes, destPos, len);

        byte[] addrBytes = BEncoder.compactAddress(node.getAddress(),
                node.getPort());

        System.arraycopy(addrBytes, 0, bytes, max, addrBytes.length);

        return bytes;
    }

    @Override
    public void channelReadComplete(final ChannelHandlerContext ctx)
            throws Exception {
        ctx.flush();
    }

    /**
     * Extract bytes from ByteBuf.
     * @param msg  ByteBuf
     * @return ByteArrayOutputStream
     */
    public ByteArrayOutputStream extractBytes(final ByteBuf msg) {
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        ByteBuf in = msg;
        while (in.isReadable()) {
            byte b = in.readByte();
            os.write(b);
        }
        return os;
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
     * @param byteBuf  ByteBuf
     * @return Map<String, Object>
     * @throws IOException  IOException
     */
    @SuppressWarnings("unchecked")
    private Map<String, Object> bdecode(final ByteBuf byteBuf)
            throws IOException {

        ByteArrayOutputStream os = extractBytes(byteBuf);
        Map<String, Object> map = (Map<String, Object>) new BDecoder()
                .decode(os.toByteArray());
        os.close();
        return map;
    }

    /**
     * Token returned to a get_peers request.
     * This is to prevent malicious hosts from signing up other hosts
     * @param inetSocketAddress address of sender
     * @return byte[]
     */
    private byte[] generateToken(final InetSocketAddress inetSocketAddress) {
        byte[] bytes = new byte[GET_PEERS_TOKEN_LENGTH];
        Random random = new Random();
        random.nextBytes(bytes);
        return bytes;
    }
}

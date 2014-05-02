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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;

import ca.gobits.dht.Arrays;
import ca.gobits.dht.BDecoder;
import ca.gobits.dht.BEncoder;

/**
 * DHTProtocolHandler  implementation of the BitTorrent protocol.
 * http://www.bittorrent.org/beps/bep_0005.html
 */
public final class DHTProtocolHandler extends
        SimpleChannelInboundHandler<DatagramPacket> {

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

    @Override
    public void channelRead0(final ChannelHandlerContext ctx,
            final DatagramPacket packet)
            throws Exception {

        Map<String, Object> response = new HashMap<String, Object>();

        try {
            InetSocketAddress addr = packet.sender();
            Map<String, Object> request = bdecode(packet.content());

            response.put("y", "r");
            response.put("t", request.get("t"));
            response.put("ip",
                    BEncoder.compactAddress(addr.getAddress().getAddress(),
                            addr.getPort()));

            String action = new String((byte[]) request.get("q"));

            @SuppressWarnings("unchecked")
            DHTArgumentRequest arguments = new DHTArgumentRequest(
                    (Map<String, Object>) request.get("a"));

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
     * Announces that a peer has joined an InfoHash.
     * @param arguments  DHTArgumentRequest
     * @param response  Map<String, Object>
     * @param packet  DatagramPacket
     */
    private void addAnnouncePeerResponse(final DHTArgumentRequest arguments,
            final Map<String, Object> response, final DatagramPacket packet) {

        if (tokenTable.valid(packet.sender(), arguments.getToken())) {

            BigInteger infoHash = Arrays.toBigInteger(arguments.getInfoHash());

            InetSocketAddress addr = packet.sender();
            byte[] address = addr.getAddress().getAddress();
            int port = isImpliedPort(arguments) ? addr.getPort()
                    : arguments.getPort().intValue();

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

        BigInteger infoHash = Arrays.toBigInteger(arguments.getInfoHash());

        Collection<byte[]> peers = peerRoutingTable.findPeers(infoHash);

        if (!CollectionUtils.isEmpty(peers)) {

            responseParameter.put("values", peers);

        } else {

            List<DHTNode> nodes = routingTable.findClosestNodes(infoHash);

            byte[] transformNodes = Arrays.toByteArray(nodes);
            responseParameter.put("nodes", transformNodes);
        }

        responseParameter.put("token", generateToken(packet.sender()));
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

        byte[] transformNodes = Arrays.toByteArray(nodes);
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

        BigInteger target = Arrays.toBigInteger(targetBytes);
        return routingTable.findClosestNodes(target);
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

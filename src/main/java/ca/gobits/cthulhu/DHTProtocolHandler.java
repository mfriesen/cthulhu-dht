package ca.gobits.cthulhu;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.socket.DatagramPacket;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import ca.gobits.dht.Arrays;
import ca.gobits.dht.BDecoder;
import ca.gobits.dht.BEncoder;
import ca.gobits.dht.DHTIdentifier;

/**
 * DHTProtocolHandler  implementation of the BitTorrent protocol.
 * http://www.bittorrent.org/beps/bep_0005.html
 */
public final class DHTProtocolHandler extends
        SimpleChannelInboundHandler<DatagramPacket> {

    /** Contact information for nodes is encoded as a 26-byte string. */
    private static final int COMPACT_NODE_LENGTH = 26;

    /** DHTProtocolHandler Logger. */
    private static final Logger LOGGER = Logger
            .getLogger(DHTProtocolHandler.class);

    /** DHT Node Routing Table. */
    private final DHTRoutingTable routingTable = new DHTBucketRoutingTable();

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

                addPingResponse(response);

            } else if (action.equals("find_node")) {

                addFindNodeResponse(request, response, packet);

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
                "ip", BEncoder.compactAddress(addr.getAddress(),
                        addr.getPort()));
    }

    /**
     * Add "ping" data to response.
     * @param response  Map<String, Object>
     */
    private void addPingResponse(final Map<String, Object> response) {
        response.put("r", map("id", DHTServer.NODE_ID));
    }

    /**
     * Transforms Nodes to "compact node info" mode.
     * @param nodes  List of DHTNode objects
     * @return byte[]
     * @throws IOException  IOException
     */
    private byte[] transformNodes(final List<DHTNode> nodes)
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

        InetAddress addr = InetAddress.getByName(node.getHost());
        byte[] addrBytes = BEncoder.compactAddress(addr, node.getPort());

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
}

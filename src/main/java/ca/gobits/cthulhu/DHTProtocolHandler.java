package ca.gobits.cthulhu;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.socket.DatagramPacket;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import ca.gobits.dht.BDecoder;
import ca.gobits.dht.BEncoder;

/**
 * DHTProtocolHandler  implementation of the BitTorrent protocol.
 * http://www.bittorrent.org/beps/bep_0005.html
 */
public final class DHTProtocolHandler extends
        SimpleChannelInboundHandler<DatagramPacket> {

    /** DHTProtocolHandler Logger. */
    private static final Logger LOGGER = Logger
            .getLogger(DHTProtocolHandler.class);

    @Override
    public void channelRead0(final ChannelHandlerContext ctx,
            final DatagramPacket packet)
            throws Exception {

        Map<String, Object> response = new HashMap<String, Object>();

        try {

            Map<String, Object> map = bdecode(packet.content());

            String action = (String) map.get("q");

            response.put("y", "r");
            response.put("t", map.get("t"));

            if (action.equals("ping")) {
                response.put("r", map("id", DHTServer.NODE_ID));
            } else {
                addServerError(response);
                response.put("r", map("204", "Method Unknown"));
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
    private ByteArrayOutputStream extractBytes(final ByteBuf msg) {
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
                .decode(new String(os.toByteArray()));
        os.close();
        return map;
    }
}

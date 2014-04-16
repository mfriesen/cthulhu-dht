package ca.gobits.cthulhu;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.ReferenceCountUtil;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import ca.gobits.dht.BDecoder;
import ca.gobits.dht.BEncoder;

/**
 * DHTProtocolHandler  implementation of the BitTorrent protocol.
 * http://www.bittorrent.org/beps/bep_0005.html
 */
public final class DHTProtocolHandler extends ChannelInboundHandlerAdapter {

    /** DHTProtocolHandler Logger. */
    private static final Logger LOGGER = Logger
            .getLogger(DHTProtocolHandler.class);

    @Override
    public void channelRead(final ChannelHandlerContext ctx, final Object msg)
            throws Exception {

        Map<String, Object> response = new HashMap<String, Object>();

        try {
            ByteArrayOutputStream os = extractBytes((ByteBuf) msg);

            Map<String, Object> map = extractMap(os);
            os.close();

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
              addServerError(response);

        } finally {

            ByteArrayOutputStream os = BEncoder.bencoding(response);
            byte[] bytes = os.toByteArray();

            final ByteBuf time = ctx.alloc().buffer(1024);
            time.writeBytes(bytes);

            final ChannelFuture f = ctx.writeAndFlush(time);
            f.addListener(ChannelFutureListener.CLOSE);

            os.close();
            ReferenceCountUtil.release(msg);
        }
    }

    @Override
    public void exceptionCaught(final ChannelHandlerContext ctx,
            final Throwable cause) throws Exception {

        LOGGER.fatal(cause, cause);

        Map<String, Object> response = new HashMap<String, Object>();
        addServerError(response);

        ChannelFuture cf = ctx.writeAndFlush(BEncoder.bencoding(response));
        cf.addListener(ChannelFutureListener.CLOSE);
        ctx.close();
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
     * @param os  ByteArrayOutputStream
     * @return Map<String, Object>
     */
    @SuppressWarnings("unchecked")
    private Map<String, Object> extractMap(final ByteArrayOutputStream os) {
        Map<String, Object> map = (Map<String, Object>) new BDecoder()
                .decode(new String(os.toByteArray()));
        return map;
    }
}

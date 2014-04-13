package ca.gobits.cthulhu;

import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import ca.gobits.dht.BDecoder;
import ca.gobits.dht.BEncoder;

/**
 * DHTProtocolHandler  implementation of the BitTorrent protocol.
 * http://www.bittorrent.org/beps/bep_0005.html
 */
public class DHTProtocolHandler extends SimpleChannelInboundHandler<String> {

    /** DHTProtocolHandler Logger. */
    private static final Logger LOGGER = Logger
            .getLogger(DHTProtocolHandler.class);

    @SuppressWarnings("unchecked")
    @Override
    public final void channelRead0(final ChannelHandlerContext ctx,
            final String msg) throws Exception {

        Map<String, Object> response = new HashMap<String, Object>();

        Map<String, Object> map = (Map<String, Object>)
            new BDecoder().decode(msg);

        try {
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
        }

        ChannelFuture cf = ctx.writeAndFlush(BEncoder.bencoding(response));
        cf.addListener(ChannelFutureListener.CLOSE);
        ctx.close();
    }

    @Override
    public final void exceptionCaught(final ChannelHandlerContext ctx,
            final Throwable cause) throws Exception {

        LOGGER.fatal(cause, cause);

        Map<String, Object> response = new HashMap<String, Object>();
        addServerError(response);

        ChannelFuture cf = ctx.writeAndFlush(BEncoder.bencoding(response));
        cf.addListener(ChannelFutureListener.CLOSE);
        ctx.close();
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
}

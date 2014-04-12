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

        Map<String, Object> map = (Map<String, Object>)
                new BDecoder().decode(msg);

        String action = (String) map.get("q");

        Map<String, Object> response = new HashMap<String, Object>();
        response.put("y", "r");
        response.put("t", map.get("t"));

        if (action.equals("ping")) {
            response.put("r", pingValues());
        } else {
            response.put("r", "unknown " + action);
        }

        ChannelFuture cf = ctx.writeAndFlush(BEncoder.bencoding(response));
        cf.addListener(ChannelFutureListener.CLOSE);
        ctx.close();
    }

    /**
     * Response Map from a Ping request.
     * @return Map<String, Object>
     */
    private Map<String, Object> pingValues() {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("id", DHTServer.NODE_ID);
        return map;
    }

    @Override
    public final void exceptionCaught(final ChannelHandlerContext ctx,
            final Throwable cause) throws Exception {
        LOGGER.fatal(cause, cause);
        ctx.close();
    }
}

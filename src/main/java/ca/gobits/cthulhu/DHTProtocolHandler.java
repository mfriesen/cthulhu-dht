package ca.gobits.cthulhu;

import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import org.apache.log4j.Logger;

/**
 * DHTProtocolHandler  implementation of the BitTorrent protocol.
 * http://www.bittorrent.org/beps/bep_0005.html
 */
public class DHTProtocolHandler extends SimpleChannelInboundHandler<String> {

    /** DHTProtocolHandler Logger. */
    private static final Logger LOGGER = Logger
            .getLogger(DHTProtocolHandler.class);

    @Override
    public final void channelRead0(final ChannelHandlerContext ctx,
            final String msg) throws Exception {
        LOGGER.info("GOT STIRNG " + msg);
        ChannelFuture cf = ctx.writeAndFlush("File not found:\n");
        cf.addListener(ChannelFutureListener.CLOSE);
        ctx.close();
    }

    @Override
    public final void exceptionCaught(final ChannelHandlerContext ctx,
            final Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }
}

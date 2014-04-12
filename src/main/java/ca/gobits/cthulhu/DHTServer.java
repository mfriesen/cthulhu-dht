package ca.gobits.cthulhu;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.LineBasedFrameDecoder;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.util.CharsetUtil;

import java.io.PrintWriter;

import org.apache.commons.cli.BasicParser;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.MissingArgumentException;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.UnrecognizedOptionException;
import org.apache.log4j.Logger;

/**
 * DHTServer implementation.
 */
public class DHTServer {

    /** Reference to DHTServer. */
    private static DHTServer server = null;

    /** the maximum length of the decoded frame. */
    private static final int LINE_BASED_MAX_LENGTH = 8192;

    /** DHT Server Logger. */
    private static final Logger LOGGER = Logger.getLogger(DHTServer.class);

    /** Default Port. */
    private static final int DEFAULT_PORT = 8080;

    /** SO_BACKLOG. */
    private static final int SO_BACKLOG = 100;

    /** Port to run server on. */
    private final int port;

    /** Main Event Loop. */
    private final EventLoopGroup bossGroup = new NioEventLoopGroup(1);

    /** Worker Event Loop. */
    private final EventLoopGroup workerGroup = new NioEventLoopGroup();

    /**
     * Constructor.
     * @param bindPort  bind to port
     */
    public DHTServer(final int bindPort) {
        this.port = bindPort;
    }

    /**
     * Run DHTServer.
     * @throws Exception  Exception
     */
    public final void run() throws Exception {

        try {
            ServerBootstrap b = new ServerBootstrap();
            b.group(bossGroup, workerGroup)
             .channel(NioServerSocketChannel.class)
             .option(ChannelOption.SO_BACKLOG, Integer.valueOf(SO_BACKLOG))
             .handler(new LoggingHandler(LogLevel.WARN))
             .childHandler(new ChannelInitializer<SocketChannel>() {
                 @Override
                 public void initChannel(final SocketChannel ch)
                         throws Exception {
                     ch.pipeline().addLast(
                             new StringEncoder(CharsetUtil.UTF_8),
                             new LineBasedFrameDecoder(LINE_BASED_MAX_LENGTH),
                             new StringDecoder(CharsetUtil.UTF_8),
                             new DHTProtocolHandler());
                 }
             });

            // Start the server.
            ChannelFuture f = b.bind(port).sync();

            // Wait until the server socket is closed.
            f.channel().closeFuture().sync();

        } finally {
            shutdownGracefully();
        }
    }

    /**
     * Shut down all event loops to terminate all threads.
     */
    public final void shutdownGracefully() {
        bossGroup.shutdownGracefully();
        workerGroup.shutdownGracefully();
    }

    /**
     * Mainline to run DHTServer.
     * @param args argument parameters.
     */
    public static void main(final String[] args) {

        Options options = new Options();
        options.addOption("p", true, "bind to port");
        options.addOption("?", false, "help");

        try {

            int port = DEFAULT_PORT;
            CommandLineParser parser = new BasicParser();

            CommandLine cmd = parser.parse(options, args);

            if (cmd.hasOption("?")) {
                showUsage(options);
            } else {

                if (cmd.hasOption("p")) {

                    String portStr = cmd.getOptionValue("p");
                    port = Integer.parseInt(portStr);
                }

                server = new DHTServer(port);
                server.run();

            }
        } catch (UnrecognizedOptionException e) {
            showUsage(options);
        } catch (MissingArgumentException e) {
            showUsage(options);
        } catch (Exception e) {
            LOGGER.fatal(e, e);
        }
    }

    /**
     * Shutsdown server.
     */
    public static void shutdown() {
        server.shutdownGracefully();
    }

    /**
     * Shows Usage Message.
     * @param options  Options command line arguments
     */
    private static void showUsage(final Options options) {

        PrintWriter writer = new PrintWriter(System.out);
        HelpFormatter usageFormatter = new HelpFormatter();
        usageFormatter.printHelp("java -jar dht.jar", "Parameters",
                options, "");
        writer.close();
    }
}

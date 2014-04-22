package ca.gobits.cthulhu;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioDatagramChannel;

import java.io.PrintWriter;

import org.apache.commons.cli.BasicParser;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.MissingArgumentException;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.UnrecognizedOptionException;
import org.apache.log4j.Logger;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.stereotype.Service;

import ca.gobits.dht.DHTIdentifier;

/**
 * DHTServer implementation.
 */
@Service
public class DHTServer {

    /** DHT Spring Context. */
    private static GenericApplicationContext ac;

    /** DHT Server Logger. */
    private static final Logger LOGGER = Logger.getLogger(DHTServer.class);

    /** Default Port. */
    public static final int DEFAULT_PORT = 8080;

    /** SO_BROADCAST. */
    private static final Boolean SO_BROADCAST = Boolean.valueOf(true);

    /** DHT Node Id. */
    public static final byte[] NODE_ID = DHTIdentifier.sha1(DHTServer.class
            .getName());

    /** Main Event Loop. */
    private final EventLoopGroup group = new NioEventLoopGroup();

    /**
     * Constructor.
     */
    public DHTServer() {
    }

    /**
     * Run DHTServer.
     * @param port port to run server on
     * @throws Exception  Exception
     */
    public final void run(final int port) throws Exception {

        LOGGER.info("starting cthulhu on " + port);

        try {
            Bootstrap b = new Bootstrap();
            b.group(group)
             .channel(NioDatagramChannel.class)
             .option(ChannelOption.SO_BROADCAST, SO_BROADCAST)
             .handler(new DHTProtocolHandler());

            b.bind(port).sync().channel().closeFuture().await();
        } finally {
            shutdownGracefully();
        }
    }

    /**
     * Shut down all event loops to terminate all threads.
     */
    public final void shutdownGracefully() {
        group.shutdownGracefully();
        ac.close();
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

                ac = new AnnotationConfigApplicationContext(
                        DHTConfiguration.class);

                DHTServer server = ac.getBean(DHTServer.class);
                server.run(port);

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
        DHTServer server = ac.getBean(DHTServer.class);
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

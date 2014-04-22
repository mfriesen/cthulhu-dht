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
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioDatagramChannel;

import java.io.PrintWriter;
import java.math.BigInteger;

import org.apache.commons.cli.BasicParser;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.MissingArgumentException;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.UnrecognizedOptionException;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
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

    /** DHT Protocol Handler. */
    @Autowired
    private DHTProtocolHandler dhtHandler;

    @Autowired
    private DHTRoutingTable routingTable;

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

        addExpectedNodesToRoutingTable();
        LOGGER.info("starting cthulhu on " + port);

        try {
            Bootstrap b = new Bootstrap();
            b.group(group)
             .channel(NioDatagramChannel.class)
             .option(ChannelOption.SO_BROADCAST, SO_BROADCAST)
             .handler(dhtHandler);

            b.bind(port).sync().channel().closeFuture().await();
        } finally {
            shutdownGracefully();
        }
    }

    private void addExpectedNodesToRoutingTable() {

        routingTable.addNode(new DHTNode(new BigInteger(
                "1025727453009050644114422909938179475956677673365"),
                "37.76.160.28", 37518));
        routingTable.addNode(new DHTNode(new BigInteger(
                "909396897490697132528408310795708133687135388426"),
                "182.59.176.199", 11503));
        routingTable.addNode(new DHTNode(new BigInteger(
                "525080541161122160152898021711579691652547262977"),
                "178.124.205.49", 16911));
        routingTable.addNode(new DHTNode(new BigInteger(
                "658070898018303575756492289276695009391046368980"),
                "5.13.218.214", 56116));
        routingTable.addNode(new DHTNode(new BigInteger(
                "732800403720670969048970409366815229228420735404"),
                "79.163.109.76", 29037));
        routingTable.addNode(new DHTNode(new BigInteger(
                "1256313872952230430598882201394466767467396215628"),
                "2.190.222.79", 58106));
        routingTable.addNode(new DHTNode(new BigInteger(
                "765028964801745612216665519019856689419949360586"),
                "92.237.93.69", 17271));
        routingTable.addNode(new DHTNode(new BigInteger(
                "304333486037502350876881646365121976203989590042"),
                "5.129.229.16", 21853));
        routingTable.addNode(new DHTNode(new BigInteger(
                "651043862618190073616414008555095633000553327254"),
                "67.166.50.31", 53162));
        routingTable.addNode(new DHTNode(new BigInteger(
                "217572328821850967755762913845138112465869557436"),
                "178.222.162.23", 18274));
        routingTable.addNode(new DHTNode(new BigInteger(
                "1235689258152504075304182876266224318368488950162"),
                "31.216.162.240", 20383));
        routingTable.addNode(new DHTNode(new BigInteger(
                "487762934236616301113020799412763967579181340675"),
                "31.181.56.194", 59935));
        routingTable.addNode(new DHTNode(new BigInteger(
                "757633304364519595494275276101980823332425611532"),
                "80.233.181.214", 12230));
        routingTable.addNode(new DHTNode(new BigInteger(
                "253718933283387888344146948372599275024431560999"),
                "79.22.67.76", 38518));
        routingTable.addNode(new DHTNode(new BigInteger(
                "890765994839177116145299793227790251293353534962"),
                "92.99.87.123", 26120));
        routingTable.addNode(new DHTNode(new BigInteger(
                "1123918148366576699094456176144333565208604527946"),
                "176.12.59.50", 61553));
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

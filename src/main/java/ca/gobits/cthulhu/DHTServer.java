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
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioDatagramChannel;

import java.io.PrintWriter;

import org.apache.commons.cli.HelpFormatter;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import ca.gobits.dht.DHTIdentifier;


/**
 * DHTServer implementation.
 */
public class DHTServer /*implements Lifecycle*/ {

    /** DHT Server Logger. */
    private static final Logger LOGGER = Logger.getLogger(DHTServer.class);

    /** SO_BROADCAST. */
    private static final Boolean SO_BROADCAST = Boolean.valueOf(true);

    /** DHT Node Id. */
    public static final byte[] NODE_ID = DHTIdentifier.sha1(DHTServer.class
            .getName());

    /** Main Event Loop. */
    private final EventLoopGroup bossGroup = new NioEventLoopGroup();

    /** DHT Protocol Handler. */
    @Autowired
    private DHTProtocolHandler dhtHandler;

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
    public void run(final int port) throws Exception {

        LOGGER.info("starting cthulhu on " + port);

        try {
            Bootstrap b = new Bootstrap();
            b.group(bossGroup)
             .channel(NioDatagramChannel.class)
             .option(ChannelOption.SO_BROADCAST, SO_BROADCAST)
             .handler(new ChannelInitializer<Channel>() {
                @Override
                protected void initChannel(final Channel ch) throws Exception {
                    ch.pipeline().addLast(dhtHandler);
                }
            });

            ChannelFuture cf = b.bind(port).sync().channel().closeFuture();
            cf.await();

        } finally {
            shutdownGracefully();
        }
    }

    /**
     * Gracefully shutdown server.
     */
    public final void shutdownGracefully() {
        bossGroup.shutdownGracefully();
    }

    /**
     * Mainline to run DHTServer.
     * @param args argument parameters.
     */
    public static void main(final String[] args) {
        ConfigurableApplicationContext ac =
            new AnnotationConfigApplicationContext(
                DHTConfiguration.class);

        try {
            main(args, ac);
        } finally {
            ac.close();
        }
    }

    /**
     * Mainline to run DHTServer.
     * @param args argument parameters.
     * @param ac ApplicationContext
     */
    public static void main(final String[] args,
            final ConfigurableApplicationContext ac) {

        DHTServerConfig config = new DHTServerConfig(args);

        if (config.isShowHelp()) {

            showUsage();

        } else {

            try {

                DHTServer server = ac.getBean(DHTServer.class);
                server.run(config.getPort());

            } catch (Exception e) {

                LOGGER.fatal(e, e);

            }
        }
    }

    /**
     * Shows Usage Message.
     */
    private static void showUsage() {

        PrintWriter writer = new PrintWriter(System.out);
        HelpFormatter usageFormatter = new HelpFormatter();
        usageFormatter.printHelp("java -jar dht.jar", "Parameters",
                DHTServerConfig.DHTSERVER_OPTIONS, "");
        writer.close();
    }
}

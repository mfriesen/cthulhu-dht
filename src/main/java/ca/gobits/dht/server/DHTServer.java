//
// Copyright 2014 Mike Friesen
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

package ca.gobits.dht.server;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

import org.apache.commons.codec.binary.Base64;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import ca.gobits.dht.server.queue.DHTFindNodeQueue;


/**
 * DHTServer implementation.
 */
public class DHTServer {

    /** DHT Server Logger. */
    private static final Logger LOGGER = Logger.getLogger(DHTServer.class);

    /** Receive Data UDP Length. */
    private static final int RECEIVE_DATA_LENGTH = 1024;

    /** DHT Protocol Handler. */
    @Autowired
    private DHTProtocolHandler dhtHandler;

    /** Thread Pool Executor. */
    @Autowired
    private ThreadPoolTaskExecutor socketThreadPool;

    /** Indicator whether to stop the server. */
    private boolean stop;

    /** Datagram Socket. */
    @Autowired
    private DatagramSocket serverSocket;

    /** DHTFindNodeQueue Handler. */
    @Autowired
    private DHTFindNodeQueue findNodeQueue;

    /** DHTServer Config. */
    @Autowired
    private DHTServerConfig config;

    /**
     * Constructor.
     */
    public DHTServer() {
    }

    /**
     * Starts the DHTServer.
     * @throws Exception  Exception
     */
    public void start() throws Exception {

        setLoggingLevels();

        LOGGER.info("starting cthulhu on " + this.serverSocket.getLocalPort()
                + " with ID "
                + Base64.encodeBase64String(this.config.getNodeId()));

        try {

            bootstrap();

            receive();

        } finally {
            shutdownGracefully();
        }
    }

    /**
     * Starts receiving packets and processing them.
     * @throws IOException  IOException
     */
    private void receive() throws IOException {

        byte[] receiveData = new byte[RECEIVE_DATA_LENGTH];

        while (true) {

            DatagramPacket receivePacket = new DatagramPacket(
                    receiveData, receiveData.length);

            this.serverSocket.receive(receivePacket);

            this.socketThreadPool.execute(new DHTProtocolRunnable(
                this.serverSocket, this.dhtHandler, receivePacket));

            if (this.stop) {
                break;
            }
        }
    }

    /**
     * Sets the DHTServer logging levels.
     */
    private void setLoggingLevels() {
        Level level = this.config.getLogLevel();
        Logger.getRootLogger().setLevel(level);
        Logger.getLogger("ca.gobits").setLevel(level);
        LOGGER.debug("setting logging level to " + level);
    }

    /**
     * Bootstrap Server with nodes.
     */
    private void bootstrap() {

        String[] nodes = this.config.getBootstrapNodes();

        if (nodes != null) {

            for (String node : nodes) {

                LOGGER.info("bootstrapping server with " + node);

                try {

                    String[] addrPort = node.split(":");
                    InetAddress addr = InetAddress.getByName(addrPort[0]);
                    int port = Integer.valueOf(addrPort[1]).intValue();

                    this.findNodeQueue.findNodesWithDelay(addr, port,
                            this.config.getNodeId());

                } catch (Exception e) {
                    LOGGER.warn("unable to bootstrap " + node + ".", e);
                }
            }
        }
    }

    /**
     * Gracefully shutdown server.
     */
    public final void shutdownGracefully() {
        LOGGER.info("shutdown cthulhu");

        this.stop = true;
        this.socketThreadPool.shutdown();

        if (this.serverSocket != null) {
            this.serverSocket.close();
        }
    }
}

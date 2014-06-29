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

package ca.gobits.cthulhu;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import ca.gobits.cthulhu.discovery.DHTNodeDiscovery;


/**
 * DHTServer implementation.
 */
public class DHTServer /*implements Lifecycle*/ {

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

    /** DHTNode Discovery Handler. */
    @Autowired
    private DHTNodeDiscovery discovery;

    /** DHTServer Config. */
    @Autowired
    private DHTServerConfig config;

    /**
     * Constructor.
     */
    public DHTServer() {
    }

    /**
     * Run DHTServer.
     * @throws Exception  Exception
     */
    public void run() throws Exception {

        setLoggingLevels();

        LOGGER.info("starting cthulhu on " + this.serverSocket.getLocalPort());
        bootstrap();

        try {

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

        } finally {
            shutdownGracefully();
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

                    this.discovery.sendFindNodeQuery(addr, port);

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
        this.stop = true;
        this.socketThreadPool.shutdown();

        if (this.serverSocket != null) {
            this.serverSocket.close();
        }
    }
}

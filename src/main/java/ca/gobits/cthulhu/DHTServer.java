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
import java.net.DatagramPacket;
import java.net.DatagramSocket;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;


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

        LOGGER.info("starting cthulhu on " + serverSocket.getLocalPort());

        try {

            byte[] receiveData = new byte[RECEIVE_DATA_LENGTH];

            while (true) {

                DatagramPacket receivePacket = new DatagramPacket(
                        receiveData, receiveData.length);

                serverSocket.receive(receivePacket);

                socketThreadPool.execute(new DHTProtocolRunnable(serverSocket,
                        dhtHandler, receivePacket));

                if (stop) {
                    break;
                }
            }

        } finally {
            shutdownGracefully();
        }
    }

    /**
     * Gracefully shutdown server.
     */
    public final void shutdownGracefully() {
        stop = true;
        socketThreadPool.shutdown();

        if (serverSocket != null) {
            serverSocket.close();
        }
    }
}

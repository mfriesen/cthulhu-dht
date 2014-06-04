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

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

/**
 * Implementation of DHTNodeStatusQueue.
 *
 */
public class DHTNodeStatusQueue {

    /** DHT Server Logger. */
    private static final Logger LOGGER = Logger
            .getLogger(DHTNodeStatusQueue.class);

    /** Reference to DHTServer and DatagramSocket. */
    @Autowired
    private DHTServer server;

    /** Reference to Node Status Thread Pool. */
    @Autowired
    private ThreadPoolTaskExecutor nodeStatusThreadPool;

    /** DatagramSocket instance. */
    @Autowired
    private DatagramSocket socket;

    /** DHTQueryProtocol instance. */
    @Autowired
    private DHTQueryProtocol queryProtocol;

//    @Scheduled(initialDelay = 5000, fixedRate = 50000000)
//    public void doSomething() {
//        System.out.println("RUNNING>..");
//
//        try {
//            ping(InetAddress.getByName("127.0.0.1"),
//    DHTServerConfig.DEFAULT_PORT);
//        } catch (IOException e) {
//            LOGGER.info(e, e);
//        }
//    }

    /**
     * Pings a node.
     * @param addr  addr
     * @param port  port
     */
    public void ping(final InetAddress addr, final int port) {
        nodeStatusThreadPool.execute(new Runnable() {
            @Override
            public void run() {
                byte[] bytes = queryProtocol.pingQuery();
                DatagramPacket packet = new DatagramPacket(bytes, bytes.length,
                        addr, port);

                try {
                    socket.send(packet);
                } catch (IOException e) {
                    LOGGER.info(e, e);
                }
            }
        });
    }
}

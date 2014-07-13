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

package ca.gobits.cthulhu.discovery;

import static ca.gobits.dht.DHTConversion.compactAddress;
import static ca.gobits.dht.DHTConversion.compactAddressPort;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.DelayQueue;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;

import ca.gobits.cthulhu.DHTQueryProtocol;
import ca.gobits.cthulhu.DHTServerConfig;
import ca.gobits.cthulhu.DHTTokenTable;

/**
 * DHTNodeDiscovery Implementation.
 *
 */
public class DHTNodeDiscoveryImpl implements DHTNodeDiscovery {

    /** DHT Server Logger. */
    private static final Logger LOGGER = Logger
            .getLogger(DHTNodeDiscovery.class);

    /** Default Ping Delay, 10 Seconds. */
    private static final int DEFAULT_PING_DELAY_MILLIS = 10000;

    /** Queue of DHTNode to ping. */
    private final BlockingQueue<DelayObject<byte[]>> pingQueue =
            new DelayQueue<DelayObject<byte[]>>();

    /** Reference to DHTServerConfig. */
    @Autowired
    private DHTServerConfig config;

    /** Reference to DHTTokenTable. */
    @Autowired
    private DHTTokenTable tokens;

    /** Reference to DatagramSocket. */
    @Autowired
    private DatagramSocket socket;

    /** Delay for every added node. */
    private long pingDelayInMillis = DEFAULT_PING_DELAY_MILLIS;

    @Override
    public void ping(final InetAddress addr, final int port) {

        byte[] payload = compactAddress(addr.getAddress(), port);
        DelayObject<byte[]> obj = new DelayObject<byte[]>(payload,
                this.pingDelayInMillis);

        this.pingQueue.offer(obj);
    }

    @Override
    @Scheduled(fixedDelay = DEFAULT_PING_DELAY_MILLIS)
    public void processPingQueue() {

        Collection<DelayObject<byte[]>> objs =
                new ArrayList<DelayObject<byte[]>>();

        this.pingQueue.drainTo(objs);

        LOGGER.info("processing ping queue: " + objs.size()
                + " out of " + this.pingQueue.size());

        for (DelayObject<byte[]> obj : objs) {

            InetAddress addr = compactAddress(obj.getPayload());
            int port = compactAddressPort(obj.getPayload());
            sendPing(addr, port);
        }
    }

    /**
     * Sends Ping Request.
     * @param addr  InetAddress
     * @param port  int
     */
    private void sendPing(final InetAddress addr, final int port) {

        String transactionId = this.tokens.getTransactionId();
        byte[] msg = DHTQueryProtocol.pingQuery(transactionId,
                this.config.getNodeId());

        DatagramPacket packet = new DatagramPacket(msg, msg.length,
                addr, port);

        LOGGER.info("sending 'ping' to " + addr.getHostName() + ":" + port);

        try {
            this.socket.send(packet);
        } catch (Exception e) {
            LOGGER.trace(e.getMessage(), e);
        }
    }

    @Override
    public void findNodes(final InetAddress addr, final int port,
            final byte[] target) {

        List<byte[]> want = getWant();
        byte[] nodeId = this.config.getNodeId();
        String transactionId = this.tokens.getTransactionId();
        byte[] msg = DHTQueryProtocol.findNodeQuery(transactionId, nodeId,
                target, want);

        DatagramPacket packet = new DatagramPacket(msg, msg.length,
                addr, port);

        LOGGER.info("sending 'find_node' to " + addr.getHostName() + ":"
                + port);

        try {
            this.socket.send(packet);
        } catch (Exception e) {
            LOGGER.trace(e.getMessage(), e);
        }
    }

    // TODO remove old nodes / peers

    /**
     * @return List<byte[]>  wants the local server supports.
     */
    private List<byte[]> getWant() {

        List<byte[]> want = new ArrayList<byte[]>();
        InetAddress addr = this.socket.getLocalAddress();
        if (addr instanceof Inet6Address) {
            want.add("n6".getBytes());
        } else {
            want.add("n4".getBytes());
        }

        return want;
    }

    @Override
    public void setPingDelayInMillis(final long delay) {
        this.pingDelayInMillis = delay;
    }
}

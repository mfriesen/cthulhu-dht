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

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Arrays;
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
import ca.gobits.dht.DHTConversion;

/**
 * DHTNodeDiscovery Implementation.
 *
 */
public class DHTNodeDiscoveryImpl implements DHTNodeDiscovery {

    /** DHT Server Logger. */
    private static final Logger LOGGER = Logger
            .getLogger(DHTNodeDiscovery.class);

    /** Fixed Interval to Process Queue. */
    private static final int SCHEDULED_FIXED_DELAY = 25000;

    /** Default Delay to added node. */
    private static final int DEFAULT_DELAY_MILLIS = 60000;

    /** Queue of DHTNode to contact. */
    private final BlockingQueue<DelayObject<byte[]>> delayed =
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
    private long delayInMillis = DEFAULT_DELAY_MILLIS;

    @Override
    public void addNode(final InetAddress addr, final int port) {
        byte[] payload = DHTConversion.compactAddress(addr.getAddress(), port);
        DelayObject<byte[]> obj = new DelayObject<byte[]>(payload,
                this.delayInMillis);

        this.delayed.offer(obj);
    }

    @Override
    @Scheduled(fixedDelay = SCHEDULED_FIXED_DELAY)
    public void process() {

        byte[] nodeId = this.config.getNodeId();
        String transactionId = this.tokens.getTransactionId();

        Collection<DelayObject<byte[]>> objs =
                new ArrayList<DelayObject<byte[]>>();

        this.delayed.drainTo(objs);

        for (DelayObject<byte[]> obj : objs) {

            try {
                InetAddress addr = compactAddress(obj.getPayload());
                int port = compactAddressPort(obj.getPayload());

                LOGGER.info("sending 'find_nodes' for "
                        + Arrays.toString(nodeId) + " to " + addr.getHostName()
                        + ":" + port);
                // TODO get Want from DHTServerConfig (is server want IPv4 or
                // IPv6 responses)
                List<byte[]> want = null;
                byte[] msg = DHTQueryProtocol.findNodeQuery(transactionId,
                        nodeId, nodeId, want);

                DatagramPacket packet = new DatagramPacket(msg, msg.length,
                        addr, port);
                this.socket.send(packet);
            } catch (IOException e) {
                LOGGER.trace(e, e);
            }
        }
    }

    @Override
    public void setDelay(final long delay) {
        this.delayInMillis = delay;
    }
}

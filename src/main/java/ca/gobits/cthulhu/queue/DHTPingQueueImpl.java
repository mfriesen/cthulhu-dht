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

package ca.gobits.cthulhu.queue;

import static ca.gobits.dht.DHTConversion.compactAddress;
import static ca.gobits.dht.DHTConversion.compactAddressPort;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Collection;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import ca.gobits.cthulhu.DHTQueryProtocol;
import ca.gobits.cthulhu.DHTServerConfig;

/**
 * DHT Ping Request queue.
 *
 */
public class DHTPingQueueImpl extends DHTQueueAbstract implements DHTPingQueue {

    /** DHTPingQueue Logger. */
    private static final Logger LOGGER = Logger
            .getLogger(DHTPingQueue.class);

    /** Reference to DHTServerConfig. */
    @Autowired
    private DHTServerConfig config;

    @Override
    public void ping(final InetAddress addr, final int port) {

        byte[] payload = compactAddress(addr.getAddress(), port);
        DelayObject<byte[]> obj = new DelayObject<byte[]>(payload,
                getDelayInMillis());

        getQueue().offer(obj);
    }

    @Override
    public void processQueue() {

        Collection<DelayObject<byte[]>> objs =
                new ArrayList<DelayObject<byte[]>>();

        getQueue().drainTo(objs);

        LOGGER.info("processing ping queue: " + objs.size()
                + " out of " + size());

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

        byte[] msg = DHTQueryProtocol.pingQuery(getTransactionId(),
                this.config.getNodeId());

        LOGGER.info("sending 'ping' to " + addr.getHostAddress() + ":" + port);

        sendToSocket(addr, port, msg);
    }
}

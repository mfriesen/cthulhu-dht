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

import java.net.Inet6Address;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import ca.gobits.cthulhu.DHTQueryProtocol;
import ca.gobits.cthulhu.DHTServerConfig;
import ca.gobits.cthulhu.domain.DHTNodeFactory;

/**
 * Implementation of DHT 'find_node' queue.
 *
 */
public class DHTFindNodeQueueImpl extends DHTQueueAbstract implements
        DHTFindNodeQueue {

    /** DHTPingQueue Logger. */
    private static final Logger LOGGER = Logger
            .getLogger(DHTFindNodeQueue.class);

    /** Reference to DHTServerConfig. */
    @Autowired
    private DHTServerConfig config;

    @Override
    public void findNodes(final InetAddress addr, final int port,
            final byte[] target) {

        byte[] addrPayload = compactAddress(addr.getAddress(), port);
        byte[] payload = new byte[addrPayload.length + target.length];
        System.arraycopy(addrPayload, 0, payload, 0, addrPayload.length);
        System.arraycopy(target, 0, payload, addrPayload.length, target.length);

        DelayObject<byte[]> obj = new DelayObject<byte[]>(payload,
                getDelayInMillis());

        getQueue().offer(obj);
    }

    @Override
    public void processQueue() {
        Collection<DelayObject<byte[]>> objs =
                new ArrayList<DelayObject<byte[]>>();

        getQueue().drainTo(objs);

        LOGGER.info("processing findnode queue: " + objs.size()
                + " out of " + size());

        for (DelayObject<byte[]> obj : objs) {

            byte[] payload = obj.getPayload();
            byte[] baddr = new byte[payload.length
                    - DHTNodeFactory.NODE_ID_LENGTH];
            byte[] target = new byte[DHTNodeFactory.NODE_ID_LENGTH];

            System.arraycopy(payload, 0, baddr, 0, baddr.length);
            System.arraycopy(payload, baddr.length, target, 0, target.length);

            InetAddress addr = compactAddress(baddr);
            int port = compactAddressPort(baddr);
            sendFindnode(addr, port, target);
        }
    }

    /**
     * Send 'findnode' request.
     * @param addr  InetAddress
     * @param port  int
     * @param target  byte[]
     */
    private void sendFindnode(final InetAddress addr, final int port,
            final byte[] target) {

        List<byte[]> want = getWant();
        byte[] nodeId = this.config.getNodeId();
        byte[] msg = DHTQueryProtocol.findNodeQuery(getTransactionId(), nodeId,
                target, want);

        LOGGER.info("sending 'find_node' to " + addr.getHostName() + ":"
                + port);

        sendToSocket(addr, port, msg);
    }

    /**
     * @return List<byte[]>  wants the local server supports.
     */
    private List<byte[]> getWant() {

        List<byte[]> want = new ArrayList<byte[]>();
        InetAddress addr = getSocket().getLocalAddress();
        if (addr instanceof Inet6Address) {
            want.add("n6".getBytes());
        } else {
            want.add("n4".getBytes());
        }

        return want;
    }
}

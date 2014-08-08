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

package ca.gobits.dht.server.scheduling;

import java.net.InetAddress;
import java.util.Date;

import ca.gobits.dht.DHTBucket;
import ca.gobits.dht.DHTNode;
import ca.gobits.dht.DHTNode.State;
import ca.gobits.dht.DHTNodeRoutingTable;
import ca.gobits.dht.server.queue.DHTNodeStatusQueue;

/**
 * Runnable tasks for DHTRoutingTable.
 *
 */
public class DHTRoutingTableExecutorAdd implements DHTRoutingTableExecutor {

    /** node identifier. */
    private final byte[] infohash;
    /** InetAddress. */
    private final InetAddress address;
    /** address port. */
    private final int addressPort;
    /** ipv6. */
    private final boolean ipv6Request;

    /** Reference to DHTNodeRoutingTable. */
    private final DHTNodeRoutingTable rt;

    /** Reference to DHTNodeStatusQueue. */
    private final DHTNodeStatusQueue nodeStatusQueue;

    /**
     * constructor.
     * @param routingTable DHTNodeRoutingTable
     * @param queue DHTNodeStatusQueue
     * @param nodeId node's identifier.
     * @param addr  InetAddress
     * @param port  int
     * @param ipv6 whether ipv6 request
     */
    public DHTRoutingTableExecutorAdd(final DHTNodeRoutingTable routingTable,
            final DHTNodeStatusQueue queue,
            final byte[] nodeId, final InetAddress addr, final int port,
            final boolean ipv6) {
        this.rt = routingTable;
        this.nodeStatusQueue = queue;
        this.infohash = nodeId;
        this.address = addr;
        this.addressPort = port;
        this.ipv6Request = ipv6;
    }

    @Override
    public void run() {

        DHTNode node = this.rt.addNode(this.infohash, this.address,
                this.addressPort, State.GOOD);

        DHTBucket bucket = this.rt.findBucket(this.infohash, this.ipv6Request);
        bucket.setLastChanged(new Date());

        this.nodeStatusQueue.addToQueue(node);
    }
}

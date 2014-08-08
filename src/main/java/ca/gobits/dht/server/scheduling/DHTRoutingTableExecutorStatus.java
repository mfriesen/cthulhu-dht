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

/**
 * Runnable tasks for updating DHTNode's status.
 *
 */
public class DHTRoutingTableExecutorStatus implements
        DHTRoutingTableExecutor {

    /** node identifier. */
    private final byte[] infohash;
    /** InetAddress. */
    private final InetAddress address;
    /** address port. */
    private final int addressPort;
    /** ipv6. */
    private final boolean ipv6Request;

    /** Flag to indicate whether node should be added if missing. */
    private final boolean addNode;

    /** Reference to DHTNodeRoutingTable. */
    private final DHTNodeRoutingTable rt;

    /** Reference to DHTRoutingTableThreadExecutor. */
    private final DHTRoutingTableThreadExecutor te;

    /**
     * constructor.
     * @param exe DHTRoutingTableThreadExecutor
     * @param routingTable DHTNodeRoutingTable
     * @param nodeId node's identifier.
     * @param addr  InetAddress
     * @param port  int
     * @param ipv6 whether ipv6 request
     * @param addNodeIfMissing Add Node if missing
     */
    public DHTRoutingTableExecutorStatus(
            final DHTRoutingTableThreadExecutor exe,
            final DHTNodeRoutingTable routingTable,
            final byte[] nodeId,
            final InetAddress addr, final int port, final boolean ipv6,
            final boolean addNodeIfMissing) {
        this.te = exe;
        this.rt = routingTable;
        this.infohash = nodeId;
        this.address = addr;
        this.addressPort = port;
        this.ipv6Request = ipv6;
        this.addNode = addNodeIfMissing;
    }

    @Override
    public void run() {

        DHTNode node = this.rt.findExactNode(this.infohash, this.ipv6Request);

        if (node != null) {
            node.setState(State.GOOD);

            DHTBucket bucket = this.rt.findBucket(this.infohash,
                    this.ipv6Request);
            bucket.setLastChanged(new Date());

        } else if (this.addNode) {

            this.te.addNode(this.infohash, this.address, this.addressPort,
                    this.ipv6Request);
        }
    }
}

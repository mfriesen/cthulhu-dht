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

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import ca.gobits.dht.DHTNode;
import ca.gobits.dht.DHTNodeRoutingTable;
import ca.gobits.dht.server.queue.DHTNodeStatusQueue;

/**
 * DHTRoutingTableThreadExecutor queues tasks for the DHTRoutingTable
 * which are performed one at a time.  This simplifies handling multiple
 * threads modifying the routing table.
 *
 */
public class DHTRoutingTableThreadExecutor extends ThreadPoolTaskExecutor {

    /** serialVersionUID. */
    private static final long serialVersionUID = 7922765746137240069L;

    /** Reference to DHTNodeRoutingTable. */
    @Autowired
    private DHTNodeRoutingTable rt;

    /** Reference to DHTNodeStatusQueue. */
    @Autowired
    private DHTNodeStatusQueue nodeStatusQueue;

    /**
     * default constructor.
     */
    public DHTRoutingTableThreadExecutor() {
        setCorePoolSize(1);
        setMaxPoolSize(1);
    }

    /**
     * Update Node Status.
     *
     * @param nodeId node's identifier.
     * @param addr  InetAddress
     * @param port  int
     * @param ipv6 whether ipv6 request
     * @param addNodeIfMissing Add Node if missing
     */
    public void updateNodeStatus(final byte[] nodeId, final InetAddress addr,
            final int port, final boolean ipv6,
            final boolean addNodeIfMissing) {

        DHTRoutingTableExecutorStatus te = new DHTRoutingTableExecutorStatus(
                this, this.rt, nodeId, addr, port, ipv6, addNodeIfMissing);

        this.execute(te);
    }

    /**
     * Adds Node to Routing Table.
     * @param nodeId node's identifier.
     * @param addr  InetAddress
     * @param port  int
     * @param ipv6 whether ipv6 request
     */
    public void addNode(final byte[] nodeId, final InetAddress addr,
            final int port, final boolean ipv6) {

        DHTRoutingTableExecutorAdd te = new DHTRoutingTableExecutorAdd(this.rt,
                this.nodeStatusQueue, nodeId, addr, port, ipv6);

        this.execute(te);
    }

    /**
     * Removes Node from Routing Table.
     * @param node DHTNode
     */
    public void removeNode(final DHTNode node) {

        DHTRoutingTableExecutorRemove tr = new DHTRoutingTableExecutorRemove(
                this.rt, node);

        this.execute(tr);
    }
}

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

import ca.gobits.dht.DHTNode;
import ca.gobits.dht.DHTNodeRoutingTable;

/**
 * Runnable tasks for DHTRoutingTable.
 *
 */
public class DHTRoutingTableExecutorRemove implements DHTRoutingTableExecutor {

    /** DHTNode. */
    private final DHTNode node;

    /** Reference to DHTNodeRoutingTable. */
    private final DHTNodeRoutingTable rt;

    /**
     * constructor.
     * @param routingTable DHTNodeRoutingTable
     * @param n DHTNode.
     */
    public DHTRoutingTableExecutorRemove(
            final DHTNodeRoutingTable routingTable,
            final DHTNode n) {
        this.node = n;
        this.rt = routingTable;
    }

    @Override
    public void run() {

        this.rt.removeNode(this.node);
    }
}

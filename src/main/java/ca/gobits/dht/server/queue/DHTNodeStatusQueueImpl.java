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

package ca.gobits.dht.server.queue;

import java.net.InetAddress;

import org.springframework.beans.factory.annotation.Autowired;

import ca.gobits.dht.DHTNode;
import ca.gobits.dht.DHTNode.State;
import ca.gobits.dht.DHTNodeRoutingTable;

/**
 * Implementation of DHTNodeStatusQueue.
 *
 */
public class DHTNodeStatusQueueImpl implements DHTNodeStatusQueue {

//    /** DHTNodeStatusQueue Logger. */
//    private static final Logger LOGGER = Logger
//            .getLogger(DHTNodeStatusQueue.class);

    /** Reference to Node Routing Table. */
    @Autowired
    private DHTNodeRoutingTable rt;

    /** Reference to DHTBucketStatusQueue. */
    @Autowired
    private DHTBucketStatusQueue bucketStatusQueue;

    @Override
    public void processQueue() {
        // TODO
    }

    @Override
    public void updateExistingNodeToGood(final byte[] nodeId,
            final boolean ipv6) {

        updateStatusExistingNode(nodeId, ipv6);
    }

    @Override
    public void receivedFindNodeResponse(final byte[] nodeId,
            final InetAddress addr, final int port, final boolean ipv6) {

        if (!updateStatusExistingNode(nodeId, ipv6)) {

            this.rt.addNode(nodeId, addr, port, State.GOOD);

            this.bucketStatusQueue.updateBucketLastChanged(nodeId, ipv6);
        }
    }

    /**
     * Update Status of Existing Node.
     * @param nodeId  node identifier
     * @param ipv6  whether ipv6 request
     * @return boolean whether successfully updated or not
     */
    private boolean updateStatusExistingNode(final byte[] nodeId,
            final boolean ipv6) {

        boolean updated = false;
        DHTNode node = this.rt.findExactNode(nodeId, ipv6);

        if (node != null) {

            node.setState(State.GOOD);

            this.bucketStatusQueue.updateBucketLastChanged(nodeId, ipv6);

            updated = true;
        }

        return updated;
    }
}

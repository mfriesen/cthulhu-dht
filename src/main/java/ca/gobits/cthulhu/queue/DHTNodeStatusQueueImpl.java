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

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import ca.gobits.cthulhu.DHTNodeRoutingTable;
import ca.gobits.cthulhu.domain.DHTBucket;
import ca.gobits.cthulhu.domain.DHTNode;
import ca.gobits.cthulhu.domain.DHTNode.State;
import ca.gobits.dht.DHTIdentifier;
import ca.gobits.dht.DateHelper;

/**
 * Implementation of DHTNodeStatusQueue.
 *
 */
public class DHTNodeStatusQueueImpl implements DHTNodeStatusQueue {

    /** DHTPingQueue Logger. */
    private static final Logger LOGGER = Logger
            .getLogger(DHTNodeStatusQueue.class);

    /** Number of minutes until Bucket expires. */
    private static final int BUCKET_EXPIRY_IN_MINUTES = 15;

    /** Reference to Node Routing Table. */
    @Autowired
    private DHTNodeRoutingTable rt;

    /** Refernece to DHTFindNode Queue. */
    @Autowired
    private DHTFindNodeQueue findNodeQueue;

    @Override
    public void processQueue() {

        processBuckets(false);
        processBuckets(true);
    }

    /**
     * Buckets that have not been changed in 15 minutes should be
     * "refreshed." This is done by picking a random ID in the range of the
     * bucket and performing a find_nodes search on it.
     *
     * @param ipv6 whether IPv6
     */
    private void processBuckets(final boolean ipv6) {

        for (DHTBucket bucket : this.rt.getBuckets(ipv6)) {

            if (isExpired(bucket)) {

                byte[] randomId = DHTIdentifier.getRandomNodeId(
                        bucket.getMin(), bucket.getMax());

                List<DHTNode> nodes = this.rt.findClosestNodes(randomId, ipv6);

                for (DHTNode node : nodes) {

                    try {
                        this.findNodeQueue.findNodes(node.getAddress(),
                            node.getPort(), randomId);
                    } catch (UnknownHostException e) {
                        LOGGER.trace(e, e);
                    }
                }
            }
        }
    }

    /**
     * Is Bucket Expired.
     * @param bucket  DHTBucket
     * @return boolean
     */
    private boolean isExpired(final DHTBucket bucket) {
        return DateHelper.isPastDateInMinutes(new Date(),
                bucket.getLastChanged(), BUCKET_EXPIRY_IN_MINUTES);
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

            updateBucketLastChanged(nodeId, ipv6);
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

            updateBucketLastChanged(nodeId, ipv6);

            updated = true;
        }

        return updated;
    }

    /**
     * Update Bucket's Last Changed Date.
     * @param nodeId  node identifier
     * @param ipv6  whether ipv6 request
     */
    private void updateBucketLastChanged(final byte[] nodeId,
            final boolean ipv6) {

        DHTBucket bucket = this.rt.findBucket(nodeId, ipv6);
        bucket.setLastChanged(new Date());
    }
}

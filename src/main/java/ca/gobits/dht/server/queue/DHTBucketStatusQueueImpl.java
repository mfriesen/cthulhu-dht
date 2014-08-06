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

import java.net.UnknownHostException;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import ca.gobits.dht.DHTBucket;
import ca.gobits.dht.DHTIdentifier;
import ca.gobits.dht.DHTNode;
import ca.gobits.dht.DHTNodeRoutingTable;
import ca.gobits.dht.util.DateHelper;

/**
 * Implementation of a DHTBucketStatusQueue.
 *
 */
public class DHTBucketStatusQueueImpl implements DHTBucketStatusQueue {

    /** DHTBucketStatusQueue Logger. */
    private static final Logger LOGGER = Logger
            .getLogger(DHTBucketStatusQueue.class);

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

    @Override
    public void updateBucketLastChanged(final byte[] nodeId,
            final boolean ipv6) {

        DHTBucket bucket = this.rt.findBucket(nodeId, ipv6);
        bucket.setLastChanged(new Date());
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
}

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

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;

/**
 * Processor for all DHT Queues.
 *
 */
public class DHTQueueScheduler {

    /** Schedule to process queue. */
    private static final int PROCESS_QUEUE_SCHEDULE_MILLIS = 5000;

    /** Reference to DHTPingQueue. */
    @Autowired
    private DHTPingQueue pingQueue;

    /** Reference to DHTFindNodeQueue. */
    @Autowired
    private DHTFindNodeQueue findNodeQueue;

    /** Reference to DHTNodeStatusQueue. */
    @Autowired
    private DHTNodeStatusQueue nodeStatusQueue;

    /**
     * Processes the queues on a FixedDelay schedule.
     */
    @Scheduled(fixedDelay = PROCESS_QUEUE_SCHEDULE_MILLIS)
    public void process() {

        this.pingQueue.processQueue();

        this.findNodeQueue.processQueue();

        this.nodeStatusQueue.processQueue();

        // TODO process DHTBuckets
        /*
         * When the bucket is full of good nodes, the new node is simply
         * discarded. If any nodes in the bucket are known to have become bad,
         * then one is replaced by the new node. If there are any questionable
         * nodes in the bucket have not been seen in the last 15 minutes, the
         * least recently seen node is pinged. If the pinged node responds then
         * the next least recently seen questionable node is pinged until one
         * fails to respond or all of the nodes in the bucket are known to be
         * good. If a node in the bucket fails to respond to a ping, it is
         * suggested to try once more before discarding the node and replacing
         * it with a new good node. In this way, the table fills with stable
         * long running nodes.
         *
         * 1) Each bucket should maintain a "last changed" property to indicate
         * how "fresh" the contents are.
         *
         * 2) When a node in a bucket is pinged and it responds, or a node is
         * added to a bucket, or a node in a bucket is replaced with another
         * node, the bucket's last changed property should be updated.
         *
         * 3) Buckets that have not been changed in 15 minutes should be
         * "refreshed." This is done by picking a random ID in the range of the
         * bucket and performing a find_nodes search on it.
         *
         * Nodes that are able to receive queries from other nodes usually do
         * not need to refresh buckets often. Nodes that are not able to receive
         * queries from other nodes usually will need to refresh all buckets
         * periodically to ensure there are good nodes in their table when the
         * DHT is needed.
         *
         * Upon inserting the first node into its routing table and when
         * starting up thereafter, the node should attempt to find the closest
         * nodes in the DHT to itself. It does this by issuing find_node
         * messages to closer and closer nodes until it cannot find any closer.
         * The routing table should be saved between invocations of the client
         * software.
         */
    }
}

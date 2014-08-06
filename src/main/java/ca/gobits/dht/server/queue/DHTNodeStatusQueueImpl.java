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
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import ca.gobits.dht.DHTNode;
import ca.gobits.dht.DHTNode.State;
import ca.gobits.dht.DHTNodeRoutingTable;
import ca.gobits.dht.util.DateHelper;

/**
 * Implementation of DHTNodeStatusQueue.
 *
 */
public class DHTNodeStatusQueueImpl extends DHTQueueAbstract<DHTNode>
        implements DHTNodeStatusQueue {

    /** DHTNodeStatusQueue Logger. */
    private static final Logger LOGGER = Logger
            .getLogger(DHTNodeStatusQueue.class);

    /** Number of minutes between checking Node . */
    private static final int NODE_CHECK_INTERVAL_IN_MINUTES = 15;

    /** Default Delay in millis. */
    private static final int DEFAULT_QUEUE_DELAY_IN_MILLIS = 15 * 60 * 1000;

    /** Reference to Node Routing Table. */
    @Autowired
    private DHTNodeRoutingTable rt;

    /** Reference to DHTBucketStatusQueue. */
    @Autowired
    private DHTBucketStatusQueue bucketStatusQueue;

    /** Reference to DHTPingQueue. */
    @Autowired
    private DHTPingQueue pingQueue;

    /**
     * constuctor.
     */
    public DHTNodeStatusQueueImpl() {
        setDelayInMillis(DEFAULT_QUEUE_DELAY_IN_MILLIS);
    }

    @Override
    public void processQueue() {
        Collection<DelayObject<DHTNode>> objs =
                new ArrayList<DelayObject<DHTNode>>();

        getQueue().drainTo(objs);

        LOGGER.info("processing nodestatus queue: " + objs.size()
                + " out of " + size());

        Date now = new Date();
        for (DelayObject<DHTNode> delayObject : objs) {

            DHTNode node = delayObject.getPayload();

            if (DateHelper.isPastDateInMinutes(now, node.getLastUpdated(),
                    NODE_CHECK_INTERVAL_IN_MINUTES)) {

                InetAddress addr = node.getAddress();
                State state = addr != null ? node.getState() : State.UNKNOWN;

                if (State.GOOD.equals(state)) {

                    node.setState(State.QUESTIONABLE);
                    ping(node, addr, node.getPort());

                } else if (State.QUESTIONABLE.equals(state)) {

                    node.setState(State.UNKNOWN);
                    ping(node, addr, node.getPort());

                } else {

                    this.rt.removeNode(node, node.isIpv6());
                }
            } else {
                addToQueue(node);
            }
        }
    }

    /**
     * Pings node and adds it back into the queue.
     * @param node DHTNode
     * @param addr InetAddress
     * @param port int
     */
    private void ping(final DHTNode node, final InetAddress addr,
            final int port) {
        this.pingQueue.ping(addr, port);

        addToQueue(node);
    }

    /**
     * Adds node to Queue for checking status.
     * @param node DHTNode
     */
    private void addToQueue(final DHTNode node) {
        DelayObject<DHTNode> obj = new DelayObject<DHTNode>(node,
                getDelayInMillis());

        getQueue().offer(obj);
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

            DHTNode node = this.rt.addNode(nodeId, addr, port, State.GOOD);

            this.bucketStatusQueue.updateBucketLastChanged(nodeId, ipv6);

            addToQueue(node);
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

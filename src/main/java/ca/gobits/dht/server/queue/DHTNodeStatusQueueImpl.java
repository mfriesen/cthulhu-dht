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
import ca.gobits.dht.server.scheduling.DHTRoutingTableThreadExecutor;
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

    /** Reference to DHTBucketStatusQueue. */
    @Autowired
    private DHTBucketStatusQueue bucketStatusQueue;

    /** Reference to DHTPingQueue. */
    @Autowired
    private DHTPingQueue pingQueue;

    /** Reference to DHTRoutingTableThreadExecutor. */
    @Autowired
    private DHTRoutingTableThreadExecutor te;

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

                    this.te.removeNode(node);
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

    @Override
    public void addToQueue(final DHTNode node) {
        DelayObject<DHTNode> obj = new DelayObject<DHTNode>(node,
                getDelayInMillis());

        getQueue().offer(obj);
    }
}

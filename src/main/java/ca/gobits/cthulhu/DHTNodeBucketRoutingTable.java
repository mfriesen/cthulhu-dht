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

package ca.gobits.cthulhu;

import java.net.Inet6Address;
import java.net.InetAddress;
import java.util.Arrays;
import java.util.List;

import org.apache.log4j.Logger;

import ca.gobits.cthulhu.domain.DHTNode;
import ca.gobits.cthulhu.domain.DHTNode.State;
import ca.gobits.cthulhu.domain.DHTNodeComparator;
import ca.gobits.cthulhu.domain.DHTNodeFactory;
import ca.gobits.dht.DHTDistance;
import ca.gobits.dht.DHTIdentifier;

/**
 * Implementation of DHT Bucket Routing Table.
 *
 * http://www.bittorrent.org/beps/bep_0005.html
 *
 */
public final class DHTNodeBucketRoutingTable implements DHTNodeRoutingTable {

    /** LOGGER. */
    private static final Logger LOGGER = Logger
            .getLogger(DHTNodeBucketRoutingTable.class);

    /** Constant to convert byte to unsigned int. */
    private static final int BYTE_TO_INT = 0xFF;

    /** Maximum number of nodes Routing Table holds. */
    private static final int MAX_NUMBER_OF_NODES = 1000000;

    /** IPv4 nodes. */
    private final ConcurrentSortedList<DHTNode> nodes;

    /** IPv6 nodes. */
    private final ConcurrentSortedList<DHTNode> nodes6;

    /**
     * constructor.
     */
    public DHTNodeBucketRoutingTable() {
        this.nodes = new ConcurrentSortedList<DHTNode>(
                DHTNodeComparator.getInstance(), false);
        this.nodes6 = new ConcurrentSortedList<DHTNode>(
                DHTNodeComparator.getInstance(), false);
    }

    @Override
    public void addNode(final byte[] infoHash, final InetAddress addr,
            final int port, final State state) {

        DHTNode node = DHTNodeFactory.create(infoHash, addr, port,
                state);

        addNode(node, addr, port, state);
    }

    /**
     * Add Node to Bucket.
     * @param node  DHTNode
     * @param addr  InetAddress
     * @param port  port
     * @param state  State
     */
    private void addNode(final DHTNode node, final InetAddress addr,
            final int port, final State state) {

        if (this.nodes.size() < MAX_NUMBER_OF_NODES) {

            addNodeLoggerDebug(node);

            if (addr instanceof Inet6Address) {
                this.nodes6.add(node);
            } else {
                this.nodes.add(node);
            }

        } else {
            LOGGER.warn("MAXIMUM number of noded reached "
                    + MAX_NUMBER_OF_NODES);
        }
    }

    /**
     * Print debug information on adding a node. (LOGGER.isDebugEnabled())
     * @param node  DHTNode
     */
    private void addNodeLoggerDebug(final DHTNode node) {

        if (LOGGER.isDebugEnabled()) {

            String host = "unknown";

            try {

                InetAddress addr = node.getAddress();
                host = addr.getHostAddress();

            } catch (Exception e) {

                LOGGER.info(e, e);
            }

            LOGGER.debug("adding node " + " " + host + ":" + node.getPort());
        }
    }

    @Override
    public List<DHTNode> findClosestNodes(final byte[] nodeId,
            final boolean ipv6) {
        return findClosestNodes(nodeId, DEFAULT_SEARCH_COUNT, ipv6);
    }

    @Override
    public DHTNode findExactNode(final byte[] nodeId, final boolean ipv6) {

        DHTNode nodeMatch = null;
        DHTNode node = DHTNodeFactory.create(nodeId, DHTNode.State.UNKNOWN);
        int index = this.nodes.indexOf(node);

        if (index >= 0 && index < this.nodes.size()) {
            DHTNode foundNode = this.nodes.get(index);
            if (Arrays.equals(foundNode.getInfoHash(), nodeId)) {
                nodeMatch = foundNode;
            }
        }

        return nodeMatch;
    }

    @Override
    public List<DHTNode> findClosestNodes(final byte[] nodeId,
            final int max, final boolean ipv6) {

        DHTNode node = findExactNode(nodeId, ipv6);

        if (node != null) {
            node.setState(State.GOOD);
        } else {
            node = DHTNodeFactory.create(nodeId, State.UNKNOWN);
        }

        return findClosestNodes(node, max, ipv6);
    }

    /**
     * Finds the closest nodes list.
     * @param node  node to find
     * @param max  number of nodes to return
     * @param ipv6  whether ipv6 request
     * @return List<DHTNode>
     */
    private List<DHTNode> findClosestNodes(final DHTNode node,
             final int max, final boolean ipv6) {

        int count = 0;
        ConcurrentSortedList<DHTNode> nodeList = ipv6 ? this.nodes6
                : this.nodes;

        int index = nodeList.indexOf(node);

        int lowIndex = index > 0 ? index - 1 : index;
        int hiIndex = lowIndex == index ? index + 1 : index;

        int[] lowDistance = distance(nodeList, node, lowIndex);
        int[] hiDistance = distance(nodeList, node, hiIndex);
        int result = compare(lowDistance, hiDistance);

        while (count < max - 1) {

            if (result < 0) {

                lowIndex--;
                lowDistance = distance(nodeList, node, lowIndex);
                if (lowIndex > -1) {
                    count++;
                }

            } else {

                count++;
                hiIndex++;
                hiDistance = distance(nodeList, node, hiIndex);
            }

            result = compare(lowDistance, hiDistance);
        }

        lowIndex = lowIndex < 0 ? 0 : lowIndex;
        return this.nodes.subList(lowIndex, hiIndex);
    }

    /**
     * Compare 2 integer arrays.
     * @param s0  integer array
     * @param s1  integer array
     * @return int
     */
    private int compare(final int[] s0, final int[] s1) {
        int result = 0;

        for (int i = 0; i < s0.length; i++) {
            result = Integer.compare(s0[i], s1[i]);
            if (result != 0) {
                break;
            }
        }

        return result;
    }

    /**
     * Calculate Distance between nodes.
     * @param nodeList  DHTNode list
     * @param node  Compare to Node
     * @param index  index
     * @return int[]
     */
    private int[] distance(final SortedCollection<DHTNode> nodeList,
            final DHTNode node, final int index) {

        int[] result = null;

        if (index >= 0 && index < nodeList.size()) {
            byte[] nodeId = nodeList.get(index).getInfoHash();
            result = DHTDistance.xor(node.getInfoHash(), nodeId);
        } else {
            result = maxDistance();
        }

        return result;
    }

    /**
     * Maximum Distance from node.
     * @return int[]
     */
    private int[] maxDistance() {
        byte[] nodeId = DHTIdentifier.getRandomNodeId();
        int[] distance = new int[nodeId.length];
        Arrays.fill(distance, BYTE_TO_INT);
        return distance;
    }

    /**
     * @return DHTBucket
     */
    public SortedCollection<DHTNode> getNodes() {
        return this.nodes;
    }

    /**
     * @return DHTBucket
     */
    public SortedCollection<DHTNode> getNodes6() {
        return this.nodes6;
    }

    @Override
    public int getTotalNodeCount(final boolean ipv6) {
        return ipv6 ? this.nodes6.size() : this.nodes.size();
    }

    @Override
    public int getMaxNodeCount() {
        return MAX_NUMBER_OF_NODES;
    }

    @Override
    public void clear() {
        this.nodes.clear();
        this.nodes6.clear();
    }

    @Override
    public boolean updateNodeState(final byte[] nodeId, final State state,
            final boolean ipv6) {

        boolean success = false;
        DHTNode node = findExactNode(nodeId, ipv6);

        if (node != null) {
            node.setState(state);
            success = true;
        }

        return success;
    }
}

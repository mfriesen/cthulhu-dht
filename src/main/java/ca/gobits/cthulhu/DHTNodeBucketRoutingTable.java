//
// Copyright 2013 Mike Friesen
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

import static ca.gobits.dht.DHTConversion.toInetAddress;

import java.math.BigInteger;
import java.net.InetAddress;
import java.util.List;

import org.apache.log4j.Logger;

import ca.gobits.cthulhu.domain.DHTNode;
import ca.gobits.cthulhu.domain.DHTNode.State;
import ca.gobits.cthulhu.domain.DHTNodeComparator;
import ca.gobits.cthulhu.domain.DHTNodeFactory;
import ca.gobits.dht.DHTConversion;

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

    /** Maximum number of nodes Routing Table holds. */
    private static final int MAX_NUMBER_OF_NODES = 1000000;

    /** root node of the routing table. */
    private final ConcurrentSortedList<DHTNode> nodes;

    /**
     * constructor.
     */
    public DHTNodeBucketRoutingTable() {
        this.nodes = new ConcurrentSortedList<DHTNode>(
                DHTNodeComparator.getInstance(), false);
    }

    @Override
    public void addNode(final byte[] infoHash, final InetAddress addr,
            final int port, final State state) {
        addNode(DHTConversion.toBigInteger(infoHash), addr, port, state);
    }

    @Override
    public void addNode(final BigInteger infoHash, final InetAddress addr,
            final int port, final State state) {

        if (nodes.size() < MAX_NUMBER_OF_NODES) {

            DHTNode node = DHTNodeFactory.create(infoHash, addr, port,
                    state);

            addNodeLoggerDebug(node);
            nodes.add(node);

        } else {
            LOGGER.warn("MAXIMUM number of noded reached "
                    + MAX_NUMBER_OF_NODES);
        }
    }

//    /**
//     * Sends a Find Request to a node to determine
//     * whether it is "good" or not.
//     * @param infoHash  info hash
//     * @param addr  address to send find request to
//     */
//    private void sendFindRequest(final byte[] infoHash,
//            final InetSocketAddress addr) {
//
//        /**
//         * TODO ignore IP addresses
//         * Ignore
//         *      if ((ip & 0xff000000) == 0x0a000000 // 10.x.x.x
//|| (ip & 0xfff00000) == 0xac100000 // 172.16.x.x
//|| (ip & 0xffff0000) == 0xc0a80000 // 192.168.x.x
//|| (ip & 0xffff0000) == 0xa9fe0000 // 169.254.x.x
//|| (ip & 0xff000000) == 0x7f000000) // 127.x.x.x
//         */
////        String message = "d1:ad2:id20:abcdefghij01234567899:info_hash20:"
////            + "mnopqrstuvwxyz123456e1:q9:get_peers1:t2:aa1:y1:qe";
//    }

    /**
     * Print debug information on adding a node. (LOGGER.isDebugEnabled())
     * @param node  DHTNode
     */
    private void addNodeLoggerDebug(final DHTNode node) {

        if (LOGGER.isDebugEnabled()) {

            String host = "unknown";

            try {

                InetAddress addr = toInetAddress(node.getAddress());
                host = addr.getHostAddress();

            } catch (Exception e) {

                LOGGER.info(e, e);
            }

            LOGGER.debug("adding node " + node.getInfoHash() + " "
                + host + ":" + node.getPort());
        }
    }

    @Override
    public List<DHTNode> findClosestNodes(final BigInteger nodeId) {
        return findClosestNodes(nodeId, DEFAULT_SEARCH_COUNT);
    }

    @Override
    public DHTNode findExactNode(final BigInteger nodeId) {

        DHTNode nodeMatch = null;
        DHTNode node = DHTNodeFactory.create(nodeId, DHTNode.State.UNKNOWN);
        int index = this.nodes.indexOf(node);

        if (index >= 0 && index < this.nodes.size()) {
            DHTNode foundNode = this.nodes.get(index);
            if (foundNode.getInfoHash().equals(nodeId)) {
                nodeMatch = foundNode;
            }
        }

        return nodeMatch;
    }

    @Override
    public List<DHTNode> findClosestNodes(final BigInteger nodeId,
            final int returnCount) {

        DHTNode node = findExactNode(nodeId);

        if (node != null) {
            node.setState(State.GOOD);
        } else {
            node = DHTNodeFactory.create(nodeId, State.UNKNOWN);
        }

        return findClosestNodes(node, returnCount);
    }

    /**
     * Finds the closest nodes list.
     * @param node  node to find
     * @param returnCount  number of nodes to return
     * @return List<DHTNode>
     */
    private List<DHTNode> findClosestNodes(final DHTNode node,
            final int returnCount) {

        int index = nodes.indexOf(node);

        int fromIndex = index > 0 ? index - 1 : 0;
        int toIndex = index < getTotalNodeCount() ? index + 1
                : getTotalNodeCount();
        int count = toIndex - fromIndex;

        while (count < returnCount && count < nodes.size()) {

            if (fromIndex > 0) {
                fromIndex--;
                count++;
            }

            if (count < returnCount
                    && toIndex < getTotalNodeCount()) {
                toIndex++;
                count++;
            }
        }

        return nodes.subList(fromIndex, toIndex);
    }

    /**
     * @return DHTBucket
     */
    public SortedCollection<DHTNode> getNodes() {
        return nodes;
    }

    @Override
    public int getTotalNodeCount() {
        return nodes.size();
    }

    @Override
    public int getMaxNodeCount() {
        return MAX_NUMBER_OF_NODES;
    }

    @Override
    public void clear() {
        this.nodes.clear();
    }
}

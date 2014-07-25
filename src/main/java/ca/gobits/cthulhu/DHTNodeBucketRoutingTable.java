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

import static ca.gobits.cthulhu.domain.DHTNodeFactory.NODE_ID_LENGTH;
import static ca.gobits.dht.DHTConversion.fitToSize;

import java.math.BigInteger;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;

import ca.gobits.cthulhu.domain.DHTBucket;
import ca.gobits.cthulhu.domain.DHTBucketComparator;
import ca.gobits.cthulhu.domain.DHTNode;
import ca.gobits.cthulhu.domain.DHTNode.State;
import ca.gobits.cthulhu.domain.DHTNodeComparator;
import ca.gobits.cthulhu.domain.DHTNodeFactory;
import ca.gobits.dht.DHTConversion;
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
    private final SortedCollection<DHTNode> nodes;

    /** IPv6 nodes. */
    private final SortedCollection<DHTNode> nodes6;

    /** DHTBuckets for IPv4 nodes. */
    private final SortedCollection<DHTBucket> buckets;

    /** DHTBuckets for IPv6 nodes. */
    private final SortedCollection<DHTBucket> buckets6;

    /** Node ID of Host. */
    private final byte[] id;

    /**
     * constructor.
     * @param nodeId  Host Identifier
     */
    public DHTNodeBucketRoutingTable(final byte[] nodeId) {
        this.id = nodeId;
        this.nodes = new ConcurrentSortedList<DHTNode>(
                DHTNodeComparator.getInstance(), false);
        this.nodes6 = new ConcurrentSortedList<DHTNode>(
                DHTNodeComparator.getInstance(), false);

        this.buckets = new ConcurrentSortedList<DHTBucket>(
                DHTBucketComparator.getInstance(), false);
        this.buckets6 = new ConcurrentSortedList<DHTBucket>(
                DHTBucketComparator.getInstance(), false);

        addDefaultBuckets();
    }

    /**
     * Add default buckets.
     */
    private void addDefaultBuckets() {
        byte[] min = new byte[] {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                0, 0, 0, 0, 0 };
        byte[] max = new byte[] {-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
                -1, -1, -1, -1, -1, -1, -1, -1, -1 };

        this.buckets.add(new DHTBucket(min, max));
        this.buckets6.add(new DHTBucket(min, max));
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

            boolean ipv6 = addr instanceof Inet6Address;
            DHTBucket bucket = findBucket(node.getInfoHash(), ipv6);

            addNodeLoggerDebug(node);

            addNode(bucket, node, ipv6);

        } else {
            LOGGER.warn("MAXIMUM number of noded reached "
                    + MAX_NUMBER_OF_NODES);
        }
    }

    /**
     * Adds node to list and bucket.
     * @param bucket  bucket to add node to.
     * @param node  node to add
     * @param ipv6  is this an ipv6 request
     */
    private void addNode(final DHTBucket bucket, final DHTNode node,
            final boolean ipv6) {

        if (!bucket.isFull()) {

            SortedCollection<DHTNode> nodeList = getNodes(ipv6);
            nodeList.add(node);

            bucket.incrementCount();
         // TODO add servermode flag to add all nodes as long as MAX_NODE
//            is not reached..
        } else if (bucket.isInRange(this.id)) {
//        } else if (true) {

            DHTBucket nb = splitBucket(bucket, ipv6);

            SortedCollection<DHTBucket> bucketList = ipv6 ? this.buckets6
                    : this.buckets;
            bucketList.add(nb);

            DHTBucket nextBucket = findBucket(node.getInfoHash(), ipv6);
            addNode(nextBucket, node, ipv6);
        }
    }

    /**
     * Splits a bucket in half and returns the top half
     * and adjusts the passed in argument to be the lower half.
     * @param bucket  bucket to splits
     * @param ipv6 whether ipv6 request
     * @return DHTBucket
     */
    private DHTBucket splitBucket(final DHTBucket bucket, final boolean ipv6) {

        BigInteger minBI = DHTConversion.toBigInteger(bucket.getMin());
        byte[] min = fitToSize(minBI.toByteArray(), NODE_ID_LENGTH);

        BigInteger maxBI = DHTConversion.toBigInteger(bucket.getMax());
        byte[] max = fitToSize(maxBI.toByteArray(), NODE_ID_LENGTH);

        BigInteger midBI = maxBI.add(minBI).divide(new BigInteger("2"));
        byte[] mid = fitToSize(midBI.toByteArray(), NODE_ID_LENGTH);

        BigInteger topMin = midBI.add(new BigInteger("1"));

        int posMin = indexOf(min, ipv6);
        int posMax = indexOf(max, ipv6);
        int posMid = indexOf(mid, ipv6);

        bucket.setMax(mid);
        bucket.setNodeCount(posMid - posMin);

        DHTBucket nb = new DHTBucket(fitToSize(topMin.toByteArray(),
                NODE_ID_LENGTH), max);
        nb.setNodeCount(posMax - posMid);

        return nb;
    }

    /**
     * Finds position in RoutingTable of an infohash.
     * @param infoHash  to find
     * @param ipv6  whether ipv6
     * @return int
     */
    private int indexOf(final byte[] infoHash, final boolean ipv6) {

        DHTNode node = DHTNodeFactory.create(infoHash, null);
        return getNodes(ipv6).indexOf(node);
    }

    /**
     * Finds bucket to add node to.
     * @param bytes  bytes to search for
     * @param ipv6  whether ipv6 request.
     * @return DHTBucket
     */
    private DHTBucket findBucket(final byte[] bytes, final boolean ipv6) {

        DHTBucket bucket = null;

//        return getBucket(ipv6).get(new DHTBucket(bytes, bytes));
        Iterator<DHTBucket> itr = ipv6 ? this.buckets6.iterator()
                : this.buckets.iterator();

        while (itr.hasNext()) {
            DHTBucket bb = itr.next();
            if (bb.isInRange(bytes)) {
                bucket = bb;
                break;
            }
        }

        return bucket;
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
        SortedCollection<DHTNode> nodeList = getNodes(ipv6);

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
     * @return SortedCollection<DHTNode>
     */
    public SortedCollection<DHTNode> getNodes6() {
        return this.nodes6;
    }

    /**
     * @param ipv6  whether ipv6
     * @return SortedCollection<DHTNode>
     */
    private SortedCollection<DHTNode> getNodes(final boolean ipv6) {
        SortedCollection<DHTNode> nodeList = ipv6 ? this.nodes6
                : this.nodes;
        return nodeList;
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

    /**
     * @return List<DHTBucket>
     */
    public SortedCollection<DHTBucket> getBuckets() {
        return this.buckets;
    }

    /**
     * @return List<DHTBucket>
     */
    public SortedCollection<DHTBucket> getBuckets6() {
        return this.buckets6;
    }

//    /**
//     * @param ipv6  whether ipv6 request
//     * @return SortedCollection<DHTBucket>
//     */
//    private SortedCollection<DHTBucket> getBucket(final boolean ipv6) {
//        return ipv6 ? getBuckets6() : getBuckets();
//    }
}

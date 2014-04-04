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

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Collection;
import java.util.Collections;

import ca.gobits.cthulhu.util.DHTUtil;

/**
 * Implementation of DHT Bucket Routing Table.
 *
 * http://www.bittorrent.org/beps/bep_0005.html
 *
 */
public class DHTBucketRoutingTable implements DHTRoutingTable {

    /** root node of the routing table. */
    private final DHTBucket root;

    /** Maximum number of nodes Routing Table holds. */
    public static final int MAX_NUMBER_OF_NODES = 1000000;

    /** Total number of nodes. */
    private int nodeCount = 0;

    /**
     * constructor.
     */
    public DHTBucketRoutingTable() {
        BigInteger max = new BigDecimal(Math.pow(2, DHTUtil.NODE_ID_LENGTH))
            .toBigInteger();
        this.root = new DHTBucket(BigInteger.ZERO, max);
    }

    @Override
    public final synchronized void addNode(final DHTNode node) {

        if (nodeCount <= MAX_NUMBER_OF_NODES) {

            DHTBucket bucket = findBucket(this.root, node);

            if (bucket.addNode(node)) {
                nodeCount++;

                if (bucket.isFull()) {
                    splitBucket(bucket);
                }
            }
        }
    }

    /**
     * Splits a bucket into left/right bucket.
     * @param bucket - bucket to be split
     */
    private void splitBucket(final DHTBucket bucket) {

        if (bucket.isFull()) {

            BigInteger half = bucket.getMax().subtract(bucket.getMin())
                    .divide(new BigInteger("2"));

            int position = bucket.findClosestToMax(half);

            DHTNode[] nodes = bucket.getNodes();

            DHTNode[] leftNodes = new DHTNode[DHTBucket.BUCKET_MAX];
            System.arraycopy(nodes, 0, leftNodes, 0, position);

            DHTNode[] rightNodes = new DHTNode[DHTBucket.BUCKET_MAX];
            System.arraycopy(nodes, position,
                    rightNodes, 0, DHTBucket.BUCKET_MAX - position);

            DHTBucket left = new DHTBucket(bucket.getMin(),
                    bucket.getMin().add(half));

            DHTBucket right = new DHTBucket(
                    left.getMax().add(new BigInteger("1"))
                    , bucket.getMax());

            bucket.setLeft(left);
            left.setNodeCount(position);
            bucket.setRight(right);
            right.setNodeCount(DHTBucket.BUCKET_MAX - position);
            bucket.setNodes(null);
        }
    }

    /**
     * Traverses Routing Tree and find bucket to add node to.
     * @param bucket - starting bucket
     * @param node - node to add
     * @return DHTBucket
     */
    private DHTBucket findBucket(final DHTBucket bucket, final DHTNode node) {

        DHTBucket retBucket = null;

        if (bucket != null && bucket.isWithinBucket(node)) {

            if (bucket.getLeft() == null && bucket.getRight() == null) {
                retBucket = bucket;
            } else {
                DHTBucket leftBucket = findBucket(bucket.getLeft(), node);
                DHTBucket rightBucket = findBucket(bucket.getRight(), node);

                retBucket = leftBucket != null ? leftBucket : rightBucket;
            }
        }

        return retBucket;
    }

    @Override
    public final Collection<DHTNode> findClosestNodes(final BigInteger nodeId) {
        return findClosestNodes(this.root, nodeId);
    }

    /**
     * Find the closest node.
     * @param bucket - bucket to search
     * @param nodeId - node id to find
     * @return Collection<DHTNode>
     */
    private Collection<DHTNode> findClosestNodes(final DHTBucket bucket,
            final BigInteger nodeId) {

        Collection<DHTNode> nodes = Collections.emptySet();

//        if (bucket != null && bucket.isWithinBucket(nodeId)) {

//            if (bucket.getLeft() == null && bucket.getRight() == null) {
//                nodes = new HashSet<DHTNode>(bucket.getNodes());
//            } else {
//
//                nodes = findClosestNodes(bucket.getLeft(), nodeId);
//
//                Collection<DHTNode> rightNodes = findClosestNodes(
//                        bucket.getRight(), nodeId);
//
//                if (nodes.size() < DHTBucket.BUCKET_MAX) {
//                    nodes.addAll(rightNodes);
//                }
//
//                // TODO truncate list to closest DHTBucket.BUCKET_MAX
//            }
//        }

        return nodes;
    }

    /**
     * @return DHTBucket
     */
    public final DHTBucket getRoot() {
        return root;
    }

    /**
     * @return int
     */
    public final int getNodeCount() {
        return nodeCount;
    }
}

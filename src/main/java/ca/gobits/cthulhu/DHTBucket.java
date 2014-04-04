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

import java.math.BigInteger;
import java.util.Arrays;

/**
 * DHTBucket: holder of DHTNodes.
 */
public class DHTBucket {

    /** Maximum number of items allowed in the bucket. */
    public static final int BUCKET_MAX = 8;

    /** minimum value of bucket. */
    private final BigInteger min;
    /** maximum value of bucket. */
    private final BigInteger max;
    /** number of nodes in bucket. */
    private int nodeCount = 0;
    /** bucket of lower values. */
    private DHTBucket left = null;
    /** bucket of higher values. */
    private DHTBucket right = null;
    /** list of nodes. */
    private DHTNode[] nodes;

    /**
     * constructor.
     *
     * @param minValue the minimum value of bucket
     * @param maxValue the maximum value of bucket
     */
    public DHTBucket(final BigInteger minValue, final BigInteger maxValue) {
        this.min = minValue;
        this.max = maxValue;

        this.nodes = new DHTNode[BUCKET_MAX];
        Arrays.fill(this.nodes, 0, BUCKET_MAX, null);
    }

    /**
     * @return boolean
     */
    public final boolean isFull() {
        return this.nodeCount >= BUCKET_MAX;
    }

    /**
     * Determines whether a node is between the min/max values of the bucket.
     * @param node -
     * @return boolean
     */
    public final boolean isWithinBucket(final DHTNode node) {
        return isWithinBucket(node.getId());
    }

    /**
     * Determines whether a node is between the min/max values of the bucket.
     * @param nodeId - NodeId to compare
     * @return boolean
     */
    public final boolean isWithinBucket(final BigInteger nodeId) {
        return min.compareTo(nodeId) <= 0 && max.compareTo(nodeId) >= 0;
    }

    /**
     * Add node to bucket.
     * @param node to be added
     * @return boolean
     */
    public final synchronized boolean addNode(final DHTNode node) {

        boolean result = insertionSort(node);
        if (result) {
            this.nodeCount++;
        }
        return result;
    }

    /**
     * Delete node from bucket.
     * @param node to be deleted
     */
    public final synchronized void deleteNode(final DHTNode node) {

        int nodePosition = findNode(node);

        if (nodePosition > -1) {
            System.arraycopy(nodes, nodePosition + 1, nodes,
                    nodePosition, BUCKET_MAX - nodePosition - 1);
            this.nodeCount--;
        }
    }

    /**
     * Finds node in bucket.
     * @param node - node to find
     * @return int - position of node, -1 if not found
     */
    private int findNode(final DHTNode node) {
        return findNode(node.getId());
    }

    /**
     * Find node by id.
     * @param nodeId to find
     * @return int
     */
    private int findNode(final BigInteger nodeId) {
        int imin = 0;
        int imax = this.nodeCount - 1;
        // continue searching while [imin,imax] is not empty
        while (imax >= imin) {
            // calculate the midpoint for roughly equal partition
            int imid = imin + ((imax - imin) / 2);

            int c = nodes[imid].getId().compareTo(nodeId);
            if (c == 0) {
                // key found at index imid
                return imid;
                // determine which subarray to search
            } else if (c < 0) {
                // change min index to search upper subarray
                imin = imid + 1;
            } else {
                // change max index to search lower subarray
                imax = imid - 1;
            }
        }

        return -1;
    }

    /**
     * Finds the maximum value less than max.
     * @param value - maximum value
     * @return int
     */
    public final int findClosestToMax(final BigInteger value) {

        int imin = 0;
        int imax = this.nodeCount - 1;

        // continually narrow search until just one element remains
        while (imin < imax) {

            int imid = imin + ((imax - imin) / 2);

            // code must guarantee the interval is reduced at each iteration
            assert (imid < imax);
            // note: 0 <= imin < imax implies imid will always be less than imax

            int c = nodes[imid].getId().compareTo(value);

            if (c < 0) {
                imin = imid + 1;
            } else {
                imax = imid;
            }
        }

        return imax == imin ? imin : -1;
    }

    /**
     * Performs an insertion sort.
     * @param node - node to insert
     * @return boolean insertion was successful
     */
    private boolean insertionSort(final DHTNode node) {

        boolean inserted = false;

        for (int i = 0; i < BUCKET_MAX; i++) {

            DHTNode n = nodes[i];

            if (n == null) {
                nodes[i] = node;
                inserted = true;
                break;
            }

            int c = node.getId().compareTo(n.getId());
            if (c <= 0) {

                if (c < 0) {
                    System.arraycopy(nodes, i, nodes,
                            i + 1, BUCKET_MAX - i - 1);
                    nodes[i] = node;
                    inserted = true;
                }

                break;
            }
        }

        return inserted;
    }

    /**
     * @return BigInteger
     */
    public final BigInteger getMin() {
        return min;
    }

    /**
     * @return BigInteger
     */
    public final BigInteger getMax() {
        return max;
    }

    /**
     * @return DHTBucket
     */
    public final DHTBucket getLeft() {
        return left;
    }

    /**
     * Sets the left bucket.
     * @param bucket DHTBucket
     */
    public final void setLeft(final DHTBucket bucket) {
        this.left = bucket;
    }

    /**
     * @return DHTBucket
     */
    public final DHTBucket getRight() {
        return right;
    }

    /**
     * Sets the right bucket.
     * @param bucket DHTBucket
     */
    public final void setRight(final DHTBucket bucket) {
        this.right = bucket;
    }

    /**
     * @return DHTNode[]
     */
    public final DHTNode[] getNodes() {
        return nodes;
    }

    /**
     * @param list - list of nodes
     */
    public final void setNodes(final DHTNode[] list) {
        this.nodes = list;
    }

    /**
     * @return boolean
     */
    public final boolean isEmpty() {
        return this.nodeCount == 0;
    }

    /**
     * @return int - number of nodes
     */
    public final int getNodeCount() {
        return nodeCount;
    }

    /**
     * @param count - set number of nodes
     */
    public final void setNodeCount(final int count) {
        this.nodeCount = count;
    }
}

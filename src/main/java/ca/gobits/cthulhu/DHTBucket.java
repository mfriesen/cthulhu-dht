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
import java.util.Collection;
import java.util.HashSet;

/**
 * DHTBucket: holder of DHTNodes.
 */
public class DHTBucket {

    /** minimum value of bucket. */
    private BigInteger min;
    /** maximum value of bucket. */
    private BigInteger max;
    /** bucket of lower values. */
    private DHTBucket left = null;
    /** bucket of higher values. */
    private DHTBucket right = null;
    /** list of nodes. */
    private Collection<DHTNode> nodes;

    /**
     * constructor.
     *
     * @param minValue the minimum value of bucket
     * @param maxValue the maximum value of bucket
     */
    public DHTBucket(final BigInteger minValue, final BigInteger maxValue) {
        this.min = minValue;
        this.max = maxValue;
        this.nodes = new HashSet<DHTNode>();
    }

    /**
     * @param node
     *            to be added
     */
    public final void addNode(final DHTNode node) {
        this.nodes.add(node);
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
     * @return Collection<DHTNode>
     */
    public final Collection<DHTNode> getNodes() {
        return nodes;
    }
}

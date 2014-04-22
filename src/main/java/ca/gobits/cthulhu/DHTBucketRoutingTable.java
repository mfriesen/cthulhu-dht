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
import java.util.List;

/**
 * Implementation of DHT Bucket Routing Table.
 *
 * http://www.bittorrent.org/beps/bep_0005.html
 *
 */
public final class DHTBucketRoutingTable implements DHTRoutingTable {

    /** root node of the routing table. */
    private final SortedList<DHTNode> nodes;

    /** Maximum number of nodes Routing Table holds. */
    public static final int MAX_NUMBER_OF_NODES = 1000000;

    /**
     * constructor.
     */
    public DHTBucketRoutingTable() {
        this.nodes = new SortedList<DHTNode>(false);
    }

    @Override
    public synchronized void addNode(final DHTNode node) {

        if (nodes.size() < MAX_NUMBER_OF_NODES) {
            nodes.add(node);
        }
    }

    @Override
    public List<DHTNode> findClosestNodes(final BigInteger nodeId) {
        return findClosestNodes(nodeId, DEFAULT_SEARCH_COUNT);
    }

    @Override
    public DHTNode findExactNode(final BigInteger nodeId) {

        DHTNode nodeMatch = null;
        DHTNode node = new DHTNode(nodeId, null, 0);
        int index = this.nodes.indexOf(node);

        if (index < this.nodes.size()) {
            DHTNode foundNode = this.nodes.get(index);
            if (foundNode.getId().equals(nodeId)) {
                nodeMatch = foundNode;
            }
        }

        return nodeMatch;
    }

    @Override
    public List<DHTNode> findClosestNodes(final BigInteger nodeId,
            final int returnCount) {

        DHTNode node = new DHTNode(nodeId, null, 0);
        int index = nodes.indexOf(node);

        int fromIndex = index > 0 ? index - 1 : 0;
        int toIndex = index < getTotalNodeCount() ? index + 1
                : getTotalNodeCount();
        int count = toIndex - fromIndex;

        while (count < returnCount) {

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
    public SortedList<DHTNode> getNodes() {
        return nodes;
    }

    /**
     * @return int
     */
    public int getTotalNodeCount() {
        return nodes.size();
    }
}

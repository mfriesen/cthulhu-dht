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
 * DHTNodeRoutingTable interface.
 *
 */
public interface DHTNodeRoutingTable {

    /** Default Max Number of nodes returned on search. */
    int DEFAULT_SEARCH_COUNT = 16;

    /**
     * Adds node to RoutingTable.
     * @param node the node to add
     */
    void addNode(DHTNode node);

    /**
     * Find the node with matching ID or NULL.
     * @param nodeId to find closest nodes to.
     * @return DHTNode
     */
    DHTNode findExactNode(BigInteger nodeId);

    /**
     * Find the closest X nodes to the nodeId.
     * @param nodeId to find closest nodes to.
     * @return List<DHTNode>
     */
    List<DHTNode> findClosestNodes(BigInteger nodeId);

    /**
     * Find the closest X nodes to the nodeId.
     * @param nodeId to find closest nodes to.
     * @param returnCount  max number of nodes to return.
     * @return List<DHTNode>
     */
    List<DHTNode> findClosestNodes(BigInteger nodeId, int returnCount);

    /**
     * @return int
     */
    int getTotalNodeCount();
}
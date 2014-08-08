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

package ca.gobits.dht;

import java.net.InetAddress;
import java.util.List;

import ca.gobits.dht.DHTNode.State;
import ca.gobits.dht.util.SortedCollection;

/**
 * DHTNodeRoutingTable interface.
 *
 */
public interface DHTNodeRoutingTable {

    /** Default Max Number of nodes returned on search. */
    int DEFAULT_SEARCH_COUNT = 16;

    /**
     * Adds node to RoutingTable.
     * @param infoHash  node identifier
     * @param addr   address of node
     * @param port  port
     * @param state  State of Node
     * @return DHTNode
     */
    DHTNode addNode(byte[] infoHash, InetAddress addr, int port, State state);

    /**
     * Find the node with matching ID or NULL.
     * @param nodeId to find closest nodes to.
     * @param ipv6  whether search ipv6 node list
     * @return DHTNode
     */
    DHTNode findExactNode(byte[] nodeId, boolean ipv6);

    /**
     * Find the closest X nodes to the nodeId.
     * @param nodeId to find closest nodes to.
     * @param ipv6  whether search ipv6 node list
     * @return List<DHTNode>
     */
    List<DHTNode> findClosestNodes(byte[] nodeId, boolean ipv6);

    /**
     * Find the closest X nodes to the nodeId.
     * @param nodeId to find closest nodes to.
     * @param max  max number of nodes to return.
     * @param ipv6  whether search ipv6 node list
     * @return List<DHTNode>
     */
    List<DHTNode> findClosestNodes(byte[] nodeId, int max, boolean ipv6);

    /**
     * Remove node from Routing Table.
     * @param node DHTNode
     * @return boolean
     */
    boolean removeNode(DHTNode node);

    /**
     * Finds Bucket Node belongs to.
     * @param nodeId  node to find bucket for
     * @param ipv6  whether ipv6 request
     * @return DHTBucket
     */
    DHTBucket findBucket(final byte[] nodeId, final boolean ipv6);

    /**
     * @param ipv6  whether search ipv6 node list
     * @return int  number of nodes in IPv6 list.
     */
    int getTotalNodeCount(boolean ipv6);

    /**
     * @return int
     */
    int getMaxNodeCount();

    /**
     * Clears Routing Table.
     */
    void clear();

    /**
     * @param ipv6 whether ipv6 request
     * @return SortedCollection<DHTBucket>
     */
    SortedCollection<DHTBucket> getBuckets(final boolean ipv6);
}

package ca.gobits.cthulhu;

import java.math.BigInteger;
import java.util.Collection;

/**
 * DHTRoutingTable interface.
 *
 */
public interface DHTRoutingTable {

    /**
     * Adds node to RoutingTable.
     * @param node the node to add
     */
    void addNode(DHTNode node);

    /**
     * Find the closest X nodes to the nodeId.
     * @param nodeId to find
     * @return Collection<DHTNode>
     */
    Collection<DHTNode> findClosestNodes(BigInteger nodeId);
}

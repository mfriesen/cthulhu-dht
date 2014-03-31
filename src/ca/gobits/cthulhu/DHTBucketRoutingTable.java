package ca.gobits.cthulhu;

import java.math.BigInteger;
import java.util.Collection;

/**
 * Implementation of DHT Bucket Routing Table.
 *
 * http://www.bittorrent.org/beps/bep_0005.html
 *
 */
public class DHTBucketRoutingTable implements DHTRoutingTable {

    /** root node of the routing table. */
    private DHTBucket root;

    /**
     * constructor.
     */
    public DHTBucketRoutingTable() {
        this.root = new DHTBucket(new BigInteger("0"), new BigInteger("0"));
    }

    @Override
    public final void addNode(final DHTNode node) {
        this.root.addNode(node);
    }

    @Override
    public final Collection<DHTNode> findClosestNodes(final BigInteger nodeId) {
        return null;
    }
}

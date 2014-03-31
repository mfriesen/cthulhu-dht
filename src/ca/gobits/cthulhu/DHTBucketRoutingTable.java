package ca.gobits.cthulhu;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Collection;

import ca.gobits.cthulhu.util.DHTUtil;

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
        BigInteger max = new BigDecimal(Math.pow(2, DHTUtil.NODE_ID_LENGTH))
            .toBigInteger();
        this.root = new DHTBucket(BigInteger.ZERO, max);
    }

    @Override
    public final void addNode(final DHTNode node) {
        this.root.addNode(node);
    }

    @Override
    public final Collection<DHTNode> findClosestNodes(final BigInteger nodeId) {
        return null;
    }

    /**
     * @return DHTBucket
     */
    public final DHTBucket getRoot() {
        return root;
    }
}

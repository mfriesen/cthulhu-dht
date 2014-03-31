package ca.gobits.cthulhu;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.math.BigInteger;

import org.junit.Test;

/**
 * DHTBucketRoutingTableTest.
 */
public class DHTBucketRoutingTableTest {

    /**
     * testConstructor01().
     */
    @Test
    public final void testConstructor01() {
        // given
        DHTBucketRoutingTable routingTable = new DHTBucketRoutingTable();

        // when
        DHTBucket result = routingTable.getRoot();

        // then
        assertEquals(0, result.getNodes().size());
        assertEquals(BigInteger.ZERO, result.getMin());
        assertEquals(new BigInteger(
                "1461501637330902918203684832716283019655932542976"),
                result.getMax());
        assertNull(result.getLeft());
        assertNull(result.getRight());
    }

}

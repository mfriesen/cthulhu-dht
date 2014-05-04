package ca.gobits.cthulhu.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.math.BigInteger;
import java.util.Collection;
import java.util.Iterator;

import org.junit.Test;

import ca.gobits.cthulhu.DHTInfoHashRoutingTable;
import ca.gobits.cthulhu.DHTInfoHashRoutingTableBasic;
import ca.gobits.cthulhu.domain.DHTInfoHash;
import ca.gobits.cthulhu.domain.DHTPeer;

/**
 * DHTInfoHashRoutingTableBasic Unit Tests.
 *
 */
public final class DHTInfoHashRoutingTableBasicUnitTest {

    /** DHTInfoHashRoutingTable. */
    private final DHTInfoHashRoutingTable rt =
            new DHTInfoHashRoutingTableBasic();

    /**
     * testFindPeers01() - peer cannot be found.
     */
    @Test
    public void testFindPeers01() {
        // given
        BigInteger infoHash = new BigInteger("123123");

        // when
        Collection<byte[]> result = rt.findPeers(infoHash);

        // then
        assertNull(result);
    }

    /**
     * testFindPeers02() - peer found.
     */
    @Test
    public void testFindPeers02() {
        // given
        BigInteger infoHash = new BigInteger("123123");
        byte[] address = new byte[] {127, 0, 0, 1 };
        int port = 1234;

        rt.addPeer(infoHash, address, port);

        // when
        Collection<byte[]> result = rt.findPeers(infoHash);

        // then
        assertEquals(1, result.size());
        byte[] bytes = result.iterator().next();
        assertEquals(6, bytes.length);
        assertEquals(127, bytes[0]);
        assertEquals(0, bytes[1]);
        assertEquals(0, bytes[2]);
        assertEquals(1, bytes[3]);
        assertEquals(4, bytes[4]);
        assertEquals(-46, bytes[5]);
    }

    /**
     * testAddPeer01() - InfoHash is missing.
     */
    @Test
    public void testAddPeer01() {
        // given
        BigInteger infoHash = new BigInteger("12341");
        byte[] address = new byte[] {127, 0, 0, 1 };
        int port = 1234;

        // when
        assertNull(rt.findInfoHash(infoHash));
        rt.addPeer(infoHash, address, port);
        DHTInfoHash result = rt.findInfoHash(infoHash);

        // then
        assertNotNull(result);
        assertEquals(1, result.getPeers().size());
        DHTPeer l = result.getPeers().iterator().next();
        assertEquals(139637976794322L, l.getAddress());
    }

    /**
     * testAddPeer02() - InfoHash exists.
     */
    @Test
    public void testAddPeer02() {
        // given
        BigInteger infoHash = new BigInteger("12341");
        byte[] address = new byte[] {127, 0, 0, 1 };
        int port = 1234;

        // when
        rt.addPeer(infoHash, address, port);
        assertNotNull(rt.findInfoHash(infoHash));
        rt.addPeer(infoHash, address, port);
        DHTInfoHash result = rt.findInfoHash(infoHash);

        // then
        assertNotNull(result);
        assertEquals(1, result.getPeers().size());
        Iterator<DHTPeer> itr = result.getPeers().iterator();
        assertEquals(139637976794322L, itr.next().getAddress());
    }
}

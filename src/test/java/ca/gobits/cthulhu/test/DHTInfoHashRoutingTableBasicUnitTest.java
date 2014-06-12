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
        Collection<DHTPeer> result = this.rt.findPeers(infoHash.toByteArray());

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

        this.rt.addPeer(infoHash.toByteArray(), address, port);

        // when
        Collection<DHTPeer> result = this.rt.findPeers(infoHash.toByteArray());

        // then
        assertEquals(1, result.size());
        DHTPeer peer = result.iterator().next();
        assertEquals(1, peer.getAddress().length);
        assertEquals(2130706433L, peer.getAddress()[0]);
        assertEquals(port, peer.getPort());
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
        assertNull(this.rt.findInfoHash(infoHash.toByteArray()));
        this.rt.addPeer(infoHash.toByteArray(), address, port);
        DHTInfoHash result = this.rt.findInfoHash(infoHash.toByteArray());

        // then
        assertNotNull(result);
        assertEquals(1, result.getPeers().size());
        DHTPeer l = result.getPeers().iterator().next();
        assertEquals(2130706433L, l.getAddress()[0]);
    }

    /**
     * testAddPeer02() - InfoHash exists.
     */
    @Test
    public void testAddPeer02() {
        // given
        byte[] infoHash = new BigInteger("12341").toByteArray();
        byte[] address = new byte[] {127, 0, 0, 1 };
        int port = 1234;

        // when
        this.rt.addPeer(infoHash, address, port);
        assertNotNull(this.rt.findInfoHash(infoHash));
        this.rt.addPeer(infoHash, address, port);
        DHTInfoHash result = this.rt.findInfoHash(infoHash);

        // then
        assertNotNull(result);
        assertEquals(1, result.getPeers().size());
        Iterator<DHTPeer> itr = result.getPeers().iterator();
        assertEquals(2130706433L, itr.next().getAddress()[0]);
    }
}

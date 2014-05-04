package ca.gobits.cthulhu.domain.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.math.BigInteger;
import java.util.Date;

import org.junit.Test;

import ca.gobits.cthulhu.domain.DHTToken;
import ca.gobits.dht.Arrays;
import ca.gobits.dht.BEncoder;

/**
 * DHTToken Unit Test.
 *
 */
public final class DHTTokenUnitTest {

    /**
     * testConstructor01.
     */
    @Test
    public void testConstructor01() {
        // given
        BigInteger nodeId = new BigInteger("1");
        byte[] addr = new byte[] {12, 12, 12, 12 };
        int port = 80;

        // when
        DHTToken result = new DHTToken(nodeId, addr, port);

        // then
        assertEquals(nodeId, result.getInfoHash());
        assertEquals(13245881253968L, result.getAddress());
        assertEquals(630, result.hashCode());
        assertNotNull(result.getAddedDate());
        assertTrue(result.toString().contains(
                "[id=<null>,infohash=1,address=12.12.12.12:80,addedDate="));
    }

    /**
     * testConstructor02.
     */
    @Test
    public void testConstructor02() {
        // given
        BigInteger nodeId = new BigInteger("1");
        byte[] addr = new byte[] {12, 12, 12, 12 };
        int port = 80;
        Date addedDate = new Date();
        Long id = Long.valueOf(234);

        // when
        DHTToken result = new DHTToken();
        result.setInfoHash(nodeId);
        result.setAddress(Arrays.toLong(BEncoder.compactAddress(addr, port)));
        result.setAddedDate(addedDate);
        result.setId(id);

        // then
        assertEquals(nodeId, result.getInfoHash());
        assertEquals(13245881253968L, result.getAddress());
        assertEquals(630, result.hashCode());
        assertNotNull(result.getAddedDate());
        assertEquals(id, result.getId());
        assertTrue(result.toString().contains(
                "[id=234,infohash=1,address=12.12.12.12:80,addedDate="));
    }

    /**
     * testEquals01()  null object.
     */
    @Test
    public void testEquals01() {
        // given
        BigInteger id = new BigInteger("1");
        byte[] addr = new byte[] {12, 12, 12, 12 };
        int port = 80;
        DHTToken token = new DHTToken(id, addr, port);

        // when
        boolean result = token.equals(null);

        // then
        assertFalse(result);
    }

    /**
     * testEquals02()  same object.
     */
    @Test
    public void testEquals02() {
        // given
        BigInteger id = new BigInteger("1");
        byte[] addr = new byte[] {12, 12, 12, 12 };
        int port = 80;
        DHTToken token = new DHTToken(id, addr, port);

        // when
        boolean result = token.equals(token);

        // then
        assertTrue(result);
    }

    /**
     * testEquals03()  non DHTInfoHash object.
     */
    @Test
    public void testEquals03() {
        // given
        BigInteger id = new BigInteger("1");
        byte[] addr = new byte[] {12, 12, 12, 12 };
        int port = 80;
        DHTToken token = new DHTToken(id, addr, port);

        // when
        boolean result = token.equals("");

        // then
        assertFalse(result);
    }

    /**
     * testEquals04()  equal DHTInfoHash object.
     */
    @Test
    public void testEquals04() {
        // given
        BigInteger id = new BigInteger("1");
        byte[] addr = new byte[] {12, 12, 12, 12 };
        int port = 80;
        DHTToken token = new DHTToken(id, addr, port);
        DHTToken token1 = new DHTToken(id, addr, port);

        // when
        boolean result = token.equals(token1);

        // then
        assertTrue(result);
    }
}

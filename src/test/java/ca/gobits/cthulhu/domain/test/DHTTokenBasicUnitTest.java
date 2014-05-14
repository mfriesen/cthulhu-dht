package ca.gobits.cthulhu.domain.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.math.BigInteger;
import java.util.Date;

import org.junit.Test;

import ca.gobits.cthulhu.domain.DHTToken;
import ca.gobits.cthulhu.domain.DHTTokenBasic;

/**
 * DHTTokenBasic Unit Test.
 *
 */
public final class DHTTokenBasicUnitTest {

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
        DHTToken result = new DHTTokenBasic(nodeId, addr, port);

        // then
        assertEquals(nodeId, result.getInfoHash());
        assertEquals(202116108L, result.getAddress()[0]);
        assertEquals(630, result.hashCode());
        assertNotNull(result.getAddedDate());
        assertTrue(result.toString().contains(
                "infohash=1,address=12.12.12.12,port=80,addedDate="));
    }

    /**
     * testConstructor02.
     */
    @Test
    public void testConstructor02() {
        // given
        BigInteger nodeId = new BigInteger("1");
        long[] addr = new long[] {12 };
        int port = 80;
        Date addedDate = new Date();

        // when
        DHTToken result = new DHTTokenBasic();
        result.setInfoHash(nodeId);
        result.setAddress(addr);
        result.setPort(port);
        result.setAddedDate(addedDate);

        // then
        assertEquals(nodeId, result.getInfoHash());
        assertEquals(12L, result.getAddress()[0]);
        assertEquals(630, result.hashCode());
        assertEquals(port, result.getPort());
        assertNotNull(result.getAddedDate());
        assertTrue(result.toString().contains(
                "infohash=1,address=0.0.0.12,port=80,addedDate="));
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
        DHTToken token = new DHTTokenBasic(id, addr, port);

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
        DHTToken token = new DHTTokenBasic(id, addr, port);

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
        DHTToken token = new DHTTokenBasic(id, addr, port);

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
        DHTToken token = new DHTTokenBasic(id, addr, port);
        DHTToken token1 = new DHTTokenBasic(id, addr, port);

        // when
        boolean result = token.equals(token1);

        // then
        assertTrue(result);
    }
}

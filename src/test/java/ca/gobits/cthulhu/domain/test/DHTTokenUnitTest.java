package ca.gobits.cthulhu.domain.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.math.BigInteger;

import org.junit.Test;

import ca.gobits.cthulhu.domain.DHTToken;

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
        BigInteger id = new BigInteger("1");
        byte[] addr = new byte[] {12, 12, 12, 12 };
        int port = 80;

        // when
        DHTToken result = new DHTToken(id, addr, port);

        // then
        assertEquals(id, result.getId());
        assertEquals(13245881253968L, result.getAddress());
        assertEquals(630, result.hashCode());
        assertNotNull(result.getAddedDate());
        assertTrue(result.toString().endsWith("[id=1,address=13245881253968]"));
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

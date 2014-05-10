package ca.gobits.cthulhu.domain.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import ca.gobits.cthulhu.domain.DHTPeer;

/**
 * DHTPeer Unit Test.
 *
 */
public final class DHTPeerUnitTest {

    /**
     * testConstructor01().
     */
    @Test
    public void testConstructor01() {
        // given
        byte[] addr = new byte[] {3, 4, 5, 6 };
        int port = 8000;

        // when
        DHTPeer result = new DHTPeer(addr, port);

        // then
        assertEquals(1, result.getAddress().length);
        assertEquals(50595078L, result.getAddress()[0]);
    }

    /**
     * testToString01().
     */
    @Test
    public void testToString01() {
        // given
        DHTPeer peer = new DHTPeer();
        peer.setAddress(new long[] {3315799039808L });
        peer.setPort(123);

        // when
        String result = peer.toString();

        // then
        assertTrue(result.startsWith("ca.gobits.cthulhu.domain.DHTPeer"));
        assertTrue(result.endsWith("address=5.6.31.64,port=123]"));
        assertEquals(123, peer.getPort());
    }

    /**
     * testEquals01()  null object.
     */
    @Test
    public void testEquals01() {
        // given
        DHTPeer node = new DHTPeer();

        // when
        boolean result = node.equals(null);

        // then
        assertFalse(result);
    }

    /**
     * testEquals02()  same object.
     */
    @Test
    public void testEquals02() {
        // given
        DHTPeer node = new DHTPeer();

        // when
        boolean result = node.equals(node);

        // then
        assertTrue(result);
    }

    /**
     * testEquals03()  non DHTPeer object.
     */
    @Test
    public void testEquals03() {
        // given
        DHTPeer node = new DHTPeer();

        // when
        boolean result = node.equals("");

        // then
        assertFalse(result);
    }

    /**
     * testEquals04()  equal DHTPeer object.
     */
    @Test
    public void testEquals04() {
        // given
        byte[] addr = new byte[] {3, 4, 5, 6 };
        int port = 8000;

        DHTPeer node = new DHTPeer(addr, port);
        DHTPeer node1 = new DHTPeer(addr, port);

        // when
        boolean result = node.equals(node1);

        // then
        assertTrue(result);
    }

}

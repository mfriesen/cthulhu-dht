package ca.gobits.cthulhu.domain.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;
import java.net.InetAddress;
import java.util.Arrays;

import org.junit.Test;

import ca.gobits.cthulhu.domain.DHTNode;
import ca.gobits.cthulhu.domain.DHTNode.State;
import ca.gobits.cthulhu.domain.DHTNodeFactory;

/**
 * DHTNodeFactory Unit Tests.
 *
 */
public final class DHTNodeFactoryUnitTest {

    /**
     * testConstructorIsPrivate().
     * @throws Exception  Exception
     */
    @Test
    public void testConstructorIsPrivate() throws Exception {
        // given
        Constructor<DHTNodeFactory> constructor = DHTNodeFactory.class
                .getDeclaredConstructor();

        // when
        int result = constructor.getModifiers();

        // then
        assertTrue(Modifier.isPrivate(result));
        constructor.setAccessible(true);
        constructor.newInstance();
    }

    /**
     * testCreate01().
     */
    @Test
    public void testCreate01() {
        // given
        byte[] infoHash = new byte[]{1};
        State state = State.GOOD;

        // when
        DHTNode result = DHTNodeFactory.create(infoHash, state);

        // then
        assertTrue(Arrays.equals(infoHash, result.getInfoHash()));
        assertEquals(state, result.getState());
        assertNotNull(result.getLastUpdated());
    }

    /**
     * testCreate02().
     * @throws Exception  Exception
     */
    @Test
    public void testCreate02() throws Exception {
        // given
        byte[] infoHash = new byte[]{1};
        State state = State.GOOD;
        InetAddress addr = InetAddress.getByName("50.71.214.139");
        int port = 64568;

        // when
        DHTNode result = DHTNodeFactory.create(infoHash, addr, port, state);

        // then
        assertTrue(Arrays.equals(infoHash, result.getInfoHash()));
        assertEquals(state, result.getState());
        assertEquals(64568, result.getPort());
        assertEquals(843568779, result.getAddress()[0]);
        assertNotNull(result.getLastUpdated());
    }

}

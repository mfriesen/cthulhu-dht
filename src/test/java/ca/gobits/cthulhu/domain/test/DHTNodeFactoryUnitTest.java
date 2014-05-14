package ca.gobits.cthulhu.domain.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;
import java.math.BigInteger;
import java.net.InetAddress;
import java.net.InetSocketAddress;

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
        BigInteger infoHash = new BigInteger("1");
        State state = State.GOOD;

        // when
        DHTNode result = DHTNodeFactory.create(infoHash, state);

        // then
        assertEquals(infoHash, result.getInfoHash());
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
        byte[] infoHash = new BigInteger("1").toByteArray();
        State state = State.GOOD;
        InetSocketAddress addr = new InetSocketAddress(
                InetAddress.getByName("50.71.214.139"), 64568);

        // when
        DHTNode result = DHTNodeFactory.create(infoHash, addr, state);

        // then
        assertEquals(new BigInteger(infoHash), result.getInfoHash());
        assertEquals(state, result.getState());
        assertEquals(64568, result.getPort());
        assertEquals(843568779, result.getAddress()[0]);
        assertNotNull(result.getLastUpdated());
    }

}

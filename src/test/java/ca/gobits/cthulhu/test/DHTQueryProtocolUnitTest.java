package ca.gobits.cthulhu.test;

import static org.junit.Assert.assertEquals;

import java.util.Map;

import org.apache.commons.codec.binary.Base64;
import org.easymock.EasyMockRunner;
import org.easymock.EasyMockSupport;
import org.easymock.TestSubject;
import org.junit.Test;
import org.junit.runner.RunWith;

import ca.gobits.cthulhu.DHTQueryProtocol;
import ca.gobits.dht.BDecoder;
import ca.gobits.dht.DHTIdentifier;

/**
 * DHTQueryProtocol Unit Tests.
 *
 */
@RunWith(EasyMockRunner.class)
public final class DHTQueryProtocolUnitTest extends EasyMockSupport {

    /** DHTQueryProtocol. */
    @TestSubject
    private final DHTQueryProtocol dht = new DHTQueryProtocol();

    /** Dummy NodeId. */
    private final byte[] nodeId = DHTIdentifier.sha1("test".getBytes());

    /**
     * testPingQuery01() - generate ping request.
     */
    @SuppressWarnings("unchecked")
    @Test
    public void testPingQuery01() {

        // given
        String transactionId = "aa";

        // when
        replayAll();
        byte[] result = dht.pingQuery(transactionId, nodeId);

        // then
        verifyAll();
        assertEquals(56, result.length);

        Map<Object, Object> map = (Map<Object, Object>) new BDecoder()
                .decode(result);
        assertEquals("aa", new String((byte[]) map.get("t")));
        assertEquals("q", new String((byte[]) map.get("y")));
        assertEquals("ping", new String((byte[]) map.get("q")));

        Map<Object, Object> a = (Map<Object, Object>) map.get("a");
        assertEquals(20, ((byte[]) a.get("id")).length);
        assertEquals(Base64.encodeBase64String(nodeId),
                Base64.encodeBase64String((byte[]) a.get("id")));
    }

    /**
     * testFindNodeQuery01() - generate find_node request.
     */
    @SuppressWarnings("unchecked")
    @Test
    public void testFindNodeQuery01() {

        // given
        String transactionId = "aa";
        byte[] target = new byte[] {-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
                -1, -1, -1, -1, -1, -1, -1, -1, -1 };

        // when
        replayAll();
        byte[] result = dht.findNodeQuery(transactionId, nodeId, target);

        // then
        verifyAll();
        assertEquals(92, result.length);

        Map<Object, Object> map = (Map<Object, Object>) new BDecoder()
                .decode(result);
        assertEquals(transactionId, new String((byte[]) map.get("t")));
        assertEquals("q", new String((byte[]) map.get("y")));
        assertEquals("find_node", new String((byte[]) map.get("q")));

        Map<Object, Object> a = (Map<Object, Object>) map.get("a");
        assertEquals(20, ((byte[]) a.get("id")).length);
        assertEquals(20, ((byte[]) a.get("target")).length);
        assertEquals(Base64.encodeBase64String(nodeId),
                Base64.encodeBase64String((byte[]) a.get("id")));
    }
}

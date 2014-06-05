package ca.gobits.cthulhu.test;

import static org.easymock.EasyMock.expect;
import static org.junit.Assert.assertEquals;

import java.util.Map;

import org.apache.commons.codec.binary.Base64;
import org.easymock.EasyMockRunner;
import org.easymock.EasyMockSupport;
import org.easymock.Mock;
import org.easymock.TestSubject;
import org.junit.Test;
import org.junit.runner.RunWith;

import ca.gobits.cthulhu.DHTQueryProtocol;
import ca.gobits.cthulhu.DHTTokenTable;
import ca.gobits.dht.BDecoder;

/**
 * DHTQueryProtocol Unit Tests.
 *
 */
@RunWith(EasyMockRunner.class)
public final class DHTQueryProtocolUnitTest extends EasyMockSupport {

    /** DHTQueryProtocol. */
    @TestSubject
    private final DHTQueryProtocol dht = new DHTQueryProtocol();

    /** Mock DHTTokenTable. */
    @Mock
    private DHTTokenTable tokens;

    /**
     * testPingQuery01() - generate ping request.
     */
    @SuppressWarnings("unchecked")
    @Test
    public void testPingQuery01() {

        // given

        // when
        expect(tokens.getTransactionId()).andReturn("aa");
        replayAll();
        byte[] result = dht.pingQuery();

        // then
        verifyAll();
        assertEquals(56, result.length);

        Map<Object, Object> map = (Map<Object, Object>) new BDecoder()
                .decode(result);
        assertEquals("aa", new String((byte[]) map.get("t")));
        assertEquals("q", new String((byte[]) map.get("y")));
        assertEquals("ping", new String((byte[]) map.get("q")));

        Map<Object, Object> a = (Map<Object, Object>) map.get("a");
        assertEquals(Base64.encodeBase64String(dht.getNodeId()),
                Base64.encodeBase64String((byte[]) a.get("id")));
    }
}

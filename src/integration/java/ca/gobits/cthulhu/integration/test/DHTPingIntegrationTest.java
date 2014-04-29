package ca.gobits.cthulhu.integration.test;

import static ca.gobits.cthulhu.integration.test.DHTServerIntegrationHelper.runDHTServerInNewThread;
import static ca.gobits.cthulhu.integration.test.DHTServerIntegrationHelper.sendUDPPacket;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.codec.binary.Base64;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import ca.gobits.cthulhu.DHTConfiguration;
import ca.gobits.cthulhu.DHTNodeRoutingTable;
import ca.gobits.cthulhu.DHTServer;
import ca.gobits.dht.BDecoder;
import ca.gobits.dht.BEncoder;

/**
 * DHT Ping Integration Test.
 *
 */
//@RunWith(SpringJUnit4ClassRunner.class)
//@ContextConfiguration(classes = DHTConfiguration.class)
public final class DHTPingIntegrationTest {

    /** Flag to indicate whether Server has been started. */
    private static boolean started = false;

//    @Autowired

    /** Application Context. */
    private final ConfigurableApplicationContext ac =
            new AnnotationConfigApplicationContext(
                    DHTConfiguration.class);

    /** Reference to DHTNodeRoutingTable. */
    private final DHTNodeRoutingTable nodeRoutingTable = ac.getBean(DHTNodeRoutingTable.class);

    /**
     *before().
     * @throws Exception  Exception
     */
    @Before
    public void before() throws Exception {

        if (!started) {
            runDHTServerInNewThread(ac);
            started = true;
        }
    }

    @After
    public void after() {
        ac.getBean(DHTServer.class).shutdown();
    }

    /**
     * testPing01() - test ping request with real response.
     * @throws Exception  Exception
     */
    @SuppressWarnings("unchecked")
    @Test(timeout = 10000)
    public void testPing01() throws Exception {
        // given
        String id = "abcdefghij0123456789";
        Map<String, Object> realResponse = (Map<String, Object>) new BDecoder()
            .decode(getRealPingResponse());

        Map<String, Object> request = createRequest("aa", id.getBytes());

        // when
        byte[] results = sendUDPPacket(BEncoder.bencoding(request)
                .toByteArray());

        // then
        Map<String, Object> response = (Map<String, Object>) new BDecoder()
            .decode(results);

        assertEquals(4, realResponse.size());
        assertEquals(4, response.size());
        assertEquals(new String((byte[]) realResponse.get("t")),
                new String((byte[]) response.get("t")));
        assertEquals(new String((byte[]) realResponse.get("y")),
                new String((byte[]) response.get("y")));
        assertTrue(BDecoder.decodeCompactAddressToString(
                (byte[]) response.get("ip")).startsWith("127.0.0.1"));

        Map<String, Object> r = (Map<String, Object>) response.get("r");
        assertEquals(1, r.size());
        assertArrayEquals(DHTServer.NODE_ID, (byte[]) r.get("id"));
    }

    /**
     * Create "ping" request.
     * @param t t
     * @param id id
     * @return Map<String, Object>
     */
    private Map<String, Object> createRequest(final String t, final byte[] id) {
        Map<String, Object> request = new HashMap<String, Object>();
        request.put("t", t);
        request.put("y", "q");
        request.put("q", "ping");

        Map<String, Object> a = new HashMap<String, Object>();
        a.put("id", id);
        request.put("a", a);

        return request;
    }

    /**
     * @return byte[]
     */
    private byte[] getRealPingResponse() {
        return Base64.decodeBase64(
                "ZDI6aXA2OjJH1ov1nTE6cmQyOmlkMjA6HbzsI8Zpc1H/S"
              + "uwpzbqr8vvjRmdlMTp0MjphYTE6eTE6cmUAAAAAAAAAAAAAAAAAAAAAAAAAAAA"
              + "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA"
              + "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA"
              + "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA"
              + "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA"
              + "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA"
              + "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA"
              + "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA"
              + "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA"
              + "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA"
              + "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA"
              + "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA"
              + "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA"
              + "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA"
              + "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA"
              + "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA"
              + "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA"
              + "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA"
              + "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA"
              + "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA"
              + "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA"
              + "AAAAAAAAAAAAAAAAAAA==");
    }
}

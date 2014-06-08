package ca.gobits.cthulhu.integration.test;

import static ca.gobits.cthulhu.integration.test.DHTServerIntegrationHelper.sendUDPPacket;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.codec.binary.Base64;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import ca.gobits.cthulhu.DHTConfiguration;
import ca.gobits.cthulhu.DHTQueryProtocol;
import ca.gobits.cthulhu.DHTServerConfig;
import ca.gobits.dht.BDecoder;
import ca.gobits.dht.BEncoder;
import ca.gobits.dht.DHTConversion;

/**
 * DHT Ping Integration Test.
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { DHTConfiguration.class,
        IntegrationTestConfiguration.class })
public final class DHTPingIntegrationTest {

    /** Async DHT Server. */
    @Autowired
    private DHTServerAsync async;

    /** DHTQueryProtocol instance. */
    @Autowired
    private DHTQueryProtocol queryProtocol;

    /** DHTServerConfig. */
    @Autowired
    private DHTServerConfig config;

    /** Starts DHTServer.
     * @throws Exception  Exception
     */
    @Before
    public void before() throws Exception {
        async.start();
        async.waitForServerStart();
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
        byte[] results = sendUDPPacket(BEncoder.bencoding(request));

        // then
        Map<String, Object> response = (Map<String, Object>) new BDecoder()
            .decode(results);

        assertEquals(4, realResponse.size());
        assertEquals(4, response.size());
        assertEquals(new String((byte[]) realResponse.get("t")),
                new String((byte[]) response.get("t")));
        assertEquals(new String((byte[]) realResponse.get("y")),
                new String((byte[]) response.get("y")));
        assertTrue(DHTConversion.decodeCompactAddressToString(
                (byte[]) response.get("ip")).startsWith("127.0.0.1"));

        Map<String, Object> r = (Map<String, Object>) response.get("r");
        assertEquals(1, r.size());
        assertArrayEquals(config.getNodeId(), (byte[]) r.get("id"));
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

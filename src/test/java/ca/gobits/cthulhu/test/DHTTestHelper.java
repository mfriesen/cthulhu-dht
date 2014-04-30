//
// Copyright 2013 Mike Friesen
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
// http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.
//

package ca.gobits.cthulhu.test;

import static ca.gobits.dht.Arrays.COMPACT_ADDR_LENGTH;
import static ca.gobits.dht.Arrays.NODE_ID_LENGTH;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;

import org.springframework.context.ApplicationContext;

import ca.gobits.cthulhu.DHTServer;
import ca.gobits.dht.Arrays;
import ca.gobits.dht.BDecoder;

/**
 * Test Helper Class.
 */
public final class DHTTestHelper {

    /** Delay to wait afte starting DHT Server. */
    private static final int START_DELAY_MILLIS = 3000;

    /**
     * private constructor.
     */
    private DHTTestHelper() {
    }

    /**
     * Runs DHT Server in new thread.
     * @param ac ApplicationContext.
     * @param port port
     * @throws Exception  Exception
     */
    public static void runDHTServerInNewThread(final ApplicationContext ac,
            final int port)
            throws Exception {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    ac.getBean(DHTServer.class)
                        .run(port);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        });

        thread.start();
        Thread.sleep(START_DELAY_MILLIS);
    }

    /**
     * Assert two "nodes" results are equal.
     * @param a  byte[]
     * @param b  byte[]
     */
    public static void assertNodesEquals(final byte[] a, final byte[] b) {
        assertEquals(a.length, b.length);

        Map<BigInteger, String> mapA = buildNodeMap(a);
        Map<BigInteger, String> mapB = buildNodeMap(b);

        for (Map.Entry<BigInteger, String> e : mapA.entrySet()) {
            assertTrue(mapB.containsValue(e.getValue()));
            assertTrue(mapB.containsKey(e.getKey()));
        }

    }

    /**
     * Build a Map from a node list.
     * @param a  byte[]
     * @return Map<BigInteger, String>
     */
    private static Map<BigInteger, String> buildNodeMap(final byte[] a) {
        Map<BigInteger, String> map = new HashMap<BigInteger, String>();

        int i = 0;
        while (i < a.length) {
            byte[] key = java.util.Arrays.copyOfRange(a, i, i + NODE_ID_LENGTH);
            i += NODE_ID_LENGTH;
            byte[] ipBytes = java.util.Arrays.copyOfRange(a, i, i
                    + COMPACT_ADDR_LENGTH);
            String addr = BDecoder.decodeCompactAddressToString(ipBytes);
            i += COMPACT_ADDR_LENGTH;

            map.put(Arrays.toBigInteger(key), addr);
        }

        return map;
    }
}

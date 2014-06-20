//
// Copyright 2014 Mike Friesen
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

import static ca.gobits.dht.DHTConversion.COMPACT_ADDR_LENGTH;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.math.BigInteger;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;

import ca.gobits.dht.DHTConversion;

/**
 * Test Helper Class.
 */
public final class DHTTestHelper {

    /** Length Node ID. */
    private static final int NODE_ID_LENGTH = 20;

    /**
     * private constructor.
     */
    private DHTTestHelper() {
    }

    /**
     * Assert two "nodes" results are equal.
     * @param a  byte[]
     * @param b  byte[]
     * @throws UnknownHostException  UnknownHostException
     */
    public static void assertNodesEquals(final byte[] a, final byte[] b)
            throws UnknownHostException {
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
     * @throws UnknownHostException  UnknownHostException
     */
    private static Map<BigInteger, String> buildNodeMap(final byte[] a)
            throws UnknownHostException {
        Map<BigInteger, String> map = new HashMap<BigInteger, String>();

        int i = 0;
        while (i < a.length) {
            byte[] key = java.util.Arrays.copyOfRange(a, i, i + NODE_ID_LENGTH);
            i += NODE_ID_LENGTH;
            byte[] ipBytes = java.util.Arrays.copyOfRange(a, i, i
                    + COMPACT_ADDR_LENGTH);
            InetAddress addr = DHTConversion.compactAddress(ipBytes);
            i += COMPACT_ADDR_LENGTH;

            map.put(DHTConversion.toBigInteger(key), addr.getHostAddress());
        }

        return map;
    }
}

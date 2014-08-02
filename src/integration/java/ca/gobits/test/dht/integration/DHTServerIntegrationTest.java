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

package ca.gobits.test.dht.integration;

import static ca.gobits.test.dht.integration.DHTServerIntegrationHelper.DATA_PACKET_LENGTH;
import static ca.gobits.test.dht.integration.DHTServerIntegrationHelper.sendUDPPacket;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

/**
 * DHTServer UnitTests.
 *
 */
public final class DHTServerIntegrationTest extends AbstractIntegrationTest {

    /**
     * testUnknownMethod01().
     * @throws Exception  Exception
     */
    @Test(timeout = 10000)
    public void testUnknownMethod01() throws Exception {

        // given
        String dat = "d1:ad2:id20:abcdefghij0123456789e1:q4:pink1:t2:aa1:y1:qe";

        // when
        byte[] result = sendUDPPacket(dat.getBytes());

        // then
        assertEquals(DATA_PACKET_LENGTH, result.length);
        String r = new String(result);
        assertTrue(r.contains(":rd3:20414:Method Unknowne1:t2:aa1:y1:ee"));
    }

    /**
     * testServerError01().
     * @throws Exception  Exception
     */
    @Test(timeout = 10000)
    public void testServerError01() throws Exception {

        // given
        String dat = "adsadadsa";

        // when
        byte[] result = sendUDPPacket(dat.getBytes());

        // then
        assertEquals(DATA_PACKET_LENGTH, result.length);
        String r = new String(result);
        assertTrue(r.startsWith("d1:rd3:20212:Server Errore1:y1:ee"));
    }
}

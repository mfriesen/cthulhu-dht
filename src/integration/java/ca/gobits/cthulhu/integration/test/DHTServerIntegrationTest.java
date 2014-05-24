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

package ca.gobits.cthulhu.integration.test;

import static ca.gobits.cthulhu.integration.test.DHTServerIntegrationHelper.DATA_PACKET_LENGTH;
import static ca.gobits.cthulhu.integration.test.DHTServerIntegrationHelper.sendUDPPacket;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import ca.gobits.cthulhu.DHTConfiguration;

/**
 * DHTServer UnitTests.
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { DHTConfiguration.class,
        IntegrationTestConfiguration.class })
public final class DHTServerIntegrationTest {

    /** Async DHT Server. */
    @Autowired
    private DHTServerAsync async;

    /** Starts DHTServer.
     * @throws Exception  Exception
     */
    @Before
    public void before() throws Exception {
        async.start();
        async.waitForServerStart();
    }

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
        String r = new String(result);
        r.contains(":rd3:20414:Method Unknowne1:t2:aa1:y1:ee");
        assertEquals(DATA_PACKET_LENGTH, new String(result).length());
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
        String result = sendUDPPacket(dat);

        // then
        assertTrue(result.startsWith("d1:rd3:20212:Server Errore1:y1:ee"));
        assertEquals(DATA_PACKET_LENGTH, result.length());
    }

    /**
     * testMissingQParameter01().
     * @throws Exception  Exception
     */
    @Test(timeout = 10000)
    public void testMissingQParameter01() throws Exception {

        // given
        String dat = "d1:ad2:id20:abcdefghij0123456789e1:t2:aa1:y1:qe";

        // when
        String result = sendUDPPacket(dat);

        // then
        assertTrue(result.contains(":rd3:20212:Server Errore1:t2:aa1:y1:ee"));
        assertEquals(DATA_PACKET_LENGTH, result.length());
    }
}

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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

import org.junit.BeforeClass;
import org.junit.Test;

import ca.gobits.cthulhu.DHTServer;

/**
 * DHTServer UnitTests.
 *
 */
public final class DHTServerIntegrationTest {

    /** DATA PACKET LENGTH. */
    private static final int DATA_PACKET_LENGTH = 1024;

    /**
     *beforeClass().
     * @throws Exception  Exception
     */
    @BeforeClass
    public static void beforeClass() throws Exception {
        Thread thread = runDHTServerInNewThread(new String[]{});
        thread.start();
        Thread.sleep(1000);
    }

    /**
     * testPing01().
     * @throws Exception  Exception
     */
    @Test
    public void testPing01() throws Exception {

        // given
        String dat = "d1:ad2:id20:abcdefghij0123456789e1:q4:ping1:t2:aa1:y1:qe";

        // when
        String result = sendUDPPacket(dat);

        // then
        assertTrue(result.startsWith("d1:rd2:id20:6h"));
        assertTrue(result.contains("e1:t2:aa1:y1:re"));
        assertEquals(DATA_PACKET_LENGTH, result.length());
    }

    /**
     * testUnknownMethod01().
     * @throws Exception  Exception
     */
    @Test
    public void testUnknownMethod01() throws Exception {

        // given
        String dat = "d1:ad2:id20:abcdefghij0123456789e1:q4:pink1:t2:aa1:y1:qe";

        // when
        String result = sendUDPPacket(dat);

        // then
        assertTrue(result
                .startsWith("d1:rd3:20414:Method Unknowne1:t2:aa1:y1:ee"));
        assertEquals(DATA_PACKET_LENGTH, result.length());
    }

    /**
     * testServerError01().
     * @throws Exception  Exception
     */
    @Test
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
    @Test
    public void testMissingQParameter01() throws Exception {

        // given
        String dat = "d1:ad2:id20:abcdefghij0123456789e1:t2:aa1:y1:qe";

        // when
        String result = sendUDPPacket(dat);

        // then
        assertTrue(
                result.startsWith("d1:rd3:20212:Server Errore1:t2:aa1:y1:ee"));
        assertEquals(DATA_PACKET_LENGTH, result.length());
    }

    /**
     * Runs DHT Server in new thread.
     * @param args argument parameters.
     * @return Runnable
     */
    private static Thread runDHTServerInNewThread(final String[] args) {
        return new Thread(new Runnable() {
            @Override
            public void run() {
                DHTServer.main(args);
            }
        });
    }

    /**
     * Sends UDP Packet to Server.
     * @param msg  String
     * @return String response
     * @throws IOException  IOException
     */
    private String sendUDPPacket(final String msg) throws IOException {

        byte[] sendData = new byte[DATA_PACKET_LENGTH];
        byte[] receiveData = new byte[DATA_PACKET_LENGTH];

        sendData = msg.getBytes();
        InetAddress ipAddress = InetAddress.getByName("localhost");

        DatagramSocket clientSocket = new DatagramSocket();
        DatagramPacket sendPacket = new DatagramPacket(sendData,
                sendData.length, ipAddress, DHTServer.DEFAULT_PORT);
        clientSocket.send(sendPacket);
        DatagramPacket receivePacket = new DatagramPacket(receiveData,
                receiveData.length);
        clientSocket.receive(receivePacket);

        String response = new String(receivePacket.getData());
        clientSocket.close();
        return response;
    }
}

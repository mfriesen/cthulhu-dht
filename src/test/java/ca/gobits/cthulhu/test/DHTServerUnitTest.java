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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

import org.junit.Test;
import org.springframework.test.util.ReflectionTestUtils;

import ca.gobits.cthulhu.DHTServer;

/**
 * DHTServer UnitTests.
 *
 */
public final class DHTServerUnitTest {

    /** DATA PACKET LENGTH. */
    private static final int DATA_PACKET_LENGTH = 1024;

    /**
     * testMain01() unknown parameter.
     */
    @Test
    public void testMain01() {
        // given
        String[] args = new String[] {"-t"};
        ByteArrayOutputStream bo = new ByteArrayOutputStream();
        ReflectionTestUtils.setField(System.out, "out", bo);

        // when
        DHTServer.main(args);

        // then
        assertUsage(bo);
    }

    /**
     * testMain02() -?.
     */
    @Test
    public void testMain02() {
        // given
        String[] args = new String[] {"-?"};
        ByteArrayOutputStream bo = new ByteArrayOutputStream();
        ReflectionTestUtils.setField(System.out, "out", bo);

        // when
        DHTServer.main(args);

        assertUsage(bo);
    }

    /**
     * testMain03() -p with port missing.
     */
    @Test
    public void testMain03() {
        // given
        String[] args = new String[] {"-p"};
        ByteArrayOutputStream bo = new ByteArrayOutputStream();
        ReflectionTestUtils.setField(System.out, "out", bo);

        // when
        DHTServer.main(args);

        assertUsage(bo);
    }

    /**
     * testMain04() -p with port.
     * @throws Exception Exception
     */
    @Test(timeout = 10000)
    public void testMain04() throws Exception {
        // given
        int port = 8000;
        final String[] args = new String[] {"-p", "" + port};

        // when
        Thread result = runDHTServerInNewThread(args);
        result.start();

        // then
        Thread.sleep(1000);

        assertConnectedToServer(port);

        DHTServer.shutdown();
    }

    /**
     * testMain05() default port.
     * @throws Exception Exception
     */
    @Test(timeout = 10000)
    public void testMain05() throws Exception {
        // given
        int port = 8080;
        final String[] args = new String[] {};

        // when
        Thread result = runDHTServerInNewThread(args);
        result.start();

        // then
        Thread.sleep(1000);

        assertConnectedToServer(port);

        DHTServer.shutdown();
    }

    /**
     * Assert can connect to server.
     * @param port Port to Send to
     * @throws IOException  IOException
     */
    private void assertConnectedToServer(final int port) throws IOException {

        byte[] sendData = new byte[DATA_PACKET_LENGTH];
        byte[] receiveData = new byte[DATA_PACKET_LENGTH];

        sendData = "test msg".getBytes();
        InetAddress ipAddress = InetAddress.getByName("localhost");

        DatagramSocket clientSocket = new DatagramSocket();
        DatagramPacket sendPacket = new DatagramPacket(sendData,
                sendData.length, ipAddress, port);
        clientSocket.send(sendPacket);
        DatagramPacket receivePacket = new DatagramPacket(receiveData,
                receiveData.length);
        clientSocket.receive(receivePacket);

        String response = new String(receivePacket.getData());
        assertTrue(response.startsWith("d1:rd3:20212:Server Errore1:y1:ee"));
        clientSocket.close();
    }

    /**
     * Assert Usage Message is shown.
     * @param bo ByteArrayOutputStream
     */
    private void assertUsage(final ByteArrayOutputStream bo) {
        String expected = "usage: java -jar dht.jar\nParameters\n"
            + " -?         help\n"
            + " -p <arg>   bind to port\n";

        assertEquals(expected, bo.toString());
    }

    /**
     * Runs DHT Server in new thread.
     * @param args argument parameters.
     * @return Runnable
     */
    private Thread runDHTServerInNewThread(final String[] args) {
        return new Thread(new Runnable() {
            @Override
            public void run() {
                DHTServer.main(args);
            }
        });
    }

}

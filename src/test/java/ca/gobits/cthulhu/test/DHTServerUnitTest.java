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

import static ca.gobits.cthulhu.test.DHTTestHelper.runDHTServerInNewThread;
import static org.junit.Assert.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

import org.junit.Test;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.test.util.ReflectionTestUtils;

import ca.gobits.cthulhu.DHTConfiguration;
import ca.gobits.cthulhu.DHTServer;

/**
 * DHTServer UnitTests.
 *
 */
public final class DHTServerUnitTest {

    /** DATA PACKET LENGTH. */
    private static final int DATA_PACKET_LENGTH = 1024;

    /**
     * testMain01() -?.
     */
    @Test
    public void testMain01() {
        // given
        String[] args = new String[] {"-?"};
        ByteArrayOutputStream bo = new ByteArrayOutputStream();
        ReflectionTestUtils.setField(System.out, "out", bo);

        // when
        DHTServer.main(args);

        assertUsage(bo);
    }

    /**
     * testMain02() -p with port.
     * @throws Exception Exception
     */
    @Test(timeout = 10000)
    public void testMain02() throws Exception {
        // given
        ConfigurableApplicationContext ac =
                new AnnotationConfigApplicationContext(
                        DHTConfiguration.class);
        int port = 8000;

        try {
            // when
            runDHTServerInNewThread(ac, port);

            // then
            assertConnectedToServer(port);
        } finally {
            ac.close();
        }
    }

    /**
     * testMain03() default port.
     * @throws Exception Exception
     */
    @Test(timeout = 10000)
    public void testMain03() throws Exception {
        // given
        int port = 8080;

        ConfigurableApplicationContext ac =
                new AnnotationConfigApplicationContext(
                        DHTConfiguration.class);

        // when
        try {
            // when
            runDHTServerInNewThread(ac, port);

            // then
            assertConnectedToServer(port);
        } finally {
            ac.close();
        }
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

        assertTrue(bo.toString().contains(expected));
    }
}

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

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

import org.apache.commons.codec.binary.Base64;
import org.apache.log4j.Logger;

/**
 * Helper Class to test sending UDP messages.
 *
 */
public final class UDPClient {

    /** Send/Receive Packet Size. */
    private static final int PACKET_SIZE = 1024;

    /** Logger. */
    private static final Logger LOGGER = Logger.getLogger(UDPClient.class);

    /** Host to send UDP Message to. */
    private static final String HOST = "router.bittorrent.com";

    /** Port to send UDP Message to. */
    private static final int PORT = 6881;

    /**
     * private constructor.
     */
    private UDPClient() {
    }

    /**
     * Creates and Sends UDP Message.
     * @param args  args
     * @throws Exception  Exception
     */
    public static void main(final String[] args) throws Exception {

        String message = "d1:ad2:id20:abcdefghij01234567899:info_hash20:"
                + "mnopqrstuvwxyz123456e1:q9:get_peers1:t2:aa1:y1:qe";

        DatagramSocket clientSocket = new DatagramSocket();
        InetAddress address = InetAddress.getByName(HOST);
        byte[] sendData = new byte[PACKET_SIZE];
        byte[] receiveData = new byte[PACKET_SIZE];

        sendData = message.getBytes();
        DatagramPacket sendPacket = new DatagramPacket(sendData,
                sendData.length, address, PORT);
        clientSocket.send(sendPacket);
        DatagramPacket receivePacket = new DatagramPacket(receiveData,
                receiveData.length);
        clientSocket.receive(receivePacket);

        byte[] response = receivePacket.getData();

        LOGGER.info(Base64.encodeBase64String(response));

        clientSocket.close();
    }
}

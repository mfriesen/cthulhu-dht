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

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.HashMap;
import java.util.Map;

import ca.gobits.cthulhu.DHTNode;
import ca.gobits.cthulhu.DHTServerConfig;
import ca.gobits.dht.BEncoder;

/**
 * Helper class for Integration Testing the DHT Server.
 *
 */
public final class DHTServerIntegrationHelper {

    /** DATA PACKET LENGTH. */
    public static final int DATA_PACKET_LENGTH = 1024;

    /**
     * private constructor.
     */
    private DHTServerIntegrationHelper() {
    }

    /**
     * Sends UDP Packet to Server.
     * @param msg  String
     * @return String response
     * @throws IOException  IOException
     */
    public static String sendUDPPacket(final String msg) throws IOException {
        return new String(sendUDPPacket(msg.getBytes()));
    }

    /**
     * Sends UDP Packet to Server.
     * @param bytes  bytes array
     * @return bytes[] response
     * @throws IOException  IOException
     */
    public static byte[] sendUDPPacket(final byte[] bytes) throws IOException {

        byte[] sendData = new byte[DATA_PACKET_LENGTH];
        byte[] receiveData = new byte[DATA_PACKET_LENGTH];

        sendData = bytes;
        InetAddress ipAddress = InetAddress.getByName("localhost");

        DatagramSocket clientSocket = new DatagramSocket();
        DatagramPacket sendPacket = new DatagramPacket(sendData,
                sendData.length, ipAddress, DHTServerConfig.DEFAULT_PORT);
        clientSocket.send(sendPacket);
        DatagramPacket receivePacket = new DatagramPacket(receiveData,
                receiveData.length);
        clientSocket.receive(receivePacket);

        clientSocket.close();
        return receivePacket.getData();
    }

    /**
     * Creates a ping request in BEncode format.
     * @param node  DHTNode to encode
     * @return byte[]
     */
    public static byte[] createPingRequest(final DHTNode node) {

        Map<String, Object> request = new HashMap<String, Object>();
        request.put("t", "aa");
        request.put("y", "q");
        request.put("q", "ping");

        Map<String, Object> a = new HashMap<String, Object>();
        a.put("id", node.getId().toByteArray());
        request.put("a", a);

        return BEncoder.bencoding(request).toByteArray();

    }
}

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

package ca.gobits.cthulhu;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

/**
 * Runnable Wrapper for the DHTProtocol.
 *
 */
public class DHTProtocolRunnable implements Runnable {

    /** DatagramSocket. */
    private final DatagramSocket serverSocket;

    /** DHTProtocolHandler. */
    private final DHTProtocolHandler handler;

    /** Receieved DatagramPacket. */
    private final DatagramPacket packet;

    /**
     *
     * @param socket  SocketServer
     * @param protocolHandler  DHTProtocolHandler
     * @param receivePacket  Received Packet
     */
    public DHTProtocolRunnable(final DatagramSocket socket,
            final DHTProtocolHandler protocolHandler,
            final DatagramPacket receivePacket) {
        this.serverSocket = socket;
        this.handler = protocolHandler;
        this.packet = receivePacket;
    }

    @Override
    public void run() {

        try {

            InetAddress addr = this.packet.getAddress();
            int port = this.packet.getPort();

            byte[] bytes = this.handler.handle(this.packet);

            if (bytes != null) {

                DatagramPacket sendPacket = new DatagramPacket(bytes,
                        bytes.length, addr, port);
                this.serverSocket.send(sendPacket);
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}

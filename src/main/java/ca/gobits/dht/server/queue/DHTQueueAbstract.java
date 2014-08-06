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

package ca.gobits.dht.server.queue;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.DelayQueue;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Abstract class for the implementation of sending
 * delayed DHT Requests.
 * @param <T> type of DelayObject<T>
 */
public abstract class DHTQueueAbstract<T> {

    /** DHTPingQueue Logger. */
    private static final Logger LOGGER = Logger
            .getLogger(DHTQueueAbstract.class);

    /** Reference to DatagramSocket. */
    @Autowired
    private DatagramSocket socket;

    /** Reference to DHTTokenTable. */
    @Autowired
    private DHTTokenQueue tokens;

    /**
     * Delay in Millis for adding nodes to queue.
     */
    private long delayInMillis = 0;

    /** Queue of requests. */
    private final BlockingQueue<DelayObject<T>> queue =
            new DelayQueue<DelayObject<T>>();

    /**
     * Sends Request to Socket.
     * @param addr  InetAddress
     * @param port  port
     * @param msg  byte[]
     */
    protected void sendToSocket(final InetAddress addr, final int port,
            final byte[] msg) {

        DatagramPacket packet = new DatagramPacket(msg, msg.length,
                addr, port);

        try {
            this.socket.send(packet);
        } catch (Exception e) {
            LOGGER.trace(e.getMessage(), e);
        }
    }

    /**
     * Sets the delay between receiving a
     * request and the time the actual request is sent.
     * @param delay  delay in milliseconds
     */
    public void setDelayInMillis(final long delay) {
        this.delayInMillis = delay;
    }

    /**
     * @return long
     */
    public long getDelayInMillis() {
        return this.delayInMillis;
    }

    /**
     * @return BlockingQueue<DelayObject<byte[]>>
     */
    public BlockingQueue<DelayObject<T>> getQueue() {
        return this.queue;
    }

    /**
     * Size of Queue.
     * @return int
     */
    public int size() {
        return this.queue.size();
    }

    /**
     * @return DatagramSocket
     */
    public DatagramSocket getSocket() {
        return this.socket;
    }

    /**
     * Gets Transaction ID.
     * @return String
     */
    public String getTransactionId() {
        return this.tokens.getTransactionId();
    }
}

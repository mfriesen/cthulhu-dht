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

package ca.gobits.test.dht.server.queue;

import static org.easymock.EasyMock.capture;
import static org.easymock.EasyMock.expect;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Arrays;
import java.util.concurrent.BlockingQueue;

import org.apache.commons.codec.binary.Base64;
import org.easymock.Capture;
import org.easymock.EasyMockRunner;
import org.easymock.EasyMockSupport;
import org.easymock.Mock;
import org.easymock.TestSubject;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.util.ReflectionTestUtils;

import ca.gobits.dht.DHTIdentifier;
import ca.gobits.dht.server.DHTServerConfig;
import ca.gobits.dht.server.queue.DHTPingQueueImpl;
import ca.gobits.dht.server.queue.DHTTokenQueue;
import ca.gobits.dht.server.queue.DelayObject;

/**
 * DHTPingQueue Unit Tests.
 *
 */
@RunWith(EasyMockRunner.class)
public final class DHTPingQueueImplUnitTest extends EasyMockSupport {

    /** Instance of DHTPingQueue. */
    @TestSubject
    private final DHTPingQueueImpl pingQueue = new DHTPingQueueImpl();

    /** Mock DHTServerConfig. */
    @Mock
    private DHTServerConfig config;

    /** Mock DHTTokenTable. */
    @Mock
    private DHTTokenQueue tokens;

    /** Mock DatagramSocket. */
    @Mock
    private DatagramSocket socket;

    /** Capture<DatagramPacket>. */
    private final Capture<DatagramPacket> cap0 = new Capture<DatagramPacket>();

    /** Reference to BlockingQueue. */
    @SuppressWarnings("unchecked")
    private final BlockingQueue<DelayObject<byte[]>> queue
        = (BlockingQueue<DelayObject<byte[]>>)
            ReflectionTestUtils.getField(this.pingQueue, "queue");

    /**
     * testPing01().
     * @throws Exception   Exception
     */
    @Test
    public void testPing01() throws Exception {
        // given
        int port = 1234;
        InetAddress addr = InetAddress.getByName("127.0.0.1");

        // when
        this.pingQueue.pingWithDelay(addr, port);

        // then
        assertEquals(1, this.pingQueue.size());
    }

    /**
     * testProcessPingQueue01().
     * @throws Exception  Exception
     */
    @Test
    public void testProcessQueue01() throws Exception {

        // given
        this.pingQueue.setDelayInMillis(0);

        byte[] nodeId = DHTIdentifier.sha1("salt".getBytes());
        byte[] payload = new byte[]{127, 0, 0, 1, 9, 41};

        DelayObject<byte[]> obj = new DelayObject<byte[]>(payload, 0);
        this.queue.add(obj);

        // when
        expect(this.config.getNodeId()).andReturn(nodeId);
        expect(this.tokens.getTransactionId()).andReturn("aa");

        this.socket.send(capture(this.cap0));

        replayAll();

        this.pingQueue.processQueue();

        // then
        verifyAll();

        DatagramPacket packet = this.cap0.getValue();
        assertTrue(Arrays.equals(new byte[] {127, 0, 0, 1}, packet
                .getAddress().getAddress()));
        assertEquals(2345, packet.getPort());
        assertEquals(
                "ZDE6YWQyOmlkMjA6spXRFxNal2PaKC59rnOlyn0+WxFl"
                + "MTpxNDpwaW5nMTp0MjphYTE6eTE6cWU=",
                Base64.encodeBase64String(packet.getData()));
    }
}

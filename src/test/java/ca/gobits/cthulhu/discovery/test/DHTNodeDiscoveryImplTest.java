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

package ca.gobits.cthulhu.discovery.test;

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

import ca.gobits.cthulhu.DHTServerConfig;
import ca.gobits.cthulhu.DHTTokenTable;
import ca.gobits.cthulhu.discovery.DHTNodeDiscovery;
import ca.gobits.cthulhu.discovery.DHTNodeDiscoveryImpl;
import ca.gobits.cthulhu.discovery.DelayObject;
import ca.gobits.dht.DHTIdentifier;

/**
 * DHTNodeDiscovery Unit Tests.
 *
 */
@SuppressWarnings("unchecked")
@RunWith(EasyMockRunner.class)
public final class DHTNodeDiscoveryImplTest extends EasyMockSupport {

    /** Instance of DHTNodeDiscovery. */
    @TestSubject
    private final DHTNodeDiscovery discovery = new DHTNodeDiscoveryImpl();

    /** Mock DHTServerConfig. */
    @Mock
    private DHTServerConfig config;

    /** Mock DHTTokenTable. */
    @Mock
    private DHTTokenTable tokens;

    /** Mock DatagramSocket. */
    @Mock
    private DatagramSocket socket;

    /** Capture<DatagramPacket>. */
    private final Capture<DatagramPacket> cap0 = new Capture<DatagramPacket>();

    /** Capture<DatagramPacket>. */
    private final Capture<DatagramPacket> cap1 = new Capture<DatagramPacket>();

    /** Reference to BlockingQueue. */
    private final BlockingQueue<DelayObject<byte[]>> queue
        = (BlockingQueue<DelayObject<byte[]>>)
            ReflectionTestUtils.getField(this.discovery, "delayed");

    /**
     * testAddNode01().
     * @throws Exception  Exception
     */
    @Test
    public void testAddNode01() throws Exception {

        // given
        int port = 2345;
        InetAddress addr = InetAddress.getByName("127.0.0.1");

        // when
        replayAll();

        this.discovery.addNode(addr, port);

        // then
        verifyAll();

        assertEquals(1, this.queue.size());
    }

    /**
     * testProcess01().
     * @throws Exception  Exception
     */
    @Test
    public void testProcess01() throws Exception {

        // given
        byte[] nodeId = DHTIdentifier.sha1("salt".getBytes());
        byte[] payload = new byte[]{127, 0, 0, 1, 9, 41};

        DelayObject<byte[]> obj = new DelayObject<byte[]>(payload, 0);
        this.queue.add(obj);

        // when
        expect(this.config.getNodeId()).andReturn(nodeId).times(2);
        expect(this.tokens.getTransactionId()).andReturn("aa");
        expect(this.socket.getLocalAddress()).andReturn(
                InetAddress.getByName("127.0.0.1"));

        this.socket.send(capture(this.cap0));

        replayAll();

        this.discovery.process();

        // then
        verifyAll();

        DatagramPacket packet = this.cap0.getValue();
        assertTrue(Arrays.equals(new byte[] {127, 0, 0, 1}, packet
                .getAddress().getAddress()));
        assertEquals(2345, packet.getPort());
        assertEquals(
                "ZDE6YWQyOmlkMjA6spXRFxNal2PaKC59rnOlyn0+WxE2OnRhcmdldDIwOrKV0R"
                + "cTWpdj2igufa5zpcp9PlsRNDp3YW50bDI6bjRlZTE6cTk6ZmluZF9ub2RlMT"
                + "p0MjphYTE6eTE6cWU=",
                Base64.encodeBase64String(packet.getData()));
    }

    /**
     * Test boot straping server.
     * @throws Exception   Exception
     */
    @Test
    public void testBootstrap01() throws Exception {
        // given
        byte[] nodeId = DHTIdentifier.sha1("salt".getBytes());

        int port = 2345;
        InetAddress addr = InetAddress.getByName("127.0.0.1");

        // when
        expect(this.config.getNodeId()).andReturn(nodeId).times(3);
        expect(this.tokens.getTransactionId()).andReturn("aa").times(2);
        expect(this.socket.getLocalAddress()).andReturn(
                InetAddress.getByName("127.0.0.1")).times(2);

        this.socket.send(capture(this.cap0));
        this.socket.send(capture(this.cap1));

        replayAll();

        this.discovery.bootstrap(addr, port);

        // then
        verifyAll();

        DatagramPacket packet = this.cap0.getValue();
        assertTrue(Arrays.equals(new byte[] {127, 0, 0, 1}, packet
                .getAddress().getAddress()));
        assertEquals(2345, packet.getPort());
        assertEquals(
                "ZDE6YWQyOmlkMjA6spXRFxNal2PaKC59rnOlyn0+WxE2OnRhcmdldDIwOrKV0R"
                + "cTWpdj2igufa5zpcp9PlsRNDp3YW50bDI6bjRlZTE6cTk6ZmluZF9ub2RlMT"
                + "p0MjphYTE6eTE6cWU=",
                Base64.encodeBase64String(packet.getData()));

        packet = this.cap1.getValue();
        assertTrue(Arrays.equals(new byte[] {127, 0, 0, 1}, packet
                .getAddress().getAddress()));
        assertEquals(2345, packet.getPort());
        assertEquals(
                "ZDE6YWQyOmlkMjA6spXRFxNal2PaKC59rnOlyn0+WxE2OnRhcmdldDIwOk1qLu"
                + "jspWicJdfRglGMWjWCwaTuNDp3YW50bDI6bjRlZTE6cTk6ZmluZF9ub2RlMT"
                + "p0MjphYTE6eTE6cWU=",
                Base64.encodeBase64String(packet.getData()));
    }
}

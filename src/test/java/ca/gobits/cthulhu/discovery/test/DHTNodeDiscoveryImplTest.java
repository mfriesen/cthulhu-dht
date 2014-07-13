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

    /** Reference to BlockingQueue. */
    @SuppressWarnings("unchecked")
    private final BlockingQueue<DelayObject<byte[]>> pingQueue
        = (BlockingQueue<DelayObject<byte[]>>)
            ReflectionTestUtils.getField(this.discovery, "pingQueue");

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
        this.discovery.ping(addr, port);

        // then
        assertEquals(1, this.pingQueue.size());
    }

    /**
     * testProcessPingQueue01().
     * @throws Exception  Exception
     */
    @Test
    public void testProcessPingQueue01() throws Exception {

        // given
        this.discovery.setPingDelayInMillis(0);

        byte[] nodeId = DHTIdentifier.sha1("salt".getBytes());
        byte[] payload = new byte[]{127, 0, 0, 1, 9, 41};

        DelayObject<byte[]> obj = new DelayObject<byte[]>(payload, 0);
        this.pingQueue.add(obj);

        // when
        expect(this.config.getNodeId()).andReturn(nodeId);
        expect(this.tokens.getTransactionId()).andReturn("aa");

        this.socket.send(capture(this.cap0));

        replayAll();

        this.discovery.processPingQueue();

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

    /**
     * Test find nodes IPv4 request.
     * @throws Exception   Exception
     */
    @Test
    public void testFindNodes01() throws Exception {
        // given
        byte[] nodeId = DHTIdentifier.sha1("salt".getBytes());
        byte[] target = DHTIdentifier.sha1("salt123".getBytes());

        int port = 2345;
        InetAddress addr = InetAddress.getByName("127.0.0.1");

        // when
        expect(this.config.getNodeId()).andReturn(nodeId);
        expect(this.tokens.getTransactionId()).andReturn("aa");
        expect(this.socket.getLocalAddress()).andReturn(
                InetAddress.getByName("127.0.0.1"));

        this.socket.send(capture(this.cap0));

        replayAll();

        this.discovery.findNodes(addr, port, target);

        // then
        verifyAll();

        DatagramPacket packet = this.cap0.getValue();
        assertTrue(Arrays.equals(new byte[] {127, 0, 0, 1}, packet
                .getAddress().getAddress()));
        assertEquals(2345, packet.getPort());
        assertEquals(
                "ZDE6YWQyOmlkMjA6spXRFxNal2PaKC59rnOlyn0+WxE2OnRhcmdldDIwOq79Fb"
                + "Yd72ZumDL/mb+a3Ms9OMRcNDp3YW50bDI6bjRlZTE6cTk6ZmluZF9ub2RlMT"
                + "p0MjphYTE6eTE6cWU=",
                Base64.encodeBase64String(packet.getData()));
    }

    /**
     * Test find nodes IPv6 request.
     * @throws Exception   Exception
     */
    @Test
    public void testFindNodes02() throws Exception {
        // given
        byte[] nodeId = DHTIdentifier.sha1("salt".getBytes());
        byte[] target = DHTIdentifier.sha1("salt123".getBytes());

        int port = 2345;
        InetAddress addr = InetAddress
                .getByName("805b:2d9d:dc28:0000:0000:fc57:d4c8:1fff");


        // when
        expect(this.config.getNodeId()).andReturn(nodeId);
        expect(this.tokens.getTransactionId()).andReturn("aa");
        expect(this.socket.getLocalAddress()).andReturn(addr);

        this.socket.send(capture(this.cap0));

        replayAll();

        this.discovery.findNodes(addr, port, target);

        // then
        verifyAll();

        DatagramPacket packet = this.cap0.getValue();

        assertTrue(Arrays.equals(new byte[] {-128, 91, 45, -99, -36, 40, 0, 0,
                0, 0, -4, 87, -44, -56, 31, -1 }, packet.getAddress()
                .getAddress()));
        assertEquals(2345, packet.getPort());
        assertEquals(
                "ZDE6YWQyOmlkMjA6spXRFxNal2PaKC59rnOlyn0+WxE2OnRhcmdldDIwOq79Fb"
                + "Yd72ZumDL/mb+a3Ms9OMRcNDp3YW50bDI6bjZlZTE6cTk6ZmluZF9ub2RlMT"
                + "p0MjphYTE6eTE6cWU=",
                Base64.encodeBase64String(packet.getData()));
    }

}

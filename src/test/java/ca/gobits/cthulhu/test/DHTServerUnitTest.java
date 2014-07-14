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

package ca.gobits.cthulhu.test;

import static org.easymock.EasyMock.eq;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.isA;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

import org.apache.log4j.Level;
import org.easymock.EasyMockRunner;
import org.easymock.EasyMockSupport;
import org.easymock.Mock;
import org.easymock.TestSubject;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.test.util.ReflectionTestUtils;

import ca.gobits.cthulhu.DHTProtocolHandler;
import ca.gobits.cthulhu.DHTServer;
import ca.gobits.cthulhu.DHTServerConfig;
import ca.gobits.cthulhu.queue.DHTFindNodeQueue;
import ca.gobits.dht.DHTIdentifier;

/**
 * DHTServer UnitTests.
 *
 */
@SuppressWarnings("boxing")
@RunWith(EasyMockRunner.class)
public final class DHTServerUnitTest extends EasyMockSupport {

    /** DHTServer instance. */
    @TestSubject
    private final DHTServer server = new DHTServer();

    /** Mock DHT Protocol Handler. */
    @Mock
    private DHTProtocolHandler dhtHandler;

    /** Mock Thread Pool Executor. */
    @Mock
    private ThreadPoolTaskExecutor socketThreadPool;

    /** Mock Datagram Socket. */
    @Mock
    private DatagramSocket serverSocket;

    /** Mock DHTServerConfig. */
    @Mock
    private DHTServerConfig config;

    /** Mock DHTFindNodeQueue. */
    @Mock
    private DHTFindNodeQueue findNodeQueue;

    /** Node ID. */
    private final byte[] nodeId = DHTIdentifier.sha1("salt".getBytes());

    /**
     * before().
     */
    @Before
    public void before() {
        ReflectionTestUtils.setField(this.server, "stop", Boolean.TRUE);
    }

    /**
     * testStart01().
     * @throws Exception  Exception
     */
    @Test
    public void testStart01() throws Exception {
        // given
        int port = 6881;

        // when
        expect(this.config.getNodeId()).andReturn(this.nodeId);
        expect(this.serverSocket.getLocalPort()).andReturn(port);
        expect(this.config.getBootstrapNodes()).andReturn(null);
        expect(this.config.getLogLevel()).andReturn(Level.INFO);
        this.serverSocket.receive(isA(DatagramPacket.class));
        this.socketThreadPool.execute(isA(Runnable.class));
        this.socketThreadPool.shutdown();
        this.serverSocket.close();

        replayAll();
        this.server.start();

        // then
        verifyAll();
    }


    /**
     * testStart02() - with bootstrap nodes.
     * @throws Exception  Exception
     */
    @Test
    public void testStart02() throws Exception {
        // given
        int port = 6881;
        String[] nodes = new String[] {"127.0.0.1:7789"};

        // when
        expect(this.config.getNodeId()).andReturn(this.nodeId).times(2);
        expect(this.serverSocket.getLocalPort()).andReturn(port);
        expect(this.config.getBootstrapNodes()).andReturn(nodes);
        this.findNodeQueue.findNodes(isA(InetAddress.class), eq(7789),
                eq(this.nodeId));

        expect(this.config.getLogLevel()).andReturn(Level.INFO);
        this.serverSocket.receive(isA(DatagramPacket.class));
        this.socketThreadPool.execute(isA(Runnable.class));
        this.socketThreadPool.shutdown();
        this.serverSocket.close();

        replayAll();
        this.server.start();

        // then
        verifyAll();
    }
}

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

import static ca.gobits.dht.factory.DHTNodeFactory.create;
import static org.easymock.EasyMock.expect;
import static org.junit.Assert.assertEquals;

import java.net.InetAddress;
import java.util.Date;

import org.easymock.EasyMockRunner;
import org.easymock.EasyMockSupport;
import org.easymock.Mock;
import org.easymock.TestSubject;
import org.junit.Test;
import org.junit.runner.RunWith;

import ca.gobits.dht.DHTIdentifier;
import ca.gobits.dht.DHTNode;
import ca.gobits.dht.DHTNode.State;
import ca.gobits.dht.DHTNodeRoutingTable;
import ca.gobits.dht.server.queue.DHTBucketStatusQueue;
import ca.gobits.dht.server.queue.DHTNodeStatusQueueImpl;
import ca.gobits.dht.server.queue.DHTPingQueue;
import ca.gobits.dht.server.queue.DelayObject;
import ca.gobits.dht.util.DateHelper;

/**
 * DHTNodeStatusQueueImpl Unit Tests.
 *
 */
@SuppressWarnings("boxing")
@RunWith(EasyMockRunner.class)
public class DHTNodeStatusQueueImplUnitTest extends EasyMockSupport {

    /** Instance of DHTNodeStatusQueue. */
    @TestSubject
    private final DHTNodeStatusQueueImpl nodeStatusQueue
        = new DHTNodeStatusQueueImpl();

    /** Mock DHTNodeRoutingTable. */
    @Mock
    private DHTNodeRoutingTable rt;

    /** Mock DHTBucketStatusQueue. */
    @Mock
    private DHTBucketStatusQueue bucketStatusQueue;

    /** Mock DHTPingQueue. */
    @Mock
    private DHTPingQueue pingQueue;

    /**
     * Update Status for Existing Node.
     */
    @Test
    public void testUpdateExistingNodeToGood01() {
        // given
        byte[] nodeId = DHTIdentifier.getRandomNodeId();
        boolean ipv6 = false;
        DHTNode node = new DHTNode();

        // when
        expect(this.rt.findExactNode(nodeId, ipv6)).andReturn(node);
        this.bucketStatusQueue.updateBucketLastChanged(nodeId, ipv6);

        replayAll();
        this.nodeStatusQueue.updateExistingNodeToGood(nodeId, ipv6);

        // then
        verifyAll();
        assertEquals(State.GOOD, node.getState());
    }

    /**
     * Update Status for NON-Existing Node.
     */
    @Test
    public void testUpdateExistingNodeToGood02() {
        // given
        byte[] nodeId = DHTIdentifier.getRandomNodeId();
        boolean ipv6 = false;
        DHTNode node = null;

        // when
        expect(this.rt.findExactNode(nodeId, ipv6)).andReturn(node);

        replayAll();
        this.nodeStatusQueue.updateExistingNodeToGood(nodeId, ipv6);

        // then
        verifyAll();
    }

    /**
     * Update Status for Existing Node.
     * @throws Exception Exception
     */
    @Test
    public void testReceivedFindNodeResponse01() throws Exception {
        // given
        int port = 8080;
        boolean ipv6 = false;
        DHTNode node = new DHTNode();
        byte[] nodeId = DHTIdentifier.getRandomNodeId();
        InetAddress addr = InetAddress.getByName("127.0.0.1");

        // when
        expect(this.rt.findExactNode(nodeId, ipv6)).andReturn(node);
        this.bucketStatusQueue.updateBucketLastChanged(nodeId, ipv6);

        replayAll();
        this.nodeStatusQueue.receivedFindNodeResponse(nodeId, addr, port, ipv6);

        // then
        verifyAll();
        assertEquals(State.GOOD, node.getState());
    }

    /**
     * Update Status for NON-Existing Node.
     * @throws Exception Exception
     */
    @Test
    public void testReceivedFindNodeResponse02() throws Exception {
        // given
        int port = 8080;
        DHTNode node = null;
        boolean ipv6 = false;
        byte[] nodeId = DHTIdentifier.getRandomNodeId();
        InetAddress addr = InetAddress.getByName("127.0.0.1");

        // when
        expect(this.rt.findExactNode(nodeId, ipv6)).andReturn(node);
        expect(this.rt.addNode(nodeId, addr, port, State.GOOD)).andReturn(null);
        this.bucketStatusQueue.updateBucketLastChanged(nodeId, ipv6);

        replayAll();
        this.nodeStatusQueue.receivedFindNodeResponse(nodeId, addr, port, ipv6);

        // then
        verifyAll();
    }

    /**
     * testProcessQueue01() - test node in "Good" state.
     * @throws Exception Exception
     */
    @Test
    public void testProcessQueue01() throws Exception {
        // given
        int port = 8080;
        State state = State.GOOD;
        byte[] nodeId = DHTIdentifier.getRandomNodeId();
        InetAddress addr = InetAddress.getByName("127.0.0.1");
        DHTNode node = create(nodeId, addr, port, state);
        node.setLastUpdated(DateHelper.addMinutesToDate(new Date(), -20));

        DelayObject<DHTNode> obj = new DelayObject<DHTNode>(node, 0);
        this.nodeStatusQueue.getQueue().add(obj);

        // when
        this.pingQueue.ping(addr, port);

        replayAll();

        this.nodeStatusQueue.processQueue();

        // then
        verifyAll();
        assertEquals(State.QUESTIONABLE, node.getState());
        assertEquals(1, this.nodeStatusQueue.getQueue().size());
    }

    /**
     * testProcessQueue02() - test node in "Questable" state.
     * @throws Exception Exception
     */
    @Test
    public void testProcessQueue02() throws Exception {
        // given
        int port = 8080;
        State state = State.QUESTIONABLE;
        byte[] nodeId = DHTIdentifier.getRandomNodeId();
        InetAddress addr = InetAddress.getByName("127.0.0.1");
        DHTNode node = create(nodeId, addr, port, state);
        node.setLastUpdated(DateHelper.addMinutesToDate(new Date(), -20));

        DelayObject<DHTNode> obj = new DelayObject<DHTNode>(node, 0);
        this.nodeStatusQueue.getQueue().add(obj);

        // when
        this.pingQueue.ping(addr, port);

        replayAll();

        this.nodeStatusQueue.processQueue();

        // then
        verifyAll();
        assertEquals(State.UNKNOWN, node.getState());
        assertEquals(1, this.nodeStatusQueue.getQueue().size());
    }

    /**
     * testProcessQueue03() - test node in "UNKNOWN" state.
     * @throws Exception Exception
     */
    @Test
    public void testProcessQueue03() throws Exception {
        // given
        int port = 8080;
        State state = State.UNKNOWN;
        byte[] nodeId = DHTIdentifier.getRandomNodeId();
        InetAddress addr = InetAddress.getByName("127.0.0.1");
        DHTNode node = create(nodeId, addr, port, state);
        node.setLastUpdated(DateHelper.addMinutesToDate(new Date(), -20));

        DelayObject<DHTNode> obj = new DelayObject<DHTNode>(node, 0);
        this.nodeStatusQueue.getQueue().add(obj);

        // when
        expect(this.rt.removeNode(node, false)).andReturn(true);

        replayAll();

        this.nodeStatusQueue.processQueue();

        // then
        verifyAll();
        assertEquals(State.UNKNOWN, node.getState());
        assertEquals(0, this.nodeStatusQueue.getQueue().size());
    }

    /**
     * testProcessQueue04() - test NULL addr..
     * @throws Exception Exception
     */
    @Test
    public void testProcessQueue04() throws Exception {
        // given
        State state = State.UNKNOWN;
        byte[] nodeId = DHTIdentifier.getRandomNodeId();
        DHTNode node = create(nodeId, state);
        node.setLastUpdated(DateHelper.addMinutesToDate(new Date(), -20));

        DelayObject<DHTNode> obj = new DelayObject<DHTNode>(node, 0);
        this.nodeStatusQueue.getQueue().add(obj);

        // when
        expect(this.rt.removeNode(node, false)).andReturn(true);

        replayAll();

        this.nodeStatusQueue.processQueue();

        // then
        verifyAll();
        assertEquals(State.UNKNOWN, node.getState());
        assertEquals(0, this.nodeStatusQueue.getQueue().size());
    }

    /**
     * testProcessQueue05() - test node in "Good" state and
     * has been heard from within the last 15 minutes.
     * @throws Exception Exception
     */
    @Test
    public void testProcessQueue05() throws Exception {
        // given
        int port = 8080;
        State state = State.GOOD;
        byte[] nodeId = DHTIdentifier.getRandomNodeId();
        InetAddress addr = InetAddress.getByName("127.0.0.1");
        DHTNode node = create(nodeId, addr, port, state);
        node.setLastUpdated(new Date());

        DelayObject<DHTNode> obj = new DelayObject<DHTNode>(node, 0);
        this.nodeStatusQueue.getQueue().add(obj);

        // when

        replayAll();

        this.nodeStatusQueue.processQueue();

        // then
        verifyAll();
        assertEquals(State.GOOD, node.getState());
        assertEquals(1, this.nodeStatusQueue.getQueue().size());
    }
}

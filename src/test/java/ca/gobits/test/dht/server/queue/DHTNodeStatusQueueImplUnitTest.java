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

import static org.easymock.EasyMock.expect;
import static org.junit.Assert.assertEquals;

import java.net.InetAddress;

import org.easymock.EasyMockRunner;
import org.easymock.EasyMockSupport;
import org.easymock.Mock;
import org.easymock.TestSubject;
import org.junit.Test;
import org.junit.runner.RunWith;

import ca.gobits.dht.DHTIdentifier;
import ca.gobits.dht.DHTNode;
import ca.gobits.dht.DHTNode.State;
import ca.gobits.dht.DHTNodeBasic;
import ca.gobits.dht.DHTNodeRoutingTable;
import ca.gobits.dht.server.queue.DHTBucketStatusQueue;
import ca.gobits.dht.server.queue.DHTNodeStatusQueueImpl;

/**
 * DHTNodeStatusQueueImpl Unit Tests.
 *
 */
@RunWith(EasyMockRunner.class)
public class DHTNodeStatusQueueImplUnitTest extends EasyMockSupport {

    /** Instance of DHTNodeStatusQueue. */
    @TestSubject
    private final DHTNodeStatusQueueImpl nodeStatusQueue
        = new DHTNodeStatusQueueImpl();

    /** Mock DHTNodeRoutingTable. */
    @Mock
    private DHTNodeRoutingTable rt;

    /** Mock DHTBucketStatusQueue bucketStatusQueue. */
    @Mock
    private DHTBucketStatusQueue bucketStatusQueue;

    /**
     * Update Status for Existing Node.
     */
    @Test
    public void testUpdateExistingNodeToGood01() {
        // given
        byte[] nodeId = DHTIdentifier.getRandomNodeId();
        boolean ipv6 = false;
        DHTNode node = new DHTNodeBasic();

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
        DHTNode node = new DHTNodeBasic();
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
        this.rt.addNode(nodeId, addr, port, State.GOOD);
        this.bucketStatusQueue.updateBucketLastChanged(nodeId, ipv6);

        replayAll();
        this.nodeStatusQueue.receivedFindNodeResponse(nodeId, addr, port, ipv6);

        // then
        verifyAll();
    }
}

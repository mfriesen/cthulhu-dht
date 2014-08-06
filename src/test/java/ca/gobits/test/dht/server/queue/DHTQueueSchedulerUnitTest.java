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

import org.easymock.EasyMockRunner;
import org.easymock.EasyMockSupport;
import org.easymock.Mock;
import org.easymock.TestSubject;
import org.junit.Test;
import org.junit.runner.RunWith;

import ca.gobits.dht.server.queue.DHTBucketStatusQueue;
import ca.gobits.dht.server.queue.DHTFindNodeQueue;
import ca.gobits.dht.server.queue.DHTNodeStatusQueue;
import ca.gobits.dht.server.queue.DHTPingQueue;
import ca.gobits.dht.server.queue.DHTQueueScheduler;
import ca.gobits.dht.server.queue.DHTTokenQueue;

/**
 * DHTQueueScheduler Unit Tests.
 *
 */
@RunWith(EasyMockRunner.class)
public class DHTQueueSchedulerUnitTest extends EasyMockSupport {

    /** Instance of DHTQueueScheduler. */
    @TestSubject
    private final DHTQueueScheduler scheduler = new DHTQueueScheduler();

    /** Mock DHTPingQueue. */
    @Mock
    private DHTPingQueue pingQueue;

    /** Mock DHTFindNodeQueue. */
    @Mock
    private DHTFindNodeQueue findNodeQueue;

    /** Mock DHTNodeStatusQueue. */
    @Mock
    private DHTNodeStatusQueue nodeStatusQueue;

    /** Mock DHTBucketStatusQueue. */
    @Mock
    private DHTBucketStatusQueue bucketStatusQueue;

    /** Mock DHTTokenQueue. */
    @Mock
    private DHTTokenQueue tokenQueue;

    /**
     * testProcess01().
     */
    @Test
    public void testProcess01() {
        // given

        // when
        this.tokenQueue.processQueue();

        this.pingQueue.processQueue();

        this.findNodeQueue.processQueue();

        this.bucketStatusQueue.processQueue();

        this.nodeStatusQueue.processQueue();

        replayAll();

        this.scheduler.process();

        // then
        verifyAll();

    }

}

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

package ca.gobits.cthulhu.queue;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;

/**
 * Processor for all DHT Queues.
 *
 */
public class DHTQueueScheduler {

    /** Schedule to process queue. */
    private static final int PROCESS_QUEUE_SCHEDULE_MILLIS = 5000;

    /** Reference to DHTPingQueue. */
    @Autowired
    private DHTPingQueue pingQueue;

    /** Reference to DHTFindNodeQueue. */
    @Autowired
    private DHTFindNodeQueue findNodeQueue;

    /**
     * Processes the queues on a FixedDelay schedule.
     */
    @Scheduled(fixedDelay = PROCESS_QUEUE_SCHEDULE_MILLIS)
    public void process() {
        this.pingQueue.processQueue();
        this.findNodeQueue.processQueue();
    }
}

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

/**
 * DHT Bucket Status Queue Checker.
 *
 */
public interface DHTBucketStatusQueue extends DHTQueue {

    /**
     * Update Bucket's Last Changed Date.
     * @param nodeId  node identifier
     * @param ipv6  whether ipv6 request
     */
    void updateBucketLastChanged(final byte[] nodeId,
            final boolean ipv6);
}

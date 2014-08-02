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

import java.net.InetAddress;

/**
 * DHT Node Status Queue.
 *
 */
public interface DHTNodeStatusQueue extends DHTQueue {

    /**
     * Update Node Status when request/response from existing node.
     *
     * @param nodeId node's identifier.
     * @param ipv6 whether ipv6 request
     */
    void updateExistingNodeToGood(byte[] nodeId, boolean ipv6);

    /**
     * Update Node Status when Find Node response is received.
     *
     * @param nodeId node's identifier.
     * @param addr  InetAddress
     * @param port  int
     * @param ipv6 whether ipv6 request
     */
    void receivedFindNodeResponse(byte[] nodeId,
            InetAddress addr, int port, boolean ipv6);
}

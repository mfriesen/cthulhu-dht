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

package ca.gobits.cthulhu;

import java.net.InetAddress;

/**
 * Service for updating the status of a DHTNode.
 *
 */
public interface DHTNodeStatusService {

    /**
     * A good node is a node has responded to one of our queries within the last
     * 15 minutes. A node is also good if it has ever responded to one of our
     * queries and has sent us a query within the last 15 minutes.
     *
     * @param id node's identifier.
     * @param addr  InetAddress
     * @param port  int
     * @param ipv6 whether ipv6 request.
     */
    void updateStatusFromResponse(byte[] id, InetAddress addr, int port,
            boolean ipv6);

    /**
     * A node is also good if it has ever responded to one of our queries and
     * has sent us a query within the last 15 minutes.
     *
     * @param id  node's identifier.
     * @param addr InetAddress
     * @param port int
     * @param ipv6 whether ipv6 request.
     */
    void updateStatusFromRequest(byte[] id, InetAddress addr, int port,
            boolean ipv6);
}

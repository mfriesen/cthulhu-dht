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

package ca.gobits.cthulhu.discovery;

import java.net.InetAddress;

/**
 * DHTNodeDiscovery defines methods for the discovery of new DHTNodes.
 *
 */
public interface DHTNodeDiscovery {

    /**
     * Process the Ping Node queue.
     */
    void processPingQueue();

    /**
     * Pings a node to determine status.
     * @param addr  InetAddress
     * @param port  port
     */
    void ping(InetAddress addr, int port);

    /**
     * Sets the delay between receiving a ping
     * request and the time the actual request is sent.
     * @param delay  delay in milliseconds
     */
    void setPingDelayInMillis(long delay);

    /**
     * Sends Find Nodes request to an Address.
     * @param addr  InetAddress
     * @param port  port
     * @param target  ID to find
     */
    void findNodes(InetAddress addr, int port, byte[] target);
}

//
// Copyright 2013 Mike Friesen
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
     * Adds a node to determine status.  The node is processed after
     * the default delay has expired.
     * @param addr  InetAddress
     * @param port  port
     */
    void addNode(final InetAddress addr, final int port);

    /**
     * Processes added Nodes, once delay has expired.
     */
    void process();

    /**
     * Sets the delay in Milliseconds.
     * @param delay  delay in Milliseconds
     */
    void setDelay(long delay);
}

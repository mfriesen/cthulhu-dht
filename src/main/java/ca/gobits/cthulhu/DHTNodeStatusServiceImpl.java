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

import org.springframework.beans.factory.annotation.Autowired;

import ca.gobits.cthulhu.domain.DHTNode;
import ca.gobits.cthulhu.domain.DHTNode.State;
import ca.gobits.cthulhu.queue.DHTPingQueue;

/**
 * DHTNodeStatus implementation.
 *
 */
public class DHTNodeStatusServiceImpl implements DHTNodeStatusService {

    /** Reference to Node Routing Table. */
    @Autowired
    private DHTNodeRoutingTable rt;

    /** DHTPingQueue instance. */
    @Autowired
    private DHTPingQueue pingQueue;

    @Override
    public void updateStatusFromResponse(final byte[] id,
            final InetAddress addr, final int port, final boolean ipv6) {
        DHTNode node = this.rt.findExactNode(id, ipv6);
        if (node != null) {
            node.setState(State.GOOD);
        } else {
            this.rt.addNode(id, addr, port, State.GOOD);
        }
    }

    @Override
    public void updateStatusFromRequest(final byte[] id,
            final InetAddress addr, final int port, final boolean ipv6) {

        DHTNode node = this.rt.findExactNode(id, ipv6);
        if (node != null) {
            node.setState(State.GOOD);
        } else {
            this.pingQueue.ping(addr, port);
        }
    }
}

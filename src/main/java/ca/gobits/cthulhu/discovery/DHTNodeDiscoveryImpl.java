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
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.DelayQueue;

import ca.gobits.dht.DHTConversion;

/**
 * DHTNodeDiscovery Implementation.
 *
 */
public class DHTNodeDiscoveryImpl implements DHTNodeDiscovery {

    private final static int DEFAULT_DELAY_MILLIS = 60000;

    /** Queue of DHTNode to contact. */
    private final BlockingQueue<DelayObject<byte[]>> delayed = new DelayQueue<DelayObject<byte[]>>();

    @Override
    public void addNode(final InetAddress addr, final int port) {
        // TODO add test
        byte[] payload = DHTConversion.compactAddress(addr.getAddress(), port);
        DelayObject<byte[]> obj = new DelayObject<byte[]>(payload, DEFAULT_DELAY_MILLIS);
        this.delayed.offer(obj);
    }

}

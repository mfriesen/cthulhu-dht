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
 * DHT Ping Request Queue.
 */
public interface DHTPingQueue extends DHTQueue {

    /**
     * Pings a node to determine status with a delay.
     * @param addr  InetAddress
     * @param port  port
     */
    void pingWithDelay(InetAddress addr, int port);

    /**
     * Pings a node to determine status.
     * @param addr  InetAddress
     * @param port  port
     */
    void ping(InetAddress addr, int port);

}

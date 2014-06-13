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

package ca.gobits.cthulhu.domain;

import java.net.InetAddress;
import java.util.Date;

import ca.gobits.cthulhu.domain.DHTNode.State;
import ca.gobits.dht.DHTConversion;

/**
 * DHTNode Factory class.
 *
 */
public final class DHTNodeFactory {

    /**
     * private constructor.
     */
    private DHTNodeFactory() {
    }

    /**
     * Creates DHTNode.
     * @param infoHash  InfoHash
     * @param state  State
     * @return DHTNode
     */
    public static DHTNode create(final byte[] infoHash, final State state) {
        DHTNodeBasic node = new DHTNodeBasic();
        node.setLastUpdated(new Date());
        node.setInfoHash(infoHash);
        node.setState(state);
        return node;
    }

    /**
     * Creates DHTNode.
     * @param infoHash  InfoHash
     * @param addr  InetAddress
     * @param port  port
     * @param state  State
     * @return DHTNode
     */
    public static DHTNode create(final byte[] infoHash,
            final InetAddress addr, final int port, final State state) {

        return create(infoHash, addr.getAddress(),
                port, state);
    }

    /**
     * Creates DHTNode.
     * @param infoHash  InfoHash
     * @param addr address
     * @param port port
     * @param state  State
     * @return DHTNode
     */
    public static DHTNode create(final byte[] infoHash,
            final byte[] addr, final int port, final State state) {

        DHTNodeBasic node = new DHTNodeBasic();
        node.setLastUpdated(new Date());
        node.setInfoHash(infoHash);
        node.setState(state);

        long[] address = DHTConversion.toLongArray(addr);
        node.setAddress(address);
        node.setPort(port);

        return node;
    }
}

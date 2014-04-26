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

package ca.gobits.cthulhu;

import java.math.BigInteger;
import java.util.Collection;

/**
 * DHT Peer Routing table interface.
 */
public interface DHTInfoHashRoutingTable {

    /** Finds peers given a info_hash.
     * @param infoHash  info_hash
     * @return Collection<Long>
     */
    Collection<byte[]> findPeers(final BigInteger infoHash);

    /**
     * adds Peer to an info_hash.
     * @param infoHash  info_hash
     * @param address peer address
     * @param port  peer port
     */
    void addPeer(final BigInteger infoHash, final byte[] address,
            final int port);

    /**
     * Finds an Info Hash.
     * @param infoHash  info_hash
     * @return DHTInfoHash
     */
    DHTInfoHash findInfoHash(final BigInteger infoHash);
}

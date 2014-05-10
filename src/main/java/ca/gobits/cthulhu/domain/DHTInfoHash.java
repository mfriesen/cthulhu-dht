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

package ca.gobits.cthulhu.domain;

import java.math.BigInteger;
import java.util.Set;

/**
 * DHTInfoHash - holder of information about a InfoHash record.
 *
 */
public interface DHTInfoHash {

    /**
     * Peers are "<compact node info>" format.
     * @return Set<DHTPeer>
     */
    Set<DHTPeer> getPeers();

    /**
     * Sets Peers.
     * @param set  Set<DHTPeer>
     */
    void setPeers(final Set<DHTPeer> set);

    /**
     * Adds a peer.
     * @param addr  IP Address of peer
     * @param port   listening port of peer
     */
    void addPeer(final byte[] addr, final int port);

    /**
     * @return BigInteger
     */
    BigInteger getInfoHash();

    /**
     * Set Info Hash.
     * @param infoHashId  Info Hash
     */
    void setInfoHash(final BigInteger infoHashId);
}

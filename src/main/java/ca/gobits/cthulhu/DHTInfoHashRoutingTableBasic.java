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
 * Default implemenation of the DHTPeerRoutingTable.
 *
 */
public final class DHTInfoHashRoutingTableBasic implements
        DHTInfoHashRoutingTable {

    /** list of peers. */
    private final SortedList<DHTInfoHash> infoHashes;

    /**
     * constructor.
     */
    public DHTInfoHashRoutingTableBasic() {
        this.infoHashes = new SortedList<DHTInfoHash>(
                DHTInfoHashComparator.getInstance(), false);
    }

    @Override
    public Collection<byte[]> findPeers(final BigInteger infoHash) {

        Collection<byte[]> nodes = null;
        DHTInfoHash peer = this.infoHashes.get(new DHTInfoHash(infoHash));
        if (peer != null) {
            nodes = peer.getPeers();
        }

        return nodes;
    }

    @Override
    public void addPeer(final BigInteger infoHashId, final byte[] address,
            final int port) {


        DHTInfoHash infoHash = new DHTInfoHash(infoHashId);
        DHTInfoHash result = this.infoHashes.get(new DHTInfoHash(infoHashId));

        if (result == null) {
            result = infoHash;
            this.infoHashes.add(result);
        }

        result.addPeer(address, port);
    }

    @Override
    public DHTInfoHash findInfoHash(final BigInteger infoHash) {
        return this.infoHashes.get(new DHTInfoHash(infoHash));
    }
}

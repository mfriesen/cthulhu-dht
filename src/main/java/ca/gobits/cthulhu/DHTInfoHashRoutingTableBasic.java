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

import java.util.Collection;

import org.apache.log4j.Logger;

import ca.gobits.cthulhu.domain.DHTInfoHash;
import ca.gobits.cthulhu.domain.DHTInfoHashBasic;
import ca.gobits.cthulhu.domain.DHTInfoHashComparator;
import ca.gobits.cthulhu.domain.DHTPeer;

/**
 * Default implemenation of the DHTPeerRoutingTable.
 *
 */
public final class DHTInfoHashRoutingTableBasic implements
        DHTInfoHashRoutingTable {

    /** Logger. */
    private static final Logger LOGGER = Logger
            .getLogger(DHTInfoHashRoutingTableBasic.class);

    /** list of peers. */
    private final ConcurrentSortedList<DHTInfoHash> infoHashes;

    /**
     * constructor.
     */
    public DHTInfoHashRoutingTableBasic() {
        this.infoHashes = new ConcurrentSortedList<DHTInfoHash>(
                DHTInfoHashComparator.getInstance(), false);
    }

    @Override
    public Collection<DHTPeer> findPeers(final byte[] infoHash) {

        Collection<DHTPeer> peers = null;

        LOGGER.debug("findPeers: looking for peers for " + infoHash);

        DHTInfoHash peer = this.infoHashes.get(new DHTInfoHashBasic(infoHash));

        if (peer != null) {

            peers = peer.getPeers();
            LOGGER.debug("found " + peers.size() + " peers");

        } else {
            LOGGER.info("found 0 peers");
        }

        return peers;
    }

    @Override
    public void addPeer(final byte[] infoHashId, final byte[] address,
            final int port) {

        LOGGER.debug("addPeer: " + infoHashId + " "
                + java.util.Arrays.toString(address) + " port " + port);

        DHTInfoHash infoHash = new DHTInfoHashBasic(infoHashId);
        DHTInfoHash result = this.infoHashes.get(
                new DHTInfoHashBasic(infoHashId));

        if (result == null) {
            LOGGER.debug("InfoHash " + infoHashId
                    + " not found..... adding to list");
            result = infoHash;
            this.infoHashes.add(result);
        }

        LOGGER.debug("adding peer " + java.util.Arrays.toString(address)
                + " port " + port + " to info hash " + infoHashId);

        result.addPeer(address, port);
    }

    @Override
    public DHTInfoHash findInfoHash(final byte[] infoHash) {
        return this.infoHashes.get(new DHTInfoHashBasic(infoHash));
    }
}

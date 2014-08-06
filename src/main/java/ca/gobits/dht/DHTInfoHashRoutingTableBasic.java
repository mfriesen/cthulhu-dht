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

package ca.gobits.dht;

import java.util.Arrays;
import java.util.Collection;

import org.apache.log4j.Logger;

import ca.gobits.dht.comparator.DHTInfoHashComparator;
import ca.gobits.dht.util.ConcurrentSortedList;

/**
 * Default implemenation of the DHTPeerRoutingTable.
 *
 */
public final class DHTInfoHashRoutingTableBasic implements
        DHTInfoHashRoutingTable {

    /** Logger. */
    private static final Logger LOGGER = Logger
            .getLogger(DHTInfoHashRoutingTableBasic.class);

    /** Default Maximum number of peers. */
    private static final int DEFAULT_PEER_MAX = 16;

    /** list of peers. */
    private final ConcurrentSortedList<DHTInfoHash> infoHashes;

    /** Maxmimum number of peers allow. */
    private int peerMax = DEFAULT_PEER_MAX;

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

        LOGGER.debug("findPeers: looking for peers for "
                + Arrays.toString(infoHash));

        DHTInfoHash peer = this.infoHashes.get(new DHTInfoHash(infoHash));

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

        // TODO ping peers every so often..
        LOGGER.debug("addPeer: " + Arrays.toString(infoHashId) + " "
                + java.util.Arrays.toString(address) + " port " + port);

        DHTInfoHash infoHash = new DHTInfoHash(infoHashId);
        DHTInfoHash result = this.infoHashes.get(
                new DHTInfoHash(infoHashId));

        if (result == null) {
            LOGGER.debug("InfoHash " + Arrays.toString(infoHashId)
                    + " not found..... adding to list");
            result = infoHash;
            this.infoHashes.add(result);
        }

        LOGGER.debug("adding peer " + java.util.Arrays.toString(address)
                + " port " + port + " to info hash "
                + Arrays.toString(infoHashId));

        if (result.getPeerCount() < (this.peerMax - 1)) {
            result.addPeer(address, port);
        } else {
            LOGGER.debug("maximum number of peers reached.");
        }
    }

    @Override
    public DHTInfoHash findInfoHash(final byte[] infoHash) {
        return this.infoHashes.get(new DHTInfoHash(infoHash));
    }

    /**
     * @return int
     */
    public int getPeerMax() {
        return this.peerMax;
    }

    /**
     * Sets the Peer Max.
     * @param max  int
     */
    public void setPeerMax(final int max) {
        this.peerMax = max;
    }
}

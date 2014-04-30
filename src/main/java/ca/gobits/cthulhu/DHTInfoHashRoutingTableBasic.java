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
import java.util.ArrayList;
import java.util.Collection;

import org.apache.log4j.Logger;

import ca.gobits.dht.Arrays;

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

        LOGGER.debug("findPeers: looking for peers for " + infoHash);

        Collection<byte[]> nodes = null;
        DHTInfoHash peer = this.infoHashes.get(new DHTInfoHash(infoHash));

        if (peer != null) {

            Collection<Long> peers = peer.getPeers();
            LOGGER.debug("found " + peers.size() + " peers");
            nodes = new ArrayList<byte[]>(peers.size());

            for (Long l : peers) {
                LOGGER.debug("returning " + l);
                nodes.add(Arrays.toByteArray(l.longValue()));
            }
        } else {
            LOGGER.info("found 0 peers");
        }

        return nodes;
    }

    @Override
    public void addPeer(final BigInteger infoHashId, final byte[] address,
            final int port) {

        LOGGER.debug("addPeer: " + infoHashId + " "
                + java.util.Arrays.toString(address) + " port " + port);

        DHTInfoHash infoHash = new DHTInfoHash(infoHashId);
        DHTInfoHash result = this.infoHashes.get(new DHTInfoHash(infoHashId));

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
    public DHTInfoHash findInfoHash(final BigInteger infoHash) {
        return this.infoHashes.get(new DHTInfoHash(infoHash));
    }
}

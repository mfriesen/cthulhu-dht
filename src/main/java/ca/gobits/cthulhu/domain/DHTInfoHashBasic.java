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

import java.util.HashSet;
import java.util.Set;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

/**
 * Basic implmentation pf DHTInfoHash.
 *
 */
public final class DHTInfoHashBasic implements DHTInfoHash {

    /** InfoHash identifier. */
    private byte[] infoHash;

    /** Collection of Peers that "announced" to the InfoHash. */
    private Set<DHTPeer> peers;

    /**
     * constructor.
     */
    public DHTInfoHashBasic() {
    }

    /**
     * constructor.
     * @param hashInfoId Info hash identifier
     */
    public DHTInfoHashBasic(final byte[] hashInfoId) {
        this.infoHash = hashInfoId;
    }

    @Override
    public String toString() {
        ToStringBuilder builder = new ToStringBuilder(this);
        builder.append("infoHash", getInfoHash());
        builder.append("# of peers", this.peers != null
                ? this.peers.size() : 0);
        return builder.toString();
    }

    /**
     * Peers are "<compact node info>" format.
     * @return Set<DHTPeer>
     */
    @Override
    public Set<DHTPeer> getPeers() {
        return this.peers;
    }

    // TODO set MAX # of Peers / Info Hash
    /**
     * Sets Peers.
     * @param set  Set<DHTPeer>
     */
    @Override
    public void setPeers(final Set<DHTPeer> set) {
        this.peers = set;
    }

    /**
     * Adds a peer.
     * @param addr  IP Address of peer
     * @param port   listening port of peer
     */
    @Override
    public void addPeer(final byte[] addr, final int port) {
        if (this.peers == null) {
            this.peers = new HashSet<DHTPeer>();
        }

        this.peers.add(new DHTPeerBasic(addr, port));
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder()
            .append(this.infoHash)
            .toHashCode();
    }

    @Override
    public boolean equals(final Object obj) {
        if (obj == null) {
            return false;
        }

        if (obj == this) {
            return true;
        }

        if (!(obj instanceof DHTInfoHash)) {
            return false;
        }

        DHTInfoHash rhs = (DHTInfoHash) obj;
        return new EqualsBuilder()
            .append(this.infoHash, rhs.getInfoHash())
            .isEquals();
    }

    /**
     * @return byte[]
     */
    @Override
    public byte[] getInfoHash() {
        return this.infoHash;
    }

    /**
     * Set Info Hash.
     * @param infoHashId  Info Hash
     */
    @Override
    public void setInfoHash(final byte[] infoHashId) {
        this.infoHash = infoHashId;
    }
}

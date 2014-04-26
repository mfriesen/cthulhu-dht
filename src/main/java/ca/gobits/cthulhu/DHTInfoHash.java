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

import java.io.Serializable;
import java.math.BigInteger;
import java.util.Collection;
import java.util.HashSet;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import ca.gobits.dht.Arrays;
import ca.gobits.dht.BEncoder;

/**
 * DHTInfoHash - holder of information about a InfoHash record.
 * @author slycer
 *
 */
public final class DHTInfoHash implements Serializable {

    /** serialVersionUID. */
    private static final long serialVersionUID = 6478991759756312268L;
    /** Node identifier. */
    private final BigInteger id;
    /** cached hashCode value. */
    private final int hashCode;
    /** Collection of Nodes "announced" to the peer. */
    private Collection<Long> peers;

    /**
     * constructor.
     * @param ident Info hash identifier
     */
    public DHTInfoHash(final BigInteger ident) {

        this.id = ident;
        this.hashCode = new HashCodeBuilder()
        .append(id)
        .toHashCode();
    }

    /**
     * Peers are "<compact node info>" format.
     * @return Collection<Long>
     */
    public Collection<Long> getPeers() {
        return peers;
    }

    /**
     * Adds a peer.
     * @param address  IP Address of peer
     * @param port   listening port of peer
     */
    public void addPeer(final byte[] address, final int port) {
        if (this.peers == null) {
            this.peers = new HashSet<Long>();
        }

        byte[] bytes = BEncoder.compactAddress(address, port);
        long addr = Arrays.toLong(bytes);
        this.peers.add(Long.valueOf(addr));
    }

    @Override
    public int hashCode() {
        return this.hashCode;
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
            .append(id, rhs.id)
            .isEquals();
    }

    @Override
    public String toString() {
        ToStringBuilder builder = new ToStringBuilder(this);
        builder.append("id", id);
        return builder.toString();
    }

    /**
     * @return BigInteger
     */
    public BigInteger getId() {
        return id;
    }
}

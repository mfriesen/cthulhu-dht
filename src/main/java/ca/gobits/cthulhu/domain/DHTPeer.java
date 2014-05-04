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

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.springframework.data.neo4j.annotation.GraphId;

import ca.gobits.dht.Arrays;
import ca.gobits.dht.BDecoder;
import ca.gobits.dht.BEncoder;

/**
 * DHTPeer - holder for information about a DHT Peer.
 *
 */
public final class DHTPeer {

    /** DHTInfoHash identifier. */
    @GraphId
    private Long id;

    /** "Compact IP-address/port info". */
    private long address;

    /**
     * default constructor.
     */
    public DHTPeer() {
    }

    /**
     * constructor.
     * @param addr IP address
     * @param port listening port
     */
    public DHTPeer(final byte[] addr, final int port) {
        this();

        byte[] bytes = BEncoder.compactAddress(addr, port);
        this.address = Arrays.toLong(bytes);
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder()
            .append(address)
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

        if (!(obj instanceof DHTPeer)) {
            return false;
        }

        DHTPeer rhs = (DHTPeer) obj;
        return new EqualsBuilder()
            .append(address, rhs.getAddress())
            .isEquals();
    }

    @Override
    public String toString() {
        ToStringBuilder builder = new ToStringBuilder(this);
        builder.append("id", id);
        builder.append("address", BDecoder.decodeCompactAddressToString(Arrays
                .toByteArray(address)));
        return builder.toString();
    }

    /**
     * @return Long
     */
    public Long getId() {
        return id;
    }

    /**
     * Sets Identifier.
     * @param ident  ident
     */
    public void setId(final Long ident) {
        this.id = ident;
    }

    /**
     * @return long
     */
    public long getAddress() {
        return address;
    }

    /**
     * Sets the address.
     * @param addr address
     */
    public void setAddress(final long addr) {
        this.address = addr;
    }
}

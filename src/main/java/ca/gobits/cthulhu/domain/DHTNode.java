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
import java.util.Date;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.springframework.data.neo4j.annotation.GraphId;
import org.springframework.data.neo4j.annotation.NodeEntity;

import ca.gobits.dht.Arrays;
import ca.gobits.dht.BEncoder;

/**
 * DHTNode - holder for information about a DHT Node.
 */
@NodeEntity
public final class DHTNode {

    /** DHTNode identifier. */
    @GraphId
    private Long id;

    /** Node identifier. */
    private BigInteger infoHash;

    /** "Compact IP-address/port info". */
    private long address;

    /** Date the node was last pinged. */
    private Date lastUpdated;

    /**
     * constructor.
     */
    public DHTNode() {
        this.lastUpdated = new Date();
    }

    /**
     * constructor.
     * @param nodeId Identifier
     * @param addr IP address
     * @param port listening port
     */
    public DHTNode(final BigInteger nodeId, final byte[] addr,
            final int port) {
        this();

        this.infoHash = nodeId;

        if (addr != null) {
            byte[] bytes = BEncoder.compactAddress(addr, port);
            this.address = Arrays.toLong(bytes);
        } else {
            this.address = 0;
        }
    }

    @Override
    public String toString() {
        ToStringBuilder builder = new ToStringBuilder(this);
        builder.append("id", id);
        builder.append("infohash", infoHash);
        builder.append("address", address);
        builder.append("lastUpdated", lastUpdated);
        return builder.toString();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder()
            .append(infoHash)
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

        if (!(obj instanceof DHTNode)) {
            return false;
        }

        DHTNode rhs = (DHTNode) obj;
        return new EqualsBuilder()
            .append(id, rhs.id)
            .isEquals();
    }

    /**
     * @return Date
     */
    public Date getLastUpdated() {
        return lastUpdated;
    }

    /**
     * Sets the Last Updated Date.
     * @param date sets Last Updated Date
     */
    public void setLastUpdated(final Date date) {
        this.lastUpdated = date;
    }

    /**
     * @return Long
     */
    public Long getId() {
        return id;
    }

    /**
     * Set Id.
     * @param ident  id
     */
    public void setId(final Long ident) {
        this.id = ident;
    }

    /**
     * @return BigInteger
     */
    public BigInteger getInfoHash() {
        return infoHash;
    }

    /**
     * Sets Info Hash.
     * @param infoHashId  InfoHash
     */
    public void setInfoHash(final BigInteger infoHashId) {
        this.infoHash = infoHashId;
    }

    /**
     * @return long
     */
    public long getAddress() {
        return address;
    }

    /**
     * Sets DHTNode's IP/Port address in Compact format.
     * @param addr  compact address
     */
    public void setAddress(final long addr) {
        this.address = addr;
    }
}

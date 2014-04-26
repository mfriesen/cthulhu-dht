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
import java.util.Date;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import ca.gobits.dht.Arrays;
import ca.gobits.dht.BEncoder;

/**
 * DHTNode - holder for information about a DHT Node.
 */
public final class DHTNode implements Serializable {

    /** serialVersionUID. */
    private static final long serialVersionUID = -32412433909683955L;
    /** Node identifier. */
    private final BigInteger id;
    /** "Compact IP-address/port info". */
    private final long address;
    /** cached hashCode value. */
    private final int hashCode;
    /** Date the node was last pinged. */
    private Date lastUpdated;

    /**
     * constructor.
     * @param nodeId Identifier
     * @param addr IP address
     * @param port listening port
     */
    public DHTNode(final BigInteger nodeId, final byte[] addr,
            final int port) {
        this.id = nodeId;

        if (addr != null) {
            byte[] bytes = BEncoder.compactAddress(addr, port);
            this.address = Arrays.toLong(bytes);
        } else {
            this.address = 0;
        }

        this.lastUpdated = new Date();

        this.hashCode = new HashCodeBuilder()
        .append(id)
        .toHashCode();
    }

    /**
     * @return BigInteger
     */
    public BigInteger getId() {
        return id;
    }

    /**
     * @return long
     */
    public long getAddress() {
        return address;
    }

    @Override
    public String toString() {
        ToStringBuilder builder = new ToStringBuilder(this);
        builder.append("id", id);
        builder.append("address", address);
        return builder.toString();
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
}

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

import ca.gobits.dht.DHTConversion;

/**
 * DHTToken - holder for an Address / Token combination.
 *
 */
public final class DHTToken {

    /** Node identifier. */
    private BigInteger infoHash;

    /** "Compact IP-address". */
    private long[] address;

    /** Port info. */
    private int port;

    /** Date the node was last pinged. */
    private Date addedDate;

    /**
     * constructor.
     */
    public DHTToken() {
        this.addedDate = new Date();
    }

    /**
     * constructor.
     * @param nodeId Identifier
     * @param addr IP address
     * @param lport listening port
     */
    public DHTToken(final BigInteger nodeId, final byte[] addr,
            final int lport) {
        this();
        this.infoHash = nodeId;

        this.address = DHTConversion.toLongArray(addr);
        this.port = lport;
    }

    @Override
    public String toString() {
        ToStringBuilder builder = new ToStringBuilder(this);
        builder.append("infohash", infoHash);
        builder.append("address", DHTConversion.toInetAddressString(address));
        builder.append("port", port);
        builder.append("addedDate", addedDate);
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

        if (!(obj instanceof DHTToken)) {
            return false;
        }

        DHTToken rhs = (DHTToken) obj;
        return new EqualsBuilder()
            .append(infoHash, rhs.infoHash)
            .isEquals();
    }

    /**
     * @return BigInteger
     */
    public BigInteger getInfoHash() {
        return infoHash;
    }

    /**
     * Sets InfoHash.
     * @param infoHashId  infoHash
     */
    public void setInfoHash(final BigInteger infoHashId) {
        this.infoHash = infoHashId;
    }

    /**
     * @return long[]
     */
    public long[] getAddress() {
        return address;
    }

    /**
     * Set the address.
     * @param addr address
     */
    public void setAddress(final long[] addr) {
        this.address = addr;
    }

    /**
     * @return Date
     */
    public Date getAddedDate() {
        return addedDate;
    }

    /**
     * Sets Added Date.
     * @param date  Added Date
     */
    public void setAddedDate(final Date date) {
        this.addedDate = date;
    }

    /**
     * @return int
     */
    public int getPort() {
        return port;
    }

    /**
     * Sets the Port.
     * @param lport  port
     */
    public void setPort(final int lport) {
        this.port = lport;
    }
}
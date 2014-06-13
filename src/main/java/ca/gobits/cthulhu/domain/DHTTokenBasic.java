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

import static ca.gobits.dht.DHTConversion.toInetAddressString;

import java.util.Date;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import ca.gobits.dht.DHTConversion;

/**
 * Basic implementation of a DHTToken.
 *
 */
public final class DHTTokenBasic implements DHTToken {

    /** Node identifier. */
    private byte[] infoHash;

    /** "Compact IP-address". */
    private long[] address;

    /** Port info. */
    private int port;

    /** Date the node was last pinged. */
    private Date addedDate;

    /**
     * constructor.
     */
    public DHTTokenBasic() {
        this.addedDate = new Date();
    }

    /**
     * constructor.
     * @param nodeId Identifier
     * @param addr IP address
     * @param lport listening port
     */
    public DHTTokenBasic(final byte[] nodeId, final byte[] addr,
            final int lport) {
        this();
        this.infoHash = nodeId;

        this.address = DHTConversion.toLongArray(addr);
        this.port = lport;
    }

    @Override
    public String toString() {
        ToStringBuilder builder = new ToStringBuilder(this);
        builder.append("infohash", this.infoHash);
        builder.append("address", toInetAddressString(this.address));
        builder.append("port", this.port);
        builder.append("addedDate", this.addedDate);
        return builder.toString();
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

        if (!(obj instanceof DHTToken)) {
            return false;
        }

        DHTToken rhs = (DHTToken) obj;
        return new EqualsBuilder()
            .append(this.infoHash, rhs.getInfoHash())
            .isEquals();
    }

    @Override
    public byte[] getInfoHash() {
        return this.infoHash;
    }

    @Override
    public void setInfoHash(final byte[] infoHashId) {
        this.infoHash = infoHashId;
    }

    @Override
    public long[] getAddress() {
        return this.address;
    }

    @Override
    public void setAddress(final long[] addr) {
        this.address = addr;
    }

    @Override
    public Date getAddedDate() {
        return this.addedDate;
    }

    @Override
    public void setAddedDate(final Date date) {
        this.addedDate = date;
    }

    @Override
    public int getPort() {
        return this.port;
    }

    @Override
    public void setPort(final int lport) {
        this.port = lport;
    }

}

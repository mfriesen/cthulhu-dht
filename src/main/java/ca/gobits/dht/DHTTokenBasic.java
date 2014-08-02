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

import static ca.gobits.dht.util.DHTConversion.toInetAddress;
import static ca.gobits.dht.util.DHTConversion.toInetAddressAsString;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Date;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import ca.gobits.dht.util.DHTConversion;

import com.google.common.primitives.UnsignedLong;

/**
 * Basic implementation of a DHTToken.
 *
 */
public final class DHTTokenBasic implements DHTToken {

    /** Node identifier. */
    private byte[] infoHash;

    /** Compact IP-address format  0 - 63 bytes. */
    private UnsignedLong highAddress;

    /** Compact IP-address format  64 - 128 bytes. */
    private UnsignedLong lowAddress;

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

        UnsignedLong[] address = DHTConversion.toUnsignedLong(addr);
        this.highAddress = address[0];
        this.lowAddress = address.length > 1 ? address[1] : null;

        this.port = lport;
    }

    @Override
    public String toString() {
        ToStringBuilder builder = new ToStringBuilder(this);
        builder.append("infohash", this.infoHash);
        builder.append("address",
                toInetAddressAsString(this.highAddress, this.lowAddress));
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
    public InetAddress getAddress() throws UnknownHostException {
        return toInetAddress(this.highAddress, this.lowAddress);
    }

    @Override
    public Date getAddedDate() {
        return this.addedDate;
    }

    @Override
    public int getPort() {
        return this.port;
    }

    /**
     * Set Added Date.
     * @param date Date
     */
    public void setAddedDate(final Date date) {
        this.addedDate = date;
    }
}

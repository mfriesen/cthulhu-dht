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

/**
 * DHTNodeBasic - basic implementation of the DHTNode.
 */
public final class DHTNodeBasic implements DHTNode {

    /** serialVersionUID. */
    private static final long serialVersionUID = -9209374329583239161L;

    /** Node identifier. */
    private byte[] infoHash;

    /** Compact IP-address. */
    private long[] address;

    /** IP Port. */
    private int port;

    /** State of Node. */
    private State state;

    /** Date the node was last pinged. */
    private Date lastUpdated;

    /**
     * constructor.
     */
    public DHTNodeBasic() {
    }

    @Override
    public String toString() {
        ToStringBuilder builder = new ToStringBuilder(this);
        builder.append("infohash", this.infoHash);
        builder.append("address", toInetAddressString(this.address));
        builder.append("port", this.port);
        builder.append("state", this.state);
        builder.append("lastUpdated", this.lastUpdated);
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

        if (!(obj instanceof DHTNode)) {
            return false;
        }

        DHTNode rhs = (DHTNode) obj;
        return new EqualsBuilder()
            .append(this.infoHash, rhs.getInfoHash())
            .isEquals();
    }

    /**
     * @return Date
     */
    @Override
    public Date getLastUpdated() {
        return this.lastUpdated;
    }

    /**
     * Sets the Last Updated Date.
     * @param date sets Last Updated Date
     */
    public void setLastUpdated(final Date date) {
        this.lastUpdated = date;
    }

    /**
     * @return byte[]
     */
    @Override
    public byte[] getInfoHash() {
        return this.infoHash;
    }

    /**
     * Sets the Info Hash.
     * @param infoHashId  infoHash
     */
    public void setInfoHash(final byte[] infoHashId) {
        this.infoHash = infoHashId;
    }

    @Override
    public long[] getAddress() {
        return this.address;
    }

    /**
     * Sets Address.
     * @param addr  compact address
     */
    public void setAddress(final long[] addr) {
        this.address = addr;
    }

    @Override
    public int getPort() {
        return this.port;
    }

    /**
     * sets port number.
     * @param lport  port
     */
    public void setPort(final int lport) {
        this.port = lport;
    }

    @Override
    public State getState() {
        return this.state;
    }

    @Override
    public void setState(final State s) {
        this.state = s;
    }
}

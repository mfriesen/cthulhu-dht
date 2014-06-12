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

import ca.gobits.dht.DHTConversion;

/**
 * Basic implementation of DHTPeer.
 *
 */
public final class DHTPeerBasic implements DHTPeer {

    /** "Compact IP-address". */
    private long[] address;

    /** Listening port. */
    private int port;

    /**
     * default constructor.
     */
    public DHTPeerBasic() {
    }

    /**
     * constructor.
     * @param addr IP address
     * @param lport listening port
     */
    public DHTPeerBasic(final byte[] addr, final int lport) {
        this();

        this.address = DHTConversion.toLongArray(addr);
        this.port = lport;
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder()
            .append(this.address)
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
            .append(this.address, rhs.getAddress())
            .isEquals();
    }

    @Override
    public String toString() {
        ToStringBuilder builder = new ToStringBuilder(this);
        builder.append("address", DHTConversion.toInetAddressString(this.address));
        builder.append("port", this.port);
        return builder.toString();
    }

    /**
     * @return long[]
     */
    @Override
    public long[] getAddress() {
        return this.address;
    }

    /**
     * Sets the address.
     * @param addr address
     */
    @Override
    public void setAddress(final long[] addr) {
        this.address = addr;
    }

    /**
     * @return int
     */
    @Override
    public int getPort() {
        return this.port;
    }

    /**
     * Sets the port.
     * @param lport  port
     */
    @Override
    public void setPort(final int lport) {
        this.port = lport;
    }
}

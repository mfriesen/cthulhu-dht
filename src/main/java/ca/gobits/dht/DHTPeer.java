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

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import ca.gobits.dht.util.DHTConversion;

import com.google.common.primitives.UnsignedLong;


/**
 * DHTPeer - holder for information about a DHT Peer.
 *
 */
public final class DHTPeer {

    /** Compact IP-address format  0 - 63 bytes. */
    private UnsignedLong highAddress;

    /** Compact IP-address format  64 - 128 bytes. */
    private UnsignedLong lowAddress;

    /** Listening port. */
    private int port;

    /**
     * default constructor.
     */
    public DHTPeer() {
    }

    /**
     * constructor.
     * @param addr IP address
     * @param lport listening port
     */
    public DHTPeer(final byte[] addr, final int lport) {
        this();

        UnsignedLong[] address = DHTConversion.toUnsignedLong(addr);
        this.highAddress = address[0];
        this.lowAddress = address.length > 1 ? address[1] : null;

        this.port = lport;
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder()
            .append(this.highAddress)
            .append(this.lowAddress)
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

        InetAddress addr0 = this.getAddress();
        InetAddress addr1 = rhs.getAddress();

        return new EqualsBuilder()
            .append(addr0, addr1)
            .isEquals();
    }

    @Override
    public String toString() {
        ToStringBuilder builder = new ToStringBuilder(this);
        builder.append("address",
                toInetAddressAsString(this.highAddress, this.lowAddress));
        builder.append("port", this.port);
        return builder.toString();
    }

    /**
     * @return InetAddress
     */
    public InetAddress getAddress() {
        try {
            return toInetAddress(this.highAddress, this.lowAddress);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * @return int
     */
    public int getPort() {
        return this.port;
    }
}


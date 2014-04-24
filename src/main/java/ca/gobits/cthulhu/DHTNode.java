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

import java.math.BigInteger;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Collection;
import java.util.Date;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

/**
 * DHTNode - holder for information about a DHT Node.
 */
public final class DHTNode {

    /** Node identifier. */
    private final BigInteger id;
    /** Node IP address. */
    private final byte[] address;
    /** Node listening port. */
    private final int port;
    /** cached hashCode value. */
    private final int hashCode;
    /** Date the node was last pinged. */
    private Date lastUpdated;

    /**
     * constructor.
     * @param nodeId Identifier
     * @param addr IP address
     * @param nodePort listening port
     */
    public DHTNode(final BigInteger nodeId, final byte[] addr,
            final int nodePort) {
        this.id = nodeId;
        this.address = addr;
        this.port = nodePort;
        this.lastUpdated = new Date();

        this.hashCode = new HashCodeBuilder()
        .append(id)
        .append(addr)
        .append(port)
        .toHashCode();
    }

    /**
     * constructor.
     * @param nodeId Identifier
     * @param addr  InetAddress
     * @param nodePort  listening port
     */
    public DHTNode(final BigInteger nodeId, final InetAddress addr,
            final int nodePort) {
        this(nodeId, addr.getAddress(), nodePort);
    }

    /**
     *
     * @param nodeId Identifier
     * @param addr  String version of address
     * @param nodePort  listening port
     * @throws UnknownHostException  UnknownHostException
     */
    public DHTNode(final BigInteger nodeId, final String addr,
            final int nodePort) throws UnknownHostException {
        this(nodeId, InetAddress.getByName(addr).getAddress(), nodePort);
    }
    /**
     * @return BigInteger
     */
    public BigInteger getId() {
        return id;
    }

    /**
     * @return byte[]
     */
    public byte[] getAddress() {
        return address;
    }

    /**
     * @return int
     */
    public int getPort() {
        return port;
    }

    @Override
    public String toString() {
        ToStringBuilder builder = new ToStringBuilder(this);
        builder.append("id", id);
        builder.append("address", address);
        builder.append("port", port);
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
            .append(address, rhs.address)
            .append(port, rhs.port)
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
     * @return Collection<DHTNode>
     */
    public Collection<DHTNode> getPeers() {
//        return this.peers;
        return null;
    }

    /**
     * Add a peer to list.
     * @param peer  InetSocketAddress
     */
    public void addPeers(final DHTNode peer) {
//        this.peers.add(peer);
    }
}

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
import java.util.Date;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

/**
 * DHTNode - holder for information about a DHT Node.
 */
public final class DHTNode implements Comparable<DHTNode> {

    /** Node identifier. */
    private final BigInteger id;
    /** Node IP address. */
    private final String host;
    /** Node listening port. */
    private final int port;
    /** cached hashCode value. */
    private final int hashCode;
    /** Date the node was last pinged. */
    private Date lastUpdated;

    /**
     * constructor.
     * @param nodeId Identifier
     * @param nodeHost listening host
     * @param nodePort listening port
     */
    public DHTNode(final BigInteger nodeId, final String nodeHost,
            final int nodePort) {
        this.id = nodeId;
        this.host = nodeHost;
        this.port = nodePort;
        this.lastUpdated = new Date();

        this.hashCode = new HashCodeBuilder()
        .append(id)
        .append(host)
        .append(port)
        .toHashCode();
    }

    /**
     * @return BigInteger
     */
    public BigInteger getId() {
        return id;
    }

    /**
     * @return String
     */
    public String getHost() {
        return host;
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
        builder.append("host", host);
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
            .append(host, rhs.host)
            .append(port, rhs.port)
            .isEquals();
    }

    @Override
    public int compareTo(final DHTNode o) {
        return getId().compareTo(o.getId());
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

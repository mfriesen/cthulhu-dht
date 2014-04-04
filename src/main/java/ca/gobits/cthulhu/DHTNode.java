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

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

/**
 * DHTNode - holder for information about a DHT Node.
 */
public class DHTNode {

    /** Node identifier. */
    private final BigInteger id;
    /** Node IP address. */
    private final String host;
    /** Node listening port. */
    private final int port;
    /** cached hashCode value. */
    private final int hashCode;

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

        this.hashCode = new HashCodeBuilder()
        .append(id)
        .append(host)
        .append(port)
        .toHashCode();
    }

    /**
     * @return BigInteger
     */
    public final BigInteger getId() {
        return id;
    }

    /**
     * @return String
     */
    public final String getHost() {
        return host;
    }

    /**
     * @return int
     */
    public final int getPort() {
        return port;
    }

    @Override
    public final String toString() {
        ToStringBuilder builder = new ToStringBuilder(this);
        builder.append("id", id);
        builder.append("host", host);
        builder.append("port", port);
        return builder.toString();
    }

    @Override
    public final int hashCode() {
        return this.hashCode;
    }

    @Override
    public final boolean equals(final Object obj) {
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
}

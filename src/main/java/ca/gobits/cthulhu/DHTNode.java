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

/**
 * DHTNode - holder for information about a DHT Node.
 */
public class DHTNode {

    /** Node identifier. */
    private BigInteger id;
    /** Node IP address. */
    private String host;
    /** Node listening port. */
    private int port;

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
}

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

package ca.gobits.cthulhu;

import java.net.Inet6Address;
import java.net.InetAddress;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Holder class for DHT Request Argument(key = "a").
 *
 */
public final class DHTArgumentRequest {

    /** Argument "id" value. */
    private final byte[] id;
    /** Argument "implied_port" value. */
    private final Long impliedPort;
    /** Argument "info_hash" value. */
    private final byte[] infoHash;
    /** Argument "port" value. */
    private final Long port;
    /** Argument "token" value. */
    private final byte[] token;
    /** Argument "target" value. */
    private final byte[] target;
    /** Is IPv6 request. */
    private final Set<String> want;

    /**
     * constructor.
     * @param addr  InetAddress
     * @param map hashmap
     */
    @SuppressWarnings("unchecked")
    public DHTArgumentRequest(final InetAddress addr,
            final Map<String, Object> map) {
        this.id = (byte[]) map.get("id");
        this.impliedPort = (Long) map.get("implied_port");
        this.infoHash = (byte[]) map.get("info_hash");
        this.port = (Long) map.get("port");
        this.token = (byte[]) map.get("token");
        this.target = (byte[]) map.get("target");
        this.want = new HashSet<String>();

        if (map.containsKey("want")) {

            List<byte[]> wantList = (List<byte[]>) map.get("want");
            for (byte[] bs : wantList) {
                this.want.add(new String(bs));
            }
        }

        if (addr instanceof Inet6Address) {
            this.want.add("n6");
        } else {
            this.want.add("n4");
        }
    }

    /**
     * @return byte[]
     */
    public byte[] getId() {
        return this.id;
    }

    /**
     * @return Long
     */
    public Long getImpliedPort() {
        return this.impliedPort;
    }

    /**
     * @return byte[]
     */
    public byte[] getInfoHash() {
        return this.infoHash;
    }

    /**
     * @return Long
     */
    public Long getPort() {
        return this.port;
    }

    /**
     * @return byte[]
     */
    public byte[] getToken() {
        return this.token;
    }

    /**
     * @return byte[]
     */
    public byte[] getTarget() {
        return this.target;
    }

    /**
     * @return boolean
     */
    public boolean isIpv6() {
        return this.want.contains("n6");
    }

    /**
     * @return boolean
     */
    public boolean isIpv4() {
        return this.want.contains("n4");
    }
}

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

package ca.gobits.dht.server;

import java.net.Inet6Address;
import java.net.InetAddress;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Representation of a DHTParameters.
 *
 */
public class DHTParameters {

    /**
     * Valid DHTQueryTypes.
     */
    public enum DHTQueryType {
        /** "ping" query. */
        PING,
        /** "find_node" query. */
        FIND_NODE,
        /** "get_peers" query. */
        GET_PEERS,
        /** "announce_peer" query. */
        ANNOUNCE_PEER;
    };

    /** DHTParameter Map. */
    private final Map<String, Object> request;

    /** Whether want ipv6 data. */
    private final boolean ipv6;

    /** Whether want ipv4 data. */
    private final boolean ipv4;

    /**
     * Constructor.
     * @param addr  InetAddress
     * @param req Map<String, Object>
     */
    public DHTParameters(final InetAddress addr,
            final Map<String, Object> req) {

        this.request = req;

        Set<String> wants = wants(addr, req);
        this.ipv6 = wants.contains("n6");
        this.ipv4 = wants.contains("n4");
    }

    /**
     * Generates "wants".
     * @param addr InetAddress
     * @param req Map<String, Object>
     * @return Set<String>
     */
    private Set<String> wants(final InetAddress addr,
            final Map<String, Object> req) {
        Set<String> set = new HashSet<String>();

        if (addr instanceof Inet6Address) {
            set.add("n6");
        } else {
            set.add("n4");
        }

        @SuppressWarnings("unchecked")
        List<byte[]> wantList = (List<byte[]>) req.get("want");

        if (wantList != null) {
            for (byte[] bs : wantList) {
                String s = new String(bs);
                if ("n6".equals(s)) {
                    set.add("n6");
                } else if ("n4".equals(bs)) {
                    set.add("n4");
                }
            }
        }

        return set;
    }

    /**
     * Is Query Request.
     * @return boolean
     */
    public boolean isQuery() {
        return "q".equals(getValueAsString("y"));
    }

    /**
     * Is Query Response.
     * @return boolean
     */
    public boolean isResponse() {
        return !this.isQuery();
    }

    /**
     * "t" parameter.
     * @return String
     */
    public String getT() {
        return getValueAsString("t");
    }

    /**
     * "q" parameter.
     * @return String
     */
    public String getQ() {
        return getValueAsString("q");
    }

    /**
     * "y" parameter.
     * @return String
     */
    public String getY() {
        return getValueAsString("y");
    }

    /**
     * "id" parameter.
     * @return byte[]
     */
    public byte[] getId() {
        return isQuery() ? (byte[]) getArguments().get("id")
                : (byte[]) getResponses().get("id");
    }

    /**
     * "target" parameter.
     * @return byte[]
     */
    public byte[] getTarget() {
        return (byte[]) getArguments().get("target");
    }

    /**
     * "implied_port" parameter.
     * @return Long
     */
    public Long getImpliedPort() {
        return (Long) getArguments().get("implied_port");
    }

    /**
     * "port" parameter.
     * @return Long
     */
    public Long getPort() {
        return (Long) getArguments().get("port");
    }

    /**
     * "info_hash" parameter.
     * @return byte[]
     */
    public byte[] getInfoHash() {
        return (byte[]) getArguments().get("info_hash");
    }

    /**
     * "token" parameter.
     * @return byte[]
     */
    public byte[] getToken() {
        return (byte[]) getArguments().get("token");
    }

    /**
     * "nodes" parameter.
     * @return byte[]
     */
    public byte[] getNodes() {
        return (byte[]) getResponses().get("nodes");
    }

    /**
     * "nodes6" parameter.
     * @return byte[]
     */
    public byte[] getNodes6() {
        return (byte[]) getResponses().get("nodes6");
    }

    /**
     * "nodes6" parameter.
     * @return byte[]
     */
    public byte[] getValues() {
        return (byte[]) getResponses().get("values");
    }

    /**
     * Returns Arguements Map.
     * @return Map<String, Object>
     */
    @SuppressWarnings("unchecked")
    private Map<String, Object> getArguments() {
        return this.request.containsKey("a")
                ? (Map<String, Object>) this.request.get("a")
                : Collections.<String, Object>emptyMap();
    }

    /**
     * Returns Responses Map.
     * @return Map<String, Object>
     */
    @SuppressWarnings("unchecked")
    private Map<String, Object> getResponses() {
        return this.request.containsKey("r")
                ? (Map<String, Object>) this.request.get("r")
                : Collections.<String, Object>emptyMap();
    }

    /**
     * @param key key to return value of
     * @return String or null
     */
    private String getValueAsString(final String key) {
        String v = null;

        if (this.request.containsKey(key)) {
            v = new String((byte[]) this.request.get(key));
        }

        return v;
    }

    /**
     * @return boolean
     */
    public boolean isIpv6() {
        return this.ipv6;
    }

    /**
     * @return boolean
     */
    public boolean isIpv4() {
        return this.ipv4;
    }

    /**
     * @return DHTQueryType
     */
    public DHTQueryType getQueryType() {
        DHTQueryType qt = null;

        try {
            qt = DHTQueryType.valueOf(getQ().toUpperCase());

        } catch (Exception e) {
            qt = null;
        }

        return qt;
    }
}

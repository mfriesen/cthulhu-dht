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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ca.gobits.dht.BEncoder;

/**
 * Class to generate DHTQuery Requests.
 */
public final class DHTQueryProtocol {

    /**
     * private constructor.
     */
    private DHTQueryProtocol() {
    }

    /**
     * Generates a Ping Request.
     * @param transactionId   TransactionId
     * @param id   idd
     * @return byte[]
     */
    public static byte[] pingQuery(final String transactionId,
            final byte[] id) {
        Map<Object, Object> r = request(transactionId, "ping");

        Map<Object, Object> a = new HashMap<Object, Object>();
        a.put("id", id);
        r.put("a", a);

        byte[] bytes = BEncoder.bencoding(r);
        return bytes;
    }

    /**
     * Creates a find request.
     * @param transactionId   TransactionId
     * @param id  id identifier
     * @param target  target identifier
     * @param want  what type of response objects you want
     *  "n4" for IPv4 or "n6" for IPv6.
     * @return Map<String, Object>
     */
    public static byte[] findNodeQuery(final String transactionId,
            final byte[] id, final byte[] target,
            final List<byte[]> want) {

        Map<Object, Object> r = request(transactionId, "find_node");

        Map<String, Object> a = new HashMap<String, Object>();
        a.put("id", id);
        a.put("target", target);

        if (want != null) {
            a.put("want", want);
        }

        r.put("a", a);

        byte[] bytes = BEncoder.bencoding(r);
        return bytes;
    }

    // TODO add test case
    /**
     * Creates a "get_peers" request.
     * @param transactionId   TransactionId
     * @param id  id identifier
     * @param infohash  infohash identifier
     * @param want  what type of response objects you want
     *  "n4" for IPv4 or "n6" for IPv6.
     * @return Map<String, Object>
     */
    public static byte[] getPeers(final String transactionId,
            final byte[] id, final byte[] infohash,
            final List<byte[]> want) {

        Map<Object, Object> r = request(transactionId, "get_peers");

        Map<String, Object> a = new HashMap<String, Object>();
        a.put("id", id);
        a.put("info_hash", infohash);

        if (want != null) {
            a.put("want", want);
        }

        r.put("a", a);

        byte[] bytes = BEncoder.bencoding(r);
        return bytes;
    }

    /**
     * Create Request Map.
     * @param transactionId TransactinId
     * @param request  request type
     * @return Map<Object, Object>
     */
    private static Map<Object, Object> request(final String transactionId,
            final String request) {
        Map<Object, Object> map = new HashMap<Object, Object>();
        map.put("t", transactionId);
        map.put("y", "q");
        map.put("q", request);
        return map;
    }

}

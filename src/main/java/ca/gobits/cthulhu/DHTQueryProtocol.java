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

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;

import ca.gobits.dht.BEncoder;

/**
 * Class to generate DHTQuery Requests.
 */
public class DHTQueryProtocol {

    /** DHTServerConfig. */
    @Autowired
    private DHTServerConfig config;

    /** DHT Tokens handler. */
    @Autowired
    private DHTTokenTable tokens;

    /**
     * Generates a Ping Request.
     * @return byte[]
     */
    public byte[] pingQuery() {
        Map<Object, Object> map = new HashMap<Object, Object>();
        map.put("t", tokens.getTransactionId());
        map.put("y", "q");
        map.put("q", "ping");

        Map<Object, Object> a = new HashMap<Object, Object>();
        a.put("id", config.getNodeId());
        map.put("a", a);

        byte[] bytes = BEncoder.bencoding(map);
        return bytes;
    }

    /**
     * Creates a find request.
     * @param target  target identifier
     * @return Map<String, Object>
     */
    public byte[] findNodeQuery(final int[] target) {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("t", tokens.getTransactionId());
        map.put("y", "q");
        map.put("q", "find_node");

        Map<String, Object> map2 = new HashMap<String, Object>();
        map2.put("id", config.getNodeId());
        map2.put("target", target);
        map.put("a", map2);

        byte[] bytes = BEncoder.bencoding(map);
        return bytes;
    }
}

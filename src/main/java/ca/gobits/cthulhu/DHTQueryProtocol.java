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

import ca.gobits.dht.BEncoder;
import ca.gobits.dht.DHTIdentifier;

/**
 * Class to generate DHTQuery Requests.
 */
public class DHTQueryProtocol {

    /** DHT Node Id. */
    private static final byte[] NODE_ID = DHTIdentifier
            .sha1(DHTQueryProtocol.class.getName());

    /**
     * Generates a Ping Request.
     * @return byte[]
     */
    public byte[] pingQuery() {
        Map<Object, Object> map = new HashMap<Object, Object>();
        map.put("t", "aa");
        map.put("y", "q");
        map.put("q", "ping");

        Map<Object, Object> a = new HashMap<Object, Object>();
        a.put("id", getNodeId());
        map.put("a", a);

        byte[] bytes = BEncoder.bencoding(map);
        return bytes;
    }

    /**
     * @return byte[]
     */
    public byte[] getNodeId() {
        return NODE_ID;
    }
}

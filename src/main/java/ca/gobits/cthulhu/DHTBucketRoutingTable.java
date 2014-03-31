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

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Collection;

import ca.gobits.cthulhu.util.DHTUtil;

/**
 * Implementation of DHT Bucket Routing Table.
 *
 * http://www.bittorrent.org/beps/bep_0005.html
 *
 */
public class DHTBucketRoutingTable implements DHTRoutingTable {

    /** root node of the routing table. */
    private DHTBucket root;

    /**
     * constructor.
     */
    public DHTBucketRoutingTable() {
        BigInteger max = new BigDecimal(Math.pow(2, DHTUtil.NODE_ID_LENGTH))
            .toBigInteger();
        this.root = new DHTBucket(BigInteger.ZERO, max);
    }

    @Override
    public final void addNode(final DHTNode node) {
        this.root.addNode(node);
    }

    @Override
    public final Collection<DHTNode> findClosestNodes(final BigInteger nodeId) {
        return null;
    }

    /**
     * @return DHTBucket
     */
    public final DHTBucket getRoot() {
        return root;
    }
}

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

package ca.gobits.cthulhu.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.math.BigInteger;

import org.junit.Test;

import ca.gobits.cthulhu.DHTBucket;
import ca.gobits.cthulhu.DHTBucketRoutingTable;

/**
 * DHTBucketRoutingTableTest.
 */
public class DHTBucketRoutingTableTest {

    /**
     * testConstructor01().
     */
    @Test
    public final void testConstructor01() {
        // given
        DHTBucketRoutingTable routingTable = new DHTBucketRoutingTable();

        // when
        DHTBucket result = routingTable.getRoot();

        // then
        assertEquals(0, result.getNodes().size());
        assertEquals(BigInteger.ZERO, result.getMin());
        assertEquals(new BigInteger(
                "1461501637330902918203684832716283019655932542976"),
                result.getMax());
        assertNull(result.getLeft());
        assertNull(result.getRight());
    }

}

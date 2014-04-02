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

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.math.BigInteger;

import org.junit.Test;

import ca.gobits.cthulhu.DHTBucket;
import ca.gobits.cthulhu.DHTNode;

/**
 * DHTBucketTest.
 */
public class DHTBucketTest {

    /**
     * test id between bucket values.
     */
    @Test
    public final void testIsWithinBucket01() {
        // given
        BigInteger maxValue = new BigInteger("10");
        BigInteger minValue = new BigInteger("1");
        BigInteger id = new BigInteger("2");
        DHTBucket bucket = new DHTBucket(minValue, maxValue);

        // when
        boolean result = bucket.isWithinBucket(new DHTNode(id, null, 0));

        // then
        assertTrue(result);
    }

    /**
     * test id match min bucket values.
     */
    @Test
    public final void testIsWithinBucket02() {
        // given
        BigInteger maxValue = new BigInteger("10");
        BigInteger minValue = new BigInteger("1");
        DHTBucket bucket = new DHTBucket(minValue, maxValue);

        // when
        boolean result = bucket.isWithinBucket(new DHTNode(minValue, null, 0));

        // then
        assertTrue(result);
    }

    /**
     * test id match max bucket values.
     */
    @Test
    public final void testIsWithinBucket03() {
        // given
        BigInteger maxValue = new BigInteger("10");
        BigInteger minValue = new BigInteger("1");
        DHTBucket bucket = new DHTBucket(minValue, maxValue);

        // when
        boolean result = bucket.isWithinBucket(new DHTNode(maxValue, null, 0));

        // then
        assertTrue(result);
    }

    /**
     * test id < min bucket values.
     */
    @Test
    public final void testIsWithinBucket04() {
        // given
        BigInteger maxValue = new BigInteger("10");
        BigInteger minValue = new BigInteger("1");
        BigInteger id = new BigInteger("0");
        DHTBucket bucket = new DHTBucket(minValue, maxValue);

        // when
        boolean result = bucket.isWithinBucket(new DHTNode(id, null, 0));

        // then
        assertFalse(result);
    }

    /**
     * test id > max bucket values.
     */
    @Test
    public final void testIsWithinBucket05() {
        // given
        BigInteger maxValue = new BigInteger("10");
        BigInteger minValue = new BigInteger("1");
        BigInteger id = new BigInteger("11");
        DHTBucket bucket = new DHTBucket(minValue, maxValue);

        // when
        boolean result = bucket.isWithinBucket(new DHTNode(id, null, 0));

        // then
        assertFalse(result);
    }
}

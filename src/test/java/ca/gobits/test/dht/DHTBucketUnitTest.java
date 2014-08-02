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

package ca.gobits.test.dht;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.Date;

import org.junit.Test;

import ca.gobits.dht.DHTBucket;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

/**
 * DHTBucket Unit Tests.
 *
 */
public class DHTBucketUnitTest {

    /**
     * testToString01().
     * @throws Exception  Eception
     */
    @Test
    public void testToString01() throws Exception {
        // given
        DHTBucket bucket = getBucket();

        // when
        String result = bucket.toString();

        // then
        assertTrue(result.startsWith("ca.gobits.dht.DHTBucket"));
        assertTrue(result
            .contains("[min={0,0,0,0,0},max={-1,-1,-1,-1,-1},nodeCount=0]"));
    }

    /**
     * testEquals01() null object.
     * @throws Exception   Exception
     */
    @Test
    public void testEquals01() throws Exception {
        // given
        DHTBucket bucket = getBucket();

        // when
        boolean result = bucket.equals(null);

        // then
        assertFalse(result);
    }

    /**
     * testEquals02() same object.
     * @throws Exception   Exception
     */
    @Test
    public void testEquals02() throws Exception {
        // given
        DHTBucket bucket = getBucket();

        // when
        boolean result = bucket.equals(bucket);

        // then
        assertTrue(result);
    }

    /**
     * testEquals03() non DHTNode object.
     * @throws Exception   Exception
     */
    @Test
    @SuppressFBWarnings(value = "EC_UNRELATED_CLASS_AND_INTERFACE")
    public void testEquals03() throws Exception {
        // given
        DHTBucket bucket = getBucket();

        // when
        boolean result = bucket.equals("");

        // then
        assertFalse(result);
    }

    /**
     * testEquals04() equal DHTNode object.
     * @throws Exception   Exception
     */
    @Test
    public void testEquals04() throws Exception {
        // given
        DHTBucket bucket = getBucket();
        DHTBucket bucket1 = getBucket();

        // when
        boolean result = bucket.equals(bucket1);

        // then
        assertTrue(result);
    }

    /**
     * Test bucket is NOT full.
     */
    @Test
    public void testIsFull01() {
        // given
        DHTBucket bucket = getBucket();

        // when
        boolean result = bucket.isFull();

        // then
        assertFalse(result);
    }

    /**
     * Test bucket ISfull.
     */
    @Test
    public void testIsFull02() {
        // given
        DHTBucket bucket = getBucket();
        bucket.setNodeCount(8);

        // when
        boolean result = bucket.isFull();

        // then
        assertTrue(result);
    }

    /**
     * Test bucket isInRange TOP range.
     */
    @Test
    public void testIsInRange01() {
        // given
        byte[] start = new byte[] {0, 0, 0, 0, 0 };
        byte[] end = new byte[] {-1, -1, -1, -1, -1 };
        DHTBucket bucket = new DHTBucket(start, end);

        // when
        boolean result = bucket.isInRange(end);

        // then
        assertTrue(result);
    }

    /**
     * Test bucket isInRange BOTTOM range.
     */
    @Test
    public void testIsInRange02() {
        // given
        byte[] start = new byte[] {0, 0, 0, 0, 0 };
        byte[] end = new byte[] {-1, -1, -1, -1, -1 };
        DHTBucket bucket = new DHTBucket(start, end);

        // when
        boolean result = bucket.isInRange(start);

        // then
        assertTrue(result);
    }

    /**
     * Test bucket isInRange OUT SIDE OF TOP range.
     */
    @Test
    public void testIsInRange03() {
        // given
        byte[] start = new byte[] {0, 0, 0, 0, 0 };
        byte[] end = new byte[] {0, 0, 0, 0, 127 };
        DHTBucket bucket = new DHTBucket(start, end);
        byte[] bytes = new byte[] {127, 0, 0, 0, 0 };

        // when
        boolean result = bucket.isInRange(bytes);

        // then
        assertFalse(result);
    }

    /**
     * Test bucket isInRange OUT SIDE OF BOTTOM range.
     */
    @Test
    public void testIsInRange04() {
        // given
        byte[] start = new byte[] {0, 0, 0, 0, 1 };
        byte[] end = new byte[] {0, 0, 0, 0, 127 };
        DHTBucket bucket = new DHTBucket(start, end);
        byte[] bytes = new byte[] {0, 0, 0, 0, 0 };

        // when
        boolean result = bucket.isInRange(bytes);

        // then
        assertFalse(result);
    }

    /**
     * test IncrementCount.
     */
    @Test
    public void testIncrementCount01() {
        // given
        DHTBucket bucket = getBucket();

        // when
        bucket.incrementCount();

        // then
        assertEquals(1, bucket.getNodeCount());
    }

    /**
     * setMin01().
     */
    @Test
    public void setMin01() {
        // given
        DHTBucket bucket = getBucket();
        byte[] min = new byte[] {0, 0, 0, 0, 1 };

        // when
        bucket.setMin(min);

        // then
        assertArrayEquals(min, bucket.getMin());
    }

    /**
     * setMax01().
     */
    @Test
    public void setMax01() {
        // given
        DHTBucket bucket = getBucket();
        byte[] max = new byte[] {127, 0, 0, 0, 1 };

        // when
        bucket.setMax(max);

        // then
        assertArrayEquals(max, bucket.getMax());
    }

    /**
     * Set Node Count > 0.
     */
    @Test
    public void testSetNodeCount01() {
        // given
        DHTBucket bucket = getBucket();

        // when
        bucket.setNodeCount(5);

        // then
        assertEquals(5, bucket.getNodeCount());
    }

    /**
     * Set Node Count < 0.
     */
    @Test
    public void testSetNodeCount02() {
        // given
        DHTBucket bucket = getBucket();

        // when
        bucket.setNodeCount(-5);

        // then
        assertEquals(0, bucket.getNodeCount());
    }

    /** testGetLastChanged01(). */
    @Test
    public void testGetLastChanged01() {
        // given
        Date date = new Date();
        DHTBucket bucket = getBucket();
        assertNull(bucket.getLastChanged());

        // when
        bucket.setLastChanged(date);

        // then
        assertEquals(date, bucket.getLastChanged());
    }

    /**
     * @return DHTBucket
     */
    private DHTBucket getBucket() {
        byte[] start = new byte[] {0, 0, 0, 0, 0 };
        byte[] end = new byte[] {-1, -1, -1, -1, -1 };
        DHTBucket bucket = new DHTBucket(start, end);
        return bucket;
    }
}

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
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
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

    /**
     * testAddNode01(): test InsertSort order.
     */
    @Test
    public final void testAddNode01() {
        // given
        BigInteger min = new BigInteger("0");
        BigInteger max = new BigInteger("100");
        DHTBucket bucket = new DHTBucket(min, max);

        DHTNode node0 = new DHTNode(new BigInteger("20"), null, 0);
        DHTNode node1 = new DHTNode(new BigInteger("10"), null, 0);
        DHTNode node2 = new DHTNode(new BigInteger("30"), null, 0);
        DHTNode node3 = new DHTNode(new BigInteger("50"), null, 0);
        DHTNode node4 = new DHTNode(new BigInteger("40"), null, 0);

        // when
        bucket.addNode(node0);
        bucket.addNode(node1);
        bucket.addNode(node2);
        bucket.addNode(node3);
        bucket.addNode(node4);

        // then
        DHTNode[] results = bucket.getNodes();
        assertEquals(5, bucket.getNodeCount());
        assertEquals(node1, results[0]);
        assertEquals(node0, results[1]);
        assertEquals(node2, results[2]);
        assertEquals(node4, results[3]);
        assertEquals(node3, results[4]);
    }

    /**
     * testAddNode02(): test insert more items than BUCKET_SIZE.
     */
    @Test
    public final void testAddNode02() {
        // given
        BigInteger min = new BigInteger("0");
        BigInteger max = new BigInteger("100");
        DHTBucket bucket = new DHTBucket(min, max);

        DHTNode node0 = new DHTNode(new BigInteger("20"), null, 0);

        // when
        bucket.addNode(node0);
        bucket.addNode(node0);

        // then
        assertEquals(1, bucket.getNodeCount());
    }

    /**
     * testAddNode03(): test adding duplicate items.
     */
    @Test
    public final void testAddNode03() {
        // given
        BigInteger min = new BigInteger("0");
        BigInteger max = new BigInteger("100");
        DHTBucket bucket = new DHTBucket(min, max);

        // when
        for (int i = 0; i < DHTBucket.BUCKET_MAX + 1; i++) {
            DHTNode n = new DHTNode(new BigInteger("" + i), null, 0);
            bucket.addNode(n);
        }

        // then
        assertEquals(DHTBucket.BUCKET_MAX, bucket.getNodeCount());
    }

    /**
     * testIsFull01() - test bucket is not full.
     */
    @Test
    public final void testIsFull01() {
        // given
        BigInteger min = new BigInteger("0");
        BigInteger max = new BigInteger("100");
        DHTBucket bucket = new DHTBucket(min, max);

        // when
        boolean result = bucket.isFull();

        // then
        assertFalse(result);
    }

    /**
     * testIsFull02() - test bucket is full.
     */
    @Test
    public final void testIsFull02() {
        // given
        BigInteger min = new BigInteger("0");
        BigInteger max = new BigInteger("100");
        DHTBucket bucket = new DHTBucket(min, max);

        for (int i = 0; i < DHTBucket.BUCKET_MAX; i++) {
            bucket.addNode(new DHTNode(new BigInteger("" + i), null, 0));
        }

        // when
        boolean result = bucket.isFull();

        // then
        assertTrue(result);
    }

    /**
     * testIsEmpty01() - test bucket is empty.
     */
    @Test
    public final void testIsEmpty01() {
        // given
        BigInteger min = new BigInteger("0");
        BigInteger max = new BigInteger("100");
        DHTBucket bucket = new DHTBucket(min, max);

        // when
        boolean result = bucket.isEmpty();

        // then
        assertTrue(result);
    }

    /**
     * testIsEmpty02() - test bucket is NOT empty.
     */
    @Test
    public final void testIsEmpty02() {
        // given
        BigInteger min = new BigInteger("0");
        BigInteger max = new BigInteger("100");
        DHTBucket bucket = new DHTBucket(min, max);
        bucket.addNode(new DHTNode(new BigInteger("0"), null, 0));

        // when
        boolean result = bucket.isEmpty();

        // then
        assertFalse(result);
    }

    /**
     * testDeleteNode01(): test DeletionSort First Item.
     */
    @Test
    public final void testDeleteNode01() {
        // given
        BigInteger min = new BigInteger("0");
        BigInteger max = new BigInteger("100");
        DHTBucket bucket = new DHTBucket(min, max);

        DHTNode node0 = new DHTNode(new BigInteger("20"), null, 0);
        DHTNode node1 = new DHTNode(new BigInteger("10"), null, 0);
        DHTNode node2 = new DHTNode(new BigInteger("30"), null, 0);

        bucket.addNode(node0);
        bucket.addNode(node1);
        bucket.addNode(node2);

        // when
        bucket.deleteNode(node1);

        // then
        DHTNode[] results = bucket.getNodes();
        assertEquals(2, bucket.getNodeCount());

        assertEquals(node0, results[0]);
        assertEquals(node2, results[1]);
    }

    /**
     * testDeleteNode02(): test DeletionSort Middle Item.
     */
    @Test
    public final void testDeleteNode02() {
        // given
        BigInteger min = new BigInteger("0");
        BigInteger max = new BigInteger("100");
        DHTBucket bucket = new DHTBucket(min, max);

        DHTNode node0 = new DHTNode(new BigInteger("20"), null, 0);
        DHTNode node1 = new DHTNode(new BigInteger("10"), null, 0);
        DHTNode node2 = new DHTNode(new BigInteger("30"), null, 0);
        DHTNode node3 = new DHTNode(new BigInteger("50"), null, 0);
        DHTNode node4 = new DHTNode(new BigInteger("40"), null, 0);

        bucket.addNode(node0);
        bucket.addNode(node1);
        bucket.addNode(node2);
        bucket.addNode(node3);
        bucket.addNode(node4);

        // when
        bucket.deleteNode(node2);

        // then
        DHTNode[] results = bucket.getNodes();
        assertEquals(4, bucket.getNodeCount());

        assertEquals(node1, results[0]);
        assertEquals(node0, results[1]);
        assertEquals(node4, results[2]);
        assertEquals(node3, results[3]);
    }

    /**
     * testDeleteNode03(): test item does not exist.
     */
    @Test
    public final void testDeleteNode03() {
        // given
        BigInteger min = new BigInteger("0");
        BigInteger max = new BigInteger("100");
        DHTBucket bucket = new DHTBucket(min, max);

        DHTNode node0 = new DHTNode(new BigInteger("20"), null, 0);
        DHTNode node1 = new DHTNode(new BigInteger("10"), null, 0);
        DHTNode node2 = new DHTNode(new BigInteger("30"), null, 0);

        bucket.addNode(node0);
        bucket.addNode(node1);

        // when
        bucket.deleteNode(node2);

        // then
        DHTNode[] results = bucket.getNodes();
        assertEquals(2, bucket.getNodeCount());

        assertEquals(node1, results[0]);
        assertEquals(node0, results[1]);
        assertNull(results[2]);
        assertNull(results[3]);
    }

    /**
     * testFindClosestToMax01() - find the minimum value less than max.
     */
    @Test
    public final void testFindClosestToMax01() {
        // given
        DHTBucket bucket = new DHTBucket(new BigInteger("0"),
                new BigInteger("10000000"));

        BigInteger max = new BigInteger("399");
        for (int i = 0; i < DHTBucket.BUCKET_MAX; i++) {
            DHTNode node = new DHTNode(new BigInteger("" + i * 100), null, 0);
            bucket.addNode(node);
        }

        // when
        int result = bucket.findClosestToMax(max);

        // then
        assertEquals(4, result);
    }

    /**
     * testFindClosestToMax02() - search on empty list.
     */
    @Test
    public final void testFindClosestToMax02() {
        // given
        DHTBucket bucket = new DHTBucket(new BigInteger("0"),
                new BigInteger("10000000"));

        BigInteger max = new BigInteger("399");

        // when
        int result = bucket.findClosestToMax(max);

        // then
        assertEquals(-1, result);
    }

    /**
     * testFindClosestToMax03() - find the minimum value less than max.
     */
    @Test
    public final void testFindClosestToMax03() {
        // given
        DHTBucket bucket = new DHTBucket(new BigInteger("0"),
                new BigInteger("10000000"));

        BigInteger max = new BigInteger("700");
        for (int i = 0; i < DHTBucket.BUCKET_MAX; i++) {
            DHTNode node = new DHTNode(new BigInteger("" + i * 100), null, 0);
            bucket.addNode(node);
        }

        // when
        int result = bucket.findClosestToMax(max);

        // then
        assertEquals(7, result);
    }
}

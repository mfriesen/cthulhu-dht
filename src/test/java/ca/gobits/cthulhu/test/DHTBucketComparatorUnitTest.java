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

package ca.gobits.cthulhu.test;

import static org.junit.Assert.assertEquals;

import java.util.Comparator;

import org.junit.Test;

import ca.gobits.cthulhu.SortedList;
import ca.gobits.cthulhu.domain.DHTBucket;
import ca.gobits.cthulhu.domain.DHTBucketComparator;

/***
 * DHTBucketComparator Unit Tests.
 *
 */
public class DHTBucketComparatorUnitTest {

    /** instance. */
    private final Comparator<DHTBucket> comp = DHTBucketComparator
            .getInstance();

    /** Minimum value of DHT Identifier. */
    private final byte[] min = new byte[] {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
            0, 0, 0, 0, 0, 0, 0, 0 };

    /** Mid value of DHT Identifier. */
    private final byte[] mid = new byte[] {0, -128, 0, 0, 0, 0, 0, 0, 0, 0, 0,
            0, 0, 0, 0, 0, 0, 0, 0, 0, 0 };

    /** Mid + 1 value of DHT Identifier. */
    private final byte[] midPlusOne = new byte[] {127, -1, -1, -1, -1, -1, -1,
            -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1 };

    /** Max value of DHT Identifier. */
    private final byte[] max = new byte[] {-1, -1, -1, -1, -1, -1, -1, -1, -1,
            -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1 };

    /**
     * testCompare01() - test Bucket 1 min/max less than Bucket 2 min.
     */
    @Test
    public void testCompare01() {

        // given
        DHTBucket o1 = new DHTBucket(this.min, this.mid);
        DHTBucket o2 = new DHTBucket(this.midPlusOne, this.max);

        // when
        int result = this.comp.compare(o1, o2);

        // then
        assertEquals(-255, result);
    }

    /**
     * testCompare02() - test Bucket 1 max equals than Bucket 2 min.
     */
    @Test
    public void testCompare02() {

        // given
        DHTBucket o1 = new DHTBucket(this.min, this.midPlusOne);
        DHTBucket o2 = new DHTBucket(this.midPlusOne, this.max);

        // when
        int result = this.comp.compare(o1, o2);

        // then
        assertEquals(-128, result);
    }

    /**
     * testCompare03() - test Bucket 1 min less than Bucket 2 min and max equals
     * than Bucket 2 max.
     */
    @Test
    public void testCompare03() {

        // given
        DHTBucket o1 = new DHTBucket(this.min, this.max);
        DHTBucket o2 = new DHTBucket(this.midPlusOne, this.max);

        // when
        int result = this.comp.compare(o1, o2);

        // then
        assertEquals(-127, result);
    }

    /**
     * testCompare04() - test Bucket 1 min/max more than Bucket 2 min/max.
     */
    @Test
    public void testCompare04() {

        // given
        DHTBucket o1 = new DHTBucket(this.midPlusOne, this.max);
        DHTBucket o2 = new DHTBucket(this.min, this.mid);

        // when
        int result = this.comp.compare(o1, o2);

        // then
        assertEquals(127, result);
    }

    /**
     * testCompare05() - test Bucket 1 min/max are the same and between Bucket 2
     * min/max.
     */
    @Test
    public void testCompare05() {

        // given
        DHTBucket o1 = new DHTBucket(this.mid, this.mid);
        DHTBucket o2 = new DHTBucket(this.min, this.max);

        // when
        int result = this.comp.compare(o1, o2);

        // then
        assertEquals(0, result);
    }

    /**
     * testCompare06() - test Bucket 2 min/max are the same and between Bucket 1
     * min/max.
     */
    @Test
    public void testCompare06() {

        // given
        DHTBucket o1 = new DHTBucket(this.min, this.max);
        DHTBucket o2 = new DHTBucket(this.mid, this.mid);

        // when
        int result = this.comp.compare(o1, o2);

        // then
        assertEquals(0, result);
    }

    /**
     * testCompare07() - test Bucket 1 min/max are the same and less than Bucket
     * 2 min/max.
     */
    @Test
    public void testCompare07() {

        // given
        DHTBucket o1 = new DHTBucket(this.mid, this.mid);
        DHTBucket o2 = new DHTBucket(this.midPlusOne, this.max);

        // when
        int result = this.comp.compare(o1, o2);

        // then
        assertEquals(-255, result);
    }

    /**
     * testCompare07() - test Bucket 1 min/max are the same and MORE than Bucket
     * 2 min/max.
     */
    @Test
    public void testCompare08() {

        // given
        DHTBucket o1 = new DHTBucket(this.max, this.max);
        DHTBucket o2 = new DHTBucket(this.min, this.mid);

        // when
        int result = this.comp.compare(o1, o2);

        // then
        assertEquals(-255, result);
    }

    /**
     * Test finding DHTBucket from SortedList.
     */
    @Test
    public void testSortedList01() {
        // given
        SortedList<DHTBucket> list = new SortedList<>(this.comp, false);
        DHTBucket b0 = new DHTBucket(this.mid, this.max);
        list.add(b0);

        DHTBucket bb = new DHTBucket(this.midPlusOne, this.midPlusOne);

        // when
        DHTBucket result = list.get(bb);

        // then
        assertEquals(b0, result);
    }
}

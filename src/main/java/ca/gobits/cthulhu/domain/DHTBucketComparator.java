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

package ca.gobits.cthulhu.domain;

import java.util.Comparator;

import com.google.common.primitives.UnsignedBytes;

/**
 * DHTBucket Comparators sort by min value.
 *
 */
public class DHTBucketComparator implements Comparator<DHTBucket> {

    /** static comparator instance. */
    private static DHTBucketComparator comparator = new DHTBucketComparator();

    /** Unisgned Bytes Comparator. */
    private static final Comparator<byte[]> COMPARATOR = UnsignedBytes
            .lexicographicalComparator();

    /**
     * @return Comparator<DHTBucket>
     */
    public static Comparator<DHTBucket> getInstance() {
        return comparator;
    }

    @Override
    public int compare(final DHTBucket o1, final DHTBucket o2) {

        int result = 0;
        boolean is1Sames = o1.getMin().equals(o1.getMax());
        boolean is2Sames = o2.getMin().equals(o2.getMax());

        if (is1Sames) {
            result = isInRange(o2, o1.getMin());
        } else if (is2Sames) {
            result = isInRange(o1, o2.getMin());
        } else {
            result = UnsignedBytes.lexicographicalComparator()
                    .compare(o1.getMin(), o2.getMin());

            if (result <= 0) {

                int maxResult = UnsignedBytes.lexicographicalComparator()
                        .compare(o1.getMax(), o2.getMax());

                if (maxResult != 0) {
                    result = maxResult;
                }
            }
        }

        return result;
    }

    /**
     * Compares a byte[] to a DHTBucket range.
     * @param bucket  DHTBucket
     * @param bytes byte[]
     * @return int
     */
    private int isInRange(final DHTBucket bucket, final byte[] bytes) {
        int min = COMPARATOR.compare(bytes, bucket.getMin());
        int max = COMPARATOR.compare(bytes, bucket.getMax());

        return min >= 0 && max <= 0 ? 0 : min < 0 ? min : max;
    }
}

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

    /**
     * @return Comparator<DHTBucket>
     */
    public static Comparator<DHTBucket> getInstance() {
        return comparator;
    }

    @Override
    public int compare(final DHTBucket o1, final DHTBucket o2) {
        return UnsignedBytes.lexicographicalComparator()
                .compare(o1.getMin(), o2.getMin());
//        int result = 1;
//        int min = UnsignedBytes.lexicographicalComparator()
//                .compare(o1.getMin(), o2.getMin());
//
//        if (min <= 0) {
//
//            result = UnsignedBytes.lexicographicalComparator()
//                    .compare(o1.getMax(), o2.getMax());
//
//            if (result <= 0) {
//                result = 0;
//            }
//        }
//
//        return result;
    }

}

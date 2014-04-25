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

import java.util.Comparator;

/**
 * DHTInfoHash Comparator.
 *
 */
public final class DHTInfoHashComparator implements Comparator<DHTInfoHash> {

    /** static comparator instance. */
    private static DHTInfoHashComparator comparator =
            new DHTInfoHashComparator();

    /**
     * @return Comparator<DHTInfoHash>
     */
    public static Comparator<DHTInfoHash> getInstance() {
        return comparator;
    }

    /**
     * private constructor.
     */
    private DHTInfoHashComparator() {
    }

    @Override
    public int compare(final DHTInfoHash o1, final DHTInfoHash o2) {
        return o1.getId().compareTo(o2.getId());
    }
}

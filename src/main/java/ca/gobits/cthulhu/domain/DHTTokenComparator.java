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

package ca.gobits.cthulhu.domain;

import java.util.Comparator;

/**
 * DHTToken Comparator.
 *
 */
public final class DHTTokenComparator implements Comparator<DHTToken> {

    /** static comparator instance. */
    private static DHTTokenComparator comparator =
            new DHTTokenComparator();

    /**
     * @return Comparator<DHTToken>
     */
    public static Comparator<DHTToken> getInstance() {
        return comparator;
    }

    /**
     * private constructor.
     */
    private DHTTokenComparator() {
    }

    @Override
    public int compare(final DHTToken o1, final DHTToken o2) {
        return o1.getId().compareTo(o2.getId());
    }
}

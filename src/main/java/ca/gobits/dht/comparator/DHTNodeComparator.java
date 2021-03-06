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

package ca.gobits.dht.comparator;

import java.util.Comparator;

import ca.gobits.dht.DHTNode;

import com.google.common.primitives.UnsignedBytes;

/**
 * DHTNode Comparator.
 *
 */
public final class DHTNodeComparator implements Comparator<DHTNode> {

    /** static comparator instance. */
    private static DHTNodeComparator comparator = new DHTNodeComparator();

    /**
     * @return Comparator<DHTNode>
     */
    public static Comparator<DHTNode> getInstance() {
        return comparator;
    }

    /**
     * private constructor.
     */
    private DHTNodeComparator() {
    }

    @Override
    public int compare(final DHTNode o1, final DHTNode o2) {
        return UnsignedBytes.lexicographicalComparator()
            .compare(o1.getInfoHash(), o2.getInfoHash());
    }
}

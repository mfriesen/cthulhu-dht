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

package ca.gobits.dht;

import static ca.gobits.dht.DHTConversion.BYTE_TO_INT;

/**
 * Calculates distance between DHT Nodes.
 */
public final class DHTDistance {

    /**
     * private constructor.
     */
    private DHTDistance() {
    }

    /**
     * Calculates the distance between two nodes.
     *
     * @param s1  byte[]
     * @param s2  byte[]
     * @return int[]
     */
    public static int[] xor(final byte[] s1,
            final byte[] s2) {

        if (s1.length != s2.length) {
            throw new IllegalArgumentException("len " + s1.length
                    + " != " + s2.length);
        }

        int[] r = new int[s1.length];

        for (int i = 0; i < s1.length; i++) {
            r[i] = s1[i] & BYTE_TO_INT ^ s2[i] & BYTE_TO_INT;
        }

        return r;
    }
}

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

package ca.gobits.dht;

import java.math.BigInteger;

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
     * @return BigInteger
     */
    public static BigInteger xor(final byte[] s1,
            final byte[] s2) {

        if (s1.length != s2.length) {
            throw new IllegalArgumentException("len " + s1.length
                    + " != " + s2.length);
        }

        BigInteger b1 = new BigInteger(s1);
        BigInteger b2 = new BigInteger(s2);

        return b1.xor(b2);
    }
}

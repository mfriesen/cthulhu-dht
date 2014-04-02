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

package ca.gobits.cthulhu.util;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Helper methods for the Distributed Hash Table.
 */
public final class DHTUtil {

    /** length of node_id. */
    public static final int NODE_ID_LENGTH = 160;

    /**
     * private constructor.
     */
    private DHTUtil() {
    }

    /**
     * Calculates the distance between two nodes.
     *
     * @param s1 -
     * @param s2 -
     * @return BigInteger
     */
    public static BigInteger distance(final BigInteger s1,
            final BigInteger s2) {
        return s1.xor(s2);
    }

    /**
     * Calculates the node_id.
     *
     * @param salt - SHA1 salt string
     * @return BigInteger
     */
    public static BigInteger sha1(final String salt) {

        try {
            MessageDigest md = MessageDigest.getInstance("SHA-1");
            byte[] bytes = md.digest(salt.getBytes());
            return new BigInteger(1, bytes);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }
}

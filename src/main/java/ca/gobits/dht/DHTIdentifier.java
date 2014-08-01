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

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.springframework.security.crypto.keygen.BytesKeyGenerator;
import org.springframework.security.crypto.keygen.KeyGenerators;

/**
 * Calculates DHT Node Identifier.
 */
public final class DHTIdentifier {

    /**
     * private constructor.
     */
    private DHTIdentifier() {
    }

    /**
     * Generates default NodeId.
     * @return byte[]
     */
    public static byte[] getRandomNodeId() {
        BytesKeyGenerator generator = KeyGenerators.secureRandom();
        byte[] key = generator.generateKey();
        return sha1(key);
    }

    /**
     * Generates random NodId between MIN / MAX.
     * @param min minimum NodeId value
     * @param max maximum NodeId value
     * @return byte[]
     */
    public static byte[] getRandomNodeId(final byte[] min, final byte[] max) {

        if (min.length != max.length) {
            throw new IllegalArgumentException(
                    "parameter lengths do not match.");
        }

        byte[] result = new byte[min.length];
        for (int i = 0; i < min.length; i++) {
            result[i] = (byte) random(min[i] & BYTE_TO_INT,
                    max[i] & BYTE_TO_INT);
        }

        return result;
    }


    /**
     * Generate Random Number in range.
     * @param min minumum value
     * @param max maximum value
     * @return int
     */
    private static int random(final int min, final int max) {
        return min + (int) (Math.random() * ((max - min) + 1));
    }

    /**
     * Calculates the node_id.
     *
     * @param salt  SHA1 salt string
     * @return int[]
     */
    public static byte[] sha1(final byte[] salt) {
        return algorithm("SHA-1", salt);
    }

    /**
     * Calculates the node_id.
     *
     * @param algorithm  algorithm to use
     * @param salt  SHA1 salt string
     * @return int[]
     */
    public static byte[] algorithm(final String algorithm, final byte[] salt) {

        try {
            MessageDigest md = MessageDigest.getInstance(algorithm);
            return md.digest(salt);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }
}

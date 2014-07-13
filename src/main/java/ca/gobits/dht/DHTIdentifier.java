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

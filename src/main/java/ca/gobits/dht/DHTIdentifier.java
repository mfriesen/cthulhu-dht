package ca.gobits.dht;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

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
     * Calculates the node_id.
     *
     * @param salt  SHA1 salt string
     * @return int[]
     */
    public static byte[] sha1(final String salt) {
        return algorithm("SHA-1", salt);
    }

    /**
     * Calculates the node_id.
     *
     * @param algorithm  algorithm to use
     * @param salt  SHA1 salt string
     * @return int[]
     */
    public static byte[] algorithm(final String algorithm, final String salt) {

        try {
            MessageDigest md = MessageDigest.getInstance(algorithm);
            byte[] bytes = md.digest(salt.getBytes());
            return bytes;
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }
}

package ca.gobits.dht;

import java.math.BigInteger;
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

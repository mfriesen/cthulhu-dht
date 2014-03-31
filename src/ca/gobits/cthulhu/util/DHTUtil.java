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
     * @param salt -
     * @return BigInteger
     * @throws NoSuchAlgorithmException -
     */
    public static BigInteger sha1(final String salt)
            throws NoSuchAlgorithmException {

        MessageDigest md = MessageDigest.getInstance("SHA-1");
        byte[] bytes = md.digest(salt.getBytes());
        return new BigInteger(bytes);
    }
}

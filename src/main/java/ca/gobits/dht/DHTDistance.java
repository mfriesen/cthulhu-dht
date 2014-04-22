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

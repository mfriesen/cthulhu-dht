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

        int[] distance = new int[s1.length];

        for (int i = 0; i < s1.length; i++) {
            distance[i] = s1[i] ^ s2[i];
        }

        byte[] result = Arrays.toByte(distance);
        return new BigInteger(result);
    }
}

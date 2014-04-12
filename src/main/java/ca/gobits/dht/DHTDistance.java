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
     * @param s1 -
     * @param s2 -
     * @return BigInteger
     */
    public static BigInteger xor(final BigInteger s1,
            final BigInteger s2) {
        return s1.xor(s2);
    }
}

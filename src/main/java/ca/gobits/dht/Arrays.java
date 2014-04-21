package ca.gobits.dht;

import java.math.BigInteger;

/**
 * Arrays Helper Methods.
 *
 */
public final class Arrays {

    /** HEX Radix. */
    private static final int HEX_RADIX = 16;

    /** Constant to convert byte to unsigned int. */
    static final int BYTE_TO_INT = 0xFF;

    /**
     * private constructor.
     */
    private Arrays() {
    }

    /**
     * Transform a signed byte array to unsigned array.
     * @param bytes  bytes to transform
     * @return int[]
     */
    public static int[] toInt(final byte[] bytes) {
        int i = 0;
        int[] in = new int[bytes.length];

        for (byte b : bytes) {
            in[i] = BYTE_TO_INT & b;
            i++;
        }

        return in;
    }

    /**
     * Transform int[] to a BigInteger.
     * @param ints  int array
     * @return BigInteger
     */
    public static BigInteger toBigInteger(final int[] ints) {

        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < ints.length; i++) {
            sb.append(Integer.toHexString(ints[i]));
        }

        return new BigInteger(sb.toString(), HEX_RADIX);
    }

    /**
     * Transforms BigInteger to byte[].
     * @param d BigInteger
     * @return  byte[]
     */
    public static byte[] toByte(final BigInteger d) {
        return d.toByteArray();
    }

    /**
     * Transformed int[] to byte[].
     * @param ins  ints to transform
     * @return byte[]
     */
    public static byte[] toByte(final int[] ins) {
        int i = 0;
        byte[] bytes = new byte[ins.length];

        for (int in : ins) {
            bytes[i] = (byte) in;
            i++;
        }

        return bytes;
    }
}

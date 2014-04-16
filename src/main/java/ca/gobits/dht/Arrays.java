package ca.gobits.dht;

/**
 * Arrays Helper Methods.
 *
 */
public final class Arrays {

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

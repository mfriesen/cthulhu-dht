package ca.gobits.dht.test;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.security.MessageDigest;

import org.junit.Test;

import ca.gobits.dht.Arrays;

/**
 * Arrays Unit Tests.
 */
public final class ArraysUnitTest {

    /** expected bytes. */
    private final byte[] expectedBytes = new byte[] {-15, 14, 40, 33, -69,
            -66, -91, 39, -22, 2, 32, 3, 82, 49, 59, -64, 89, 68, 81, -112 };

    /** expected ints. */
    private final int[] expectedInts = new int[] {241, 14, 40, 33, 187, 190,
            165, 39, 234, 2, 32, 3, 82, 49, 59, 192, 89, 68, 81, 144 };

    /**
     * testToInt01().
     * @throws Exception  Exception
     */
    @Test
    public void testToInt01() throws Exception {
        // given
        MessageDigest md = MessageDigest.getInstance("SHA-1");
        byte[] bytes = md.digest("asd".getBytes());

        // when
        int[] result = Arrays.toInt(bytes);

        // then
        assertEquals(20, result.length);

        assertArrayEquals(expectedBytes, bytes);
        assertArrayEquals(expectedInts, result);
    }

    /**
     * testToByte01().
     */
    @Test
    public void testToByte01() {
        // given

        // when
        byte[] result = Arrays.toByte(expectedInts);

        // then
        assertArrayEquals(expectedBytes, result);
    }

    /**
     * testToByte02() - convert BigInteger to bytes[].
     */
    @Test
    public void testToByte02() {
        // given
        BigInteger d = new BigInteger("59527");
        byte[] expected = new byte[] {0, -24, -121 };

        // when
        byte[] result = Arrays.toByte(d);

        // then
        assertArrayEquals(expected, result);
    }

    /**
     * testToByte03() - convert double to bytes[].
     */
    @Test
    public void testToByte03() {
        // given
        double dd = Math.pow(2, 160);
        BigInteger d = new BigDecimal(dd).toBigInteger();
        byte[] expected = new byte[] {1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                0, 0, 0, 0, 0, 0, 0, 0 };

        // when
        byte[] result = Arrays.toByte(d);

        // then
        assertArrayEquals(expected, result);
    }

    /**
     * testConstructorIsPrivate().
     * @throws Exception  Exception
     */
    @Test
    public void testConstructorIsPrivate() throws Exception {
        Constructor<Arrays> constructor = Arrays.class
                .getDeclaredConstructor();
        assertTrue(Modifier.isPrivate(constructor.getModifiers()));
        constructor.setAccessible(true);
        constructor.newInstance();
    }

    /**
     * testToBigInteger01() - convert bytes[] to BigInteger.
     */
    @Test
    public void testToBigInteger01() {
        // given
        byte[] bytes = new byte[] {-24, -121 };
        int[] ints = Arrays.toInt(bytes);

        // when
        BigInteger result = Arrays.toBigInteger(ints);

        // then
        assertEquals(59527, result.intValue());
    }

    /**
     * testToBigInteger02() - convert ints[] to BigInteger.
     */
    @Test
    public void testToBigInteger02() {
        // given
        int[] ints = new int[] {255, 255, 255, 255, 255, 255, 255, 255,
                255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255 };

        // when
        BigInteger result = Arrays.toBigInteger(ints);

        // then
        assertEquals(Math.pow(2, 160), result.doubleValue(), 0);
    }
}

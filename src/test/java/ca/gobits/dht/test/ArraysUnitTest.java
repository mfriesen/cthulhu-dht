package ca.gobits.dht.test;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;
import java.math.BigDecimal;
import java.math.BigInteger;

import org.junit.Test;

import ca.gobits.dht.Arrays;

/**
 * Arrays Unit Tests.
 */
public final class ArraysUnitTest {

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
     * testToBigInteger01() - convert unsigned bytes[] to BigInteger.
     */
    @Test
    public void testToBigInteger01() {
        // given
        byte[] bytes = new byte[] {-24, -121 };

        // when
        BigInteger result = Arrays.toBigInteger(bytes);

        // then
        assertEquals(59527, result.intValue());
    }

    /**
     * testToBigInteger02() - convert ints[] to BigInteger.
     */
    @Test
    public void testToBigInteger02() {
        // given
        byte[] bytes = new byte[] {-1, -1, -1, -1, -1, -1, -1, -1,
                -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1 };

        // when
        BigInteger result = Arrays.toBigInteger(bytes);

        // then
        assertEquals(Math.pow(2, 160), result.doubleValue(), 0);
    }
}

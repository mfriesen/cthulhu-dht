package ca.gobits.dht.test;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;
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
}

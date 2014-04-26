//
// Copyright 2013 Mike Friesen
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
// http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.
//

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

    /**
     * testToLong01().
     */
    @Test
    public void testToLong01() {
        // given
        byte[] bytes = new byte[] {127, 0, 123, 43, 12, 32 };

        // when
        long result = Arrays.toLong(bytes);

        // then
        assertEquals(139640043146272L, result);
    }

    /**
     * testToLong02().
     */
    @Test
    public void testToLong02() {
        // given
        byte[] bytes = new byte[] {-1, -1, -1, -1, -1, -1 };

        // when
        long result = Arrays.toLong(bytes);

        // then
        assertEquals(281474976710655L, result);
    }

    /**
     * testToByteArray01().
     */
    @Test
    public void testToByteArray01() {
        // given
        long l = 139640043146272L;

        // when
        byte[] results = Arrays.toByteArray(l);

        // then
        assertEquals("[0, 0, 127, 0, 123, 43, 12, 32]",
                java.util.Arrays.toString(results));
    }

    /**
     * testToByteArray02().
     */
    @Test
    public void testToByteArray02() {
        // given
        long l = 281474976710655L;

        // when
        byte[] results = Arrays.toByteArray(l);

        // then
        assertEquals("[0, 0, -1, -1, -1, -1, -1, -1]",
                java.util.Arrays.toString(results));
    }
}

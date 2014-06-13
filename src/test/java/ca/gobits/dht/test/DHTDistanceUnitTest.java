//
// Copyright 2014 Mike Friesen
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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Arrays;

import org.junit.Test;

import ca.gobits.dht.DHTDistance;

/**
 * DHTDistance UnitTest.
 */
public final class DHTDistanceUnitTest {

    /**
     * testXor01().
     */
    @Test
    public void testXor01() {
        // given
        BigInteger id0 = new BigInteger("10"); // 1010
        BigInteger id1 = new BigInteger("2"); // 0010

        // when
        BigInteger result = DHTDistance.xor(id0.toByteArray(),
                id1.toByteArray());

        // then
        assertEquals(8, result.intValue());
    }

    /**
     * testXor02().
     */
    @Test
    public void testXor02() {
        // given
        byte[] id0 = new byte[21];
        Arrays.fill(id0, 0, 21, (byte) 0);
        BigDecimal bd = new BigDecimal(Math.pow(2, 160));
        BigInteger id1 = bd.toBigInteger();

        // when
        BigInteger result = DHTDistance.xor(id0,
                id1.toByteArray());

        // then
        assertEquals("1461501637330902918203684832716283019655932542976",
                result.toString());
    }

    /**
     * testXor03().
     * @throws Exception Exception
     */
    @Test
    public void testXor03() throws Exception {
        // given
        BigInteger id0 = new BigInteger("10");
        BigInteger id1 = new BigInteger("51");
        BigInteger expect = new BigInteger("57");

        // when
        BigInteger result = DHTDistance.xor(id0.toByteArray(),
                id1.toByteArray());

        // then
        assertEquals(expect, result);
    }

    /**
     * testXor04().
     * @throws Exception Exception
     */
    @Test(expected = IllegalArgumentException.class)
    public void testXor04() throws Exception {
        // given
        BigInteger id0 = new BigInteger("10123");
        BigInteger id1 = new BigInteger("51");

        // when
        DHTDistance.xor(id0.toByteArray(),
                id1.toByteArray());

        // then
    }

    /**
     * testConstructorIsPrivate().
     * @throws Exception  Exception
     */
    @Test
    public void testConstructorIsPrivate() throws Exception {
        Constructor<DHTDistance> constructor = DHTDistance.class
                .getDeclaredConstructor();
        assertTrue(Modifier.isPrivate(constructor.getModifiers()));
        constructor.setAccessible(true);
        constructor.newInstance();
    }
}

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

import org.apache.commons.codec.binary.Hex;
import org.junit.Test;

import ca.gobits.dht.DHTIdentifier;

/**
 * DHTUtil Testcases.
 */
public final class DHTIdentifierUnitTest {

    /**
     * testSha101().
     *
     * @throws Exception  Exception
     */
    @Test
    public void testSha101() throws Exception {
        // given
        String s = "sample string";

        // when
        byte[] result = DHTIdentifier.sha1(s.getBytes());

        // then
        assertEquals("243182b9d0b085c06005bf773212854bf7cd4694",
                Hex.encodeHexString(result));
    }

    /**
     * testSha102().
     *
     * @throws Exception  Exception
     */
    @Test
    public void testSha102() throws Exception {
        // given
        String s = "10";

        // when
        byte[] result = DHTIdentifier.sha1(s.getBytes());

        // then
        assertEquals("b1d5781111d84f7b3fe45a0852e59758cd7a87e5",
                Hex.encodeHexString(result));
    }

    /**
     * testAlgorithm01().
     *
     * @throws Exception  Exception
     */
    @Test
    public void testAlgorithm01() throws Exception {
        // given
        String s = "10";

        // when
        byte[] result = DHTIdentifier.algorithm("SHA-1", s.getBytes());

        // then
        assertEquals("b1d5781111d84f7b3fe45a0852e59758cd7a87e5",
                Hex.encodeHexString(result));
    }

    /**
     * testAlgorithm02() - unknown algorithm.
     *
     * @throws Exception  Exception
     */
    @Test(expected = RuntimeException.class)
    public void testAlgorithm02() throws Exception {
        // given
        String s = "10";

        // when
        DHTIdentifier.algorithm("SHA-112", s.getBytes());

        // then
    }

    /**
     * testConstructorIsPrivate().
     * @throws Exception  Exception
     */
    @Test
    public void testConstructorIsPrivate() throws Exception {
        Constructor<DHTIdentifier> constructor = DHTIdentifier.class
                .getDeclaredConstructor();
        assertTrue(Modifier.isPrivate(constructor.getModifiers()));
        constructor.setAccessible(true);
        constructor.newInstance();
    }
}

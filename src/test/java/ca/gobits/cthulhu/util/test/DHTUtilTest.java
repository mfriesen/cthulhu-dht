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

package ca.gobits.cthulhu.util.test;

import static org.junit.Assert.assertEquals;

import java.math.BigInteger;

import org.junit.Test;

import ca.gobits.cthulhu.util.DHTUtil;

/**
 * DHTUtil Testcases.
 */
public final class DHTUtilTest {

    /**
     * testSha101().
     * @throws Exception -
     */
    @Test
    public void testSha101() throws Exception {
        // given
        String s = "sample string";

        // when
        BigInteger result = DHTUtil.sha1(s);

        // then
        assertEquals(new BigInteger(
                "206627792091191212784374861007573277743147468436"), result);
    }

    /**
     * testSha102().
     * @throws Exception -
     */
    @Test
    public void testSha102() throws Exception {
        // given
        String s = "10";

        // when
        BigInteger result = DHTUtil.sha1(s);

        // then
        assertEquals(new BigInteger(
                "1015251884445938691528948434323377243417585813477"), result);
    }

    /**
     * testDistance01.
     */
    @Test
    public void testDistance01() {
        // given
        BigInteger id0 = new BigInteger("102"); // 1010
        BigInteger id1 = new BigInteger("183"); // 0010
        BigInteger expect = new BigInteger("209"); // 1000

        // when
        BigInteger result = DHTUtil.distance(id0, id1);

        // then
        assertEquals(expect, result);
    }

    /**
     * testDistance02.
     * @throws Exception -
     */
    @Test
    public void testDistance02() throws Exception {
        // given
        BigInteger id0 = DHTUtil.sha1("salt");
        BigInteger id1 = DHTUtil.sha1("salt");
        BigInteger expect = new BigInteger("0");

        // when
        BigInteger result = DHTUtil.distance(id0, id1);

        // then
        assertEquals(expect, result);
    }

    /**
     * testDistance03.
     * @throws Exception -
     */
    @Test
    public void testDistance03() throws Exception {
        // given
        BigInteger id0 = new BigInteger("10");
        BigInteger id1 = new BigInteger("51");
        BigInteger expect = new BigInteger("57");

        // when
        BigInteger result = DHTUtil.distance(id0, id1);

        // then
        assertEquals(expect, result);
    }
}

package ca.gobits.cthulhu.util;

import static org.junit.Assert.assertEquals;

import java.math.BigInteger;

import org.junit.Test;

/**
 * DHTUtil Testcases.
 */
public class DHTUtilTest {

    /**
     * testSha101.
     * @throws Exception -
     */
    @Test
    public final void testSha101() throws Exception {
        // given
        String s = "sample string";

        // when
        BigInteger result = DHTUtil.sha1(s);

        // then
        assertEquals(new BigInteger(
                "206627792091191212784374861007573277743147468436"), result);
    }

    /**
     * testDistance01.
     */
    @Test
    public final void testDistance01() {
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
    public final void testDistance02() throws Exception {
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
    public final void testDistance03() throws Exception {
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

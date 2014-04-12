package ca.gobits.cthulhu.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.math.BigInteger;

import org.junit.Test;

import ca.gobits.cthulhu.DHTNode;

/**
 * Unit Test for DHTNode.
 */
public final class DHTNodeUnitTest {

    /**
     * testConstructor01().
     */
    @Test
    public void testConstructor01() {
        // given
        BigInteger nodeId = new BigInteger("123");
        String nodeHost = "localhost";
        int nodePort = 103;

        // when
        DHTNode result = new DHTNode(nodeId, nodeHost, nodePort);

        // then
        assertEquals(nodeId, result.getId());
        assertEquals(nodeHost, result.getHost());
        assertEquals(nodePort, result.getPort());
        assertEquals(-1619759594, result.hashCode());
    }

    /**
     * testToString01().
     */
    @Test
    public void testToString01() {
        // given
        BigInteger nodeId = new BigInteger("123");
        String nodeHost = "localhost";
        int nodePort = 103;

        // when
        DHTNode result = new DHTNode(nodeId, nodeHost, nodePort);

        // then
        assertTrue(result.toString().startsWith("ca.gobits.cthulhu.DHTNode"));
        assertTrue(result.toString().endsWith(
                "[id=123,host=localhost,port=103]"));
    }

    /**
     * testEquals01()  null object.
     */
    @Test
    public void testEquals01() {
        // given
        BigInteger nodeId = new BigInteger("123");
        String nodeHost = "localhost";
        int nodePort = 103;
        DHTNode node = new DHTNode(nodeId, nodeHost, nodePort);

        // when
        boolean result = node.equals(null);

        // then
        assertFalse(result);
    }

    /**
     * testEquals02()  same object.
     */
    @Test
    public void testEquals02() {
        // given
        BigInteger nodeId = new BigInteger("123");
        String nodeHost = "localhost";
        int nodePort = 103;
        DHTNode node = new DHTNode(nodeId, nodeHost, nodePort);

        // when
        boolean result = node.equals(node);

        // then
        assertTrue(result);
    }

    /**
     * testEquals03()  non DHTNode object.
     */
    @Test
    public void testEquals03() {
        // given
        BigInteger nodeId = new BigInteger("123");
        String nodeHost = "localhost";
        int nodePort = 103;
        DHTNode node = new DHTNode(nodeId, nodeHost, nodePort);

        // when
        boolean result = node.equals("");

        // then
        assertFalse(result);
    }

    /**
     * testEquals04()  equal DHTNode object.
     */
    @Test
    public void testEquals04() {
        // given
        BigInteger nodeId = new BigInteger("123");
        String nodeHost = "localhost";
        int nodePort = 103;
        DHTNode node = new DHTNode(nodeId, nodeHost, nodePort);
        DHTNode node1 = new DHTNode(nodeId, nodeHost, nodePort);

        // when
        boolean result = node.equals(node1);

        // then
        assertTrue(result);
    }

    /**
     * testEquals05()  NOT equal DHTNode object.
     */
    @Test
    public void testEquals05() {
        // given
        BigInteger nodeId = new BigInteger("123");
        String nodeHost = "localhost";
        int nodePort = 103;
        DHTNode node = new DHTNode(nodeId, nodeHost, nodePort);
        DHTNode node1 = new DHTNode(nodeId, nodeHost, 1);

        // when
        boolean result = node.equals(node1);

        // then
        assertFalse(result);
    }

    /**
     * testCompareTo01() IDs are equal.
     */
    @Test
    public void testCompareTo01() {
        // given
        DHTNode node0 = new DHTNode(new BigInteger("2"), null, 0);
        DHTNode node1 = new DHTNode(new BigInteger("2"), null, 0);

        // when
        int result = node0.compareTo(node1);

        // then
        assertEquals(0, result);
    }

    /**
     * testCompareTo02() ID less than.
     */
    @Test
    public void testCompareTo02() {
        // given
        DHTNode node0 = new DHTNode(new BigInteger("2"), null, 0);
        DHTNode node1 = new DHTNode(new BigInteger("5"), null, 0);

        // when
        int result = node0.compareTo(node1);

        // then
        assertEquals(-1, result);
    }

    /**
     * testCompareTo03() ID greater than.
     */
    @Test
    public void testCompareTo03() {
        // given
        DHTNode node0 = new DHTNode(new BigInteger("5"), null, 0);
        DHTNode node1 = new DHTNode(new BigInteger("2"), null, 0);

        // when
        int result = node0.compareTo(node1);

        // then
        assertEquals(1, result);
    }
}

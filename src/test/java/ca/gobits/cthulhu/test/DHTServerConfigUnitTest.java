package ca.gobits.cthulhu.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.apache.commons.codec.binary.Base64;
import org.junit.Test;

import ca.gobits.cthulhu.DHTServerConfig;

/**
 * DHTServerConfig Unit Test.
 *
 */
public final class DHTServerConfigUnitTest {

    /**
     * testContructor01() - Test has "?" option.
     */
    @Test
    public void testContructor01() {
        // given
        String[] args = new String[] {"-?"};

        // when
        DHTServerConfig result = new DHTServerConfig(args);

        // then
        assertTrue(result.isShowHelp());
    }

    /**
     * testContructor02() - Test has "-p" option.
     */
    @Test
    public void testContructor02() {
        // given
        String[] args = new String[] {"-p", "8000"};

        // when
        DHTServerConfig result = new DHTServerConfig(args);

        // then
        assertFalse(result.isShowHelp());
        assertEquals(8000, result.getPort());
    }

    /**
     * testContructor03() - Test MissingArgumentException.
     */
    @Test
    public void testContructor03() {
        // given
        String[] args = new String[] {"-p"};

        // when
        DHTServerConfig result = new DHTServerConfig(args);

        // then
        assertTrue(result.isShowHelp());
    }

    /**
     * testContructor04() - Test UnrecognizedOptionException.
     */
    @Test
    public void testContructor04() {
        // given
        String[] args = new String[] {"-a"};

        // when
        DHTServerConfig result = new DHTServerConfig(args);

        // then
        assertTrue(result.isShowHelp());
    }

    /**
     * testContructor05() - Test Exception.
     */
    @Test
    public void testContructor05() {
        // given
        String[] args = new String[] {"-p", "AAAA"};

        // when
        DHTServerConfig result = new DHTServerConfig(args);

        // then
        assertTrue(result.isShowHelp());
    }

    /**
     * testContructor06() - salt used.
     */
    @Test
    public void testContructor06() {
        // given
        String[] args = new String[] {"-salt", "AAAA"};

        // when
        DHTServerConfig result = new DHTServerConfig(args);

        // then
        assertEquals("4lEhcqv4zJ9n/dSetsrPLfcbutM=",
                Base64.encodeBase64String(result.getNodeId()));
    }

    /**
     * testContructor07() - no params.
     */
    @Test
    public void testContructor07() {
        // given
        String[] args = new String[] {};

        // when
        DHTServerConfig result = new DHTServerConfig(args);

        // then
        assertEquals(6881, result.getPort());
        assertEquals(20, result.getNodeId().length);
    }

    /**
     * testContructor08() - no params.
     */
    @Test
    public void testContructor08() {
        // given

        // when
        DHTServerConfig result = new DHTServerConfig();

        // then
        assertEquals(6881, result.getPort());
        assertEquals(20, result.getNodeId().length);
    }
}

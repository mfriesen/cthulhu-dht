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

package ca.gobits.cthulhu.test;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.apache.commons.codec.binary.Base64;
import org.apache.log4j.Level;
import org.junit.Test;

import ca.gobits.cthulhu.DHTServerConfig;

/**
 * DHTServerConfig Unit Test.
 *
 */
public final class DHTServerConfigUnitTest {

    /**
     * testParse01() - Test has "?" option.
     */
    @Test
    public void testParse01() {
        // given
        String[] args = new String[] {"-?"};
        DHTServerConfig result = new DHTServerConfig();

        // when
        result.parse(args);

        // then
        assertTrue(result.isShowHelp());
    }

    /**
     * testParse02() - Test has "-p" option.
     */
    @Test
    public void testParse02() {
        // given
        String[] args = new String[] {"-p", "8000"};
        DHTServerConfig result = new DHTServerConfig();

        // when
        result.parse(args);

        // then
        assertFalse(result.isShowHelp());
        assertEquals(8000, result.getPort());
    }

    /**
     * testParse03() - Test MissingArgumentException.
     */
    @Test
    public void testParse03() {
        // given
        String[] args = new String[] {"-p"};
        DHTServerConfig result = new DHTServerConfig();

        // when
        result.parse(args);

        // then
        assertTrue(result.isShowHelp());
    }

    /**
     * testParse04() - Test UnrecognizedOptionException.
     */
    @Test
    public void testParse04() {
        // given
        String[] args = new String[] {"-a"};
        DHTServerConfig result = new DHTServerConfig();

        // when
        result.parse(args);

        // then
        assertTrue(result.isShowHelp());
    }

    /**
     * testParse05() - Test Exception.
     */
    @Test
    public void testParse05() {
        // given
        String[] args = new String[] {"-p", "AAAA"};
        DHTServerConfig result = new DHTServerConfig();

        // when
        result.parse(args);

        // then
        assertTrue(result.isShowHelp());
    }

    /**
     * testParse06() - salt used.
     */
    @Test
    public void testParse06() {
        // given
        String[] args = new String[] {"-salt", "AAAA"};
        DHTServerConfig result = new DHTServerConfig();

        // when
        result.parse(args);

        // then
        assertEquals("4lEhcqv4zJ9n/dSetsrPLfcbutM=",
                Base64.encodeBase64String(result.getNodeId()));
    }

    /**
     * testParse07() - no params.
     */
    @Test
    public void testParse07() {
        // given
        String[] args = new String[] {};
        DHTServerConfig result = new DHTServerConfig();

        // when
        result.parse(args);

        // then
        assertEquals(6881, result.getPort());
        assertEquals(20, result.getNodeId().length);
    }

    /**
     * testParse08() - no params.
     */
    @Test
    public void testParse08() {
        // given
        DHTServerConfig result = new DHTServerConfig();

        // when

        // then
        assertEquals(6881, result.getPort());
        assertEquals(20, result.getNodeId().length);
    }

    /**
     * testParse09() - bootstrap nodes.
     */
    @Test
    public void testParse09() {
        // given
        String[] args = new String[] {"-nodes",
                " 23.43.12.4:43, 23.2.2.1:123 " };
        DHTServerConfig config = new DHTServerConfig();

        // when
        config.parse(args);
        String[] result = config.getBootstrapNodes();

        // then
        assertFalse(config.isShowHelp());
        assertArrayEquals(new String[]{"23.43.12.4:43", "23.2.2.1:123"},
                result);
    }

    /**
     * testParse10() - set -debug flag.
     */
    @Test
    public void testParse10() {
        // given
        String[] args = new String[] {"-debug"};
        DHTServerConfig config = new DHTServerConfig();

        // when
        config.parse(args);
        Level result = config.getLogLevel();

        // then
        assertEquals(Level.DEBUG, result);
    }

    /**
     * testParse11() - set -verbose flag.
     */
    @Test
    public void testParse11() {
        // given
        String[] args = new String[] {"-verbose"};
        DHTServerConfig config = new DHTServerConfig();

        // when
        config.parse(args);
        Level result = config.getLogLevel();

        // then
        assertEquals(Level.ALL, result);
    }

    /**
     * testParse12() - set -debug and -verbose flag.
     */
    @Test
    public void testParse12() {
        // given
        String[] args = new String[] {"-verbose", "-debug"};
        DHTServerConfig config = new DHTServerConfig();

        // when
        config.parse(args);
        Level result = config.getLogLevel();

        // then
        assertEquals(Level.ALL, result);
    }

    /**
     * Test bootstrap node missing port number.
     */
    @Test
    public void testParse13() {
        String[] args = new String[] {"-nodes", " 23.43.12.4, 23.2.2.1 "};
        DHTServerConfig config = new DHTServerConfig();

        // when
        config.parse(args);
        String[] result = config.getBootstrapNodes();

        // then
        assertTrue(config.isShowHelp());
        assertNull(result);
    }

    /**
     * Test invlalid bootstrap nodes.
     */
    @Test
    public void testParse14() {
        String[] args = new String[] {"-nodes", " asd"};
        DHTServerConfig config = new DHTServerConfig();

        // when
        config.parse(args);
        String[] result = config.getBootstrapNodes();

        // then
        assertTrue(config.isShowHelp());
        assertNull(result);
    }
}

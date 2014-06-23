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

package ca.gobits.cthulhu.domain.test;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;
import java.net.InetAddress;

import org.junit.Test;

import ca.gobits.cthulhu.domain.DHTNode;
import ca.gobits.cthulhu.domain.DHTNode.State;
import ca.gobits.cthulhu.domain.DHTNodeFactory;

/**
 * DHTNodeFactory Unit Tests.
 *
 */
public final class DHTNodeFactoryUnitTest {

    /**
     * testConstructorIsPrivate().
     * @throws Exception  Exception
     */
    @Test
    public void testConstructorIsPrivate() throws Exception {
        // given
        Constructor<DHTNodeFactory> constructor = DHTNodeFactory.class
                .getDeclaredConstructor();

        // when
        int result = constructor.getModifiers();

        // then
        assertTrue(Modifier.isPrivate(result));
        constructor.setAccessible(true);
        constructor.newInstance();
    }

    /**
     * testCreate01().
     */
    @Test
    public void testCreate01() {
        // given
        byte[] infoHash = new byte[]{1};
        State state = State.GOOD;

        // when
        DHTNode result = DHTNodeFactory.create(infoHash, state);

        // then
        assertArrayEquals(new byte[] {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                0, 0, 0, 0, 0, 0, 1 }, result.getInfoHash());
        assertEquals(state, result.getState());
        assertNotNull(result.getLastUpdated());
    }

    /**
     * testCreate02().
     * @throws Exception  Exception
     */
    @Test
    public void testCreate02() throws Exception {
        // given
        byte[] infoHash = new byte[]{1};
        State state = State.GOOD;
        InetAddress addr = InetAddress.getByName("50.71.214.139");
        int port = 64568;

        // when
        DHTNode result = DHTNodeFactory.create(infoHash, addr, port, state);

        // then
        assertArrayEquals(new byte[] {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                0, 0, 0, 0, 0, 0, 1 }, result.getInfoHash());
        assertEquals(state, result.getState());
        assertEquals(64568, result.getPort());
        assertEquals(addr, result.getAddress());
        assertNotNull(result.getLastUpdated());
    }

}

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

package ca.gobits.cthulhu.test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.Calendar;

import org.junit.Test;
import org.springframework.test.util.ReflectionTestUtils;

import ca.gobits.cthulhu.DHTToken;
import ca.gobits.cthulhu.DHTTokenTable;
import ca.gobits.cthulhu.DHTTokenTableBasic;

/**
 * DHTTokenTableBasicTest.
 *
 */
public final class DHTTokenTableBasicTest {

    /**
     * testAdd01().
     * @throws Exception  Exception
     */
    @Test
    public void testAdd01() throws Exception {
        // given
        DHTTokenTable tt = new DHTTokenTableBasic();
        String secret = "secret";
        InetSocketAddress addr = new InetSocketAddress(
                InetAddress.getByName("50.71.214.139"), 64568);

        // when
        tt.add(addr, secret.getBytes());
        DHTToken result = tt.get(addr, secret.getBytes());

        // then
        assertNotNull(result);
    }

    /**
     * testValid01() - token not found for address / secret.
     * @throws Exception  Exception
     */
    @Test
    public void testValid01() throws Exception {
        // given
        DHTTokenTable tt = new DHTTokenTableBasic();
        String secret = "secret";
        InetSocketAddress addr = new InetSocketAddress(
                InetAddress.getByName("50.71.214.139"), 64568);

        // when
        boolean result = tt.valid(addr, secret.getBytes());

        // then
        assertFalse(result);
    }

    /**
     * testValid02() - token is valid.
     * @throws Exception  Exception
     */
    @Test
    public void testValid02() throws Exception {
        // given
        DHTTokenTable tt = new DHTTokenTableBasic();
        String secret = "secret";
        InetSocketAddress addr = new InetSocketAddress(
                InetAddress.getByName("50.71.214.139"), 64568);

        tt.add(addr, secret.getBytes());

        // when
        boolean result = tt.valid(addr, secret.getBytes());

        // then
        assertTrue(result);
    }

    /**
     * testValid03() - token is expired.
     * @throws Exception  Exception
     */
    @Test
    public void testValid03() throws Exception {
        // given
        DHTTokenTable tt = new DHTTokenTableBasic();
        String secret = "secret";
        InetSocketAddress addr = new InetSocketAddress(
                InetAddress.getByName("50.71.214.139"), 64568);

        tt.add(addr, secret.getBytes());
        DHTToken token = tt.get(addr, secret.getBytes());

        Calendar c = Calendar.getInstance();
        c.setTime(token.getAddedDate());
        c.add(Calendar.MINUTE, -1 * DHTTokenTable.TOKEN_EXPIRY_IN_MINUTES - 1);

        ReflectionTestUtils.setField(token, "addedDate", c.getTime());

        // when
        boolean result = tt.valid(addr, secret.getBytes());

        // then
        assertFalse(result);
    }

    /**
     * testValid04() - token is valid, but wrong address.
     * @throws Exception  Exception
     */
    @Test
    public void testValid04() throws Exception {
        // given
        DHTTokenTable tt = new DHTTokenTableBasic();
        String secret = "secret";
        InetSocketAddress addr0 = new InetSocketAddress(
                InetAddress.getByName("50.71.214.139"), 64568);
        InetSocketAddress addr1 = new InetSocketAddress(
                InetAddress.getByName("50.71.214.139"), 64569);

        tt.add(addr0, secret.getBytes());

        // when
        boolean result = tt.valid(addr1, secret.getBytes());

        // then
        assertFalse(result);
    }
}

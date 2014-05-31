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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.math.BigInteger;
import java.net.InetAddress;
import java.util.Arrays;
import java.util.Calendar;

import org.junit.Test;
import org.springframework.test.util.ReflectionTestUtils;

import ca.gobits.cthulhu.ConcurrentSortedList;
import ca.gobits.cthulhu.DHTTokenTable;
import ca.gobits.cthulhu.DHTTokenTableBasic;
import ca.gobits.cthulhu.domain.DHTToken;
import ca.gobits.cthulhu.domain.DHTTokenBasic;

/**
 * DHTTokenTableBasicTest.
 *
 */
public final class DHTTokenTableBasicUnitTest {

    /**
     * testAdd01().
     * @throws Exception  Exception
     */
    @Test
    public void testAdd01() throws Exception {
        // given
        DHTTokenTable tt = new DHTTokenTableBasic();
        String secret = "secret";
        int port = 64568;
        InetAddress addr = InetAddress.getByName("50.71.214.139");

        // when
        tt.add(addr, port, secret.getBytes());
        DHTToken result = tt.get(addr, port, secret.getBytes());

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
        int port = 64568;
        InetAddress addr = InetAddress.getByName("50.71.214.139");

        // when
        boolean result = tt.valid(addr, port, secret.getBytes());

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
        int port = 64568;
        InetAddress addr = InetAddress.getByName("50.71.214.139");

        tt.add(addr, port, secret.getBytes());

        // when
        boolean result = tt.valid(addr, port, secret.getBytes());

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
        DHTTokenTableBasic tt = new DHTTokenTableBasic();
        String secret = "secret";
        int port = 64568;
        InetAddress addr = InetAddress.getByName("50.71.214.139");

        tt.add(addr, port, secret.getBytes());
        DHTToken token = tt.get(addr, port, secret.getBytes());

        Calendar c = Calendar.getInstance();
        c.setTime(token.getAddedDate());
        c.add(Calendar.MINUTE, -1 * tt.getTokenExpiryInMinutes() - 1);

        ReflectionTestUtils.setField(token, "addedDate", c.getTime());

        // when
        boolean result = tt.valid(addr, port, secret.getBytes());

        // then
        assertFalse(result);
    }

    /**
     * testValid04() - token is valid, but wrong port.
     * @throws Exception  Exception
     */
    @Test
    public void testValid04() throws Exception {
        // given
        DHTTokenTable tt = new DHTTokenTableBasic();
        String secret = "secret";
        int port0 = 64568;
        InetAddress addr0 = InetAddress.getByName("50.71.214.139");
        int port1 = 64569;
        InetAddress addr1 = InetAddress.getByName("50.71.214.139");

        tt.add(addr0, port0, secret.getBytes());

        // when
        boolean result = tt.valid(addr1, port1, secret.getBytes());

        // then
        assertFalse(result);
    }


    /**
     * testValid05() - token is valid, but wrong address.
     * @throws Exception  Exception
     */
    @Test
    public void testValid05() throws Exception {
        // given
        DHTTokenTable tt = new DHTTokenTableBasic();
        String secret = "secret";
        int port = 64568;
        InetAddress addr0 = InetAddress.getByName("50.71.214.139");
        InetAddress addr1 = InetAddress.getByName("50.71.214.140");

        tt.add(addr0, port, secret.getBytes());

        // when
        boolean result = tt.valid(addr1, port, secret.getBytes());

        // then
        assertFalse(result);
    }

    /**
     * testValid06() - sets Token Expiry to negative number and token is expired
     * right away.
     * @throws Exception  Exception
     */
    @Test
    public void testValid06() throws Exception {
        // given
        DHTTokenTableBasic tt = new DHTTokenTableBasic();
        tt.setTokenExpiryInMinutes(-20);

        String secret = "secret";
        int port = 64568;
        InetAddress addr0 = InetAddress.getByName("50.71.214.139");

        tt.add(addr0, port, secret.getBytes());

        // when
        boolean result = tt.valid(addr0, port, secret.getBytes());

        // then
        assertFalse(result);
    }

    /**
     * testRemoveExpiredTokens01().
     * @throws Exception  Exception
     */
    @SuppressWarnings("unchecked")
    @Test
    public void testRemoveExpiredTokens01() throws Exception {
        // given
        DHTTokenTableBasic tt = new DHTTokenTableBasic();
        ConcurrentSortedList<DHTToken> list = (ConcurrentSortedList<DHTToken>)
                ReflectionTestUtils.getField(tt, "tokens");

        byte[] addr = InetAddress.getByName("50.71.214.139").getAddress();

        Calendar c = Calendar.getInstance();
        c.add(Calendar.MINUTE, -1 * tt.getTokenExpiryInMinutes() - 1);
        DHTToken t0 = new DHTTokenBasic(new BigInteger("1"), addr, 10);
        t0.setAddedDate(c.getTime());

        DHTToken t1 = new DHTTokenBasic(new BigInteger("2"), addr, 10);

        DHTToken t2 = new DHTTokenBasic(new BigInteger("3"), addr, 10);

        list.addAll(Arrays.asList(t0, t1, t2));

        // when
        tt.removeExpiredTokens();

        // then
        assertEquals(2, list.size());
        assertNull(list.get(t0));
        assertNotNull(list.get(t1));
        assertNotNull(list.get(t2));
    }

    /**
     * testRemoveExpiredTokens02() no expired tokens.
     * @throws Exception  Exception
     */
    @SuppressWarnings("unchecked")
    @Test
    public void testRemoveExpiredTokens02() throws Exception {
        // given
        DHTTokenTableBasic tt = new DHTTokenTableBasic();
        ConcurrentSortedList<DHTToken> list = (ConcurrentSortedList<DHTToken>)
                ReflectionTestUtils.getField(tt, "tokens");

        byte[] addr = InetAddress.getByName("50.71.214.139").getAddress();

        DHTToken t0 = new DHTTokenBasic(new BigInteger("1"), addr, 10);

        list.addAll(Arrays.asList(t0));

        // when
        tt.removeExpiredTokens();

        // then
        assertEquals(1, list.size());
        assertNotNull(list.get(t0));
    }

    /**
     * testRemoveExpiredTokens03() no expired tokens.
     * @throws Exception  Exception
     */
    @SuppressWarnings("unchecked")
    @Test
    public void testRemoveExpiredTokens03() throws Exception {
        // given
        DHTTokenTableBasic tt = new DHTTokenTableBasic();
        ConcurrentSortedList<DHTToken> list = (ConcurrentSortedList<DHTToken>)
                ReflectionTestUtils.getField(tt, "tokens");

        byte[] addr = InetAddress.getByName("50.71.214.139").getAddress();

        DHTToken t0 = new DHTTokenBasic(new BigInteger("1"), addr, 10);

        list.addAll(Arrays.asList(t0));

        // when
        tt.removeExpiredTokens();

        // then
        assertEquals(1, list.size());
        assertNotNull(list.get(t0));
    }

    /**
     * testRemoveExpiredTokens04() - test adding / remove tokens at the same
     * time.
     *
     * @throws Exception  Exception
     */
    @SuppressWarnings("unchecked")
    @Test
    public void testRemoveExpiredTokens04() throws Exception {
        // given
        final DHTTokenTableBasic tt = new DHTTokenTableBasic();
        final ConcurrentSortedList<DHTToken> list =
            (ConcurrentSortedList<DHTToken>)
                ReflectionTestUtils.getField(tt, "tokens");

        final Calendar expired = Calendar.getInstance();
        expired.add(Calendar.MINUTE, -1 * tt.getTokenExpiryInMinutes() - 1);

        final int port = 64568;
        final InetAddress addr = InetAddress.getByName("50.71.214.139");

        // when
        Thread add = new Thread(new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < 100; i++) {
                    tt.add(addr, port, "secret".getBytes());
                    DHTToken t0 = new DHTTokenBasic(new BigInteger("" + i),
                            addr.getAddress(), 10);
                    t0.setAddedDate(expired.getTime());
                    list.add(t0);
                }
            }
        });
        add.start();

        Thread remove = new Thread(new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < 100; i++) {
                    tt.removeExpiredTokens();
                }
            }
        });
        remove.start();

        // then
        while (add.isAlive() && remove.isAlive()) {
            Thread.sleep(50);
        }

        assertFalse(add.isAlive());
        assertFalse(remove.isAlive());

        assertEquals(1, list.size());
    }
}

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

package ca.gobits.cthulhu;

import java.math.BigInteger;
import java.net.InetSocketAddress;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;

import org.apache.log4j.Logger;

import ca.gobits.cthulhu.domain.DHTToken;
import ca.gobits.cthulhu.domain.DHTTokenComparator;
import ca.gobits.dht.DHTConversion;

/**
 * Basic implementation of DHTTokenTable.
 *
 */
public final class DHTTokenTableBasic implements DHTTokenTable {

    /** DHTTokenTableBasic Logger. */
    private static final Logger LOGGER = Logger
            .getLogger(DHTTokenTableBasic.class);

    /** List of Tokens. */
    private final SortedList<DHTToken> tokens = new SortedList<>(
            DHTTokenComparator.getInstance(), false);

    @Override
    public void add(final InetSocketAddress addr, final byte[] secret) {
        LOGGER.debug("add token: " + java.util.Arrays.toString(secret)
                + " from "
                + java.util.Arrays.toString(addr.getAddress().getAddress())
                + " port " + addr.getPort());

        DHTToken dhtToken = createToken(addr, secret);

        this.tokens.add(dhtToken);
    }

    @Override
    public DHTToken get(final InetSocketAddress addr, final byte[] secret) {
        return this.tokens.get(createToken(addr, secret));
    }

    @Override
    public boolean valid(final InetSocketAddress addr, final byte[] secret) {

        boolean valid = false;
        DHTToken token = this.tokens.get(createToken(addr, secret));

        if (token != null) {

            Date now = new Date();
            Calendar c = Calendar.getInstance();
            c.setTime(token.getAddedDate());
            c.add(Calendar.MINUTE, TOKEN_EXPIRY_IN_MINUTES);

            byte[] addr0 = DHTConversion.toByteArray(token.getAddress());

            valid = now.before(c.getTime())
                    && Arrays.equals(addr0, addr.getAddress().getAddress())
                    && token.getPort() == addr.getPort();
        }

        return valid;
    }

    /**
     * Creates a token.
     * @param addr  address
     * @param secret  secret
     * @return DHTToken
     */
    private DHTToken createToken(final InetSocketAddress addr,
            final byte[] secret) {
        BigInteger id = DHTConversion.toBigInteger(secret);
        DHTToken dhtToken = new DHTToken(id, addr.getAddress().getAddress(),
                addr.getPort());
        return dhtToken;
    }
}

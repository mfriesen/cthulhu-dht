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
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.scheduling.annotation.Scheduled;

import ca.gobits.cthulhu.domain.DHTToken;
import ca.gobits.cthulhu.domain.DHTTokenBasic;
import ca.gobits.cthulhu.domain.DHTTokenComparator;
import ca.gobits.dht.DHTConversion;

/**
 * Basic implementation of DHTTokenTable.
 */
public final class DHTTokenTableBasic implements DHTTokenTable {

    /** Default Token Expiry. */
    private static final int DEFAULT_TOKEN_EXPIRY = 15;

    /** How to Token stays valid. */
    private int tokenExpiryInMinutes = DEFAULT_TOKEN_EXPIRY;

    /** Check for Expired Tokens every 5 minutes. */
    private static final int EXPIRED_TOKEN_TIMER = 1000 * 60 * 5;

    /** DHTTokenTableBasic Logger. */
    private static final Logger LOGGER = Logger
            .getLogger(DHTTokenTableBasic.class);

    /** List of Tokens. */
    private final ConcurrentSortedList<DHTToken> tokens =
            new ConcurrentSortedList<DHTToken>(
                    DHTTokenComparator.getInstance(), false);

    @Override
    public void add(final InetAddress addr, final int port,
            final byte[] secret) {

        LOGGER.debug("add token: " + java.util.Arrays.toString(secret)
                + " from "
                + java.util.Arrays.toString(addr.getAddress())
                + " port " + port);

        DHTToken dhtToken = createToken(addr, port, secret);

        this.tokens.add(dhtToken);
    }

    @Override
    public DHTToken get(final InetAddress addr, final int port,
            final byte[] secret) {
        return this.tokens.get(createToken(addr, port, secret));
    }

    @Override
    public boolean valid(final InetAddress addr, final int port,
            final byte[] secret) {

        boolean valid = false;
        DHTToken token = this.tokens.get(createToken(addr, port, secret));

        if (token != null) {

            byte[] addr0 = DHTConversion.toByteArray(token.getAddress());

            valid = isValid(token, new Date())
                    && Arrays.equals(addr0, addr.getAddress())
                    && token.getPort() == port;
        }

        return valid;
    }

    /**
     * Checks whether DHTToken is valid.
     * @param token  DHTToken
     * @param now date
     * @return boolean
     */
    private boolean isValid(final DHTToken token, final Date now) {

        Calendar c = Calendar.getInstance();
        c.setTime(token.getAddedDate());
        c.add(Calendar.MINUTE, getTokenExpiryInMinutes());

        return now.before(c.getTime());
    }

    /**
     * Every 15 minutes check for expired Tokens.
     */
    @Scheduled(fixedDelay = EXPIRED_TOKEN_TIMER)
    public void removeExpiredTokens() {

        Date now = new Date();
        LOGGER.debug("deleting expired tokens " + now);

        List<DHTToken> removeTokens = new ArrayList<DHTToken>();

        Object[] objs = tokens.toArray();

        for (int i = 0; i < objs.length; i++) {

            DHTToken token = (DHTToken) objs[i];
            if (!isValid(token, now)) {
                LOGGER.debug("removing expired token " + token.getInfoHash()
                    + " added " + token.getAddedDate() + " now " + now);

                removeTokens.add(token);
            }
        }

        if (!removeTokens.isEmpty()) {
            this.tokens.removeAll(removeTokens);
        }
    }

    /**
     * Creates a token.
     * @param addr  address
     * @param port  port
     * @param secret  secret
     * @return DHTToken
     */
    private DHTToken createToken(final InetAddress addr, final int port,
            final byte[] secret) {
        BigInteger id = DHTConversion.toBigInteger(secret);
        DHTToken dhtToken = new DHTTokenBasic(id, addr.getAddress(), port);
        return dhtToken;
    }

    /**
     * @return int
     */
    public int getTokenExpiryInMinutes() {
        return tokenExpiryInMinutes;
    }

    /**
     * Sets Token Expiry.
     * @param timeout timeout in minutes
     */
    public void setTokenExpiryInMinutes(final int timeout) {
        this.tokenExpiryInMinutes = timeout;
    }
}

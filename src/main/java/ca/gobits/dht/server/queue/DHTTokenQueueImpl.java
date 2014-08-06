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

package ca.gobits.dht.server.queue;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Random;

import org.apache.log4j.Logger;

import ca.gobits.dht.DHTToken;
import ca.gobits.dht.comparator.DHTTokenComparator;
import ca.gobits.dht.util.ConcurrentSortedList;

/**
 * Basic implementation of DHTTokenTable.
 */
public final class DHTTokenQueueImpl implements DHTTokenQueue {

    /** Used to build output as Hex. */
    private static final char[] HEX_CHARS = {'0', '1', '2', '3', '4', '5',
            '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f' };

    /** Pseudorandom Numbers Generator. */
    private static final Random RANDOM = new Random();

    /** Default Token Expiry. */
    private static final int DEFAULT_TOKEN_EXPIRY = 15;

    /** How to Token stays valid. */
    private int tokenExpiryInMinutes = DEFAULT_TOKEN_EXPIRY;

    /** DHTTokenTableBasic Logger. */
    private static final Logger LOGGER = Logger
            .getLogger(DHTTokenQueueImpl.class);

    /** List of Tokens. */
    private final ConcurrentSortedList<DHTToken> tokens =
            new ConcurrentSortedList<DHTToken>(
                    DHTTokenComparator.getInstance(), false);

    /** Date TransactionId was last updated. */
    private Date transactionLastUpdated = null;

    /** Current Transaction Id. */
    private String transactionId1;

    /** Last Transaction Id. */
    private String transactionId2;

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
            final byte[] secret) throws UnknownHostException {

        boolean valid = false;
        DHTToken token = this.tokens.get(createToken(addr, port, secret));

        if (token != null) {

            byte[] addr0 = token.getAddress().getAddress();
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
        return isValid(token.getAddedDate(), now);
    }

    /**
     * Verify that now is before date + getTokenExpiryInMinutes().
     * @param date  date
     * @param now  now
     * @return boolean
     */
    private boolean isValid(final Date date, final Date now) {
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        c.add(Calendar.MINUTE, getTokenExpiryInMinutes());

        return now.before(c.getTime());
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
        DHTToken dhtToken = new DHTToken(secret, addr.getAddress(), port);
        return dhtToken;
    }

    /**
     * @return int
     */
    public int getTokenExpiryInMinutes() {
        return this.tokenExpiryInMinutes;
    }

    /**
     * Sets Token Expiry.
     * @param timeout timeout in minutes
     */
    public void setTokenExpiryInMinutes(final int timeout) {
        this.tokenExpiryInMinutes = timeout;
    }

    @Override
    public String getTransactionId() {

        if (this.transactionLastUpdated == null) {
            this.transactionLastUpdated = new Date();
        }

        if (!isValid(this.transactionLastUpdated, new Date())) {
            this.transactionId2 = this.transactionId1;
            this.transactionId1 = null;
        }

        if (this.transactionId1 == null) {
            this.transactionId1 = ""
                    + HEX_CHARS[RANDOM.nextInt(HEX_CHARS.length)]
                    + HEX_CHARS[RANDOM.nextInt(HEX_CHARS.length)];
        }

        if (this.transactionId2 == null) {
            this.transactionId2 = this.transactionId1;
        }

        return this.transactionId1;
    }

    @Override
    public boolean isValidTransactionId(final String transactionId) {

        boolean valid = false;

        if (transactionId != null) {
            valid = transactionId.equals(this.transactionId1)
                    || transactionId.equals(this.transactionId2);
        }

        return valid;
    }

    @Override
    public void processQueue() {

        Date now = new Date();
        LOGGER.debug("deleting expired tokens " + now);

        List<DHTToken> removeTokens = new ArrayList<DHTToken>();

        Object[] objs = this.tokens.toArray();

        for (int i = 0; i < objs.length; i++) {

            DHTToken token = (DHTToken) objs[i];
            if (!isValid(token, now)) {
                LOGGER.debug("removing expired token "
                        + Arrays.toString(token.getInfoHash()) + " added "
                        + token.getAddedDate() + " now " + now);

                removeTokens.add(token);
            }
        }

        if (!removeTokens.isEmpty()) {
            this.tokens.removeAll(removeTokens);
        }
    }
}

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

package ca.gobits.dht;

import java.io.IOException;
import java.math.BigInteger;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import ca.gobits.cthulhu.domain.DHTNode;
import ca.gobits.cthulhu.domain.DHTNodeFactory;
import ca.gobits.cthulhu.domain.DHTPeer;

import com.google.common.primitives.UnsignedLong;

/**
 * DHT Data Conversion Helper class.
 *
 */
public final class DHTConversion {

    /** Length of IPV6 Bytes. */
    private static final int BYTES_IPV6_LENGTH = 8;

    /** Length of IPV4 Bytes. */
    private static final int BYTES_IPV4_LENGTH = 4;

    /** Constant to convert byte to unsigned int. */
    private static final int BYTE_TO_INT = 0xFF;

    /** Constant number of Bits per Byte. */
    private static final int BITS_PER_BYTE = 8;

    /** Maxmimum number of bytes in an unsigned long. */
    private static final int MAX_LONG_BYTES_LENGTH = 64;

    /** Length Node ID. */
    private static final int NODE_ID_LENGTH = 20;

    /** Contact information for nodes is encoded as a 26-byte string. */
    private static final int COMPACT_NODE_LENGTH = 26;

    /** Compact Node Info length. */
    public static final int COMPACT_ADDR_LENGTH = COMPACT_NODE_LENGTH
            - NODE_ID_LENGTH;

    /** Logger. */
    private static final Logger LOGGER = Logger
            .getLogger(DHTConversion.class);

    /**
     * private constructor.
     */
    private DHTConversion() {
    }

    /**
     * Convert byte array to unsigned long.
     * @param bytes  bytes array
     * @return long
     */
    public static UnsignedLong[] toUnsignedLong(final byte[] bytes) {

        UnsignedLong high = null;
        UnsignedLong low = null;
        StringBuilder sb = new StringBuilder();

        for (byte b : bytes) {
            int i = b & BYTE_TO_INT;
            sb.append(StringUtils.leftPad(Integer.toBinaryString(i),
                    BITS_PER_BYTE, '0'));
        }

        int len = sb.length();

        String highString = sb.substring(0,
                Math.min(len, MAX_LONG_BYTES_LENGTH));
        high = UnsignedLong.valueOf(new BigInteger(highString, 2));

        if (len > MAX_LONG_BYTES_LENGTH) {
            String lowString = sb.substring(MAX_LONG_BYTES_LENGTH);
            low = UnsignedLong.valueOf(new BigInteger(lowString, 2));
        }

        return low != null ? new UnsignedLong[] {high, low}
                : new UnsignedLong[] {high};
    }

    /**
     * Convert UnsignedLong to InetAddress support ipv4 or ipv6.
     * @param high   Most significant (max 64 bits) of the IPv4 or IPv6 Address
     * (IPv4 only 32 bits).
     * @param low    Least significant (max 64 bits) of the IPv6 Address
     * @return InetAddress
     * @throws UnknownHostException  UnknownHostException
     */
    public static InetAddress toInetAddress(final UnsignedLong high,
            final UnsignedLong low) throws UnknownHostException {

        byte[] bytes = toByteArray(high, low);

        return InetAddress.getByAddress(bytes);
    }

    /**
     * Convert UnsignedLong to String.
     * @param high   Most significant (max 64 bits) of the IPv4 or IPv6 Address
     * (IPv4 only 32 bits).
     * @param low    Least significant (max 64 bits) of the IPv6 Address
     * @return String
     */
    public static String toInetAddressAsString(final UnsignedLong high,
            final UnsignedLong low) {

        String ret = null;
        byte[] bytes = toByteArray(high, low);

        try {
            ret = InetAddress.getByAddress(bytes).getHostAddress();
        } catch (UnknownHostException e) {
            ret = "unknown host " + Arrays.toString(bytes);
        }

        return ret;
    }

    /**
     * Convert UnsignedLong to InetAddress support ipv4 or ipv6.
     * @param high   Most significant (max 64 bits) of the IPv4 or IPv6 Address
     * (IPv4 only 32 bits).
     * @param low    Least significant (max 64 bits) of the IPv6 Address
     * @return byte[]
     */
    private static byte[] toByteArray(final UnsignedLong high,
            final UnsignedLong low) {
        boolean isIPV6 = low != null;
        int bcount = isIPV6 ? BYTES_IPV6_LENGTH : BYTES_IPV4_LENGTH;
        byte[] bytes = isIPV6 ? new byte[bcount * 2] : new byte[bcount];
        int destPos = 0;

        byte[] bi = new BigInteger(high.toString()).toByteArray();
        int srcpos = Math.max(bi.length - bcount, 0);
        int len = Math.min(bi.length, bcount);

        System.arraycopy(bi, srcpos, bytes, destPos + (bcount - len), len);
        destPos += bcount;

        if (low != null) {

            bi = new BigInteger(low.toString()).toByteArray();
            srcpos = Math.max(bi.length - bcount, 0);
            len = Math.min(bi.length, bcount);

            System.arraycopy(bi, srcpos, bytes, destPos + (bcount - len), len);
            destPos += bcount;
        }
        return bytes;
    }

    /**
     * Transforms DHTPeer into "Compact IP/Port" format.
     * @param peers  peers to transform
     * @return byte[]
     */
    public static List<byte[]> toByteArrayFromDHTPeer(
            final Collection<DHTPeer> peers) {

        List<byte[]> list = new ArrayList<byte[]>();

        for (DHTPeer peer : peers) {

            try {
                byte[] bb = compactAddress(peer.getAddress(), peer.getPort());
                list.add(bb);
            } catch (UnknownHostException e) {
                LOGGER.trace("Unknown Peer Host: " + peer.toString());
            }
        }

        return list;
    }

    /**
     * Transforms Nodes to "compact node info" mode.
     *
     * @param nodes
     *            Collection of DHTNode objects
     * @return byte[]
     * @throws IOException
     *             IOException
     */
    public static byte[] toByteArrayFromDHTNode(final Collection<DHTNode> nodes)
            throws IOException {

        byte[] bytes = new byte[nodes.size() * COMPACT_NODE_LENGTH];

        int pos = 0;
        for (DHTNode node : nodes) {
            byte[] nodeints = transform(node);
            System.arraycopy(nodeints, 0, bytes, pos, nodeints.length);
            pos += nodeints.length;
        }

        return bytes;
    }

    /**
     * Transform DHTNode into a "Compact node info" format.
     * The first 20-bytes are the Node ID in network byte order.
     * If IPv4 a 6 byte compact IP address /port is added at the end.
     *
     * @param node  DHTNode
     * @return int[]
     * @throws IOException
     *             IOException
     */
    private static byte[] transform(final DHTNode node) throws IOException {

        byte[] dest = new byte[COMPACT_NODE_LENGTH];

        byte[] ids = node.getInfoHash();
        int len = ids.length;
        int length = Math.min(len, NODE_ID_LENGTH);
        int srcpos = len > NODE_ID_LENGTH ? len - NODE_ID_LENGTH : 0;
        int destpos = NODE_ID_LENGTH > len ? NODE_ID_LENGTH - len : 0;
        System.arraycopy(ids, srcpos, dest, destpos, length);

        byte[] addr = node.getAddress().getAddress();
        byte[] addrBytes = compactAddress(addr, node.getPort());

        System.arraycopy(addrBytes, addrBytes.length - COMPACT_ADDR_LENGTH,
                dest, COMPACT_NODE_LENGTH - COMPACT_ADDR_LENGTH,
                COMPACT_ADDR_LENGTH);

        return dest;
    }

    /**
     * "Compact IP-address/port info" the 4-byte IP address is in network
     * byte order with the 2 byte port in network byte order concatenated
     * onto the end.
     * @param address  IP Address
     * @param port  port number
     * @return byte[]
     */
    public static byte[] compactAddress(final InetAddress address,
            final int port) {
        byte[] bytes = address.getAddress();
        return compactAddress(bytes, port);
    }

    /**
     * "Compact IP-address/port info" the 4-byte IP address is in network
     * byte order with the 2 byte port in network byte order concatenated
     * onto the end.
     * @param address  IP Address
     * @param port  port number
     * @return byte[]
     */
    public static byte[] compactAddress(final byte[] address,
            final int port) {
        byte[] ret = new byte[address.length + 2];
        System.arraycopy(address, 0, ret, 0, address.length);
        System.arraycopy(new byte[] {(byte) (port >>> BITS_PER_BYTE),
                (byte) port }, 0, ret, address.length, 2);

        return ret;
    }

    /**
     * Converts "Compact IP-address/port info" into an InetAddress.
     * @param bytes  bytes
     * @return InetAddress
     * @throws UnknownHostException  UnknownHostException
     */
    public static InetAddress compactAddress(final byte[] bytes)
            throws UnknownHostException {
        byte[] bb = new byte[bytes.length - 2];
        System.arraycopy(bytes, 0, bb, 0, bb.length);
        return InetAddress.getByAddress(bb);
    }

    /**
     * Decodes a compact IP-address/port info" and returns the port.
     * @param bytes  compact IP-address/port info
     * @return int
     */
    public static int compactAddressPort(final byte[] bytes) {
        int len = bytes.length;
        int port = (bytes[len - 2] & BYTE_TO_INT) << BITS_PER_BYTE
                | (bytes[len - 1] & BYTE_TO_INT);

        return port;
    }

    /**
     * Transform byte[] unsigned byte then to a BigInteger.
     *
     * @param bytes
     *            byte array
     * @return BigInteger
     */
    public static BigInteger toBigInteger(final byte[] bytes) {
        byte[] bb = new byte[bytes.length + 1];
        System.arraycopy(bytes, 0, bb, 1, bytes.length);
        return new BigInteger(bb);
    }

    /**
     * Transforms byte array to DHTNode.
     *
     * @param bytes
     *            bytes array
     * @return Collection<DHTNode>
     */
    public static Collection<DHTNode> toDHTNode(final byte[] bytes) {

        if (bytes.length % COMPACT_NODE_LENGTH == 0) {

            int pos = 0;
            int count = bytes.length / COMPACT_NODE_LENGTH;
            Collection<DHTNode> nodes = new ArrayList<DHTNode>(count);

            while (pos < bytes.length) {

                byte[] nodeId = new byte[NODE_ID_LENGTH];
                byte[] addr = new byte[COMPACT_ADDR_LENGTH];
                System.arraycopy(bytes, pos, nodeId, 0, NODE_ID_LENGTH);
                System.arraycopy(bytes, pos + NODE_ID_LENGTH, addr, 0,
                        COMPACT_ADDR_LENGTH);
                pos += COMPACT_NODE_LENGTH;

                int port = compactAddressPort(addr);

                DHTNode node = DHTNodeFactory.create(nodeId,
                        java.util.Arrays.copyOfRange(addr, 0, addr.length - 2),
                        port, DHTNode.State.UNKNOWN);

                nodes.add(node);
            }

            return nodes;
        }

        throw new IllegalArgumentException("invalid byte length");
    }
}

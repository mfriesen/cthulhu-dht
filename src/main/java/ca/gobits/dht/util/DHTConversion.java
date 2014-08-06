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

package ca.gobits.dht.util;

import static ca.gobits.dht.DHTIdentifier.NODE_ID_LENGTH;
import static ca.gobits.dht.factory.DHTNodeFactory.create;

import java.io.IOException;
import java.math.BigInteger;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import ca.gobits.dht.DHTNode;
import ca.gobits.dht.DHTNode.State;
import ca.gobits.dht.DHTPeer;

import com.google.common.primitives.UnsignedLong;

/**
 * DHT Data Conversion Helper class.
 *
 */
public final class DHTConversion {

    /** Length of IPV6 Bytes. */
    private static final int BYTES_IPV6_LENGTH = 16;

    /** Length of IPV4 Bytes. */
    private static final int BYTES_IPV4_LENGTH = 4;

    /** Constant to convert byte to unsigned int. */
    public static final int BYTE_TO_INT = 0xFF;

    /** Constant number of Bits per Byte. */
    private static final int BITS_PER_BYTE = 8;

    /** Maxmimum number of bytes in an unsigned long. */
    private static final int MAX_LONG_BYTES_LENGTH = 64;

    /**
     * private constructor.
     */
    private DHTConversion() {
    }

    /**
     * Make sure bytes array fits a certain length,
     * if bytes.length > len then end bytes are returned.
     * IE: bytes.substring(bytes.length - len, len)
     * @param bytes  bytes
     * @param len  length
     * @return byte[]
     */
    public static byte[] fitToSize(final byte[] bytes, final int len) {

        byte[] ret = bytes;

        if (bytes.length != len) {
            ret = new byte[len];
            System.arraycopy(bytes,
                    Math.max(0, bytes.length - len),
                    ret,
                    len - Math.min(bytes.length, len),
                    Math.min(bytes.length, len));
        }

        return ret;
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

        byte[] bytes = null;

        if (low != null) {

            bytes = new byte[BYTES_IPV6_LENGTH];

            byte[] msb = fitToSize(
                    new BigInteger(high.toString()).toByteArray(),
                    BYTES_IPV6_LENGTH / 2);

            byte[] lsb = fitToSize(
                    new BigInteger(low.toString()).toByteArray(),
                    BYTES_IPV6_LENGTH / 2);

            System.arraycopy(msb, 0, bytes, 0, msb.length);
            System.arraycopy(lsb, 0, bytes, msb.length, lsb.length);

        } else {

            bytes = new byte[BYTES_IPV4_LENGTH];

            byte[] msb = fitToSize(
                    new BigInteger(high.toString()).toByteArray(),
                    BYTES_IPV4_LENGTH);

            System.arraycopy(msb, 0, bytes, 0, msb.length);
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

            InetAddress addr = peer.getAddress();

            if (addr != null) {
                byte[] bb = compactAddress(peer.getAddress(), peer.getPort());
                list.add(bb);
            }
        }

        return list;
    }

    /**
     * Transforms Nodes to "compact node info" mode.
     *
     * @param nodes  Collection of DHTNode objects
     * @param ipv6  is IPv6
     * @return byte[]
     * @throws IOException
     *             IOException
     */
    public static byte[] toByteArrayFromDHTNode(
            final Collection<DHTNode> nodes, final boolean ipv6)
            throws IOException {

        int pos = 0;
        byte[] bytes = null;

        for (DHTNode node : nodes) {
            byte[] nodeints = transform(node, ipv6);

            if (bytes == null) {
                bytes = new byte[nodes.size() * nodeints.length];
            }

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
     * @param isIPv6  whether IPv6 transform
     * @return int[]
     * @throws IOException
     *             IOException
     */
    private static byte[] transform(final DHTNode node, final boolean isIPv6)
            throws IOException {

        byte[] infohash = node.getInfoHash();
        byte[] addr = node.getAddress().getAddress();
        byte[] compact = compactAddress(addr, node.getPort());

        byte[] dest = new byte[infohash.length + compact.length];
        System.arraycopy(infohash, 0, dest, 0, infohash.length);
        System.arraycopy(compact, 0, dest, infohash.length, compact.length);

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
     */
    public static InetAddress compactAddress(final byte[] bytes) {

        InetAddress addr;

        try {
            byte[] bb = new byte[bytes.length - 2];
            System.arraycopy(bytes, 0, bb, 0, bb.length);
            addr = InetAddress.getByAddress(bb);
        } catch (Exception e) {
            addr = null;
        }

        return addr;
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
     * @param bytes  bytes array
     * @param isIPv6  is IPv6 request
     * @return Collection<DHTNode>
     */
    public static Collection<DHTNode> toDHTNode(final byte[] bytes,
            final boolean isIPv6) {

        int addrLen = isIPv6 ? BYTES_IPV6_LENGTH + 2 : BYTES_IPV4_LENGTH + 2;
        int len = NODE_ID_LENGTH + addrLen;

        if (bytes.length % len == 0) {

            Collection<DHTNode> nodes = new ArrayList<DHTNode>();

            for (int i = 0; i < bytes.length / len; i++) {

                byte[] infoHash = new byte[NODE_ID_LENGTH];
                System.arraycopy(bytes, len * i, infoHash, 0, NODE_ID_LENGTH);

                byte[] addr = new byte[addrLen];
                System.arraycopy(bytes, len * i + NODE_ID_LENGTH, addr, 0,
                        addrLen);

                InetAddress iaddr = compactAddress(addr);
                int port = compactAddressPort(addr);

                if (iaddr != null) {

                    DHTNode node = create(infoHash, iaddr, port, State.UNKNOWN);
                    nodes.add(node);
                }
            }

            return nodes;
        }

        throw new IllegalArgumentException("invalid byte length");
    }
}

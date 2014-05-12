package ca.gobits.dht;

import java.io.IOException;
import java.math.BigInteger;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import ca.gobits.cthulhu.domain.DHTNode;
import ca.gobits.cthulhu.domain.DHTNodeBasic;
import ca.gobits.cthulhu.domain.DHTPeer;

/**
 * DHT Data Conversion Helper class.
 *
 */
public final class DHTConversion {

    /** Constant to convert byte to unsigned int. */
    static final int BYTE_TO_INT = 0xFF;

    /** Constant number of Bits per Byte. */
    static final int BITS_PER_BYTE = 8;

    /** Bytes in Signed Long. */
    static final int MAX_LONG_BYTES = BITS_PER_BYTE / 2;

    /** Length Node ID. */
    public static final int NODE_ID_LENGTH = 20;

    /** Contact information for nodes is encoded as a 26-byte string. */
    static final int COMPACT_NODE_LENGTH = 26;

    /** Compact Node Info length. */
    public static final int COMPACT_ADDR_LENGTH = COMPACT_NODE_LENGTH
            - NODE_ID_LENGTH;

    /**
     * private constructor.
     */
    private DHTConversion() {
    }

    /**
     * Every 4 bytes into 1 long.
     * @param bytes  bytes
     * @return long[]
     */
    public static long[] toLongArray(final byte[] bytes) {

        int pos = 0;
        long[] arr = new long[bytes.length / MAX_LONG_BYTES];

        for (int i = 0; i < bytes.length; i += MAX_LONG_BYTES) {
            byte[] by = new byte[MAX_LONG_BYTES];
            System.arraycopy(bytes, i, by, 0, MAX_LONG_BYTES);
            arr[pos] = toLong(by);
            pos++;
        }

        return arr;
    }

    /**
     * Convert byte array to long.
     * @param bytes  bytes array
     * @return long
     */
    private static long toLong(final byte[] bytes) {

        long l = 0;
        for (byte b : bytes) {
            l = l << BITS_PER_BYTE;
            l += b & BYTE_TO_INT;
        }

        return l;
    }

    /**
     * Converts a long array to byte array.
     *
     * @param longs   long array
     * @return byte[]
     */
    public static byte[] toByteArray(final long[] longs) {

        byte[] r = new byte[longs.length * MAX_LONG_BYTES];

        for (int i = 0; i < longs.length; i++) {
            byte[] bb = toByteArray(longs[i]);
            System.arraycopy(bb, 0, r, i * MAX_LONG_BYTES, bb.length);
        }

        return r;
    }

    /**
     * Converts a long to byte array.
     *
     * @param l   long
     * @return byte[]
     */
    public static byte[] toByteArray(final long l) {

        byte[] bytes = ByteBuffer.allocate(BITS_PER_BYTE).putLong(l).array();

        int count = BITS_PER_BYTE / 2; // remove signed bit
        return java.util.Arrays.copyOfRange(bytes, count, bytes.length);
    }

    /**
     * Convert long array to InetAddress.
     * @param longs  longs array
     * @return InetAddress
     * @throws UnknownHostException  UnknownHostException
     */
    public static InetAddress toInetAddress(final long[] longs)
            throws UnknownHostException {

        InetAddress addr = null;
        byte[] bytes = toByteArray(longs);

        if (bytes.length > MAX_LONG_BYTES) {
            addr = Inet6Address.getByAddress(bytes);
        } else {
            addr = InetAddress.getByAddress(bytes);
        }

        return addr;
    }

    /**
     * Converts longs[] to IP Address.
     * @param longs  address
     * @return String
     */
    public static String toInetAddressString(final long[] longs) {

        String s = null;

        try {
            InetAddress addr = toInetAddress(longs);
            s = addr.getHostAddress();
        } catch (UnknownHostException e) {
            s = "unknown";
        }

        return s;
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
            byte[] bytes = compactAddress(peer.getAddress(), peer.getPort());
            list.add(bytes);
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

        byte[] ids = node.getInfoHash().toByteArray();
        int len = ids.length;
        int length = Math.min(len, NODE_ID_LENGTH);
        int srcpos = len > NODE_ID_LENGTH ? len - NODE_ID_LENGTH : 0;
        int destpos = NODE_ID_LENGTH > len ? NODE_ID_LENGTH - len : 0;
        System.arraycopy(ids, srcpos, dest, destpos, length);

        byte[] addr = toByteArray(node.getAddress());
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
    public static byte[] compactAddress(final long[] address,
            final int port) {
        byte[] bytes = toByteArray(address);
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
     * Decodes a compact IP-address/port info".
     * The 4-byte IP address is in network byte order with
     * the 2 byte port in network byte order concatenated onto the end.
     * @param bytes  compact IP-address/port info
     * @return String <ipadress>:<port>
     */
    public static String decodeCompactAddressToString(final byte[] bytes) {

        String ip = decodeCompactAddress(bytes);

        int port = decodeCompactAddressPort(bytes);

        return ip + ":" + port;
    }

    /**
     * Decodes a compact IP-address/port info".
     * @param bytes  compact IP-address/port info
     * @return String <ipaddress>
     */
    public static String decodeCompactAddress(final byte[] bytes) {

        int i = 0;
        int len = bytes.length;

        StringBuilder ip = new StringBuilder();
        while (i < len - 2) {
            if (i > 0) {
                ip.append(".");
            }
            ip.append(bytes[i] & BYTE_TO_INT);
            i++;
        }
        return ip.toString();
    }

    /**
     * Decodes a compact IP-address/port info" and returns the port.
     * @param bytes  compact IP-address/port info
     * @return int
     */
    public static int decodeCompactAddressPort(final byte[] bytes) {
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

                byte[] id = new byte[NODE_ID_LENGTH];
                byte[] addr = new byte[COMPACT_ADDR_LENGTH];
                System.arraycopy(bytes, pos, id, 0, NODE_ID_LENGTH);
                System.arraycopy(bytes, pos + NODE_ID_LENGTH, addr, 0,
                        COMPACT_ADDR_LENGTH);
                pos += COMPACT_NODE_LENGTH;

                BigInteger nodeId = DHTConversion.toBigInteger(id);

                int port = decodeCompactAddressPort(addr);

                DHTNode node = new DHTNodeBasic(nodeId,
                        java.util.Arrays.copyOfRange(addr, 0, addr.length - 2),
                        port);
                nodes.add(node);
            }

            return nodes;

        }

        throw new IllegalArgumentException("invalid byte length");
    }

}
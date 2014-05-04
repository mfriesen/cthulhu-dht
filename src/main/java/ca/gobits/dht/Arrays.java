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

package ca.gobits.dht;

import java.io.IOException;
import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Collection;

import ca.gobits.cthulhu.domain.DHTNode;

/**
 * Arrays Helper Methods.
 *
 */
public final class Arrays {

    /** Constant to convert byte to unsigned int. */
    static final int BYTE_TO_INT = 0xFF;

    /** Constant number of Bits per Byte. */
    static final int BITS_PER_BYTE = 8;

    /** Length Node ID. */
    public static final int NODE_ID_LENGTH = 20;

    /** Contact information for nodes is encoded as a 26-byte string. */
    public static final int COMPACT_NODE_LENGTH = 26;

    /** Compact Node Info length. */
    public static final int COMPACT_ADDR_LENGTH = COMPACT_NODE_LENGTH
            - NODE_ID_LENGTH;

    /**
     * private constructor.
     */
    private Arrays() {
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
     * Transforms BigInteger to Byte array.
     * @param bi  BigInteger
     * @return byte[]
     */
    public static byte[] toByteArray(final BigInteger bi) {
        return bi.toByteArray();
    }

    /**
     * Converts a byte array to a long.
     *
     * @param bytes
     *            byte array
     * @return long
     */
    public static long toLong(final byte[] bytes) {

        long l = 0;
        for (byte b : bytes) {
            l = l << BITS_PER_BYTE;
            l += b & BYTE_TO_INT;
        }

        return l;
    }

    /**
     * Converts a long to byte array.
     *
     * @param l
     *            long
     * @return byte[]
     */
    public static byte[] toByteArray(final long l) {

        int count = 0;
        byte[] bytes = ByteBuffer.allocate(BITS_PER_BYTE).putLong(l).array();

        for (byte b : bytes) {

            if (b == 0) {
                count++;
            } else {
                break;
            }
        }

        return count > 0
                ? java.util.Arrays.copyOfRange(bytes, count, bytes.length)
                : bytes;
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

                BigInteger nodeId = Arrays.toBigInteger(id);

                int port = BDecoder.decodeCompactAddressPort(addr);

                DHTNode node = new DHTNode(nodeId,
                        java.util.Arrays.copyOfRange(addr, 0, addr.length - 2),
                        port);
                nodes.add(node);
            }

            return nodes;

        }

        throw new IllegalArgumentException("invalid byte length");
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
    public static byte[] toByteArray(final Collection<DHTNode> nodes)
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
     * Transform DHTNode into 26-byte string. Known as "Compact node info" the
     * 20-byte Node ID in network byte order has the compact IP-address/port
     * info concatenated to the end.
     *
     * @param node
     *            DHTNode
     * @return int[]
     * @throws IOException
     *             IOException
     */
    private static byte[] transform(final DHTNode node) throws IOException {

        byte[] dest = new byte[COMPACT_NODE_LENGTH];

        byte[] ids = node.getId().toByteArray();
        int len = ids.length;
        int length = Math.min(len, NODE_ID_LENGTH);
        int srcpos = len > NODE_ID_LENGTH ? len - NODE_ID_LENGTH : 0;
        int destpos = NODE_ID_LENGTH > len ? NODE_ID_LENGTH - len : 0;
        System.arraycopy(ids, srcpos, dest, destpos, length);

        byte[] addrBytes = Arrays.toByteArray(node.getAddress());
        System.arraycopy(addrBytes, addrBytes.length - COMPACT_ADDR_LENGTH,
                dest, COMPACT_NODE_LENGTH - COMPACT_ADDR_LENGTH,
                COMPACT_ADDR_LENGTH);

        return dest;
    }
}

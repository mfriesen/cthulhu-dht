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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * BDecoder decodes objects as per spec
 * https://wiki.theory.org/BitTorrentSpecification#Bencoding.
 */
public final class BDecoder {

    /** Number of bits per byte. */
    private static final int BIT_COUNT = 8;

    /** decoder string position. */
    private int position = 0;

    /**
     * BDecoder constructor.
     */
    public BDecoder() {
    }

    /**
     * Decodes a compact IP-address/port info".
     * The 4-byte IP address is in network byte order with
     * the 2 byte port in network byte order concatenated onto the end.
     * @param bytes  compact IP-address/port info
     * @return String <ipadress>:<port>
     */
    public static String decodeCompactIP(final byte[] bytes) {

        int i = 0;
        int len = bytes.length;

        StringBuilder ip = new StringBuilder();
        while (i < len - 2) {
            if (i > 0) {
                ip.append(".");
            }
            ip.append(bytes[i] & Arrays.BYTE_TO_INT);
            i++;
        }

        int port = (bytes[len - 2] & Arrays.BYTE_TO_INT) << BIT_COUNT
                | (bytes[len - 1] & Arrays.BYTE_TO_INT);

        return ip + ":" + port;
    }

    /**
     * B decodes an character array.
     * @param bytes  bytes array to decode
     * @return Object
     */
    public Object decode(final byte[] bytes) {

        Object obj = null;
        char type = (char) bytes[position];

        if (type == 'd') { // map

            obj = buildMap(bytes);

        } else if (type == 'l') { // list

            obj = buildList(bytes);

        } else if (type == 'i') {  // long

            obj = buildLong(bytes);

        } else {  // bytes

            int len = getInt(bytes);
            position++; // throw away ':'
            obj = getBytes(bytes, len);
        }

        return obj;
    }

    /**
     * Builds List<Object> Object.
     * @param bytes  bytes array
     * @return List<Object>
     */
    private List<Object> buildList(final byte[] bytes) {
        position++;
        List<Object> list = new ArrayList<Object>();

        while ((char) bytes[position] != 'e') {
            list.add(decode(bytes));
        }

        position++;

        return list;
    }

    /**
     * Builds Map<Object, Object> Object.
     * @param bytes  bytes array
     * @return Map<Object, Object>
     */
    private Map<Object, Object> buildMap(final byte[] bytes) {

        position++;

        Map<Object, Object> map = new HashMap<Object, Object>();

        while ((char) bytes[position] != 'e') {

            int len = getInt(bytes);
            position++; // throw away ':'

            String key = new String(getBytes(bytes, len));

            map.put(key, decode(bytes));
        }

        position++;

        return map;
    }

    /**
     * Builds Long Object.
     * @param bytes bytes array
     * @return Long
     */
    private Long buildLong(final byte[] bytes) {
        position++;
        StringBuilder sb = new StringBuilder();

        while ((char) bytes[position] != 'e') {
            sb.append((char) bytes[position]);
            position++;
        }

        position++;

        return Long.valueOf(sb.toString());
    }

    /**
     * Copies a len of bytes.
     * @param bytes  bytes
     * @param len  length to copy
     * @return byte[]
     */
    private byte[] getBytes(final byte[] bytes, final int len) {
        byte[] dest = new byte[len];
        System.arraycopy(bytes, position, dest, 0, len);
        position += len;
        return dest;
    }

    /**
     * Copies an integer from the character array.
     * @param bytes bytes array
     * @return int
     */
    private int getInt(final byte[] bytes) {

        StringBuilder sb = new StringBuilder();

        while (position < bytes.length) {

            if (Character.isDigit((char) bytes[position])) {
                sb.append((char) bytes[position]);
            } else {
                break;
            }

            position++;
        }

        return Integer.valueOf(sb.toString()).intValue();
    }
}

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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * BDecoder decodes objects as per spec
 * https://wiki.theory.org/BitTorrentSpecification#Bencoding.
 */
public final class BDecoder {

    /** Constant to convert byte to unsigned int. */
    static final int BYTE_TO_INT = 0xFF;

    /** decoder string position. */
    private int position = 0;

    /**
     * BDecoder constructor.
     */
    public BDecoder() {
    }

    /**
     * B decodes an character array.
     * @param bytes  bytes array to decode
     * @return Object
     */
    public Object decode(final byte[] bytes) {

        Object obj = null;
        char type = (char) bytes[this.position];

        if (type == 'd') { // map

            obj = buildMap(bytes);

        } else if (type == 'l') { // list

            obj = buildList(bytes);

        } else if (type == 'i') {  // long

            obj = buildLong(bytes);

        } else {  // bytes

            int len = getInt(bytes);
            this.position++; // throw away ':'
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
        this.position++;
        List<Object> list = new ArrayList<Object>();

        while ((char) bytes[this.position] != 'e') {
            list.add(decode(bytes));
        }

        this.position++;

        return list;
    }

    /**
     * Builds Map<Object, Object> Object.
     * @param bytes  bytes array
     * @return Map<Object, Object>
     */
    private Map<Object, Object> buildMap(final byte[] bytes) {

        this.position++;

        Map<Object, Object> map = new HashMap<Object, Object>();

        while ((char) bytes[this.position] != 'e') {

            int len = getInt(bytes);
            this.position++; // throw away ':'

            String key = new String(getBytes(bytes, len));

            map.put(key, decode(bytes));
        }

        this.position++;

        return map;
    }

    /**
     * Builds Long Object.
     * @param bytes bytes array
     * @return Long
     */
    private Long buildLong(final byte[] bytes) {
        this.position++;
        StringBuilder sb = new StringBuilder();

        while ((char) bytes[this.position] != 'e') {
            sb.append((char) bytes[this.position]);
            this.position++;
        }

        this.position++;

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
        System.arraycopy(bytes, this.position, dest, 0, len);
        this.position += len;
        return dest;
    }

    /**
     * Copies an integer from the character array.
     * @param bytes bytes array
     * @return int
     */
    private int getInt(final byte[] bytes) {

        StringBuilder sb = new StringBuilder();

        while (this.position < bytes.length) {

            if (Character.isDigit((char) bytes[this.position])) {
                sb.append((char) bytes[this.position]);
            } else {
                break;
            }

            this.position++;
        }

        return Integer.valueOf(sb.toString()).intValue();
    }
}

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

package ca.gobits.cthulhu.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * BDecoder decodes objects as per spec
 * https://wiki.theory.org/BitTorrentSpecification#Bencoding.
 */
public class BDecoder {

    /** decoder string position. */
    private int position = 0;

    /**
     * BDecoder constructor.
     */
    public BDecoder() {
    }

    /**
     * B decodes an string.
     * @param s  string to decode
     * @return Object
     */
    public final Object decode(final String s) {

        position = 0;

        char[] chars = s.toCharArray();
        return decode(chars);
    }

    /**
     * B decodes an character array.
     * @param chars  char array to decode
     * @return Object
     */
    public final Object decode(final char[] chars) {

        Object obj = null;
        char type = chars[position];

        if (type == 'd') { // map

            obj = buildMap(chars);

        } else if (type == 'l') { // list

            obj = buildList(chars);

        } else if (type == 'i') {  // long

            obj = buildLong(chars);

        } else {  // string

            obj = buildString(chars);
        }

        return obj;
    }

    /**
     * Builds String.
     * @param chars character array
     * @return String
     */
    private String buildString(final char[] chars) {
        int len = getInt(chars);
        position++; // throw away ':'
        return getString(chars, len);
    }

    /**
     * Builds List<Object> Object.
     * @param chars character array
     * @return List<Object>
     */
    private List<Object> buildList(final char[] chars) {
        position++;
        List<Object> list = new ArrayList<Object>();

        while (chars[position] != 'e') {
            list.add(decode(chars));
        }

        position++;

        return list;
    }

    /**
     * Builds Map<Object, Object> Object.
     * @param chars character array
     * @return Map<Object, Object>
     */
    private Map<Object, Object> buildMap(final char[] chars) {

        position++;

        Map<Object, Object> map = new HashMap<Object, Object>();

        while (chars[position] != 'e') {

            int len = getInt(chars);
            position++; // throw away ':'

            String key = getString(chars, len);

            map.put(key, decode(chars));
        }

        position++;

        return map;
    }

    /**
     * Builds Long Object.
     * @param chars character array
     * @return Long
     */
    private Long buildLong(final char[] chars) {
        position++;
        StringBuilder sb = new StringBuilder();

        while (chars[position] != 'e') {
            sb.append(chars[position]);
            position++;
        }

        position++;

        return Long.valueOf(sb.toString());
    }

    /**
     * Copys a substring of the character array.
     * @param chars character array
     * @param len length to copy
     * @return String
     */
    private String getString(final char[] chars, final int len) {
        char[] dest = new char[len];
        System.arraycopy(chars, position, dest, 0, len);

        String s = new String(dest);
        position += s.length();
        return s;
    }

    /**
     * Copies an integer from the character array.
     * @param chars character array
     * @return int
     */
    private int getInt(final char[] chars) {

        StringBuilder sb = new StringBuilder();

        while (position < chars.length) {

            if (Character.isDigit(chars[position])) {
                sb.append(chars[position]);
            } else {
                break;
            }

            position++;
        }

        return Integer.valueOf(sb.toString()).intValue();
    }
}

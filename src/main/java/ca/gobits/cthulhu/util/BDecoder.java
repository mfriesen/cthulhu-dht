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

        char type = chars[position];

        // i , d, l
        // map

        if (type == 'd') {

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

        } else if (type == 'l') {

            position++;
            List<Object> list = new ArrayList<Object>();

            while (chars[position] != 'e') {
                list.add(decode(chars));
//                position++;
            }

            position++;

            return list;

        } else if (type == 'i') {

            position++;
            StringBuilder sb = new StringBuilder();

            while (chars[position] != 'e') {
                sb.append(chars[position]);
                position++;
            }

            position++;

            return Long.valueOf(sb.toString());

        } else {
            int len = getInt(chars);
            position++; // throw away ':'

            String key = getString(chars, len);
            return key;
        }
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

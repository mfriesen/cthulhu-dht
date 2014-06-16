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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Collection;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

/**
 * BEncoder encodes objects as per spec
 * https://wiki.theory.org/BitTorrentSpecification#Bencoding.
 *
 */
public final class BEncoder {

    /** Constant to convert byte to unsigned int. */
    private static final int BYTE_TO_INT = 0xFF;

    /**
     * private constructor.
     */
    private BEncoder() {
    }

    /**
     * B encode an object.
     * @param ob  object to encode
     * @return String
     */
    public static byte[] bencoding(final Object ob) {

        ByteArrayOutputStream os = new ByteArrayOutputStream();
        bencoding(ob, os);

        byte[] bytes = os.toByteArray();

        try {
            os.close();
        } catch (IOException e) {
            return bytes;
        }

        return bytes;
    }

    /**
     * Encodes an object.
     * @param ob object to encode
     * @param os ByteArrayOutputStream to append to
     */
    @SuppressWarnings("unchecked")
    private static void bencoding(final Object ob,
            final ByteArrayOutputStream os) {

        if (ob instanceof byte[]) {
            bencoding((byte[]) ob, os);
        } else if (ob instanceof int[]) {
            bencoding((int[]) ob, os);
        } else if (ob instanceof Map) {
            bencoding((Map<Object, Object>) ob, os);
        } else if (ob instanceof Collection) {
            bencoding((Collection<Object>) ob, os);
        } else if (ob instanceof Number) {
            bencoding((Number) ob, os);
        } else if (ob instanceof String) {
            bencoding((String) ob, os);
        } else {
            throw new IllegalArgumentException("Unsupported Object: "
                    + ob.getClass().getName());
        }
    }

    /**
     * B encode an object.
     * @param c  collection of object to encode
     * @param os ByteArrayOutputStream to append to
     */
    private static void bencoding(final Collection<Object> c,
            final ByteArrayOutputStream os) {

        os.write('l');
        for (Object elem : c) {
            bencoding(elem, os);
        }
        os.write('e');
    }

    /**
     * B encode an object.
     * @param map  map of object to encode
     * @param os ByteArrayOutputStream to append to
     */
    private static void bencoding(final Map<Object, Object> map,
            final ByteArrayOutputStream os) {
        os.write('d');

        SortedMap<Object, Object> smap = new TreeMap<Object, Object>();
        smap.putAll(map);

        for (Map.Entry<Object, Object> e : smap.entrySet()) {
            bencoding(e.getKey(), os);
            bencoding(e.getValue(), os);
        }
        os.write('e');
    }

    /**
     * B encode an object.
     * @param string  string to encode
     * @param os ByteArrayOutputStream to append to
     */
    private static void bencoding(final String string,
            final ByteArrayOutputStream os) {
        bencoding(string.getBytes(), os);
    }

    /**
     * Encode a array of bytes.
     * @param bytes  bytes to encode
     * @param os ByteArrayOutputStream to append to
     */
    private static void bencoding(final byte[] bytes,
            final ByteArrayOutputStream os) {
        byte[] len = String.valueOf(bytes.length).getBytes();

        os.write(len, 0, len.length);
        os.write(':');
        for (byte b : bytes) {
            int i = b & BYTE_TO_INT;
            os.write(i);
        }
    }

    /**
     * Encode a array of ints.
     * @param ints  ints to encode
     * @param os ByteArrayOutputStream to append to
     */
    private static void bencoding(final int[] ints,
            final ByteArrayOutputStream os) {
        byte[] len = String.valueOf(ints.length).getBytes();

        os.write(len, 0, len.length);
        os.write(':');
        for (int i : ints) {
            os.write(i);
        }
    }

    /**
     * B encode an object.
     * @param n  Number to encode
     * @param os ByteArrayOutputStream to append to
     */
    private static void bencoding(final Number n,
            final ByteArrayOutputStream os) {
        byte[] bytes = n.toString().getBytes();
        os.write('i');
        os.write(bytes, 0, bytes.length);
        os.write('e');
    }
}

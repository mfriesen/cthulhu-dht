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
    @SuppressWarnings("unchecked")
    public static String bencoding(final Object ob) {

        String s;
        if (ob instanceof Map) {
            s = bencoding((Map<Object, Object>) ob);
        } else if (ob instanceof Collection) {
            s = bencoding((Collection<Object>) ob);
        } else if (ob instanceof Number) {
            s = bencoding((Number) ob);
        } else if (ob instanceof String) {
            s = bencoding((String) ob);
        } else {
            throw new IllegalArgumentException("Unsupported Object: "
                    + ob.getClass().getName());
        }

        return s;
    }

    /**
     * B encode an object.
     * @param c  collection of object to encode
     * @return String
     */
    public static String bencoding(final Collection<Object> c) {
        StringBuilder sb = new StringBuilder();
        sb.append("l");
        for (Object elem : c) {
            sb.append(bencoding(elem));
        }
        sb.append("e");

        return sb.toString();
    }

    /**
     * B encode an object.
     * @param map  map of object to encode
     * @return String
     */
    public static String bencoding(final Map<Object, Object> map) {
        StringBuilder sb = new StringBuilder();
        sb.append("d");
        SortedMap<Object, Object> smap = new TreeMap<Object, Object>();
        smap.putAll(map);

        for (Map.Entry<Object, Object> e : smap.entrySet()) {
            sb.append(bencoding(e.getKey()) + bencoding(e.getValue()));
        }
        sb.append("e");

        return sb.toString();
    }

    /**
     * B encode an object.
     * @param string  string to encode
     * @return String
     */
    public static String bencoding(final String string) {
        return string.length() + ":" + string;
    }

    /**
     * B encode an object.
     * @param n  Number to encode
     * @return String
     */
    public static String bencoding(final Number n) {
        return "i" + n.toString() + "e";
    }
}

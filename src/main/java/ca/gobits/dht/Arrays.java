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

import java.math.BigInteger;

/**
 * Arrays Helper Methods.
 *
 */
public final class Arrays {

    /** HEX Radix. */
    private static final int HEX_RADIX = 16;

    /** Constant to convert byte to unsigned int. */
    static final int BYTE_TO_INT = 0xFF;

    /**
     * private constructor.
     */
    private Arrays() {
    }

    /**
     * Transform byte[] unsigned byte then to a BigInteger.
     * @param bytes  byte array
     * @return BigInteger
     */
    public static BigInteger toBigInteger(final byte[] bytes) {

        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < bytes.length; i++) {
            int ii = bytes[i] & BYTE_TO_INT;
            sb.append(Integer.toHexString(ii));
        }

        return new BigInteger(sb.toString(), HEX_RADIX);
    }
    /**
     * Transforms BigInteger to byte[].
     * @param d BigInteger
     * @return  byte[]
     */
    public static byte[] toByte(final BigInteger d) {
        return d.toByteArray();
    }
}

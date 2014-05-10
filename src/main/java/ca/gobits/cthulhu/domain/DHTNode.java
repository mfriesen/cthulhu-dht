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

package ca.gobits.cthulhu.domain;

import java.math.BigInteger;
import java.util.Date;

/**
 * DHTNode - holder for information about a DHT Node.
 */
public interface DHTNode {

    /**
     * @return Date
     */
    Date getLastUpdated();

    /**
     * Sets the Last Updated Date.
     * @param date sets Last Updated Date
     */
    void setLastUpdated(final Date date);

    /**
     * @return BigInteger
     */
    BigInteger getInfoHash();

    /**
     * @return long[]
     */
    long[] getAddress();

    /**
     * @return int
     */
    int getPort();
}

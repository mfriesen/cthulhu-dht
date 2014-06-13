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

package ca.gobits.cthulhu.domain;

import java.util.Date;

/**
 * DHTToken - holder for an Address / Token combination.
 *
 */
public interface DHTToken {

    /**
     * @return byte[]
     */
    byte[] getInfoHash();

    /**
     * Sets InfoHash.
     * @param infoHashId  infoHash
     */
    void setInfoHash(final byte[] infoHashId);

    /**
     * @return long[]
     */
    long[] getAddress();

    /**
     * Set the address.
     * @param addr address
     */
    void setAddress(final long[] addr);

    /**
     * @return Date
     */
    Date getAddedDate();

    /**
     * Sets Added Date.
     * @param date  Added Date
     */
    void setAddedDate(final Date date);

    /**
     * @return int
     */
    int getPort();

    /**
     * Sets the Port.
     * @param port  port
     */
    void setPort(final int port);
}

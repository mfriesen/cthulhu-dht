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

package ca.gobits.cthulhu;

import java.net.InetAddress;

import ca.gobits.cthulhu.domain.DHTToken;

/**
 * DHTTokenTable - maintain a list of secrets / ip addresses.
 */
public interface DHTTokenTable {

    /**
     * Adds Address / Token to Token.
     * @param addr  address
     * @param port  port
     * @param secret  secret
     */
    void add(InetAddress addr, int port, byte[] secret);

    /**
     * Gets a token.
     * @param addr  address
     * @param port  port
     * @param secret  secret
     * @return DHTToken
     */
    DHTToken get(InetAddress addr, int port, byte[] secret);

    /**
     * Validates an addr and token combination exists.
     * @param addr  address
     * @param port  port
     * @param secret  secret
     * @return boolean
     */
    boolean valid(InetAddress addr, int port, byte[] secret);
}

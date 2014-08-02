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

package ca.gobits.test.dht.integration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;

import ca.gobits.dht.server.DHTServer;

/**
 * Runs DHTServer Async.
 *
 */
public class DHTServerAsync {

    /** Reference to DHTServer. */
    @Autowired
    private DHTServer server;

//    /** Has server been started. */
//    private static boolean isStarted = false;

    /**
     * Start DHTServer is not already started.
     * @throws Exception  Exception
     */
    @Async
    public void start() throws Exception {
//        if (!isStarted) {
//            isStarted = true;
            this.server.start();
//        }
    }

    /**
     * Shutdown server.
     */
    public void shutdown() {
        this.server.shutdownGracefully();
    }
}

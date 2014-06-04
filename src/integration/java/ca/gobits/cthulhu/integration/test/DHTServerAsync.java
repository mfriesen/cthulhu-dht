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

package ca.gobits.cthulhu.integration.test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;

import ca.gobits.cthulhu.DHTServer;
import ca.gobits.cthulhu.DHTServerConfig;

/**
 * Runs DHTServer Async.
 *
 */
public class DHTServerAsync {

    /** Wait for Server to Start. */
    private static final int SERVER_START_DELAY = 500;

    /** Reference to DHTServer. */
    @Autowired
    private DHTServer server;

    /** Has server been started. */
    private static boolean isStarted = false;

    /**
     * Start DHTServer is not already started.
     * @throws Exception  Exception
     */
    @Async
    public void start() throws Exception {
        if (!isStarted) {
            System.setProperty("port", "" + DHTServerConfig.DEFAULT_PORT);
            isStarted = true;
            server.run();
        }
    }

    /**
     * For for DHTServer to be started.
     * @throws Exception  Exception
     */
    public void waitForServerStart() throws Exception {
        Thread.sleep(SERVER_START_DELAY);
    }
}

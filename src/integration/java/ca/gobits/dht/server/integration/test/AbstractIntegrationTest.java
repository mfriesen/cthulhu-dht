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

package ca.gobits.dht.server.integration.test;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import ca.gobits.dht.server.DHTConfiguration;

/**
 * Abstract Helper Test for Integration Tests that use DHTServer.
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { DHTConfiguration.class,
        IntegrationTestConfiguration.class })
@DirtiesContext(classMode = ClassMode.AFTER_CLASS)
public abstract class AbstractIntegrationTest {

    /** Wait for Server to Start. */
    private static final int SERVER_START_DELAY = 500;

    /** Reference to DHTServer. */
    @Autowired
    private DHTServerAsync server;

    /** Static Reference to DHTServer. */
    private static DHTServerAsync staticServer;

    /** Has server been started. */
    private static boolean isStarted = false;

    /** Starts DHTServer.
     * @throws Exception  Exception
     */
    @Before
    public void before() throws Exception {
        if (!isStarted) {
            isStarted = true;
            this.server.start();
            waitForServerStart();
        }
    }

    /**
     * After test runs.
     * @throws Exception  Exception
     */
    @After
    public void after() throws Exception {
        staticServer = this.server;
    }

    /**
     * AfterClass shutdown server.
     */
    @AfterClass
    public static void onClassTearDown() {
        staticServer.shutdown();
        staticServer = null;
        isStarted = false;
    }

    /**
     * For for DHTServer to be started.
     * @throws Exception  Exception
     */
    public final void waitForServerStart() throws Exception {
        Thread.sleep(SERVER_START_DELAY);
    }
}

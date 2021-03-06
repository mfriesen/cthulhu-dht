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

package ca.gobits.test.dht.server;

import static org.easymock.EasyMock.expect;
import static org.junit.Assert.assertTrue;
import static org.powermock.api.easymock.PowerMock.createMock;
import static org.powermock.api.easymock.PowerMock.replay;
import static org.powermock.api.easymock.PowerMock.verify;
import static org.powermock.api.mockito.PowerMockito.whenNew;

import java.io.ByteArrayOutputStream;
import java.io.PrintWriter;

import org.apache.commons.cli.HelpFormatter;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import ca.gobits.dht.server.DHTServer;
import ca.gobits.dht.server.DHTServerConfig;
import ca.gobits.dht.server.Main;
/**
 * DHTServer UnitTests.
 *
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({ HelpFormatter.class })
public final class MainUnitTest {

    /** Mock ApplicationContext. */
    private final AnnotationConfigApplicationContext ac =
            createMock(AnnotationConfigApplicationContext.class);

    /**
     * testMain01() -?.
     * @throws Exception  Exception
     */
    @Test
    public void testMain01() throws Exception {
        // given
        String[] args = new String[] {"-?"};
        ByteArrayOutputStream bo = new ByteArrayOutputStream();
        PrintWriter pw = new PrintWriter(bo);
        whenNew(PrintWriter.class).withAnyArguments().thenReturn(pw);

        // when
        Main.main(args);

        assertUsage(bo.toString());
    }

    /**
     * testMain02().
     * @throws Exception Exception
     */
    @Test
    public void testMain02() throws Exception {
        // given
        final String[] args = new String[]{};

        DHTServer mockServer = createMock(DHTServer.class);
        DHTServerConfig mockConfig = createMock(DHTServerConfig.class);

        // when
        expect(this.ac.getBean(DHTServerConfig.class)).andReturn(mockConfig);
        mockConfig.parse(args);

        expect(this.ac.getBean(DHTServer.class)).andReturn(mockServer);
        mockServer.start();
        replay(this.ac);
        Main.main(args, this.ac);

        // verify
        verify(this.ac);
    }

    /**
     * testMain03() - server throws exception.
     * @throws Exception Exception
     */
    @Test
    public void testMain03() throws Exception {
        // given
        final String[] args = new String[]{};
        DHTServerConfig mockConfig = createMock(DHTServerConfig.class);

        // when
        expect(this.ac.getBean(DHTServerConfig.class)).andReturn(mockConfig);

        expect(this.ac.getBean(DHTServer.class))
                .andThrow(new RuntimeException());
        replay(this.ac);
        Main.main(args, this.ac);

        // verify
        verify(this.ac);
    }

    /**
     * Assert Usage Message is shown.
     * @param s  String
     */
    private void assertUsage(final String s) {

        String expected1 = "usage: java -jar dht.jar";
        String expected2 = "Parameters";
        String expected3 = "-?             help";
        String expected4 = "-p <arg>       bind to port";
        String expected5 = "-salt <arg>    DHT Node Identifier salt";
        String expected6 = "-nodes <arg>   "
               + "comma-separated list of bootstrap nodes format \"host:port\"";

        assertTrue(s.contains(expected1));
        assertTrue(s.contains(expected2));
        assertTrue(s.contains(expected3));
        assertTrue(s.contains(expected4));
        assertTrue(s.contains(expected5));
        assertTrue(s.contains(expected6));
    }
}

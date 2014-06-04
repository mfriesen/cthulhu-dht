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

package ca.gobits.cthulhu.test;

import static org.easymock.EasyMock.expect;
import static org.junit.Assert.assertTrue;

import java.io.ByteArrayOutputStream;

import org.easymock.EasyMockRunner;
import org.easymock.EasyMockSupport;
import org.easymock.Mock;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.test.util.ReflectionTestUtils;

import ca.gobits.cthulhu.DHTServer;
import ca.gobits.cthulhu.Main;

/**
 * DHTServer UnitTests.
 *
 */
@RunWith(EasyMockRunner.class)
public final class MainUnitTest extends EasyMockSupport {

    /** Mock ApplicationContext. */
    @Mock
    private AnnotationConfigApplicationContext ac;

    /**
     * testMain01() -?.
     */
    @Test
    public void testMain01() {
        // given
        String[] args = new String[] {"-?"};
        ByteArrayOutputStream bo = new ByteArrayOutputStream();
        ReflectionTestUtils.setField(System.out, "out", bo);

        // when
        Main.main(args);

        assertUsage(bo);
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

        // when
        ac.refresh();
        expect(ac.getBean(DHTServer.class)).andReturn(mockServer);
        mockServer.run();
        replayAll();
        Main.main(args, ac);

        // verify
        verifyAll();
    }

    /**
     * testMain03() - server throws exception.
     * @throws Exception Exception
     */
    @Test
    public void testMain03() throws Exception {
        // given
        final String[] args = new String[]{};

        // when
        ac.refresh();
        expect(ac.getBean(DHTServer.class))
                .andThrow(new RuntimeException());
        replayAll();
        Main.main(args, ac);

        // verify
        verifyAll();
    }

    /**
     * Assert Usage Message is shown.
     * @param bo ByteArrayOutputStream
     */
    private void assertUsage(final ByteArrayOutputStream bo) {
        String expected = "usage: java -jar dht.jar\nParameters\n"
            + " -?         help\n"
            + " -p <arg>   bind to port\n";

        assertTrue(bo.toString().contains(expected));
    }
}

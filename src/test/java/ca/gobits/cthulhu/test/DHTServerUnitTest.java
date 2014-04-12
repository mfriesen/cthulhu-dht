package ca.gobits.cthulhu.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.ByteArrayOutputStream;
import java.net.Socket;

import org.easymock.EasyMockRunner;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.util.ReflectionTestUtils;

import ca.gobits.cthulhu.DHTServer;

/**
 * DHTServer UnitTests.
 *
 */
@RunWith(EasyMockRunner.class)
public final class DHTServerUnitTest {

    /**
     * testMain01() unknown parameter.
     */
    @Test
    public void testMain01() {
        // given
        String[] args = new String[] {"-t"};
        ByteArrayOutputStream bo = new ByteArrayOutputStream();
        ReflectionTestUtils.setField(System.out, "out", bo);

        // when
        DHTServer.main(args);

        // then
        assertUsage(bo);
    }

    /**
     * testMain02() -?.
     */
    @Test
    public void testMain02() {
        // given
        String[] args = new String[] {"-?"};
        ByteArrayOutputStream bo = new ByteArrayOutputStream();
        ReflectionTestUtils.setField(System.out, "out", bo);

        // when
        DHTServer.main(args);

        assertUsage(bo);
    }

    /**
     * testMain03() -p with port missing.
     */
    @Test
    public void testMain03() {
        // given
        String[] args = new String[] {"-p"};
        ByteArrayOutputStream bo = new ByteArrayOutputStream();
        ReflectionTestUtils.setField(System.out, "out", bo);

        // when
        DHTServer.main(args);

        assertUsage(bo);
    }

    /**
     * testMain04() -p with port.
     * @throws Exception Exception
     */
    @Test(timeout = 10000)
    public void testMain04() throws Exception {
        // given
        int port = 8000;
        final String[] args = new String[] {"-p", "" + port};

        // when
        Thread result = runDHTServerInNewThread(args);
        result.start();

        // then
        Thread.sleep(1000);

        Socket client = new Socket("127.0.0.1", port);
        assertTrue(client.isConnected());
        client.close();

        DHTServer.shutdown();
    }

    /**
     * testMain05() default port.
     * @throws Exception Exception
     */
    @Test(timeout = 10000)
    public void testMain05() throws Exception {
        // given
        int port = 8080;
        final String[] args = new String[] {};

        // when
        Thread result = runDHTServerInNewThread(args);
        result.start();

        // then
        Thread.sleep(1000);

        Socket client = new Socket("127.0.0.1", port);
        assertTrue(client.isConnected());
        client.close();

        DHTServer.shutdown();
    }

    /**
     * Assert Usage Message is shown.
     * @param bo ByteArrayOutputStream
     */
    private void assertUsage(final ByteArrayOutputStream bo) {
        String expected = "usage: java -jar dht.jar\nParameters\n"
            + " -?         help\n"
            + " -p <arg>   bind to port\n";

        assertEquals(expected, bo.toString());
    }

    /**
     * Runs DHT Server in new thread.
     * @param args argument parameters.
     * @return Runnable
     */
    private Thread runDHTServerInNewThread(final String[] args) {
        return new Thread(new Runnable() {
            @Override
            public void run() {
                DHTServer.main(args);
            }
        });
    }

}

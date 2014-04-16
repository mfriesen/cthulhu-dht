package ca.gobits.cthulhu.integration.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.Socket;

import org.junit.BeforeClass;
import org.junit.Test;

import ca.gobits.cthulhu.DHTServer;

/**
 * DHTServer UnitTests.
 *
 */
public final class DHTServerIntegrationTest {

    /**
     *beforeClass().
     * @throws Exception  Exception
     */
    @BeforeClass
    public static void beforeClass() throws Exception {
        Thread thread = runDHTServerInNewThread(new String[]{});
        thread.start();
        Thread.sleep(1000);
    }

    /**
     * testPing01().
     * @throws Exception  Exception
     */
    @Test
    public void testPing01() throws Exception {

        // given
        String dat = "d1:ad2:id20:abcdefghij0123456789e1:q4:ping1:t2:aa1:y1:qe";

        // when
        Socket client = new Socket("127.0.0.1", DHTServer.DEFAULT_PORT);

        DataOutputStream out = new DataOutputStream(client.getOutputStream());
        BufferedReader in = new BufferedReader(
                new InputStreamReader(client.getInputStream()));

        out.writeBytes(dat + '\n');

        String res = in.readLine();

        // then
        assertTrue(res.startsWith("d1:rd2:id20:6h"));
        assertTrue(res.endsWith("e1:t2:aa1:y1:re"));
        client.close();
    }

    /**
     * testUnknownMethod01().
     * @throws Exception  Exception
     */
    @Test
    public void testUnknownMethod01() throws Exception {

        // given
        String dat = "d1:ad2:id20:abcdefghij0123456789e1:q4:pink1:t2:aa1:y1:qe";

        // when
        Socket client = new Socket("127.0.0.1", DHTServer.DEFAULT_PORT);

        DataOutputStream out = new DataOutputStream(client.getOutputStream());
        BufferedReader in = new BufferedReader(
                new InputStreamReader(client.getInputStream()));

        out.writeBytes(dat + '\n');
        String res = in.readLine();

        // then
        assertEquals("d1:rd3:20414:Method Unknowne1:t2:aa1:y1:ee", res);
        client.close();
    }

    /**
     * testServerError01().
     * @throws Exception  Exception
     */
    @Test
    public void testServerError01() throws Exception {

        // given
        String dat = "adsadadsa";

        // when
        Socket client = new Socket("127.0.0.1", DHTServer.DEFAULT_PORT);

        DataOutputStream out = new DataOutputStream(client.getOutputStream());
        BufferedReader in = new BufferedReader(
                new InputStreamReader(client.getInputStream()));

        out.writeBytes(dat + '\n');
        String res = in.readLine();

        // then
        assertEquals("d1:rd3:20212:Server Errore1:y1:ee", res);
        client.close();
    }

    /**
     * testMissingQParameter01().
     * @throws Exception  Exception
     */
    @Test
    public void testMissingQParameter01() throws Exception {

        // given
        String dat = "d1:ad2:id20:abcdefghij0123456789e1:t2:aa1:y1:qe";

        // when
        Socket client = new Socket("127.0.0.1", DHTServer.DEFAULT_PORT);

        DataOutputStream out = new DataOutputStream(client.getOutputStream());
        BufferedReader in = new BufferedReader(
                new InputStreamReader(client.getInputStream()));

        out.writeBytes(dat + '\n');
        String res = in.readLine();

        // then
        assertEquals("d1:rd3:20212:Server Errore1:t2:aa1:y1:ee", res);
        client.close();
    }

    /**
     * Runs DHT Server in new thread.
     * @param args argument parameters.
     * @return Runnable
     */
    private static Thread runDHTServerInNewThread(final String[] args) {
        return new Thread(new Runnable() {
            @Override
            public void run() {
                DHTServer.main(args);
            }
        });
    }
}

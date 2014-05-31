package ca.gobits.cthulhu.test;

import static org.easymock.EasyMock.capture;
import static org.easymock.EasyMock.expect;
import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;

import org.easymock.Capture;
import org.easymock.EasyMock;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.easymock.PowerMock;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import ca.gobits.cthulhu.DHTProtocolHandler;
import ca.gobits.cthulhu.DHTProtocolRunnable;

/**
 * DHTProtocolRunnable Unit Tests.
 *
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({ DatagramPacket.class, DHTProtocolHandler.class })
public final class DHTProtocolRunnableUnitTest {

    /** Mock DatagramSocket. */
    private final DatagramSocket serverSocket = EasyMock
            .createMock(DatagramSocket.class);

    /** Mock DHTProtocolHandler. */
    private final DHTProtocolHandler handler = PowerMock
            .createMock(DHTProtocolHandler.class);

    /** Mock Receieved DatagramPacket. */
    private final DatagramPacket packet = PowerMock
            .createMock(DatagramPacket.class);

    /** Capture DatagramPacket. */
    private final Capture<DatagramPacket> capture
        = new Capture<DatagramPacket>();

    /** DHTProtocol Runnable instance. */
    private final DHTProtocolRunnable runnable = new DHTProtocolRunnable(
            serverSocket, handler, packet);

    /**
     * testRun01().
     * @throws Exception  Exception
     */
    @Test
    public void testRun01() throws Exception {

        // given
        byte[] bb = "asda".getBytes();

        // when
        expect(handler.handle(packet)).andReturn(bb);
        serverSocket.send(capture(capture));
        EasyMock.replay(serverSocket);
        PowerMock.replayAll();

        this.runnable.run();

        // then
        EasyMock.verify(serverSocket);
        PowerMock.verifyAll();

        DatagramPacket result = capture.getValue();
        assertEquals(bb, result.getData());
    }

    /**
     * testRun02() - IOException is throwns.
     * @throws Exception  Exception
     */
    @Test(expected = RuntimeException.class)
    public void testRun02() throws Exception {

        // given

        // when
        expect(handler.handle(packet)).andThrow(new IOException());
        serverSocket.send(capture(capture));
        EasyMock.replay(serverSocket);
        PowerMock.replayAll();

        this.runnable.run();

        // then
    }

}

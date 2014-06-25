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

package ca.gobits.cthulhu.test;

import static org.easymock.EasyMock.capture;
import static org.easymock.EasyMock.expect;
import static org.junit.Assert.assertArrayEquals;

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
            this.serverSocket, this.handler, this.packet);

    /**
     * testRun01().
     * @throws Exception  Exception
     */
    @Test
    public void testRun01() throws Exception {

        // given
        byte[] bb = "asda".getBytes();

        // when
        expect(this.handler.handle(this.packet)).andReturn(bb);
        this.serverSocket.send(capture(this.capture));
        EasyMock.replay(this.serverSocket);
        PowerMock.replayAll();

        this.runnable.run();

        // then
        EasyMock.verify(this.serverSocket);
        PowerMock.verifyAll();

        DatagramPacket result = this.capture.getValue();
        assertArrayEquals(bb, result.getData());
    }

    /**
     * testRun02() - IOException is throwns.
     * @throws Exception  Exception
     */
    @Test(expected = RuntimeException.class)
    public void testRun02() throws Exception {

        // given

        // when
        expect(this.handler.handle(this.packet)).andThrow(new IOException());
        this.serverSocket.send(capture(this.capture));
        EasyMock.replay(this.serverSocket);
        PowerMock.replayAll();

        this.runnable.run();

        // then
    }

}

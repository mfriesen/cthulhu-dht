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
import java.io.PrintWriter;
import java.net.DatagramPacket;
import java.net.DatagramSocket;

import org.apache.commons.cli.HelpFormatter;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import ca.gobits.dht.DHTIdentifier;


/**
 * DHTServer implementation.
 */
public class DHTServer /*implements Lifecycle*/ {

    /** DHT Server Logger. */
    private static final Logger LOGGER = Logger.getLogger(DHTServer.class);

    /** Receive Data UDP Length. */
    private static final int RECEIVE_DATA_LENGTH = 1024;

    /** DHT Node Id. */
    public static final byte[] NODE_ID = DHTIdentifier.sha1(DHTServer.class
            .getName());

    /** DHT Protocol Handler. */
    @Autowired
    private DHTProtocolHandler dhtHandler;

    /** Thread Pool Executor. */
    @Autowired
    private ThreadPoolTaskExecutor threadPool;

    /** Indicator whether to stop the server. */
    private boolean stop;

    /** Datagram Socket. */
    private DatagramSocket serverSocket;

    /**
     * Constructor.
     */
    public DHTServer() {
    }

    /**
     * Run DHTServer.
     * @param port port to run server on
     * @throws Exception  Exception
     */
    public void run(final int port) throws Exception {

        LOGGER.info("starting cthulhu on " + port);

        try {
            serverSocket = new DatagramSocket(port);
            byte[] receiveData = new byte[RECEIVE_DATA_LENGTH];

            while (true) {

                if (stop) {
                    break;
                }

                DatagramPacket receivePacket = new DatagramPacket(
                        receiveData, receiveData.length);
                serverSocket.receive(receivePacket);

                threadPool.execute(new DHTProtocolRunnable(serverSocket,
                        dhtHandler, receivePacket));
            }

        } finally {
            shutdownGracefully();
        }
    }

//    @Scheduled(initialDelay=1000, fixedRate=50000000)
//    public void doSomething() {
//        System.out.println ("RUNNING>..");
//
//        try {
    // String dat = "d1:ad2:id20:abcdefghij0123456789e1:q4:ping1:t2:aa1:y1:qe";
//            byte[] bytes = dat.getBytes();
//            DatagramPacket sendPacket = new DatagramPacket(bytes,
//                    bytes.length, InetAddress.getByName("localhost"), 8080);
//            serverSocket.send(sendPacket);
//        } catch (IOException e) {
//            LOGGER.warn(e, e);
//        }
//    }

    /**
     * Gracefully shutdown server.
     */
    public final void shutdownGracefully() {
        stop = true;
        threadPool.shutdown();

        if (serverSocket != null) {
            serverSocket.close();
        }
    }

    /**
     * Mainline to run DHTServer.
     * @param args argument parameters.
     */
    public static void main(final String[] args) {
        ConfigurableApplicationContext ac =
            new AnnotationConfigApplicationContext(
                DHTConfiguration.class);

        try {
            main(args, ac);
        } finally {
            ac.close();
        }
    }

    /**
     * Mainline to run DHTServer.
     * @param args argument parameters.
     * @param ac ApplicationContext
     */
    public static void main(final String[] args,
            final ConfigurableApplicationContext ac) {

        DHTServerConfig config = new DHTServerConfig(args);

        if (config.isShowHelp()) {

            showUsage();

        } else {

            try {

                DHTServer server = ac.getBean(DHTServer.class);
                server.run(config.getPort());

            } catch (Exception e) {

                LOGGER.fatal(e, e);

            }
        }
    }

    /**
     * Shows Usage Message.
     */
    private static void showUsage() {

        PrintWriter writer = new PrintWriter(System.out);
        HelpFormatter usageFormatter = new HelpFormatter();
        usageFormatter.printHelp("java -jar dht.jar", "Parameters",
                DHTServerConfig.DHTSERVER_OPTIONS, "");
        writer.close();
    }
}

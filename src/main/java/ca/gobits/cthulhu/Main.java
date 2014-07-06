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

package ca.gobits.cthulhu;

import org.apache.commons.cli.HelpFormatter;
import org.apache.log4j.Logger;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

/**
 * Mainline to run DHTServer.
 *
 */
public final class Main {

    /** DHT Server Logger. */
    private static final Logger LOGGER = Logger.getLogger(Main.class);

    /**
     * private constructor.
     */
    private Main() {
    }

    /**
     * Mainline to run DHTServer.
     *
     * @param args  argument parameters.
     */
    public static void main(final String[] args) {
        AnnotationConfigApplicationContext ac
            = new AnnotationConfigApplicationContext();

        ac.register(DHTConfiguration.class);
        ac.register(DHTServerConfig.class);
        ac.refresh();

        try {
            main(args, ac);
        } finally {
            ac.close();
        }
    }

    /**
     * Mainline to run DHTServer.
     *
     * @param args
     *            argument parameters.
     * @param ac
     *            ApplicationContext
     */
    public static void main(final String[] args,
            final AnnotationConfigApplicationContext ac) {

        DHTServerConfig config = ac.getBean(DHTServerConfig.class);
        config.parse(args);

        if (config.isShowHelp()) {

            showUsage();

        } else {

            try {

                DHTServer server = ac.getBean(DHTServer.class);
                server.start();

            } catch (Exception e) {

                LOGGER.fatal(e, e);

            }
        }
    }

    /**
     * Shows Usage Message.
     */
    private static void showUsage() {
        HelpFormatter usageFormatter = new HelpFormatter();
        usageFormatter.printHelp("java -jar dht.jar", "Parameters",
                DHTServerConfig.DHTSERVER_OPTIONS, "");
    }
}

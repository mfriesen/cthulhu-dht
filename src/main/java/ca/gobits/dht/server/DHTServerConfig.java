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

package ca.gobits.dht.server;

import org.apache.commons.cli.BasicParser;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.MissingArgumentException;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.UnrecognizedOptionException;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import ca.gobits.dht.DHTIdentifier;

/**
 * DHTServer Configuration.
 *
 */
public class DHTServerConfig {

    /** DHT Server Logger. */
    private static final Logger LOGGER = Logger
            .getLogger(DHTServerConfig.class);

    /** DHTServer Command Line Options. */
    static final Options DHTSERVER_OPTIONS = new Options()
        .addOption("?", false, "help")
        .addOption("debug", false, "sets server to debug mode")
        .addOption("nodes", true,
                "comma-separated list of bootstrap nodes format \"host:port\"")
        .addOption("p", true, "bind to port")
        .addOption("salt", true, "DHT Node Identifier salt")
        .addOption("verbose", false, "be extra verbose");

    /** Default Port. */
    public static final int DEFAULT_PORT = 6881;

    /** DHT Server Port. */
    private int port = DEFAULT_PORT;

    /** Default NodeId. */
    private byte[] nodeId = DHTIdentifier.getRandomNodeId();

    /** Is Display Help. */
    private boolean showHelp;

    /** List of nodes to bootstrap server with. */
    private String[] bootstrapNodes;

    /** Level of logging. */
    private Level logLevel = Level.INFO;

    /**
     * default constructor needed for registering with Spring Application
     * Context.
     */
    public DHTServerConfig() {
    }

    /**
     * Creates DHT Server Config from Command Line arguments.
     * @param args  args
     */
    public void parse(final String[] args) {

        try {
            CommandLineParser parser = new BasicParser();
            CommandLine cmd = parser.parse(DHTSERVER_OPTIONS, args);

            parse(cmd);

        } catch (UnrecognizedOptionException e) {
            this.showHelp = true;
        } catch (MissingArgumentException e) {
            this.showHelp = true;
        } catch (Exception e) {
            LOGGER.fatal(e, e);
            this.showHelp = true;
        }
    }

    /**
     * Parses the CommandLine options that are set.
     * @param cmd  CommandLine
     */
    private void parse(final CommandLine cmd) {

        if (cmd.hasOption("?")) {
            this.showHelp = true;
        } else {

            if (cmd.hasOption("p")) {

                String portStr = cmd.getOptionValue("p");
                this.port = Integer.parseInt(portStr);
            }

            if (cmd.hasOption("salt")) {

                String salt = cmd.getOptionValue("salt");
                this.nodeId = DHTIdentifier.sha1(salt.getBytes());
            }

            if (cmd.hasOption("nodes")) {
                String value = cmd.getOptionValue("nodes");
                this.bootstrapNodes = value.split(",");
                trim(this.bootstrapNodes);
            }

            if (cmd.hasOption("debug")) {
                this.logLevel = Level.DEBUG;
            }

            if (cmd.hasOption("verbose")) {
                this.logLevel = Level.ALL;
            }

            if (!isValid()) {
                this.showHelp = true;
                this.bootstrapNodes = null;
            }
        }
    }

    /**
     * Validates parameters passed in are valid.
     * @return boolean
     */
    private boolean isValid() {

        boolean valid = true;

        if (this.bootstrapNodes != null) {

            for (String node : this.bootstrapNodes) {
                if (node.split(":").length != 2) {
                    valid = false;
                    break;
                }
            }
        }

        return valid;
    }

    /**
     * Trim all strings.
     * @param list list of strings
     */
    private void trim(final String[] list) {
        for (int i = 0; i < list.length; i++) {
            list[i] = list[i].trim();
        }
    }

    /**
     * @return int
     */
    public int getPort() {
        return this.port;
    }

    /**
     * @return boolean
     */
    public boolean isShowHelp() {
        return this.showHelp;
    }

    /**
     * @return byte[]
     */
    public byte[] getNodeId() {
        return this.nodeId;
    }

    /**
     * @return String[]
     */
    public String[] getBootstrapNodes() {
        return this.bootstrapNodes;
    }

    /**
     * @return LogLevel
     */
    public Level getLogLevel() {
        return this.logLevel;
    }
}

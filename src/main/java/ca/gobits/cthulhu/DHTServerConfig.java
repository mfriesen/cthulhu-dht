package ca.gobits.cthulhu;

import org.apache.commons.cli.BasicParser;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.MissingArgumentException;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.UnrecognizedOptionException;
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
        .addOption("p", true, "bind to port")
        .addOption("salt", true, "DHT Node Identifier salt")
        .addOption("?", false, "help");

    /** Default Port. */
    public static final int DEFAULT_PORT = 6881;

    /** DHT Server Port. */
    private int port = DEFAULT_PORT;

    /** Default NodeId. */
    private byte[] nodeId = DHTIdentifier.getDefaultNodeId();

    /** Is Display Help. */
    private boolean showHelp;

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
    public DHTServerConfig(final String[] args) {

        try {
            CommandLineParser parser = new BasicParser();
            CommandLine cmd = parser.parse(DHTSERVER_OPTIONS, args);

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
            }

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
}

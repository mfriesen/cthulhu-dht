package ca.gobits.cthulhu;

import org.apache.commons.cli.BasicParser;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.MissingArgumentException;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.UnrecognizedOptionException;
import org.apache.log4j.Logger;

/**
 * DHTServer Configuration.
 *
 */
public final class DHTServerConfig {

    /** DHT Server Logger. */
    private static final Logger LOGGER = Logger
            .getLogger(DHTServerConfig.class);

    /** DHTServer Command Line Options. */
    static final Options DHTSERVER_OPTIONS = new Options()
        .addOption("p", true, "bind to port")
        .addOption("?", false, "help");

    /** Default Port. */
    public static final int DEFAULT_PORT = 6881;

    /** DHT Server Port. */
    private int port = DEFAULT_PORT;

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
            } else if (cmd.hasOption("p")) {

                String portStr = cmd.getOptionValue("p");
                this.port = Integer.parseInt(portStr);

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
        return port;
    }

    /**
     * @return boolean
     */
    public boolean isShowHelp() {
        return showHelp;
    }
}

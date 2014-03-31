package ca.gobits.cthulhu;

import java.math.BigInteger;

/**
 * DHTNode - holder for information about a DHT Node.
 */
public class DHTNode {

    /** Node identifier. */
    private BigInteger id;
    /** Node IP address. */
    private String host;
    /** Node listening port. */
    private int port;

    /**
     * constructor.
     * @param nodeId Identifier
     * @param nodeHost listening host
     * @param nodePort listening port
     */
    public DHTNode(final BigInteger nodeId, final String nodeHost,
            final int nodePort) {
        this.id = nodeId;
        this.host = nodeHost;
        this.port = nodePort;
    }

    /**
     * @return BigInteger
     */
    public final BigInteger getId() {
        return id;
    }

    /**
     * @return String
     */
    public final String getHost() {
        return host;
    }

    /**
     * @return int
     */
    public final int getPort() {
        return port;
    }
}

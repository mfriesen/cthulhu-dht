package ca.gobits.cthulhu.domain;

import java.math.BigInteger;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

/**
 * Basic implmentation pf DHTInfoHash.
 *
 */
public final class DHTInfoHashBasic implements DHTInfoHash {

    /** InfoHash identifier. */
    private BigInteger infoHash;

    /** Collection of Peers that "announced" to the InfoHash. */
    private Set<DHTPeer> peers;

    /**
     * constructor.
     */
    public DHTInfoHashBasic() {
    }

    /**
     * constructor.
     * @param hashInfoId Info hash identifier
     */
    public DHTInfoHashBasic(final BigInteger hashInfoId) {
        this.infoHash = hashInfoId;
    }

    @Override
    public String toString() {
        ToStringBuilder builder = new ToStringBuilder(this);
        builder.append("infoHash", getInfoHash());
        builder.append("# of peers", peers != null ? peers.size() : 0);
        return builder.toString();
    }

    /**
     * Peers are "<compact node info>" format.
     * @return Set<DHTPeer>
     */
    @Override
    public Set<DHTPeer> getPeers() {
        return peers;
    }

    /**
     * Sets Peers.
     * @param set  Set<DHTPeer>
     */
    @Override
    public void setPeers(final Set<DHTPeer> set) {
        this.peers = set;
    }

    /**
     * Adds a peer.
     * @param addr  IP Address of peer
     * @param port   listening port of peer
     */
    @Override
    public void addPeer(final byte[] addr, final int port) {
        if (this.peers == null) {
            this.peers = new HashSet<DHTPeer>();
        }

        this.peers.add(new DHTPeer(addr, port));
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder()
            .append(infoHash)
            .toHashCode();
    }

    @Override
    public boolean equals(final Object obj) {
        if (obj == null) {
            return false;
        }

        if (obj == this) {
            return true;
        }

        if (!(obj instanceof DHTInfoHash)) {
            return false;
        }

        DHTInfoHash rhs = (DHTInfoHash) obj;
        return new EqualsBuilder()
            .append(infoHash, rhs.getInfoHash())
            .isEquals();
    }

    /**
     * @return BigInteger
     */
    @Override
    public BigInteger getInfoHash() {
        return infoHash;
    }

    /**
     * Set Info Hash.
     * @param infoHashId  Info Hash
     */
    @Override
    public void setInfoHash(final BigInteger infoHashId) {
        this.infoHash = infoHashId;
    }
}

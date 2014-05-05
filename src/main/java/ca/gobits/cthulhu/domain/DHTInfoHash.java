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

package ca.gobits.cthulhu.domain;

import java.math.BigInteger;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.neo4j.graphdb.Direction;
import org.springframework.data.neo4j.annotation.GraphId;
import org.springframework.data.neo4j.annotation.NodeEntity;
import org.springframework.data.neo4j.annotation.RelatedTo;

/**
 * DHTInfoHash - holder of information about a InfoHash record.
 *
 */
@NodeEntity
public final class DHTInfoHash {

    /** DHTInfoHash identifier. */
    @GraphId
    private Long id;

    /** InfoHash identifier. */
    private BigInteger infoHash;

    /** Info hash latitude. */
    private double latitude;

    /** Info hash longitude. */
    private double longitude;

    /** Source of DHTInfoHash. */
    private DHTInfoHashSource source;

    /** Collection of Peers that "announced" to the InfoHash. */
    @RelatedTo(type = "peer", direction = Direction.OUTGOING)
    private Set<DHTPeer> peers;

    /**
     * default constructor.
     */
    public DHTInfoHash() {
    }

    /**
     * constructor.
     * @param hashInfoId Info hash identifier
     */
    public DHTInfoHash(final BigInteger hashInfoId) {
        this();
        this.infoHash = hashInfoId;
    }

    /**
     * Peers are "<compact node info>" format.
     * @return Set<DHTPeer>
     */
    public Set<DHTPeer> getPeers() {
        return peers;
    }

    /**
     * Sets Peers.
     * @param set  Set<DHTPeer>
     */
    public void setPeers(final Set<DHTPeer> set) {
        this.peers = set;
    }

    /**
     * Adds a peer.
     * @param addr  IP Address of peer
     * @param port   listening port of peer
     */
    public void addPeer(final byte[] addr, final int port) {
        if (this.peers == null) {
            this.peers = new HashSet<DHTPeer>();
        }

        this.peers.add(new DHTPeer(addr, port));
    }

    @Override
    public String toString() {
        ToStringBuilder builder = new ToStringBuilder(this);
        builder.append("id", id);
        builder.append("infoHash", infoHash);
        builder.append("source", source);
        builder.append("latitude", latitude);
        builder.append("longitude", longitude);
        return builder.toString();
    }

    /**
     * @return Long
     */
    public Long getId() {
        return id;
    }

    /**
     * @param ident  identifier
     */
    public void setId(final Long ident) {
        this.id = ident;
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
            .append(id, rhs.id)
            .isEquals();
    }

    /**
     * @return BigInteger
     */
    public BigInteger getInfoHash() {
        return infoHash;
    }

    /**
     * Set Info Hash.
     * @param infoHashId  Info Hash
     */
    public void setInfoHash(final BigInteger infoHashId) {
        this.infoHash = infoHashId;
    }

    /**
     * @return double
     */
    public double getLatitude() {
        return latitude;
    }

    /**
     * Set Latitude.
     * @param latitudeCoordinate  latitude
     */
    public void setLatitude(final double latitudeCoordinate) {
        this.latitude = latitudeCoordinate;
    }

    /**
     * @return double
     */
    public double getLongitude() {
        return longitude;
    }

    /**
     * Sets Longitude.
     * @param longitudeCoordinate  longitude
     */
    public void setLongitude(final double longitudeCoordinate) {
        this.longitude = longitudeCoordinate;
    }

    /**
     * @return DHTInfoHashSource
     */
    public DHTInfoHashSource getSource() {
        return source;
    }

    /**
     * Sets the Source.
     * @param s source of DHTInfoHash
     */
    public void setSource(final DHTInfoHashSource s) {
        this.source = s;
    }
}

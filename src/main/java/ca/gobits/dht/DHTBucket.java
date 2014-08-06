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

package ca.gobits.dht;

import java.util.Comparator;
import java.util.Date;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import com.google.common.primitives.UnsignedBytes;

/**
 * DHTBucket represents a collection of DHTNode with IDs in close promity.
 *
 */
public class DHTBucket {

    /** Maximum number of nodes allow. */
    private static final int MAX_NODE_COUNT = 8;

    /** Start of range. */
    private byte[] min;

    /** End of range. */
    private byte[] max;

    /** Number of nodes in bucket. */
    private int nodeCount = 0;

    /** Property to indicate how "fresh" the contents are. */
    private Date lastChanged;

    /**
     * Comparator used to determine whether byte[] are between
     * rangeStart/rangeEnd.
     */
    private static final Comparator<byte[]> COMPARATOR = UnsignedBytes
            .lexicographicalComparator();

    /**
     * DHTBucket constructor.
     * @param startRange  Start of ID range
     * @param endRange  End of ID range
     */
    public DHTBucket(final byte[] startRange, final byte[] endRange) {
        this.min = startRange;
        this.max = endRange;
    }

    /**
     * Whether Bucket is full.
     * @return boolean
     */
    public boolean isFull() {
        return this.nodeCount == MAX_NODE_COUNT;
    }

    /**
     * Whether passed in bytes are within range of bucket.
     * @param bytes  bytes to check
     * @return boolean
     */
    public boolean isInRange(final byte[] bytes) {
        return COMPARATOR.compare(this.min, bytes) <= 0
                && COMPARATOR.compare(bytes, this.max) <= 0;
    }

    /**
     * Increment Node count.
     */
    public void incrementCount() {
        this.nodeCount++;
    }

    /**
     * Decrement Node count.
     */
    public void decrementCount() {
        this.nodeCount--;
    }

    /**
     * @return byte[]
     */
    public byte[] getMin() {
        return this.min;
    }

    /**
     * Set min value.
     * @param bytes  bytes
     */
    public void setMin(final byte[] bytes) {
        this.min = bytes;
    }

    /**
     * @return byte[]
     */
    public byte[] getMax() {
        return this.max;
    }

    /**
     * Sets max value.
     * @param bytes  bytes
     */
    public void setMax(final byte[] bytes) {
        this.max = bytes;
    }

    /**
     * @return int
     */
    public int getNodeCount() {
        return this.nodeCount;
    }

    /**
     * Set Node Count.
     * @param count  number of nodes
     */
    public void setNodeCount(final int count) {
        this.nodeCount = count > 0 ? count : 0;
    }

    @Override
    public String toString() {
        ToStringBuilder builder = new ToStringBuilder(this);
        builder.append("min", this.min);
        builder.append("max", this.max);
        builder.append("nodeCount", this.nodeCount);
        return builder.toString();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder()
            .append(this.min)
            .append(this.max)
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

        if (!(obj instanceof DHTBucket)) {
            return false;
        }

        DHTBucket rhs = (DHTBucket) obj;
        return new EqualsBuilder()
            .append(this.min, rhs.getMin())
            .append(this.max, rhs.getMax())
            .isEquals();
    }

    /**
     * @return Date
     */
    public Date getLastChanged() {
        return this.lastChanged;
    }

    /**
     * Sets the Last Changed Date.
     * @param date last changed date
     */
    public void setLastChanged(final Date date) {
        this.lastChanged = date;
    }
}

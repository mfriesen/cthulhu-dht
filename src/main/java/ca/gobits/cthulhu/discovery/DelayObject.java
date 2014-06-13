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

package ca.gobits.cthulhu.discovery;

import java.util.concurrent.Delayed;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

/**
 * Generic representation of a DelayObject.
 *
 * @param <T>  type of DelayObject
 */
public class DelayObject<T> implements Delayed {

    /** DelayObject Payload. */
    private final T payload;

    /** Time to start. */
    private final Long start;

    /**
     * Constructor.
     * @param payloadObject  payload
     * @param delay  time to wait
     */
    public DelayObject(final T payloadObject, final long delay) {
        this.payload = payloadObject;
        this.start = Long.valueOf(System.currentTimeMillis() + delay);
    }

    @Override
    @SuppressWarnings("rawtypes")
    public int compareTo(final Delayed o) {
        return this.start.compareTo(((DelayObject) o).start);
    }

    @Override
    public long getDelay(final TimeUnit unit) {
        long diff = this.start.longValue() - System.currentTimeMillis();
        return unit.convert(diff, TimeUnit.MILLISECONDS);
    }

    @Override
    public String toString() {
        ToStringBuilder builder = new ToStringBuilder(this);
        builder.append("payload", this.payload);
        builder.append("start", this.start);
        return builder.toString();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder()
            .append(this.payload)
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

        if (!(obj instanceof DelayObject)) {
            return false;
        }

        @SuppressWarnings("unchecked")
        DelayObject<T> rhs = (DelayObject<T>) obj;
        return new EqualsBuilder().append(this.payload, rhs.getPayload())
                .isEquals();
    }

    /**
     * @return T
     */
    public T getPayload() {
        return this.payload;
    }
}

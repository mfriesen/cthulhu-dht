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

package ca.gobits.cthulhu.discovery.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.concurrent.TimeUnit;

import org.easymock.EasyMock;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.easymock.PowerMock;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import ca.gobits.cthulhu.discovery.DelayObject;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

/**
 * DelayObject Unit Tests.
 *
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest(DelayObject.class)
public final class DelayObjectTest {

    /**
     * testCompare01().
     */
    @Test
    public void testCompare01() {
        // given
        DelayObject<String> d0 = new DelayObject<String>("test", 5000);
        DelayObject<String> d1 = new DelayObject<String>("test", 10000);

        // when
        int result0 = d0.compareTo(d1); // test less
        int result1 = d1.compareTo(d0); // test more
        int result2 = d1.compareTo(d1); // test same

        // then
        assertEquals(-1, result0);
        assertEquals(1, result1);
        assertEquals(0, result2);
    }

    /**
     * testGetDelay01().
     */
    @SuppressWarnings("boxing")
    @Test
    public void testGetDelay01() {
        // given
        PowerMock.mockStatic(System.class);
        long start = 1402512407195L;
        long current = 1402512408195L;

        // when
        EasyMock.expect(System.currentTimeMillis()).andReturn(start);
        EasyMock.expect(System.currentTimeMillis()).andReturn(current);

        PowerMock.replayAll();

        DelayObject<String> d0 = new DelayObject<String>("test", 5000);
        long result = d0.getDelay(TimeUnit.MILLISECONDS);

        // then
        PowerMock.verifyAll();

        assertEquals(4000, result);
    }

    /**
     * testToString01().
     */
    @Test
    public void testToString01() {
        // given
        DelayObject<String> obj = new DelayObject<String>("test", 5000);

        // when
        String result = obj.toString();

        // then
        assertTrue(result.startsWith(
                "ca.gobits.cthulhu.discovery.DelayObject"));
        assertTrue(result.contains("[payload=test,start="));
    }

    /**
     * testEquals01() null object.
     */
    @Test
    public void testEquals01() {
        // given
        DelayObject<String> obj = new DelayObject<String>("test", 5000);

        // when
        boolean result = obj.equals(null);

        // then
        assertFalse(result);
    }

    /**
     * testEquals02() same object.
     */
    @Test
    public void testEquals02() {
        // given
        DelayObject<String> obj = new DelayObject<String>("test", 5000);

        // when
        boolean result = obj.equals(obj);

        // then
        assertTrue(result);
    }

    /**
     * testEquals03() non DHTNode object.
     */
    @Test
    @SuppressFBWarnings(value = "EC_UNRELATED_TYPES")
    public void testEquals03() {
        // given
        DelayObject<String> obj = new DelayObject<String>("test", 5000);

        // when
        boolean result = obj.equals("");

        // then
        assertFalse(result);
    }

    /**
     * testEquals04() equal DHTNode object.
     */
    @Test
    public void testEquals04() {
        // given
        DelayObject<String> obj = new DelayObject<String>("test", 5000);
        DelayObject<String> obj1 = new DelayObject<String>("test", 5000);

        // when
        boolean result = obj.equals(obj1);

        // then
        assertTrue(result);
    }

}

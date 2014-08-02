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

package ca.gobits.test.dht.util;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;
import java.util.Calendar;
import java.util.Date;

import org.junit.Test;

import ca.gobits.dht.util.DateHelper;

/**
 * Date Helper Unit Tests.
 *
 */
public class DateHelperUnitTest {

    /**
     * testConstructorIsPrivate().
     * @throws Exception  Exception
     */
    @Test
    public void testConstructorIsPrivate() throws Exception {
        Constructor<DateHelper> constructor = DateHelper.class
                .getDeclaredConstructor();
        assertTrue(Modifier.isPrivate(constructor.getModifiers()));
        constructor.setAccessible(true);
        constructor.newInstance();
    }

    /**
     * Test NOT past date in minutes.
     */
    @Test
    public void testIsPastDateInMinutes01() {
        // given
        Date now = new Date();
        Date date = new Date();
        int minutes = 15;

        // when
        boolean result = DateHelper.isPastDateInMinutes(now, date, minutes);

        // then
        assertFalse(result);
    }


    /**
     * Test past date in minutes.
     */
    @Test
    public void testIsPastDateInMinutes02() {
        // given
        Calendar c = Calendar.getInstance();
        c.add(Calendar.MINUTE, 16);
        Date now = c.getTime();
        Date date = new Date();
        int minutes = 15;

        // when
        boolean result = DateHelper.isPastDateInMinutes(now, date, minutes);

        // then
        assertTrue(result);
    }
}

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

package ca.gobits.dht.util;

import java.util.Calendar;
import java.util.Date;

/**
 * Date Helper Methods.
 *
 */
public final class DateHelper {

    /**
     * private constructor.
     */
    private DateHelper() {
    }

    /**
     * Verify that now is past date + minutes.
     * @param now  current date
     * @param date  date
     * @param minutes  number of minutes
     * @return boolean
     */
    public static boolean isPastDateInMinutes(final Date now, final Date date,
            final int minutes) {
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        c.add(Calendar.MINUTE, minutes);

        return now.after(c.getTime());
    }
}

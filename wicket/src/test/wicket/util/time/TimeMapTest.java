/*
 * $Id$
 * $Revision$
 * $Date$
 *
 * ====================================================================
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package wicket.util.time;


import junit.framework.Assert;
import junit.framework.TestCase;

import java.text.ParseException;

import wicket.util.time.Time;
import wicket.util.time.TimeFrame;
import wicket.util.time.TimeMap;
import wicket.util.time.TimeOfDay;

/**
 * Test cases for this object
 * @author Jonathan Locke
 */
public final class TimeMapTest extends TestCase
{
    /**
     * 
     * @throws ParseException
     */
    public void testSimpleStaticTimeFrame() throws ParseException
    {
        final TimeMap map = new TimeMap();
        final Time start = Time.valueOf(TimeOfDay.valueOf("3.14pm"));
        final Time end = Time.valueOf(TimeOfDay.valueOf("3.20pm"));
        final String value = "test";

        map.put(TimeFrame.valueOf(start, end), value);
        Assert.assertEquals(value, map.get(Time.valueOf(TimeOfDay.valueOf("3.15pm"))));
        Assert.assertNull(map.get(Time.valueOf(TimeOfDay.valueOf("3.21pm"))));
        Assert.assertNull(map.get(Time.valueOf(TimeOfDay.valueOf("3.13pm"))));
    }
}


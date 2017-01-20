package com.droid.mooresoft.diabeto.util;

import junit.framework.TestCase;

/**
 * Created by Edward Moore on 4/9/16.
 * Copyright (c) 2016 MooreSoft. All rights reserved.
 */
public class UtilsTest extends TestCase {

   private static final long EPOCH_TIME = 1460234220000l; // millis
   private static final String
         DATE_STRING = "Sat, Apr 9, 2016",
         TIME_STRING = "3:37 PM"; // and 0 seconds

   /**
    * Tests if the conversion from epoch millis to formatted time string is working correctly.
    *
    * @throws Exception Hopefully not...
    */
   public void testGetDateString() throws Exception {
      String dateString = DatetimeUtils.getDateString(EPOCH_TIME);
      assertEquals(DATE_STRING, dateString);
   }

   /**
    * Tests if the conversion from epoch millis to formatted time string is working correctly.
    *
    * @throws Exception Hopefully not...
    */
   public void testGetTimeString() throws Exception {
      String timeString = DatetimeUtils.getTimeString(EPOCH_TIME);
      assertEquals(TIME_STRING, timeString);
   }

   /**
    * Parses epoch time from DATE_STRING and TIME_STRING and ensures it matches EPOCH_TIME.
    * <p/>
    * Note that if EPOCH_TIME doesn't correspond to a whole number of minutes
    * (i.e. 3:37:01 PM instead of 3:37:00 PM) then this test will fail.
    *
    * @throws Exception Hopefully not...
    */
   public void testParseEpochTime() throws Exception {
      long epochTime = DatetimeUtils.parseEpochTime(DATE_STRING, TIME_STRING);
      assertEquals(EPOCH_TIME, epochTime);
   }
}
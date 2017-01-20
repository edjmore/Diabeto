package com.droid.mooresoft.diabeto.data;

import java.util.Random;

/**
 * Created by Edward Moore on 4/8/16.
 * Copyright (c) 2016 MooreSoft. All rights reserved.
 */
class LogEntryRandomizer {

   private Random mRandom;

   LogEntryRandomizer() {
      mRandom = new Random();
   }

   LogEntry getRandomLogEntry() {
      return randomizeLogEntry(new LogEntry());
   }

   LogEntry randomizeLogEntry(LogEntry logEntry) {
      logEntry.setTime(mRandom.nextLong());
      logEntry.setBloodGlucose(mRandom.nextInt());
      logEntry.setBolus(mRandom.nextDouble());
      logEntry.setCarbohydrate(mRandom.nextInt());
      return logEntry;
   }
}

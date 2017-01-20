package com.droid.mooresoft.diabeto.data;

import android.provider.BaseColumns;

/**
 * Created by Edward Moore on 4/7/16.
 * Copyright (c) 2016 MooreSoft. All rights reserved.
 */
public class DbSchema {

   public static final String NAME = "diabeto.db";
   public static final int VERSION = 1;

   public static abstract class LogEntryTbl implements BaseColumns {
      public static final String NAME = "LogEntry";

      public static final String
            _TIME = "_time",
            _BLOOD_GLUCOSE = "_blood_glucose",
            _BOLUS = "_bolus",
            _CARBOHYDRATE = "_carbohydrate";
   }
}

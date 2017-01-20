package com.droid.mooresoft.diabeto.data;

import android.test.AndroidTestCase;

import com.droid.mooresoft.diabeto.data.DbSchema.LogEntryTbl;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Edward Moore on 4/8/16.
 * Copyright (c) 2016 MooreSoft. All rights reserved.
 */
public class DbManagerTest extends AndroidTestCase {

   private static final int NUM_TESTS = 10;

   private LogEntry mLogEntry;
   private LogEntryRandomizer mLogEntryRandomizer;

   @Override
   protected void setUp() throws Exception {
      mLogEntryRandomizer = new LogEntryRandomizer();
   }

   public void testNTimes() throws Exception {
      for (int i = 0; i < NUM_TESTS; i++) {
         mLogEntry = mLogEntryRandomizer.getRandomLogEntry();
         doInsertFetchUpdateDeleteLogEntry();
      }
   }

   private void doInsertFetchUpdateDeleteLogEntry() throws Exception {
      DbManager dbManager = DbManager.getInstance();
      assertTrue(dbManager.insertLogEntry(mLogEntry, getContext()));

      doFetchLogEntry(mLogEntry.getId(), mLogEntry);

      mLogEntry = mLogEntryRandomizer.randomizeLogEntry(mLogEntry);
      assertTrue(dbManager.updateLogEntry(mLogEntry, getContext()));
      doFetchLogEntry(mLogEntry.getId(), mLogEntry);

      List<LogEntry> logEntryList = new ArrayList<>(1);
      logEntryList.add(mLogEntry);
      assertEquals(1, dbManager.deleteLogEntries(logEntryList, getContext()));
      doFetchLogEntry(mLogEntry.getId(), null);
   }

   private void doFetchLogEntry(long id, LogEntry expected) {
      DbManager dbManager = DbManager.getInstance();
      String selection = String.format("%s = ?", LogEntryTbl._ID);
      String[] selectionArgs = {String.valueOf(id)};
      List<LogEntry> logEntryList = dbManager.fetchLogEntries(selection, selectionArgs, getContext());

      if (null == expected) {
         assertTrue(logEntryList.isEmpty());
         return;
      }

      assertEquals(1, logEntryList.size());
      LogEntry fetched = logEntryList.get(0);
      assertEquals(expected, fetched);
   }
}
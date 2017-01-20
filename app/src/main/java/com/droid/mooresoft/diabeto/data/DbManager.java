package com.droid.mooresoft.diabeto.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.NonNull;

import com.droid.mooresoft.diabeto.data.DbSchema.LogEntryTbl;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Edward Moore on 4/7/16.
 * Copyright (c) 2016 MooreSoft. All rights reserved.
 */
public class DbManager {

   private static final DbManager sInstance = new DbManager();

   private DbManager() {
   }

   public static DbManager getInstance() {
      return sInstance;
   }

   public boolean insertLogEntry(@NonNull LogEntry logEntry, Context context) {
      DbHelper dbHelper = null;
      SQLiteDatabase db = null;
      try {
         dbHelper = new DbHelper(context);
         db = dbHelper.getWritableDatabase();

         ContentValues cv = logEntry.toContentValues(new ContentValues());
         long id = db.insert(LogEntryTbl.NAME, null, cv);
         logEntry.setId(id);
         return id != -1;
      } finally {
         if (db != null) db.close();
         if (dbHelper != null) dbHelper.close();
      }
   }

   public List<LogEntry> fetchLogEntries(String selection, String[] selectionArgs, Context context) {
      DbHelper dbHelper = null;
      SQLiteDatabase db = null;
      Cursor c = null;
      try {
         dbHelper = new DbHelper(context);
         db = dbHelper.getReadableDatabase();

         String orderBy = String.format("%s DESC", LogEntryTbl._TIME); // Default to chronological order.
         c = db.query(LogEntryTbl.NAME, null, selection, selectionArgs, null, null, orderBy);
         int count = c.getCount();
         List<LogEntry> logEntryList = new ArrayList<>(count);

         if (c.moveToFirst()) {
            do {
               LogEntry logEntry = new LogEntry(c);
               logEntryList.add(logEntry);
            } while (c.moveToNext());
         }
         return logEntryList;
      } finally {
         if (c != null) c.close();
         if (db != null) db.close();
         if (dbHelper != null) dbHelper.close();
      }
   }

   public boolean updateLogEntry(@NonNull LogEntry logEntry, Context context) {
      if (logEntry.getId() <= 0) throw new IllegalStateException("No such LogEntry in database");

      DbHelper dbHelper = null;
      SQLiteDatabase db = null;
      try {
         dbHelper = new DbHelper(context);
         db = dbHelper.getWritableDatabase();

         ContentValues cv = logEntry.toContentValues(new ContentValues());
         String whereClause = String.format("%s = ?", LogEntryTbl._ID);
         String[] whereArgs = {String.valueOf(logEntry.getId())};
         return db.update(LogEntryTbl.NAME, cv, whereClause, whereArgs) == 1;
      } finally {
         if (db != null) db.close();
         if (dbHelper != null) dbHelper.close();
      }
   }

   public boolean deleteLogEntry(LogEntry logEntry, Context context) {
      List<LogEntry> logEntryList = new ArrayList<>(1);
      logEntryList.add(logEntry);
      return deleteLogEntries(logEntryList, context) == 1;
   }

   public int deleteLogEntries(List<LogEntry> logEntryList, Context context) {
      if (logEntryList.isEmpty()) return 0;

      DbHelper dbHelper = null;
      SQLiteDatabase db = null;
      try {
         dbHelper = new DbHelper(context);
         db = dbHelper.getWritableDatabase();

         String whereClause = genWhereClause(logEntryList);
         String[] whereArgs = genWhereArgs(logEntryList);
         return db.delete(LogEntryTbl.NAME, whereClause, whereArgs);
      } finally {
         if (db != null) db.close();
         if (dbHelper != null) dbHelper.close();
      }
   }

   private static String genWhereClause(List<LogEntry> logEntryList) {
      String whereClause = "";
      int count = logEntryList.size();
      for (int i = 0; i < count; i++) {
         whereClause += String.format("%s = ?", LogEntryTbl._ID);
         if (count - 1 == i) break;
         whereClause += " OR ";
      }
      return whereClause;
   }

   private static String[] genWhereArgs(List<LogEntry> logEntryList) {
      int count = logEntryList.size();
      String[] whereArgs = new String[count];
      for (int i = 0; i < count; i++) {
         LogEntry logEntry = logEntryList.get(i);
         long time = logEntry.getId();
         whereArgs[i] = String.valueOf(time);
      }
      return whereArgs;
   }
}

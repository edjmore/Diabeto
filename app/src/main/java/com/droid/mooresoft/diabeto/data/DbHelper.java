package com.droid.mooresoft.diabeto.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.droid.mooresoft.diabeto.data.DbSchema.LogEntryTbl;

/**
 * Created by Edward Moore on 4/7/16.
 * Copyright (c) 2016 MooreSoft. All rights reserved.
 */
class DbHelper extends SQLiteOpenHelper {
   private static final String TAG = DbHelper.class.getSimpleName();

   private static final String CREATE_LOG_ENTRY_TBL =
         String.format("CREATE TABLE %s (" +
                     "%s INTEGER PRIMARY KEY AUTOINCREMENT, " +
                     "%s INTEGER, " +
                     "%s INTEGER, " +
                     "%s REAL, " +
                     "%s INTEGER" +
                     ");",
               LogEntryTbl.NAME,
               LogEntryTbl._ID,
               LogEntryTbl._TIME,
               LogEntryTbl._BLOOD_GLUCOSE,
               LogEntryTbl._BOLUS,
               LogEntryTbl._CARBOHYDRATE);
   private static final String DELETE_LOG_ENTRY_TBL = "DROP TABLE IF EXISTS " + LogEntryTbl.NAME;

   DbHelper(Context context) {
      super(context, DbSchema.NAME, null, DbSchema.VERSION);
   }

   @Override
   public void onCreate(SQLiteDatabase db) {
      Log.d(TAG, String.format("Creating database %s v%d", DbSchema.NAME, DbSchema.VERSION));
      db.execSQL(CREATE_LOG_ENTRY_TBL);
   }

   @Override
   public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
      Log.d(TAG, String.format("Upgrading database %s from v%d to v%d", DbSchema.NAME, oldVersion, newVersion));
      db.execSQL(DELETE_LOG_ENTRY_TBL);
      onCreate(db);
   }
}

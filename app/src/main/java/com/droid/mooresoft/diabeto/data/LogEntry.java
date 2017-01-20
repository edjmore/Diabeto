package com.droid.mooresoft.diabeto.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

import com.droid.mooresoft.diabeto.data.DbSchema.LogEntryTbl;
import com.droid.mooresoft.diabeto.util.DiabetesUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Edward Moore on 4/7/16.
 * Copyright (c) 2016 MooreSoft. All rights reserved.
 * <p/>
 * Represents a collection of data points related to diabetes care.
 */
public class LogEntry implements Parcelable {

   public static final String EXTRA_LOG_ENTRY = "log_entry";

   private long mId;
   private long mTime;
   private int mBloodGlucose;
   private double mBolus;
   private int mCarbohydrate;

   /**
    * Creates an empty LogEntry object.
    */
   public LogEntry() {
   }

   /**
    * Creates a new LogEntry object.
    *
    * @param time         An timestamp in millis since epoch.
    * @param bloodGlucose A blood glucose level (or -1 if there is none).
    * @param bolus        An insulin bolus (may be zero).
    * @param carbohydrate A carbohydrate intake (in grams).
    */
   public LogEntry(long time, int bloodGlucose, double bolus, int carbohydrate) {
      mId = 0;
      mTime = time;
      mBloodGlucose = bloodGlucose;
      mBolus = bolus;
      mCarbohydrate = carbohydrate;
   }

   /**
    * Copies values from source to destination. This should be used to update a destination LogEntry
    * which has a valid database ID (access to getting/setting IDs is restricted).
    *
    * @param src  LogEntry to take values from.
    * @param dest LogEntry to copy values to.
    */
   public static void copy(LogEntry src, LogEntry dest) {
      dest.setTime(src.getTime());
      dest.setBloodGlucose(src.getBloodGlucose());
      dest.setBolus(src.getBolus());
      dest.setCarbohydrate(src.getCarbohydrate());
   }

   /**
    * A utility method for checking if the blood glucose field within the LogEntry is non-empty.
    *
    * @return True iff this LogEntry holds a blood glucose reading.
    */
   public boolean hasBloodGlucose() {
      return mBloodGlucose >= 0;
   }

   /**
    * A utility method for checking if the bolus field within the LogEntry is non-empty.
    *
    * @return True iff this LogEntry holds a bolus reading.
    */
   public boolean hasBolus() {
      return mBolus > 0;
   }

   /**
    * A utility method for checking if the carbohydrate field within the LogEntry is non-empty.
    *
    * @return True iff this LogEntry holds a carbohydrate reading.
    */
   public boolean hasCarbohydrate() {
      return mCarbohydrate > 0;
   }

   public long getTime() {
      return mTime;
   }

   public void setTime(long time) {
      mTime = time;
   }

   public int getBloodGlucose() {
      return mBloodGlucose;
   }

   public void setBloodGlucose(int bloodGlucose) {
      mBloodGlucose = bloodGlucose;
   }

   public double getBolus() {
      return mBolus;
   }

   public void setBolus(double bolus) {
      mBolus = bolus;
   }

   public int getCarbohydrate() {
      return mCarbohydrate;
   }

   public void setCarbohydrate(int carbohydrate) {
      mCarbohydrate = carbohydrate;
   }

   /**
    * Calculates the estimated active insulin at the time of this LogEntry (not including any
    * bolus recorded in this LogEntry). Note that this method will require a database query.
    *
    * @param context The current Context.
    * @return Active insulin level (in units).
    */
   public double getActiveInsulin(Context context) {
      float activeInsulinPeriod = TreatmentSettings.getActiveInsulinPeriod(context);
      return DiabetesUtils.calculateActiveInsulin(mTime, activeInsulinPeriod, context);
   }

   /**
    * Compares this LogEntry to another object. This method will compare all LogEntry data fields
    * for the two objects EXCEPT the database ID (see haveSameId(2) for ID comparison). In other
    * words if this method returns true the two LogEntry objects will appear identical to the end-user.
    *
    * @param o Another object.
    * @return True iff the object is a LogEntry w/ the same values.
    */
   @Override
   public boolean equals(Object o) {
      if (o instanceof LogEntry) {
         LogEntry other = (LogEntry) o;
         return mTime == other.getTime() &&
               mBloodGlucose == other.getBloodGlucose() &&
               mBolus == other.getBolus() &&
               mCarbohydrate == other.getCarbohydrate();
      }
      return false;
   }

   /**
    * For checking if two LogEntry objects represent the same database row.
    *
    * @param l1 A LogEntry.
    * @param l2 Another LogEntry.
    * @return True iff the two have the same row ID.
    */
   public static boolean haveSameId(LogEntry l1, LogEntry l2) {
      return l1.getId() == l2.getId();
   }

   // PARCELABLE IMPLEMENTATION //

   private LogEntry(Parcel src) {
      mId = src.readLong();
      mTime = src.readLong();
      mBloodGlucose = src.readInt();
      mBolus = src.readDouble();
      mCarbohydrate = src.readInt();
   }

   /**
    * {@inheritDoc}
    *
    * @see <a href="https://groups.google.com/forum/#!topic/android-developers/QVSWGS-8W0A">groups.google.com/forum</a>
    */
   @Override
   public int describeContents() {
      return 0;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void writeToParcel(Parcel dest, int flags) {
      dest.writeLong(mId);
      dest.writeLong(mTime);
      dest.writeInt(mBloodGlucose);
      dest.writeDouble(mBolus);
      dest.writeInt(mCarbohydrate);
   }

   // Parcelable implementations must have this 'CREATOR' field OR ELSE...
   public static final Parcelable.Creator<LogEntry> CREATOR =
         new Parcelable.Creator<LogEntry>() {
            @Override
            public LogEntry createFromParcel(Parcel src) {
               return new LogEntry(src);
            }

            @Override
            public LogEntry[] newArray(int size) {
               return new LogEntry[size];
            }
         };

   // FOR INTERFACING WITH THE DATABASE //

   LogEntry(Cursor c) {
      int
            idIdx = c.getColumnIndex(LogEntryTbl._ID),
            timeIdx = c.getColumnIndex(LogEntryTbl._TIME),
            bloodGlucoseIdx = c.getColumnIndex(LogEntryTbl._BLOOD_GLUCOSE),
            bolusIdx = c.getColumnIndex(LogEntryTbl._BOLUS),
            carbohydrateIdx = c.getColumnIndex(LogEntryTbl._CARBOHYDRATE);
      mId = c.getLong(idIdx);
      mTime = c.getLong(timeIdx);
      mBloodGlucose = c.getInt(bloodGlucoseIdx);
      mBolus = c.getDouble(bolusIdx);
      mCarbohydrate = c.getInt(carbohydrateIdx);
   }

   long getId() {
      return mId;
   }

   void setId(long id) {
      mId = id;
   }

   ContentValues toContentValues(@NonNull ContentValues cv) {
      cv.put(LogEntryTbl._TIME, mTime);
      cv.put(LogEntryTbl._BLOOD_GLUCOSE, mBloodGlucose);
      cv.put(LogEntryTbl._BOLUS, mBolus);
      cv.put(LogEntryTbl._CARBOHYDRATE, mCarbohydrate);
      return cv;
   }

   // FOR IMPORTING/EXPORTING CSV //

   static LogEntry fromCsvRecord(List<String> csvRecord) {
      if (csvRecord.size() != 4) {
         throw new IllegalArgumentException("Expected CSV record with 4 values");
      }

      long time = Long.parseLong(csvRecord.get(0));
      int bloodGlucose = Integer.parseInt(csvRecord.get(1));
      int carbohydrate = Integer.parseInt(csvRecord.get(2));
      double bolus = Double.parseDouble(csvRecord.get(3));

      return new LogEntry(time, bloodGlucose, bolus, carbohydrate);
   }

   List<String> toCsvRecord() {
      List<String> record = new ArrayList<>(4);
      record.add("" + getTime());
      record.add("" + getBloodGlucose());
      record.add("" + getCarbohydrate());
      record.add("" + getBolus());
      return record;
   }
}

package com.droid.mooresoft.diabeto.util;

import android.content.Context;
import android.util.Log;

import com.droid.mooresoft.diabeto.data.DbManager;
import com.droid.mooresoft.diabeto.data.DbSchema;
import com.droid.mooresoft.diabeto.data.LogEntry;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Edward Moore on 4/14/16.
 * Copyright (c) 2016 MooreSoft. All rights reserved.
 */
public class DiabetesUtils {
   private static final String TAG = DiabetesUtils.class.getSimpleName();

   /**
    * Sums the carbohydrates recorded over the time period.
    *
    * @param logEntryList A chronologically ordered List of LogEntry objects.
    * @param oldest       Start of the relevant period (in millis since epoch).
    * @param newest       End of the relevant period.
    * @return Total carbs (in grams).
    */
   public static double sumCarbsOverPeriod(List<LogEntry> logEntryList, long oldest, long newest) {
      double sum = 0;
      for (LogEntry logEntry : logEntryList) {
         // Only want LogEntry objects between oldest and newest times.
         if (logEntry.getTime() >= newest) continue;
         if (logEntry.getTime() <= oldest) break;

         if (logEntry.hasCarbohydrate()) sum += logEntry.getCarbohydrate();
      }
      return sum;
   }

   /**
    * Sums the boluses recorded over the time period.
    *
    * @param logEntryList A chronologically ordered List of LogEntry objects.
    * @param oldest       Start of the relevant period (in millis since epoch).
    * @param newest       End of the relevant period.
    * @return Total bolused (in units).
    */
   public static double sumBolusesOverPeriod(List<LogEntry> logEntryList, long oldest, long newest) {
      double sum = 0;
      for (LogEntry logEntry : logEntryList) {
         // Only want LogEntry objects between oldest and newest times.
         if (logEntry.getTime() >= newest) continue;
         if (logEntry.getTime() <= oldest) break;

         if (logEntry.hasBolus()) sum += logEntry.getBolus();
      }
      return sum;
   }

   /**
    * Averages carbohydrate intake by day over the given period of time.
    *
    * @param logEntryList A chronologically ordered List of LogEntry objects.
    * @param oldest       Start of the relevant period (in millis since epoch).
    * @param newest       End of the relevant period.
    * @return Average daily carbs consumed over the period (in grams).
    */
   public static double calcAverageDailyCarbs(List<LogEntry> logEntryList, long oldest, long newest) {
      Map<Long, Double> dayToSumMap = new HashMap<>();
      for (LogEntry logEntry : logEntryList) {
         // Only want LogEntry objects between oldest and newest times.
         if (logEntry.getTime() >= newest) continue;
         if (logEntry.getTime() <= oldest) break;

         if (logEntry.hasCarbohydrate()) {
            // Update the day-to-sum map with this carb entry.
            long day = DatetimeUtils.startOfDay(logEntry.getTime());
            Double curSum = dayToSumMap.get(day);
            double newSum = logEntry.getCarbohydrate() + (curSum != null ? curSum : 0);
            dayToSumMap.put(day, newSum);
         }
      }
      // Calculate the average from the day-to-sum map.
      // i.e.  avg = total sum / # days
      double totalSum = 0;
      for (Double aDouble : dayToSumMap.values()) {
         totalSum += aDouble;
      }
      return totalSum / dayToSumMap.keySet().size();
   }

   /**
    * Averages insulin boluses by day over the given period of time.
    *
    * @param logEntryList A chronologically ordered List of LogEntry objects.
    * @param oldest       Start of the relevant period (in millis since epoch).
    * @param newest       End of the relevant period.
    * @return Average daily insulin bolused over the period (in units).
    */
   public static double calcAverageDailyBoluses(List<LogEntry> logEntryList, long oldest, long newest) {
      Map<Long, Double> dayToSumMap = new HashMap<>();
      for (LogEntry logEntry : logEntryList) {
         // Only want LogEntry objects between oldest and newest times.
         if (logEntry.getTime() >= newest) continue;
         if (logEntry.getTime() <= oldest) break;

         if (logEntry.hasBolus()) {
            // Update the day-to-sum map with this bolus entry.
            long day = DatetimeUtils.startOfDay(logEntry.getTime());
            Double curSum = dayToSumMap.get(day);
            double newSum = logEntry.getBolus() + (curSum != null ? curSum : 0);
            dayToSumMap.put(day, newSum);
         }
      }
      // Calculate the average from the day-to-sum map.
      // i.e.  avg = total sum / # days
      double totalSum = 0;
      for (Double aDouble : dayToSumMap.values()) {
         totalSum += aDouble;
      }
      return totalSum / dayToSumMap.keySet().size();
   }

   /**
    * Estimates a patients Hba1c using average blood glucose level. See the link below for more info.
    *
    * @param averageBg An average blood glucose level.
    * @return An estimated Hba1c value.
    * @see <a href='http://care.diabetesjournals.org/content/early/2008/06/07/dc08-0545.abstract">Translating the A1C Assay Into Estimated Average Glucose Values</a>
    */
   public static double calculateHba1c(double averageBg) {
      // Using the following formula to estimate Hba1c:
      // AG_{mg/dl} = 28.7 × A1C − 46.7, R^2 = 0.84, P < 0.0001
      return (averageBg + 46.7) / 28.7;
   }

   /**
    * Helper method to calculate an average blood glucose level over a given period of time.
    *
    * @param logEntryList A chronologically ordered List of LogEntry objects.
    * @param oldest       Start of the relevant period (in millis since epoch).
    * @param newest       End of the relevant period.
    * @return The average blood glucose level for LogEntry objects between oldest and newest.
    */
   public static double calculateAverageBloodGlucose(List<LogEntry> logEntryList, long oldest, long newest) {
      List<Number> bloodGlucoseList = extractBloodGlucoses(logEntryList, oldest, newest);
      return Statistics.average(bloodGlucoseList);
   }

   /**
    * Creates a List of blood glucose measurements from LogEntries within the given time period.
    *
    * @param logEntryList A chronologically ordered List of LogEntry objects.
    * @param oldest       Start of the relevant period (in millis since epoch).
    * @param newest       End of the relevant period.
    * @return A List of blood glucose values.
    */
   public static List<Number> extractBloodGlucoses(List<LogEntry> logEntryList, long oldest, long newest) {
      List<Number> bloodGlucoseList = new ArrayList<>();
      for (LogEntry logEntry : logEntryList) {
         // Only want LogEntry objects between oldest and newest times.
         if (logEntry.getTime() >= newest) continue;
         if (logEntry.getTime() <= oldest) break;

         // Add the blood glucose reading to the List.
         if (logEntry.hasBloodGlucose()) bloodGlucoseList.add(logEntry.getBloodGlucose());
      }
      return bloodGlucoseList;
   }

   /**
    * Estimates the amount of insulin which is active in the user's bloodstream at the given time.
    * In contrast w/ the other calculateActiveInsulin(3) this method uses a given List of LogEntry
    * objects for calculations instead of making a database query.
    *
    * @param logEntryList        A List of LogEntry objects ordered from most to least recent.
    * @param time                Epoch time in millis.
    * @param activeInsulinPeriod The user's chosen time period for which to consider insulin
    *                            active after entering the bloodstream (in hours).
    * @return Units of active insulin at the given time.
    */
   public static double calculateActiveInsulin(List<LogEntry> logEntryList, long time, float activeInsulinPeriod) {
      // Calculate the relevant time period.
      long activeInsulinPeriodMillis = DatetimeUtils.hoursToMillis(activeInsulinPeriod);
      long
            newest = time,
            oldest = newest - activeInsulinPeriodMillis;

      double total = 0;
      for (int i = 0; i < logEntryList.size(); i++) {
         LogEntry logEntry = logEntryList.get(i);
         // Iterate thru List until we find an old enough LogEntry.
         if (logEntry.getTime() >= newest) continue;
         // If the LogEntry is too old, we're done.
         if (logEntry.getTime() <= oldest) break;

         // If the LogEntry has a bolus we add the portion of
         // active insulin from this bolus to the total.
         if (logEntry.hasBolus()) {
            total += calculateActiveInsulin(logEntry, time, activeInsulinPeriodMillis);
         }
      }
      return total;
   }

   /**
    * Estimates the amount of insulin which is active in the user's bloodstream at the given time.
    * Note that this method will query the LogEntry database.
    *
    * @param time                Epoch time in millis.
    * @param activeInsulinPeriod The user's chosen time period for which to consider insulin
    *                            active after entering the bloodstream (in hours).
    * @param context             The current Context.
    * @return Units of active insulin at the given time.
    */
   public static double calculateActiveInsulin(long time, float activeInsulinPeriod, Context context) {
      // We want to fetch all LogEntry objects with boluses that fall within the activeInsulinPeriod.
      // i.e. _time < time AND _time > (time - activeInsulinPeriodMillis) AND _bolus > 0
      String selection = String.format("%s <= ? AND %s >= ? AND %s > ?",
            DbSchema.LogEntryTbl._TIME, DbSchema.LogEntryTbl._TIME, DbSchema.LogEntryTbl._BOLUS);
      long
            activeInsulinPeriodMillis = DatetimeUtils.hoursToMillis(activeInsulinPeriod),
            startTime = time - activeInsulinPeriodMillis,
            endTime = time;
      String[] selectionArgs = {String.valueOf(endTime), String.valueOf(startTime), "0"};

      DbManager dbManager = DbManager.getInstance();
      List<LogEntry> logEntryList = dbManager.fetchLogEntries(selection, selectionArgs, context);

      Log.d(TAG, String.format("Returned %d LogEntry objects w/in activeInsulinPeriod (%.2f hrs)",
            logEntryList.size(), activeInsulinPeriod));

      // Return the sum of active insulin components from each LogEntry.
      double total = 0;
      for (LogEntry logEntry : logEntryList) {
         total += calculateActiveInsulin(logEntry, time, activeInsulinPeriodMillis);
      }
      return total;
   }

   /**
    * Calculates the fraction of insulin from the given LogEntry which is still active at the given time.
    * This is a helper used by the above method of the same name.
    *
    * @param logEntry            A LogEntry with a non-zero bolus.
    * @param time                Epoch time in millis.
    * @param activeInsulinPeriod The user's chosen time period for which to consider insulin
    *                            active after entering the bloodstream (in millis).
    * @return Units of active insulin from the given LogEntry at the given time.
    */
   private static double calculateActiveInsulin(LogEntry logEntry, long time, long activeInsulinPeriod) {
      float elapsedTime = (float) (time - logEntry.getTime());
      float frac = elapsedTime / activeInsulinPeriod;

      if (frac >= 1) return 0; // Sanity check (shouldn't happen).

      return (1 - frac) * logEntry.getBolus();
   }
}

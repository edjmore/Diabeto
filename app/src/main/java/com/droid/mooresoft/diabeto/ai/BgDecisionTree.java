package com.droid.mooresoft.diabeto.ai;

import android.content.Context;
import android.util.Pair;

import com.droid.mooresoft.diabeto.ai.abstract_decision_tree.Attribute;
import com.droid.mooresoft.diabeto.ai.abstract_decision_tree.DecisionTree;
import com.droid.mooresoft.diabeto.ai.abstract_decision_tree.Instance;
import com.droid.mooresoft.diabeto.data.LogEntry;
import com.droid.mooresoft.diabeto.data.TreatmentSettings;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.droid.mooresoft.diabeto.util.DatetimeUtils.ONE_DAY;
import static com.droid.mooresoft.diabeto.util.DatetimeUtils.ONE_HOUR;
import static com.droid.mooresoft.diabeto.util.DatetimeUtils.startOfDay;
import static com.droid.mooresoft.diabeto.util.DiabetesUtils.calculateAverageBloodGlucose;
import static com.droid.mooresoft.diabeto.util.DiabetesUtils.sumBolusesOverPeriod;
import static com.droid.mooresoft.diabeto.util.DiabetesUtils.sumCarbsOverPeriod;

/**
 * Created by Edward Moore on 4/28/16.
 * Copyright (c) 2016 MooreSoft. All rights reserved.
 */
public class BgDecisionTree extends DecisionTree {

   private static final String[] FIVE_PERCENTILES = new String[]{
         "20th", "40th", "60th", "80th", "100th"
   };
   private static final String[] TEN_PERCENTILES = new String[]{
         "10th", "20th", "30th", "40th", "50th", "60th", "70th", "80th", "90th", "100th"
   };
   private static final Attribute[] ATTRIBUTES = {
         // new Attribute("Avg. Bg -6 Hours", 0, FIVE_PERCENTILES),
         new Attribute("Avg. Bg -3 Hours", 0, FIVE_PERCENTILES),
         new Attribute("Avg. Bg -1 Hours", 1, FIVE_PERCENTILES),
         // new Attribute("Total Carb -6 Hours", 3, FIVE_PERCENTILES),
         new Attribute("Total Carb -3 Hours", 2, FIVE_PERCENTILES),
         new Attribute("Total Carb -1 Hours", 3, FIVE_PERCENTILES),
         // new Attribute("Total Bolus -6 Hours", 6, FIVE_PERCENTILES),
         new Attribute("Total Bolus -3 Hours", 4, FIVE_PERCENTILES),
         new Attribute("Total Bolus -1 Hours", 5, FIVE_PERCENTILES),
         new Attribute("Last Bg", 9, FIVE_PERCENTILES),
         new Attribute("Time Since Last Bg", 6, FIVE_PERCENTILES),
         new Attribute("2nd to Last Bg", 7, FIVE_PERCENTILES),
         new Attribute("Time Since 2nd to Last Bg", 8, FIVE_PERCENTILES),
         new Attribute("Last Carb", 9, FIVE_PERCENTILES),
         new Attribute("Time Since Last Carb", 10, FIVE_PERCENTILES),
         new Attribute("Last Bolus", 11, FIVE_PERCENTILES),
         new Attribute("Time Since Last Bolus", 12, FIVE_PERCENTILES),
         // new Attribute("Time of Day", 17, FIVE_PERCENTILES)
   };
   private static final String[] LABELS = {"Hypoglycemic", "Normal", "Hyperglycemic"};

   public BgDecisionTree(List<Instance> instanceList) {
      super(Arrays.asList(ATTRIBUTES), instanceList, LABELS);
   }

   public static List<Instance> generateInstanceList(List<LogEntry> allLogEntries, long oldest, long newest, Context context) {
      List<Instance> instanceList = new ArrayList<>();
      for (LogEntry logEntry : allLogEntries) {
         // Only want LogEntry objects between oldest and newest times.
         if (logEntry.getTime() >= newest) continue;
         if (logEntry.getTime() <= oldest) break;

         if (logEntry.hasBloodGlucose()) {
            // Create and add a new Instance.
            long time = logEntry.getTime();
            String label = labelLogEntry(logEntry, context);
            Instance instance = generateInstance(allLogEntries, time, label);
            instanceList.add(instance);
         }
      }
      return instanceList;
   }

   private static String labelLogEntry(LogEntry logEntry, Context context) {
      double
            hypo = TreatmentSettings.getHypoCutoff(context),
            hyper = TreatmentSettings.getHyperCutoff(context);
      int bloodGlucose = logEntry.getBloodGlucose();

      if (bloodGlucose < hypo) return LABELS[0];
      else if (bloodGlucose >= hypo && bloodGlucose <= hyper) return LABELS[1];
      else return LABELS[2];
   }

   public static Instance generateInstance(List<LogEntry> logEntryList, long time, String label) {
      // Calculate avg bg, total carb, and total bolus over the past 6, 3, and 1 hours.
      double
            avgBg6 = calculateAverageBloodGlucose(logEntryList, time - 6 * ONE_HOUR, time),
            avgBg3 = calculateAverageBloodGlucose(logEntryList, time - 3 * ONE_HOUR, time),
            avgBg1 = calculateAverageBloodGlucose(logEntryList, time - 1 * ONE_HOUR, time);
      double
            totalCarb6 = sumCarbsOverPeriod(logEntryList, time - 6 * ONE_HOUR, time),
            totalCarb3 = sumCarbsOverPeriod(logEntryList, time - 3 * ONE_HOUR, time),
            totalCarb1 = sumCarbsOverPeriod(logEntryList, time - 1 * ONE_HOUR, time);
      double
            totalBolus6 = sumBolusesOverPeriod(logEntryList, time - 6 * ONE_HOUR, time),
            totalBolus3 = sumBolusesOverPeriod(logEntryList, time - 3 * ONE_HOUR, time),
            totalBolus1 = sumBolusesOverPeriod(logEntryList, time - 1 * ONE_HOUR, time);

      // Get the last two bg, carb, and bolus entries (may be null).
      Pair<LogEntry, LogEntry> last2Bgs = getLast2Bgs(logEntryList, time);
      Pair<LogEntry, LogEntry> last2Carbs = getLast2Carbs(logEntryList, time);
      Pair<LogEntry, LogEntry> last2Boluses = getLast2Boluses(logEntryList, time);

      int lastBg = -1, last2Bg = -1;
      long timeSinceLastBg = -1, timeSinceLast2Bg = -1;
      if (last2Bgs.first != null) {
         lastBg = last2Bgs.first.getBloodGlucose();
         timeSinceLastBg = time - last2Bgs.first.getTime();
      }
      if (last2Bgs.second != null) {
         last2Bg = last2Bgs.second.getBloodGlucose();
         timeSinceLast2Bg = time - last2Bgs.second.getTime();
      }

      int lastCarb = -1;
      long timeSinceLastCarb = -1;
      if (last2Carbs.first != null) {
         lastCarb = last2Carbs.first.getCarbohydrate();
         timeSinceLastCarb = time - last2Carbs.first.getTime();
      }

      double lastBolus = -1;
      long timeSinceLastBolus = -1;
      if (last2Boluses.first != null) {
         lastBolus = last2Boluses.first.getBolus();
         timeSinceLastBolus = last2Boluses.first.getTime();
      }

      return new Instance(label, new String[]{
            // discretizeBg(avgBg6),
            discretizeBg(avgBg3), discretizeBg(avgBg1),
            // discretizeTotalCarb(totalCarb6),
            discretizeTotalCarb(totalCarb3), discretizeTotalCarb(totalCarb1),
            // discretizeTotalBolus(totalBolus6),
            discretizeTotalBolus(totalBolus3), discretizeTotalBolus(totalBolus1),
            discretizeBg(lastBg), discretizeElapsedTime(timeSinceLastBg),
            discretizeBg(last2Bg), discretizeElapsedTime(timeSinceLast2Bg),
            discretizeCarb(lastCarb), discretizeElapsedTime(timeSinceLastCarb),
            discretizeBolus(lastBolus), discretizeElapsedTime(timeSinceLastBolus),
            // discretizeTimeOfDay(time)
      });
   }

   // DISCRETIZATION HELPERS //

   private static String discretizeBg(double bg) {
      final double maxBg = 400;
      return get5thPercentile(bg, maxBg);
   }

   private static String discretizeCarb(double carb) {
      final double maxCarb = 100;
      return get5thPercentile(carb, maxCarb);
   }

   private static String discretizeBolus(double bolus) {
      final double maxBolus = 12.5;
      return get5thPercentile(bolus, maxBolus);
   }

   private static String discretizeTotalCarb(double totalCarb) {
      final double maxTotalCarb = 200;
      return get5thPercentile(totalCarb, maxTotalCarb);
   }

   private static String discretizeTotalBolus(double totalBolus) {
      final double maxTotalBolus = 20;
      return get5thPercentile(totalBolus, maxTotalBolus);
   }

   private static String discretizeElapsedTime(long elapsedTime) {
      final long maxElapsedTime = 12 * ONE_HOUR;
      return get5thPercentile(elapsedTime, maxElapsedTime);
   }

   private static String discretizeTimeOfDay(long time) {
      long startOfDay = startOfDay(time);
      long elapsed = time - startOfDay;
      return get5thPercentile(elapsed, ONE_DAY);
   }

   private static String get10thPercentile(double numerator, double denominator) {
      int idx = (int) (numerator / denominator * 10);
      if (idx >= 10) idx = 9;
      else idx = Math.max(0, idx);
      return TEN_PERCENTILES[idx];
   }

   private static String get5thPercentile(double numerator, double denominator) {
      int idx = (int) (numerator / denominator * 5);
      if (idx >= 5) idx = 4;
      else idx = Math.max(0, idx);
      return FIVE_PERCENTILES[idx];
   }

   // HELPERS FOR LAST 2 BGS, CARBS, AND BOLUSES //

   private static Pair<LogEntry, LogEntry> getLast2Bgs(List<LogEntry> logEntryList, long time) {
      LogEntry last = null, last2 = null;
      for (LogEntry logEntry : logEntryList) {
         if (logEntry.getTime() < time) {
            if (logEntry.hasBloodGlucose()) {
               if (null == last) last = logEntry;
               else {
                  last2 = logEntry;
                  break;
               }
            }
         }
      }
      return new Pair<>(last, last2);
   }

   private static Pair<LogEntry, LogEntry> getLast2Carbs(List<LogEntry> logEntryList, long time) {
      LogEntry last = null, last2 = null;
      for (LogEntry logEntry : logEntryList) {
         if (logEntry.getTime() < time) {
            if (logEntry.hasCarbohydrate()) {
               if (null == last) last = logEntry;
               else {
                  last2 = logEntry;
                  break;
               }
            }
         }
      }
      return new Pair<>(last, last2);
   }

   private static Pair<LogEntry, LogEntry> getLast2Boluses(List<LogEntry> logEntryList, long time) {
      LogEntry last = null, last2 = null;
      for (LogEntry logEntry : logEntryList) {
         if (logEntry.getTime() < time) {
            if (logEntry.hasBolus()) {
               if (null == last) last = logEntry;
               else {
                  last2 = logEntry;
                  break;
               }
            }
         }
      }
      return new Pair<>(last, last2);
   }
}

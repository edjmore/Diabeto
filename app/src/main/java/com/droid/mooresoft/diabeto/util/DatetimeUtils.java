package com.droid.mooresoft.diabeto.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

/**
 * Created by Edward Moore on 4/9/16.
 * Copyright (c) 2016 MooreSoft. All rights reserved.
 */
public class DatetimeUtils {

   public static final long
         ONE_MINUTE = 60 * 1000, // millis
         ONE_HOUR = 60 * ONE_MINUTE,
         ONE_DAY = 24 * ONE_HOUR,
         ONE_WEEK = 7 * ONE_DAY,
         ONE_MONTH = 31 * ONE_DAY,
         ONE_YEAR = 365 * ONE_DAY;

   private static final String
         DATE_PATTERN = "E MMM d, yyyy",  // e.g. Thu Jan 1, 1970
         TIME_PATTERN = "h:mm a";         // e.g. 12:00 AM

   /**
    * Converts an absolute date string like "Thu Jan 1, 1970" to a relative one like "Thursday",
    * "Yesterday", "Today", or "Tomorrow". Per the Android Material Design spec, dates should be
    * represented in a manner analogous to the way people naturally speak with one another.
    *
    * @param absoluteDateString A formatted date string.
    * @param curTime            A current time this date string should be compared to.
    * @return A relative date string.
    */
   public static String convertToRelativeDateString(String absoluteDateString, long curTime) {
      long time = parseEpochTime(absoluteDateString);

      // First check if the date string represents today, tomorrow, or yesterday.
      if (isToday(time, curTime)) return "Today";
      if (isTomorrow(time, curTime)) return "Tomorrow";
      if (isYesterday(time, curTime)) return "Yesterday";

      // If the date is within a week (past or future) of the curTime, just return the day of the week.
      if (Math.abs(curTime - time) < ONE_WEEK) {
         Date date = new Date(time);
         return new SimpleDateFormat("cccc").format(date);
      }

      // Can't convert to relative date, just return the original string.
      return absoluteDateString;
   }

   /*
    * Checks if the queryTime occurs on the same day as the curTime.
    */
   private static boolean isToday(long queryTime, long curTime) {
      return startOfDay(queryTime) == startOfDay(curTime);
   }

   /*
    * Checks if the queryTime occurs the day after the curTime.
    */
   private static boolean isTomorrow(long queryTime, long curTime) {
      return startOfDay(queryTime) == startOfDay(curTime + ONE_DAY);
   }

   /*
    * Checks if the queryTime occured the day before the curTime.
    */
   private static boolean isYesterday(long queryTime, long curTime) {
      return startOfDay(queryTime) == startOfDay(curTime - ONE_DAY);
   }

   /**
    * Checks if the given date string represents a day in the past week.
    *
    * @param dateString A formatted date string.
    * @return True iff this date occured in the past week.
    */
   public static boolean wasThisWeek(String dateString) {
      long
            endOfWeek = System.currentTimeMillis(),
            startOfWeek = endOfWeek - ONE_WEEK;
      long time = parseEpochTime(dateString);
      return startOfWeek <= time && time <= endOfWeek;
   }

   /**
    * Helper for converting hours to millis.
    *
    * @param hours A number of hours.
    * @return Number of millis.
    */
   public static long hoursToMillis(double hours) {
      return (long) (hours * ONE_HOUR);
   }


   /**
    * Helper for converting minutes to millis.
    *
    * @param minutes A number of minutes.
    * @return Number of millis.
    */
   public static long minutesToMillis(double minutes) {
      return (long) (minutes * ONE_MINUTE);
   }

   /**
    * Calculates the start of a day in epoch time. More specifically, this method returns the
    * epoch time for 12:00 AM on the day during which the given time will come to pass.
    *
    * @param time An epoch time (in millis).
    * @return The epoch time at the start of the day.
    */
   public static long startOfDay(long time) {
      String
            dateString = getDateString(time),
            timeString = "12:00 AM";
      return parseEpochTime(dateString, timeString);
   }

   /**
    * Returns a formatted time string representing the given time.
    *
    * @param hours   Hours of day.
    * @param minutes Minutes of hour.
    * @return A formatted time string (e.g. 12:00 AM).
    */
   public static String getTimeString(int hours, int minutes) {
      SimpleDateFormat sdf = getFormatter(TIME_PATTERN);
      long
            quarterDay = 6 * ONE_HOUR,
            millis = hours * ONE_HOUR + minutes * ONE_MINUTE;
      Date date = new Date(quarterDay + millis);
      return sdf.format(date);
   }

   /**
    * Creates a formatted date string representing the given time (e.g. Thu Jan 1, 1970).
    *
    * @param epochTime The epoch time value in millis.
    * @return A formatted date string.
    */
   public static String getDateString(long epochTime) {
      return getFormattedDatetimeString(DATE_PATTERN, epochTime);
   }

   /**
    * Creates a formatted time string representing the given time (e.g. 12:00 AM).
    *
    * @param epochTime The epoch time value in millis.
    * @return A formatted time string.
    */
   public static String getTimeString(long epochTime) {
      return getFormattedDatetimeString(TIME_PATTERN, epochTime);
   }

   /**
    * Helper method equivalent to parseEpochTime(dateString, "12:00 AM").
    *
    * @param dateString A formatted date string.
    * @return Epoch time at 12:00 AM on the given date.
    */
   public static long parseEpochTime(String dateString) {
      return parseEpochTime(dateString, "12:00 AM");
   }

   /**
    * Parses the formatted date and time strings, then calculates the epoch time they represent.
    * This is intended to extract epoch time values from formatted strings created by the above methods.
    *
    * @param dateString A formatted date string of the form returned by getDateString(1).
    * @param timeString A formatted time string of the form returned by getTimeString(1).
    * @return Epoch time in millis.
    */
   public static long parseEpochTime(String dateString, String timeString) {
      String combinedPattern = String.format("%s %s", DATE_PATTERN, TIME_PATTERN);
      SimpleDateFormat sdf = new SimpleDateFormat(combinedPattern);
      try {
         String combinedString = String.format("%s %s", dateString, timeString);
         Date date = sdf.parse(combinedString);
         return date.getTime();
      } catch (ParseException e) {
         // If there's a ParseException then I made a programming error somewhere.
         throw new RuntimeException(e);
      }
   }

   private static String getFormattedDatetimeString(String pattern, long epochTime) {
      SimpleDateFormat sdf = getFormatter(pattern);
      Date datetime = new Date(epochTime);
      return sdf.format(datetime);
   }

   private static SimpleDateFormat getFormatter(String pattern) {
      Locale locale = Locale.getDefault();
      SimpleDateFormat sdf = new SimpleDateFormat(pattern, locale);
      TimeZone timeZone = TimeZone.getDefault();
      sdf.setTimeZone(timeZone);
      return sdf;
   }
}

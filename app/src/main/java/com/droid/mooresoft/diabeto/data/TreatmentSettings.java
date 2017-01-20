package com.droid.mooresoft.diabeto.data;

import android.content.Context;
import android.content.SharedPreferences;

import com.droid.mooresoft.diabeto.R;

/**
 * Created by Edward Moore on 4/14/16.
 * Copyright (c) 2016 MooreSoft. All rights reserved.
 */
public class TreatmentSettings {

   public static double getHypoCutoff(Context context) {
      return 70;
   }

   public static double getHyperCutoff(Context context) {
      return 140;
   }

   /**
    * Queries preferences for the user's current active insulin time period setting.
    *
    * @param context The current Context.
    * @return The active insulin period.
    */
   public static float getActiveInsulinPeriod(Context context) {
      String key = context.getString(R.string.key_active_insulin);
      return getPrefs(context).getFloat(key, 0);
   }

   /**
    * Sets the preference value for the active insulin time period.
    *
    * @param activeInsulinPeriod The new active insulin time period.
    * @param context             The current Context.
    * @return True iff the change was successfully commited to preferences.
    */
   public static boolean setActiveInsulinPeriod(float activeInsulinPeriod, Context context) {
      String key = context.getString(R.string.key_active_insulin);
      return getPrefsEditor(context).putFloat(key, activeInsulinPeriod).commit();
   }

   public static float getSensitivity(Context context) {
      String key = context.getString(R.string.title_sensitivity);
      return getPrefs(context).getFloat(key, 0);
   }

   public static boolean setSensitivity(float sensitivity, Context context) {
      String key = context.getString(R.string.title_sensitivity);
      return getPrefsEditor(context).putFloat(key, sensitivity).commit();
   }

   public static float getCurrentCarbohydrateRatio(Context context) {
      String key = context.getString(R.string.title_carbohydrate_ratio);
      return getPrefs(context).getFloat(key, 0);
   }

   public static boolean setCarbohydrateRatio(float carbohydrateRatio, Context context) {
      String key = context.getString(R.string.title_carbohydrate_ratio);
      return getPrefsEditor(context).putFloat(key, carbohydrateRatio).commit();
   }

   public static float getCurrentBasalRate(Context context) {
      String key = context.getString(R.string.title_basal_rate);
      return getPrefs(context).getFloat(key, 0);
   }

   public static boolean setBasalRate(float basalRate, Context context) {
      String key = context.getString(R.string.title_basal_rate);
      return getPrefsEditor(context).putFloat(key, basalRate).commit();
   }

   // SHARED PREFERENCES HELPERS //

   private static SharedPreferences.Editor getPrefsEditor(Context context) {
      return getPrefs(context).edit();
   }

   private static SharedPreferences getPrefs(Context context) {
      String filename = context.getString(R.string.prefs_file);
      return context.getSharedPreferences(filename, Context.MODE_PRIVATE);
   }
}

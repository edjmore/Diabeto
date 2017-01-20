package com.droid.mooresoft.diabeto.external;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.SystemClock;
import android.util.Log;

import com.droid.mooresoft.diabeto.util.DatetimeUtils;

import static com.droid.mooresoft.diabeto.util.DatetimeUtils.ONE_MINUTE;

/**
 * Created by Edward Moore on 4/27/16.
 * Copyright (c) 2016 MooreSoft. All rights reserved.
 */
public class AlarmHelper {
   private static final String TAG = AlarmHelper.class.getSimpleName();

   public static void setTestingAlarm(Context context) {
      // Set a repeating alarm to go off approximately once every 45 minutes.
      Intent alarmIntent = new Intent(context, AlarmReciever.class)
            .setAction(AlarmReciever.ACTION_NOTIFY_SERVICE);
      AlarmHelper.setInexactRepeatingAlarm(45 * ONE_MINUTE, alarmIntent, context);
   }

   /**
    * Sets an inexact repeating alarm to be repeated at the given interval. The given intent should
    * be an explicit intent for {@link AlarmReciever} and must have some action defined.
    *
    * @param interval The interval for alarms to repeat on.
    * @param intent   An Intent (see above).
    * @param context  The current Context.
    */
   public static void setInexactRepeatingAlarm(long interval, Intent intent, Context context) {
      // Create a pending broadcast intent to be handled by the {@link AlarmReceiver}.
      PendingIntent operation =
            PendingIntent.getBroadcast(context, intent.getAction().hashCode(), intent, PendingIntent.FLAG_UPDATE_CURRENT);

      // Set the alarm.
      AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
      alarmManager.setInexactRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime(), interval, operation);

      Log.d(TAG, String.format("Set an inexact repeating alarm:\n\tInterval = %d minutes\n\tAction = %s",
            (int) (interval / DatetimeUtils.ONE_MINUTE), intent.getAction()));
   }
}

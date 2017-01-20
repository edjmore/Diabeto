package com.droid.mooresoft.diabeto.external;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * Created by Edward Moore on 4/27/16.
 * Copyright (c) 2016 MooreSoft. All rights reserved.
 */
public class BootReceiver extends BroadcastReceiver {
   private static final String TAG = BootReceiver.class.getSimpleName();

   @Override
   public void onReceive(Context context, Intent intent) {
      Log.d(TAG, "Booting...");

      if (intent.getAction() == Intent.ACTION_BOOT_COMPLETED) {
         AlarmHelper.setTestingAlarm(context);
      }
   }
}

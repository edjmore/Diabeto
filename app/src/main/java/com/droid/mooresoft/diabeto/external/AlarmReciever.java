package com.droid.mooresoft.diabeto.external;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * Created by Edward Moore on 4/27/16.
 * Copyright (c) 2016 MooreSoft. All rights reserved.
 */
public class AlarmReciever extends BroadcastReceiver {
   private static final String TAG = AlarmReciever.class.getSimpleName();

   public static final String ACTION_NOTIFY_SERVICE = "action_notify_service";

   @Override
   public void onReceive(Context context, Intent intent) {
      Log.d(TAG, String.format("Received alarm:\n\tAction = %s", intent.getAction()));

      if (intent.getAction() == ACTION_NOTIFY_SERVICE) {
         // Start the AiService.
         Intent reminderService = new Intent(context, AiService.class);
         context.startService(reminderService);
      }
   }
}

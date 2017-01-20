package com.droid.mooresoft.diabeto.external;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;

import com.droid.mooresoft.diabeto.R;
import com.droid.mooresoft.diabeto.view.activity.MainActivity;

/**
 * Created by Edward Moore on 4/26/16.
 * Copyright (c) 2016 MooreSoft. All rights reserved.
 */
public class Notify {

   /**
    * Builds a warning Notification about a potential upcoming blood glucose.
    *
    * @param warning The warning to display.
    * @param context The current Context.
    * @return The built Notification.
    */
   public static Notification buildBgWarningNotification(String warning, Context context) {
      NotificationCompat.Builder builder = new NotificationCompat.Builder(context)
            .setSmallIcon(R.drawable.ic_face_black)
            .setContentTitle("Warning from diabeto.ai")
            .setContentText(warning);
      // This notification will launch the {@link MainActivity}.
      Intent intent = new Intent(context, MainActivity.class);

      // Setup back stack so the back button brings user back to home screen.
      TaskStackBuilder stackBuilder = TaskStackBuilder.create(context)
            .addParentStack(MainActivity.class)
            .addNextIntent(intent);

      // Grab pending intent from stack builder.
      PendingIntent pendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
      builder.setContentIntent(pendingIntent);

      return builder.build();
   }

   /**
    * Simple convenience method for posting a Notification.
    *
    * @param id           An integer ID for the Notification.
    * @param notification The Notification to post.
    * @param context      The current Context.
    */
   public static void postNotification(int id, Notification notification, Context context) {
      NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
      notificationManager.notify(id, notification);
   }
}

package com.droid.mooresoft.diabeto.external;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import java.io.File;

/**
 * Created by Edward Moore on 4/28/16.
 * Copyright (c) 2016 MooreSoft. All rights reserved.
 */
public class SharingIsCaring {

   public static Intent shareLogEntries(Uri uri, Context context) {
      final String title = "Share logbook with";

      // Intent csvChooser = shareFile(logEntriesCsvFile, "text/csv", title);
      // if (isResolvable(csvChooser, context)) return csvChooser;

      // else {
      Intent txtChooser = shareFile(uri, "text/csv", title);
      if (isResolvable(txtChooser, context)) return txtChooser;
      // }

      return null;
   }

   private static Intent shareFile(Uri uri, String mime, final String title) {
      Intent sharingIntent = new Intent(Intent.ACTION_SEND)
            .setType(mime)
            .putExtra(Intent.EXTRA_STREAM, uri);

      return Intent.createChooser(sharingIntent, title);
   }

   private static boolean isResolvable(Intent chooserIntent, Context context) {
      return chooserIntent.resolveActivity(context.getPackageManager()) != null;
   }
}
